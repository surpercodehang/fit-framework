/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.launch;

import modelengine.fitframework.protocol.Handlers;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarEntryLocation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.Locale;
import java.util.Objects;
import java.util.jar.Manifest;

/**
 * 为 All In One 场景提供应用程序启动程序。
 *
 * @author 梁济时
 * @since 2023-02-01
 */
public final class AggregatedFitLauncher {
    /** 表示 META-INF 的目录的入口名。 */
    public static final String META_DIRECTORY_ENTRY_NAME = "META-INF" + JarEntryLocation.ENTRY_PATH_SEPARATOR;

    /** 表示 MANIFEST.MF 的入口名。 */
    public static final String MANIFEST_ENTRY_NAME = META_DIRECTORY_ENTRY_NAME + "MANIFEST.MF";

    /** 表示 FIT-INF 的目录的入口名。 */
    public static final String FIT_ROOT_ENTRY_NAME = "FIT-INF" + JarEntryLocation.ENTRY_PATH_SEPARATOR;

    /** 表示类文件的目录的入口名。 */
    public static final String CLASS_DIRECTORY_ENTRY_NAME = FIT_ROOT_ENTRY_NAME + "classes"
            + JarEntryLocation.ENTRY_PATH_SEPARATOR;

    /** 表示标准库的目录的入口名。 */
    public static final String LIB_ENTRY_NAME = FIT_ROOT_ENTRY_NAME + "lib" + JarEntryLocation.ENTRY_PATH_SEPARATOR;

    /** 表示三方库的目录的入口名。 */
    public static final String THIRD_PARTY_ENTRY_NAME = FIT_ROOT_ENTRY_NAME + "third-party"
            + JarEntryLocation.ENTRY_PATH_SEPARATOR;

    /** 表示共享库的目录的入口名。 */
    public static final String SHARED_ENTRY_NAME = FIT_ROOT_ENTRY_NAME + "shared"
            + JarEntryLocation.ENTRY_PATH_SEPARATOR;

    /** 表示 FIT 的入口类的键。 */
    public static final String MANIFEST_ENTRY_CLASS_KEY = "FIT-Entry-Class";

