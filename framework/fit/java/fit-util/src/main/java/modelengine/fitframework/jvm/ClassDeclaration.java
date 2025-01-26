/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.jvm.support.DefaultClassDeclaration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * 为类型提供声明。
 *
 * @author 梁济时
 * @since 2022-06-08
 */
public interface ClassDeclaration {
    /**
     * 获取类型的名称。
     *
     * @return 表示类型名称的 {@link String}。
     */
    String name();

    /**
     * 获取父类型的名称。
     *
     * @return 表示父类型名称的 {@link String}。
     */
    String superclass();

    /**
     * 获取类型上定义的修饰符的集合。
     *
     * @return 表示修饰符集合的 {@link Set}{@code <}{@link Modifier}{@code >}。
     */
    Set<Modifier> modifiers();

    /**
     * 从指定的类型文件中加载类型定义的信息。
     *
     * @param file 表示类型文件的 {@link ClassFile}。
     * @return 表示从类型文件中加载到的类型定义的 {@link ClassDeclaration}。
     * @throws IllegalArgumentException {@code file} 为 {@code null}。
     */
    static ClassDeclaration load(ClassFile file) {
        return new DefaultClassDeclaration(file);
    }

    /**
     * 从指定的输入流中加载类型定义的信息。
     *
     * @param in 表示包含类型定义的输入流的 {@link InputStream}。
     * @return 表示加载到的类型定义的 {@link ClassDeclaration}。
     * @throws IllegalArgumentException {@code in} 为 {@code null}。
     * @throws IOException 加载过程发生输入输出异常。
     */
    static ClassDeclaration load(InputStream in) throws IOException {
        notNull(in, "The input stream to load class declaration cannot be null.");
        ClassFile file = new ClassFile(in);
        return load(file);
    }
}
