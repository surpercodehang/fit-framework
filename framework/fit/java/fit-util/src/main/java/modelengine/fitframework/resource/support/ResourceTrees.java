/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.pattern.flyweight.WeakCache;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarLocation;
import modelengine.fitframework.resource.ResourceTree;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 为 {@link ResourceTree} 提供工具方法。
 *
 * @author 梁济时
 * @since 2023-01-29
 */
public final class ResourceTrees {
    private static final WeakCache<Jar, ResourceTree> JARS =
            WeakCache.create(JarResourceTree::new, JarResourceTree::jar);
    private static final WeakCache<File, ResourceTree> DIRS =
            WeakCache.create(DirectoryResourceTree::new, DirectoryResourceTree::directory);

    /**
     * 根据 {@link URL} 创建一个资源树。
     *
     * @param url 表示给定的 {@link URL}。
     * @return 表示资源树的 {@link ResourceTree}。
     */
    public static ResourceTree of(URL url) {
        notNull(url, "The URL of resource tree cannot be null.");
        JarLocation location = JarLocation.parse(url);
        if (location.nests().isEmpty() && location.file().isDirectory()) {
            return DIRS.get(location.file());
        } else {
            try {
                return JARS.get(Jar.from(location));
            } catch (IOException ex) {
                throw new IllegalStateException(StringUtils.format("Failed to load JAR of resource tree. [url=%s]",
                        url.toExternalForm()), ex);
            }
        }
    }

    /**
     * 根据给定的 {@link Jar} 文件创建一个资源树。
     *
     * @param jar 表示给定的 {@link Jar}。
     * @return 表示资源树的 {@link ResourceTree}。
     */
    public static ResourceTree of(Jar jar) {
        return JARS.get(jar);
    }

    /**
     * 根据给定的文件创建一个资源树。
     *
     * @param file 表示文件的 {@link File}。
     * @return 表示资源树的 {@link ResourceTree}。
     */
    public static ResourceTree of(File file) {
        if (file.isFile()) {
            try {
                return JARS.get(Jar.from(file));
            } catch (IOException ex) {
                throw new IllegalStateException(StringUtils.format(
                        "Failed to load JAR of resource tree from file. [file={0}]",
                        FileUtils.path(file)), ex);
            }
        } else {
            return DIRS.get(file);
        }
    }
}
