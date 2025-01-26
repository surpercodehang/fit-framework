/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.classpath.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.io.virtualization.VirtualDirectory;
import modelengine.fitframework.resource.classpath.ClassPath;
import modelengine.fitframework.resource.classpath.ClassPathKey;

import java.io.Closeable;
import java.io.IOException;

/**
 * 表示类路径信息。
 *
 * @author 梁济时
 * @since 2022-07-27
 */
public final class DefaultClassPath implements ClassPath {
    private final ClassPathKey key;
    private final VirtualDirectory directory;

    /**
     * 使用类路径的键和虚拟目录初始化 {@link DefaultClassPath} 类的新实例。
     *
     * @param key 表示类路径的键的 {@link ClassPathKey}。
     * @param directory 表示类路径的目录的 {@link VirtualDirectory}。
     * @throws IllegalArgumentException {@code key} 或 {@code directory} 为 {@code null}。
     */
    public DefaultClassPath(ClassPathKey key, VirtualDirectory directory) {
        this.key = notNull(key, "The key of a classpath cannot be null.");
        this.directory = notNull(directory, "The directory of a classpath cannot be null.");
    }

    @Override
    public ClassPathKey key() {
        return this.key;
    }

    @Override
    public VirtualDirectory directory() {
        return this.directory;
    }

    @Override
    public void close() throws IOException {
        if (this.directory instanceof Closeable) {
            ((Closeable) this.directory).close();
        }
    }

    @Override
    public String toString() {
        return this.key().toString();
    }
}
