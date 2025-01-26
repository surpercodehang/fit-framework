/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.resource.ClassPath;
import modelengine.fitframework.resource.ClassPathResource;
import modelengine.fitframework.resource.ResourceResolver;
import modelengine.fitframework.resource.ResourceTree;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.wildcard.Pattern;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link ResourceResolver} 提供基于类路径的实现。
 *
 * @author 梁济时
 * @since 2023-01-17
 */
public class DefaultClassPath implements ClassPath {
    private static final String CLASS_PATH_PROPERTY_KEY = "java.class.path";

    private final ClassLoader loader;
    private final ResourceTree resources;

    private DefaultClassPath(ClassLoader loader, ResourceTree resources) {
        this.loader = loader;
        this.resources = resources;
    }

    /**
     * 获取待解析的资源所在的类加载程序。
     *
     * @return 表示类加载程序的 {@link ClassLoader}。
     */
    @Override
    public final ClassLoader loader() {
        return this.loader;
    }

    /**
     * 获取待解析的资源所在的类路径。
     *
     * @return 表示类路径的 {@link URL}。
     * @throws MalformedURLException 当 URL 的格式不正确时。
     */
    @Override
    public final URL url() throws MalformedURLException {
        return this.resources.location();
    }

    private static List<ResourceTree.Node> children(ResourceTree.Node node) {
        if (node instanceof ResourceTree.DirectoryNode) {
            return ((ResourceTree.DirectoryNode) node).children().toList();
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 解析指定样式的资源。
     *
     * @param pattern 表示资源的样式的 {@link Pattern}。
     * @return 表示解析到的资源的列表的 {@link List}{@code <}{@link ClassPathResource}{@code >}。
     */
    @Override
    public final List<ClassPathResource> resolve(Pattern<String> pattern) {
        return pattern.match(this.resources().roots().toList(), DefaultClassPath::children, ResourceTree.Node::name)
                .stream()
                .filter(ResourceTree.FileNode.class::isInstance)
                .map(ResourceTree.FileNode.class::cast)
                .map(file -> this.new FileNodeResource(file))
                .collect(Collectors.toList());
    }

    @Override
    public ResourceTree resources() {
        return this.resources;
    }

    private final class FileNodeResource implements ClassPathResource {
        private final ResourceTree.FileNode node;

        private FileNodeResource(ResourceTree.FileNode node) {
            this.node = node;
        }

        @Override
        public ClassLoader loader() {
            return DefaultClassPath.this.loader();
        }

        @Override
        public String key() {
            return this.node.path();
        }

        @Override
        public String filename() {
            return this.node.name();
        }

        @Override
        public URL url() throws MalformedURLException {
            return this.node.url();
        }

        @Override
        public InputStream read() throws IOException {
            return this.node.read();
        }

        @Override
        public String toString() {
            return this.node.toString();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            DefaultClassPath another = (DefaultClassPath) obj;
            return Objects.equals(this.loader, another.loader) && Objects.equals(this.resources, another.resources);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.loader, this.resources});
    }

    @Override
    public String toString() {
        return this.resources.toString();
    }

    /**
     * 为指定类加载器中的指定 URL 创建类路径。
     *
     * @param loader 表示类加载器的 {@link ClassLoader}。
     * @param url 表示类路径的位置的 {@link URL}。
     * @return 表示类路径的 {@link ClassPath}。
     */
    public static ClassPath create(ClassLoader loader, URL url) {
        notNull(loader, "The class loader of a class path cannot be null.");
        ResourceTree resourceTree = ResourceTree.of(url);
        return new DefaultClassPath(loader, resourceTree);
    }

    /**
     * 为指定类加载器中的指定 URL 创建类路径。
     *
     * @param loader 表示类加载器的 {@link ClassLoader}。
     * @param resources 表示类路径中的资源树的 {@link ResourceTree}。
     * @return 表示类路径的 {@link ClassPath}。
     */
    public static ClassPath create(ClassLoader loader, ResourceTree resources) {
        notNull(loader, "The class loader of a class path cannot be null.");
        notNull(resources, "The resource tree of a class path cannot be null.");
        return new DefaultClassPath(loader, resources);
    }

    /**
     * 从指定的类加载器中加载类路径信息。
     * <p>将通过 {@link ClassLoader#getParent()} 进行递归加载，当一个类路径的 {@link URL} 重复出现时，将优先使用
     * {@link ClassLoader#getParent()} 中的一个。</p>
     *
     * @param loader 表示待从中加载类路径的类加载程序的 {@link ClassLoader}。
     * @param recursive 若为 {@code true}，则通过 {@link ClassLoader#getParent()} 递归在所有 {@link ClassLoader}
     * 中查找，否则仅在当前 {@link ClassLoader} 中查找。
     * @return 表示从类加载程序中加载到的所有类路径的 {@link List}{@code <}{@link ClassPath}{@code >}。
     */
    public static List<ClassPath> from(ClassLoader loader, boolean recursive) {
        List<ClassLoader> loaders = loaders(loader, recursive);
        List<ClassPath> classPaths = new LinkedList<>();
        Map<String, URL> urls = new HashMap<>();
        for (ClassLoader current : loaders) {
            Map<String, URL> currentClassPaths = urlsOf(current);
            for (Map.Entry<String, URL> currentClassPath : currentClassPaths.entrySet()) {
                if (urls.containsKey(currentClassPath.getKey())) {
                    continue;
                }
                ClassPath classPath = ClassPath.of(current, currentClassPath.getValue());
                classPaths.add(classPath);
                urls.put(currentClassPath.getKey(), currentClassPath.getValue());
            }
        }
        return classPaths;
    }

    private static List<ClassLoader> loaders(ClassLoader loader, boolean recursive) {
        if (!recursive) {
            return Collections.singletonList(loader);
        }
        List<ClassLoader> loaders = new LinkedList<>();
        ClassLoader current = loader;
        while (current != null) {
            loaders.add(0, current);
            current = current.getParent();
        }
        return loaders;
    }

    private static Map<String, URL> urlsOf(ClassLoader loader) {
        Map<String, URL> classPaths = new HashMap<>();
        if (loader instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader) loader).getURLs();
            Stream.of(urls).forEach(url -> classPaths.put(url.toString(), url));
        }
        if (loader == ClassLoader.getSystemClassLoader()) {
            String value = System.getProperty(CLASS_PATH_PROPERTY_KEY, StringUtils.EMPTY);
            String[] parts = StringUtils.split(value, File.pathSeparatorChar);
            for (String part : parts) {
                String actual = StringUtils.trim(part);
                if (StringUtils.isEmpty(actual)) {
                    continue;
                }
                File file = new File(actual);
                try {
                    file = file.getCanonicalFile();
                } catch (IOException ex) {
                    throw new IllegalStateException(StringUtils.format(
                            "The class path in property '{0}' is not a file. [classPath={1}]",
                            CLASS_PATH_PROPERTY_KEY,
                            actual), ex);
                }
                URL url = FileUtils.urlOf(file);
                classPaths.put(url.toString(), url);
            }
        }
        return classPaths;
    }
}