    public static void main(String[] args) throws Throwable {
        Handlers.register();
        UrlClassLoader sharedClassLoader = obtainSharedClassLoader();
        UrlClassLoader frameworkClassLoader = new UrlClassLoader(new URL[0], sharedClassLoader);
        String entryClassName = getEntryClassName(sharedClassLoader, frameworkClassLoader, Jar.from(startup()));
        if (entryClassName == null || entryClassName.isEmpty()) {
            throw new IllegalStateException("No FIT-Class-Name configured in manifest of JAR.");
        }
        Method main = obtainMainMethod(frameworkClassLoader, entryClassName);
        Thread.currentThread().setContextClassLoader(frameworkClassLoader);
        try {
            main.invoke(null, (Object) args);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to access main method. [class=%s, method=%s]",
                    main.getDeclaringClass().getName(),
                    signatureOf(main)), e);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private static File startup() {
        CodeSource source = AggregatedFitLauncher.class.getProtectionDomain().getCodeSource();
        if (source == null) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to obtain code source of entry class. [entry=%s]",
                    AggregatedFitLauncher.class.getName()));
        }
        URL location = source.getLocation();
        if (location == null) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to locate launcher JAR. [entry=%s]",
                    AggregatedFitLauncher.class.getName()));
        }
        URI uri;
        try {
            uri = location.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to obtain URI of protection domain. [url=%s]",
                    location.toExternalForm()), e);
        }
        File file = new File(uri);
        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to canonicalize file of launcher jar.", e);
        }
        if (!file.isFile()) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "The protection domain is not a file. [url=%s]",
                    location.toExternalForm()));
        }
        return file;
    }

    private static String getEntryClassName(UrlClassLoader sharedClassLoader, UrlClassLoader frameworkClassLoader,
            Jar jar) throws MalformedURLException {
        String entryClassName = null;
        for (Jar.Entry entry : jar.entries()) {
            if (MANIFEST_ENTRY_NAME.equalsIgnoreCase(entry.name())) {
                Manifest manifest = manifestOf(entry);
                entryClassName = obtainEntryClassName(manifest);
                continue;
            }
            if (CLASS_DIRECTORY_ENTRY_NAME.equalsIgnoreCase(entry.name())) {
                frameworkClassLoader.addURL(entry.location().asJar().toUrl());
                continue;
            }
            if (!isJarEntry(entry.name())) {
                continue;
            }
            if (inDirectory(entry.name(), LIB_ENTRY_NAME)) {
                frameworkClassLoader.addURL(entry.location().asJar().toUrl());
                continue;
            }
            if (inDirectory(entry.name(), SHARED_ENTRY_NAME)) {
                sharedClassLoader.addURL(entry.location().asJar().toUrl());
                continue;
            }
            if (inDirectory(entry.name(), THIRD_PARTY_ENTRY_NAME)) {
                frameworkClassLoader.addURL(entry.location().asJar().toUrl());
            }
        }
        return entryClassName;
    }

    private static UrlClassLoader obtainSharedClassLoader() {
        ClassLoader loader = AggregatedFitLauncher.class.getClassLoader();
        return new UrlClassLoader(new URL[0], loader);
    }

    private static Manifest manifestOf(Jar.Entry entry) {
        try (InputStream in = entry.read()) {
            return new Manifest(in);
        } catch (IOException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to load manifest of JAR. [url=%s]",
                    entry.location()), e);
        }
    }

    private static String obtainEntryClassName(Manifest manifest) {
        String className = manifest.getMainAttributes().getValue(MANIFEST_ENTRY_CLASS_KEY);
        if (className != null) {
            className = className.trim();
        }
        return className;
    }

    private static Method obtainMainMethod(ClassLoader loader, String entryClassName) {
        Class<?> entryClass;
        try {
            entryClass = loader.loadClass(entryClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "The entry class of application not found. [class=%s]",
                    entryClassName), e);
        }
        Method method;
        try {
            method = entryClass.getDeclaredMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "No main method found in entry class. [class=%s]",
                    entryClassName), e);
        }
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "The main method must be public. [class=%s, method=%s]",
                    method.getDeclaringClass().getName(),
                    signatureOf(method)));
        } else if (!Modifier.isStatic(modifiers)) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "The main method must be static. [class=%s, method=%s]",
                    method.getDeclaringClass().getName(),
                    signatureOf(method)));
        } else if (Objects.equals(method.getReturnType(), Void.class)) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "The main method cannot return any object. [class=%s, method=%s]",
                    method.getDeclaringClass().getName(),
                    signatureOf(method)));
        } else {
            return method;
        }
    }

    private static boolean inDirectory(String string, String directory) {
        return string.length() >= directory.length() && string.regionMatches(true, 0, directory, 0, directory.length());
    }

    private static boolean isJarEntry(String entryName) {
        return entryName.length() >= Jar.FILE_EXTENSION.length() && entryName.regionMatches(true,
                entryName.length() - Jar.FILE_EXTENSION.length(),
                Jar.FILE_EXTENSION,
                0,
                Jar.FILE_EXTENSION.length());
    }

    private static String signatureOf(Method method) {
        StringBuilder builder = new StringBuilder();
        builder.append(method.getName());
        builder.append('(');
        if (method.getParameterCount() > 0) {
            Parameter[] parameters = method.getParameters();
            builder.append(parameters[0].getParameterizedType().getTypeName());
            for (int i = 1; i < parameters.length; i++) {
                builder.append(", ").append(parameters[i].getParameterizedType().getTypeName());
            }
        }
        builder.append(')');
        return builder.toString();
    }

    /**
     * 表示 {@link URLClassLoader} 的共享类加载器的实现。
     */
    public static class UrlClassLoader extends URLClassLoader {
        public UrlClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        public void addURL(URL url) {
            super.addURL(url);
        }
    }
}
