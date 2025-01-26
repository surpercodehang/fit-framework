/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.resource.ClassPath;
import modelengine.fitframework.resource.ClassPathResource;
import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.resource.ResourcePath;
import modelengine.fitframework.resource.ResourceResolver;
import modelengine.fitframework.resource.ResourceTree;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.wildcard.Pattern;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * 为 {@link ResourceResolver} 提供在类加载程序中解析资源的实现。
 *
 * @author 梁济时
 * @since 2022-07-25
 */
public final class ClassLoaderResourceResolver implements ResourceResolver {
    private final ClassLoader loader;

    private List<ClassPath> classPaths;

    /**
     * 使用资源所在的类加载程序初始化 {@link ClassLoaderResourceResolver} 类的新实例。
     *
     * @param loader 使用包含资源的类加载程序的 {@link ClassLoader}。
     * @throws IllegalArgumentException {@code loader} 为 {@code null}。
     */
    public ClassLoaderResourceResolver(ClassLoader loader) {
        this.loader = notNull(loader, "The class loader of resources to resolve cannot be null.");
    }

    @Override
    public Resource[] resolve(String locationPattern) throws IOException {
        String prefix = obtainFixedPrefix(locationPattern);
        if (StringUtils.isEmpty(prefix)) {
            return this.resolveFuzzily(locationPattern);
        } else if (prefix.length() == locationPattern.length()) {
            return this.resolveExactly(locationPattern);
        } else {
            List<Resource> resources = new ArrayList<>();
            Enumeration<URL> urls = this.loader.getResources(prefix);
            ResourcePath path = ResourcePath.parse(locationPattern.substring(prefix.length()));
            Pattern<String> pattern = path.asPattern();
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                ResourceTree tree = ResourceTree.of(url);
                resources.addAll(tree.match(pattern));
            }
            return resources.toArray(Resource.EMPTY_ARRAY);
        }
    }

    /**
     * 获取固定内容的前缀。
     *
     * @param pattern 表示原始的样式字符串的 {@link String}。
     * @return 表示固定前缀的 {@link String}。
     */
    private static String obtainFixedPrefix(String pattern) {
        int index = pattern.indexOf(WILDCARD);
        if (index < 0) {
            return pattern;
        }
        int last = pattern.lastIndexOf(JarEntryLocation.ENTRY_PATH_SEPARATOR, index);
        if (last > -1) {
            return pattern.substring(0, last + 1);
        } else {
            return StringUtils.EMPTY;
        }
    }

    private Resource[] resolveExactly(String locationPattern) throws IOException {
        Enumeration<URL> resourceUrls = this.loader.getResources(locationPattern);
        List<Resource> resources = new LinkedList<>();
        while (resourceUrls.hasMoreElements()) {
            URL resourceUrl = resourceUrls.nextElement();
            Resource resource = new UrlResource(resourceUrl);
            resources.add(resource);
        }
        return resources.toArray(Resource.EMPTY_ARRAY);
    }

    private Resource[] resolveFuzzily(String locationPattern) {
        if (this.classPaths == null) {
            this.classPaths = ClassPath.fromClassLoader(this.loader, true);
        }

        ResourcePath resourcePath = ResourcePath.parse(locationPattern);
        Pattern<String> pattern = resourcePath.asPattern();
        return this.classPaths.stream()
                .map(resolver -> resolver.resolve(pattern))
                .flatMap(Collection::stream)
                .toArray(ClassPathResource[]::new);
    }
}
