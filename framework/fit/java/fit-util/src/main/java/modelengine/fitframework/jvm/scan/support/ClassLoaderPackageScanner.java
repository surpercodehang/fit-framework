/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.scan.support;

import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.jvm.scan.PackageScanner;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.protocol.jar.JarLocation;
import modelengine.fitframework.resource.UrlUtils;
import modelengine.fitframework.util.ClassUtils;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 为 {@link PackageScanner} 提供基于类加载程序的实现。
 *
 * @author 梁济时
 * @since 2023-02-23
 */
public class ClassLoaderPackageScanner implements PackageScanner {
    private final ClassLoader loader;
    private final Callback callback;
    private final Set<String> scannedClassNames;

    public ClassLoaderPackageScanner(ClassLoader loader, Callback callback) {
        this.loader = loader;
        this.callback = callback;
        this.scannedClassNames = new HashSet<>();
    }

    @Override
    public void scan(Collection<String> toScanPackages) {
        Set<String> actual = this.removeDuplicated(toScanPackages);
        for (String basePackage : actual) {
            this.scan(basePackage);
        }
    }

    private Set<String> removeDuplicated(Collection<String> toScanPackages) {
        if (CollectionUtils.isEmpty(toScanPackages)) {
            return Collections.emptySet();
        }
        Set<String> existPackages = new HashSet<>();
        for (String toScanPackage : toScanPackages) {
            String current = toScanPackage + ".";
            if (existPackages.stream().anyMatch(current::startsWith)) {
                continue;
            }
            Set<String> toDelete = existPackages.stream()
                    .filter(existPackage -> existPackage.startsWith(current))
                    .collect(Collectors.toSet());
            existPackages.removeAll(toDelete);
            existPackages.add(current);
        }
        return existPackages.stream()
                .map(sp -> sp.substring(0, sp.length() - 1))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
    }

    @Override
    public void scan(Class<?> entry) {
        if (!this.scannedClassNames.contains(entry.getName())) {
            this.scannedClassNames.add(entry.getName());
            this.callback.notify(this, entry);
        }
    }

    /**
     * 获取类加载器。
     *
     * @return 表示类加载器的 {@link ClassLoader}。
     */
    protected ClassLoader getLoader() {
        return this.loader;
    }

    private void scan(String basePackage) {
        String resourceName = toResourceName(basePackage);
        Enumeration<URL> resourceUrls = this.getPackageResources(basePackage, resourceName);
        while (resourceUrls.hasMoreElements()) {
            URL resourceUrl = resourceUrls.nextElement();
            if (UrlUtils.isJdkResource(resourceUrl)) {
                continue;
            }
            if (StringUtils.equalsIgnoreCase(resourceUrl.getProtocol(), JarLocation.FILE_PROTOCOL)) {
                File directory;
                try {
                    directory = new File(resourceUrl.toURI());
                } catch (URISyntaxException ex) {
                    throw new IllegalStateException(StringUtils.format(
                            "Failed to obtain directory from resource URL. [url={0}]",
                            resourceUrl.toExternalForm()), ex);
                }
                this.scanClassesInDirectory(basePackage, directory);
            } else {
                JarLocation location = JarEntryLocation.parse(resourceUrl).asJar();
                Jar jar;
                try {
                    jar = Jar.from(location);
                } catch (IOException ex) {
                    throw new IllegalStateException(StringUtils.format(
                            "Failed to load JAR to scan classes. [location={0}]",
                            location), ex);
                }
                this.scanClassesInJar(basePackage, jar);
            }
        }
    }

    /**
     * 获取包路径下的所有指定名字的资源。
     *
     * @param basePackage 表示指定的包路径的 {@link String}。
     * @param resourceName 表示指定资源名字的 {@link String}。
     * @return 表示包路径下的所有指定名字的资源的 {@link Enumeration}{@code <}{@link URL}{@code >}。
     */
    protected Enumeration<URL> getPackageResources(String basePackage, String resourceName) {
        Enumeration<URL> resourceUrls;
        try {
            resourceUrls = this.loader.getResources(resourceName);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to obtain resources for package. [package={0}]",
                    basePackage), e);
        }
        return resourceUrls;
    }

    private static String toResourceName(String basePackage) {
        return StringUtils.replace(basePackage, ClassUtils.PACKAGE_SEPARATOR, JarEntryLocation.ENTRY_PATH_SEPARATOR);
    }

    private static String toClassName(String basePackage, String resourceName) {
        String name = resourceName.substring(0, resourceName.length() - ClassFile.FILE_EXTENSION.length());
        name = StringUtils.replace(name, JarEntryLocation.ENTRY_PATH_SEPARATOR, ClassUtils.PACKAGE_SEPARATOR);
        name = basePackage + ClassUtils.PACKAGE_SEPARATOR + name;
        return name;
    }

    private void scanClassesInDirectory(String basePackage, File directory) {
        File[] files = directory.listFiles();
        if (files == null || files.length < 1) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                this.scanClassesInDirectory(basePackage + ClassUtils.PACKAGE_SEPARATOR + file.getName(), file);
                continue;
            }
            if (StringUtils.endsWithIgnoreCase(file.getName(), ClassFile.FILE_EXTENSION)) {
                this.notifyClassScanned(basePackage, file.getName());
            }
        }
    }

    private void scanClassesInJar(String basePackage, Jar jar) {
        for (Jar.Entry entry : jar.entries()) {
            if (StringUtils.endsWithIgnoreCase(entry.name(), ClassFile.FILE_EXTENSION)) {
                this.notifyClassScanned(basePackage, entry.name());
            }
        }
    }

    private void notifyClassScanned(String basePackage, String resourceName) {
        String className = toClassName(basePackage, resourceName);
        if (this.scannedClassNames.contains(className)) {
            return;
        }
        this.scannedClassNames.add(className);
        Class<?> clazz;
        try {
            clazz = this.loader.loadClass(className);
        } catch (ClassNotFoundException ignored) {
            return;
        }
        this.callback.notify(this, clazz);
    }
}
