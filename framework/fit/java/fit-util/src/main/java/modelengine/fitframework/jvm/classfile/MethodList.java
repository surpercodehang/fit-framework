/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.lang.U2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * 为字段提供列表。
 *
 * @author 梁济时
 * @since 2022-06-09
 */
public final class MethodList implements Iterable<MethodInfo> {
    private final ClassFile file;
    private final List<MethodInfo> methods;

    /**
     * 获取方法列表所在的类文件。
     *
     * @param file 表示方法列表所在类文件的 {@link ClassFile}。
     * @param in 表示方法列表的输入流的 {@link InputStream}。
     * @throws IOException 如果发生 I/O 错误。
     */
    public MethodList(ClassFile file, InputStream in) throws IOException {
        this.file = Validation.notNull(file, "The owning class file of the field list cannot be null.");
        U2 count = U2.read(in);
        this.methods = new ArrayList<>(count.intValue());
        for (U2 i = U2.ZERO; i.compareTo(count) < 0; i = i.add(U2.ONE)) {
            this.methods.add(new MethodInfo(this, in));
        }
    }

    /**
     * 获取方法列表所在的类文件。
     *
     * @return 表示方法列表所在类文件的 {@link ClassFile}。
     */
    public ClassFile file() {
        return this.file;
    }

    /**
     * 获取列表中包含方法信息的数量。
     *
     * @return 表示方法数量的 {@link U2}。
     */
    public U2 count() {
        return U2.of(this.methods.size());
    }

    /**
     * 获取指定索引处的方法。
     *
     * @param index 表示方法信息所在列表中的索引的 {@link U2}。
     * @return 表示该索引处的方法信息的 {@link MethodInfo}。
     */
    public MethodInfo get(U2 index) {
        return this.methods.get(index.intValue());
    }

    /**
     * 返回一个操作流，用以操作列表中的方法信息。
     *
     * @return 表示用以操作列表中方法信息的操作流的 {@link Stream}{@code <}{@link MethodInfo}{@code >}。
     */
    public Stream<MethodInfo> stream() {
        return this.methods.stream();
    }

    @Override
    public Iterator<MethodInfo> iterator() {
        return Collections.unmodifiableList(this.methods).iterator();
    }
}
