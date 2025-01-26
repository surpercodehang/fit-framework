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

/**
 * 为字段提供列表。
 *
 * @author 梁济时
 * @since 2022-06-09
 */
public final class FieldList implements Iterable<FieldInfo> {
    private final ClassFile file;
    private final List<FieldInfo> fields;

    /**
     * 构造一个字段列表。
     *
     * @param file 表示字段列表所属的类文件的 {@link ClassFile}。
     * @param in 表示字段列表输入流的 {@link InputStream}。
     * @throws IOException 如果发生 I/O 错误。
     */
    public FieldList(ClassFile file, InputStream in) throws IOException {
        this.file = Validation.notNull(file, "The owning class file of the field list cannot be null.");
        U2 count = U2.read(in);
        this.fields = new ArrayList<>(count.intValue());
        for (U2 i = U2.ZERO; i.compareTo(count) < 0; i = i.add(U2.ONE)) {
            this.fields.add(new FieldInfo(this, in));
        }
    }

    /**
     * 获取字段列表所属的类文件。
     *
     * @return 表示类文件的 {@link ClassFile}。
     */
    public ClassFile file() {
        return this.file;
    }

    /**
     * 获取列表中包含字段的数量。
     *
     * @return 表示字段数量的 {@link U2}。
     */
    public U2 count() {
        return U2.of(this.fields.size());
    }

    /**
     * 获取指定索引处的字段。
     *
     * @param index 表示字段在列表中的索引的 {@link U2}。
     * @return 表示该索引出的字段信息的 {@link FieldInfo}。
     */
    public FieldInfo get(U2 index) {
        return this.fields.get(index.intValue());
    }

    @Override
    public Iterator<FieldInfo> iterator() {
        return Collections.unmodifiableList(this.fields).iterator();
    }
}
