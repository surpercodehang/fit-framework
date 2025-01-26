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
 * 为 {@link AttributeInfo} 提供列表。
 *
 * @author 梁济时
 * @since 2022-06-09
 */
public final class AttributeList implements Iterable<AttributeInfo> {
    private final List<AttributeInfo> attributes;

    private final ClassFile file;

    /**
     * 获取属性列表所属的类文件。
     *
     * @param file 表示属性列表所属类文件的 {@link ClassFile}。
     * @param in 表示用于读取属性的输入流的 {@link InputStream}。
     * @throws IOException 如果发生 I/O 错误。
     */
    public AttributeList(ClassFile file, InputStream in) throws IOException {
        this.file = Validation.notNull(file, "The owning class file of an attribute list cannot be null.");
        Validation.notNull(in, "The input stream to read attributes cannot be null.");
        U2 count = U2.read(in);
        this.attributes = new ArrayList<>(count.intValue());
        for (U2 i = U2.ZERO; i.compareTo(count) < 0; i = i.add(U2.ONE)) {
            this.attributes.add(new AttributeInfo(this, in));
        }
    }

    /**
     * 获取属性列表所属的类文件。
     *
     * @return 表示属性列表所属类文件的 {@link ClassFile}。
     */
    public ClassFile file() {
        return this.file;
    }

    /**
     * 获取包含属性的数量。
     *
     * @return 表示属性数量的 {@link U2}。
     */
    public U2 count() {
        return U2.of(this.attributes.size());
    }

    /**
     * 获取指定索引出的属性。
     *
     * @param index 表示属性所在索引的 {@link U2}。
     * @return 表示该索引出的属性的 {@link AttributeInfo}。
     */
    public AttributeInfo get(U2 index) {
        return this.attributes.get(index.intValue());
    }

    @Override
    public Iterator<AttributeInfo> iterator() {
        return Collections.unmodifiableList(this.attributes).iterator();
    }
}
