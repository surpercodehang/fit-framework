/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.classpath;

import modelengine.fitframework.io.virtualization.VirtualDirectory;
import modelengine.fitframework.resource.classpath.support.DefaultClassPath;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 表示类路径。
 *
 * @author 梁济时
 * @since 2022-07-27
 */
public interface ClassPath extends Closeable {
    /**
     * 获取类路径的键。
     *
     * @return 表示类路径的键的 {@link ClassPathKey}。
     */
    ClassPathKey key();

    /**
     * 获取类路径的目录。
     *
     * @return 表示类路径的根目录的 {@link VirtualDirectory}。
     */
    VirtualDirectory directory();

    /**
     * 使用类路径的键和虚拟目录创建类路径的新实例。
     *
     * @param key 表示类路径的键的 {@link ClassPathKey}。
     * @param directory 表示类路径的目录的 {@link VirtualDirectory}。
     * @return 表示创建出来的新的类路径的 {@link ClassPath}。
     * @throws IllegalArgumentException 当 {@code key} 或 {@code directory} 为 {@code null} 时。
     */
    static ClassPath of(ClassPathKey key, VirtualDirectory directory) {
        return new DefaultClassPath(key, directory);
    }

    /**
     * 根据指定的类路径的键的集合，创建类路径实例。
     *
     * @param keys 表示类路径的键的集合的 {@link Collection}{@code <}{@link ClassPathKey}{@code >}。
     * @return 表示新创建的类路径的列表的 {@link List}{@code <}{@link ClassPath}{@code >}。
     * @throws IOException 加载过程发生输入输出异常。
     */
    static List<ClassPath> create(Collection<ClassPathKey> keys) throws IOException {
        List<ClassPath> classPaths = new ArrayList<>(keys.size());
        for (ClassPathKey key : keys) {
            try {
                classPaths.add(key.create());
            } catch (IOException e) {
                closeAll(e, classPaths);
                throw e;
            }
        }
        return classPaths;
    }

    /**
     * 关闭所有的类路径，并将发生的异常加入原始异常中。
     *
     * @param originCause 表示原始异常的 {@link IOException}。
     * @param toCloseClassPaths 表示待关闭的类路径列表的 {@link List}{@code <}{@link ClassPath}{@code >}。
     */
    static void closeAll(IOException originCause, List<ClassPath> toCloseClassPaths) {
        for (ClassPath classPath : toCloseClassPaths) {
            try {
                classPath.close();
            } catch (IOException suppressed) {
                if (originCause != null) {
                    originCause.addSuppressed(suppressed);
                }
            }
        }
    }
}
