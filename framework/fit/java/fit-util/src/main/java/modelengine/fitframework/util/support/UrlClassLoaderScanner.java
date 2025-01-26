/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.resource.UrlUtils;
import modelengine.fitframework.util.ClassScanner;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.FunctionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;

/**
 * 为 {@link ClassScanner} 基于 {@link URLClassLoader} 的类型扫描工具。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-08-28
 */
public class UrlClassLoaderScanner implements ClassScanner {
    private final URLClassLoader classLoader;
    private final Stack<URL> urlStack;
    private final Predicate<URL> urlFilter;
    private Consumer<String> classDetectedObserver;
    private Predicate<String> classNameFilter;

    /**
     * 使用待扫描的类加载器初始化 {@link UrlClassLoaderScanner} 类的新实例。
     *
     * @param classLoader 表示类加载器的 {@link URLClassLoader}。
     * @param urlFilter 表示待扫描的 URL 的过滤器的 {@link Predicate}{@code <}{@link URL}{@code >}。
     */
    public UrlClassLoaderScanner(URLClassLoader classLoader, Predicate<URL> urlFilter) {
        this.classLoader = Validation.notNull(classLoader, "The URL class loader to scan cannot be null.");
        this.urlStack = new Stack<>();
        this.urlFilter = ObjectUtils.nullIf(urlFilter, url -> true);
    }

    @Override
    public final URLClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public final void addClassDetectedObserver(Consumer<String> observer) {
        this.classDetectedObserver = FunctionUtils.connect(this.classDetectedObserver, observer);
    }

    @Override
    public final void addClassNameFilter(Predicate<String> classNameFilter) {
        this.classNameFilter = FunctionUtils.and(this.classNameFilter, classNameFilter);
    }

    @Override
    public void scan() {
        URL[] urls = this.classLoader.getURLs();
        this.push(urls);
        while (!this.urlStack.isEmpty()) {
            URL url = this.urlStack.pop();
            if (this.urlFilter.test(url)) {
                this.createURLScanner(url).scan(url);
            }
        }
    }

    private UrlScanner createURLScanner(URL url) {
        return UrlUtils.isJar(url) ? new JarUrlScanner(this) : new FileUrlScanner(this);
    }

    /**
     * 当一个 {@code .class} 文件被发现的时候，通知相应的观察者进行相应的处理。
     *
     * @param name 表示被发现的 {@code .class} 文件的名字的 {@link String}。
     */
    private void onClassDetected(String name) {
        String className = StringUtils.substring(name, 0, -ClassFile.FILE_EXTENSION.length());
        className = className.replace('/', '.').replace('\\', '.');
        if (isValidClassName(className) && FunctionUtils.test(this.classNameFilter, className, true)) {
            this.notify(this.classDetectedObserver, className);
        }
    }

    private static boolean isValidClassName(String className) {
        return !className.contains("-");
    }

    private void notify(Consumer<String> observer, String value) {
        if (observer != null) {
            observer.accept(value);
        }
    }

    private void push(URL[] urls) {
        if (urls.length == 0) {
            return;
        }
        Stream.of(urls).forEach(this.urlStack::push);
    }

    private abstract static class UrlScanner {
        /** 表示扫描 {@link URLClassLoader} 的扫描器。 */
        protected final UrlClassLoaderScanner scanner;

        public UrlScanner(UrlClassLoaderScanner scanner) {
            this.scanner = scanner;
        }

        /**
         * 扫描指定的资源文件。
         *
         * @param url 表示待扫描的资源文件的 {@link URL}。
         */
        public abstract void scan(URL url);
    }

    /**
     * 针对 {@link File} 的 {@link URL} 的扫描类。
     *
     * @author 张越
     * @author 季聿阶
     * @since 2021-02-23
     */
    private static class FileUrlScanner extends UrlScanner {
        public FileUrlScanner(UrlClassLoaderScanner scanner) {
            super(scanner);
        }

        @Override
        public void scan(URL url) {
            File file = new File(url.getPath());
            if (file.isDirectory()) {
                this.scanDirectory(file);
                return;
            }
            if (file.getName().endsWith(ClassFile.FILE_EXTENSION)) {
                this.scanner.onClassDetected(file.getName());
            }
        }

        private void scanDirectory(File directory) {
            Path root = directory.toPath();
            try (Stream<Path> walk = Files.walk(root, Integer.MAX_VALUE)) {
                walk.filter(path -> path.toFile().getName().endsWith(ClassFile.FILE_EXTENSION))
                        .forEach(path -> this.scanner.onClassDetected(FileUtils.path(root.relativize(path).toFile())));
            } catch (IOException e) {
                throw new IllegalStateException(StringUtils.format("Failed to scan class directory. [directory={0}]",
                        FileUtils.path(directory)), e);
            }
        }
    }

    /**
     * 针对 {@code JAR} 类型的 {@link URL} 的扫描类。
     *
     * @author 张越
     * @author 季聿阶
     * @since 2021-02-23
     */
    private static class JarUrlScanner extends UrlScanner {
        private static final URL[] EMPTY = new URL[0];

        public JarUrlScanner(UrlClassLoaderScanner scanner) {
            super(scanner);
        }

        @Override
        public void scan(URL url) {
            if (!UrlUtils.exists(url)) {
                return;
            }
            try (JarFile jar = UrlUtils.toJarFile(url)) {
                if (hasClassPathAttribute(jar)) {
                    URL[] urls = this.getClassPathUrls(url, jar);
                    this.scanner.push(urls);
                    return;
                }
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        this.onResourceEntryDetected(entry);
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException(StringUtils.format("Failed to load JAR file. [url={0}]",
                        url.toExternalForm()), e);
            }
        }

        private static boolean hasClassPathAttribute(JarFile jar) throws IOException {
            Manifest man = jar.getManifest();
            return Optional.ofNullable(man)
                    .map(Manifest::getMainAttributes)
                    .map(attr -> attr.containsKey(Attributes.Name.CLASS_PATH))
                    .orElse(false);
        }

        private URL[] getClassPathUrls(URL base, JarFile jar) throws IOException {
            Manifest man = jar.getManifest();
            return Optional.ofNullable(man)
                    .map(Manifest::getMainAttributes)
                    .map(attr -> attr.getValue(Attributes.Name.CLASS_PATH))
                    .map(classPathValue -> this.parseClassPath(base, classPathValue))
                    .orElse(EMPTY);
        }

        private URL[] parseClassPath(URL base, String classPathValue) {
            try {
                List<String> classPaths = StringUtils.splitToList(classPathValue, ' ');
                URL[] urls = new URL[classPaths.size()];
                for (int i = 0; i < classPaths.size(); i++) {
                    urls[i] = new URL(base, classPaths.get(i));
                }
                return urls;
            } catch (MalformedURLException e) {
                throw new IllegalStateException(StringUtils.format("Failed to parse class path. [classPath={0}]",
                        classPathValue), e);
            }
        }

        private void onResourceEntryDetected(JarEntry entry) {
            String name = entry.getName();
            if (name.endsWith(ClassFile.FILE_EXTENSION)) {
                this.scanner.onClassDetected(name);
            }
        }
    }
}
