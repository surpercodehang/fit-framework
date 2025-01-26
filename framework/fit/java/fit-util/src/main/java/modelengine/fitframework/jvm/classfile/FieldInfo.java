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

/**
 * 为字段提供信息。
 *
 * @author 梁济时
 * @since 2022-06-09
 */
public final class FieldInfo {
    private final U2 accessFlags;
    private final U2 nameIndex;
    private final U2 descriptorIndex;
    private final AttributeList attributes;

    private final FieldList list;

    /**
     * 构造一个表示字段信息的对象。
     *
     * @param list 表示字段所在列表的 {@link FieldList}。
     * @param in 表示用于读取字段信息输入流的 {@link InputStream}。
     * @throws IOException 如果发生 I/O 错误。
     */
    public FieldInfo(FieldList list, InputStream in) throws IOException {
        this.list = Validation.notNull(list, "The owning list of a field cannot be null.");
        Validation.notNull(in, "The input stream to read field info cannot be null.");
        this.accessFlags = U2.read(in);
        this.nameIndex = U2.read(in);
        this.descriptorIndex = U2.read(in);
        this.attributes = new AttributeList(list.file(), in);
    }

    /**
     * 获取字段所在的列表。
     *
     * @return 表示字段所在列表的 {@link FieldList}。
     */
    public FieldList list() {
        return this.list;
    }

    /**
     * 获取字段的访问标记。
     *
     * @return 表示访问标记的 {@link U2}。
     */
    public U2 accessFlags() {
        return this.accessFlags;
    }

    /**
     * 获取字段名称在常量池中的索引。
     *
     * @return 表示字段名称在常量池中索引的 {@link U2}。
     */
    public U2 nameIndex() {
        return this.nameIndex;
    }

    /**
     * 获取字段描述符在常量池中的索引。
     *
     * @return 表示字段描述符在常量池中索引的 {@link U2}。
     */
    public U2 descriptorIndex() {
        return this.descriptorIndex;
    }

    /**
     * 获取字段上包含属性的列表。
     *
     * @return 表示属性列表的 {@link AttributeList}。
     */
    public AttributeList attributes() {
        return this.attributes;
    }
}
