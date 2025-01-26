/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.jvm.classfile.lang.U4;
import modelengine.fitframework.util.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 表示属性的信息。
 *
 * @author 梁济时
 * @since 2022-06-09
 */
public final class AttributeInfo {
    private final AttributeList list;
    private final U2 attributeNameIndex;
    private final U4 attributeLength;
    private final byte[] content;

    /**
     * 构造一个表示属性的信息的新实例。
     *
     * @param list 表示属性所属的属性列表的 {@link AttributeList}。
     * @param in 表示用于读取属性的输入流的 {@link InputStream}。
     * @throws IOException 当读取过程中发生 I/O 异常时。
     */
    public AttributeInfo(AttributeList list, InputStream in) throws IOException {
        this.list = Validation.notNull(list, "The owning list of an attribute cannot be null.");
        Validation.notNull(in, "The input stream to read attribute cannot be null.");
        this.attributeNameIndex = U2.read(in);
        this.attributeLength = U4.read(in);
        this.content = IoUtils.read(in, this.attributeLength.intValue());
    }

    /**
     * 获取属性所属的属性列表。
     *
     * @return 表示属性所属的属性列表的 {@link AttributeList}。
     */
    public AttributeList list() {
        return this.list;
    }

    /**
     * 获取属性所属的类文件。
     *
     * @return 表示属性所属的类文件的 {@link ClassFile}。
     */
    public ClassFile file() {
        return this.list().file();
    }

    /**
     * 获取属性名字的下标序号。
     *
     * @return 表示属性名字的下标序号的 {@link U2}。
     */
    public U2 attributeNameIndex() {
        return this.attributeNameIndex;
    }

    /**
     * 获取属性内容的长度。
     *
     * @return 表示属性内容长度的 {@link U4}。
     */
    public U4 attributeLength() {
        return this.attributeLength;
    }

    /**
     * 获取包含属性内容的输入流。
     *
     * @return 表示包含属性内容的输入流的 {@link InputStream}。
     * @throws IOException 当读取过程中发生 IO 异常时。
     */
    public InputStream read() throws IOException {
        return new ByteArrayInputStream(this.content);
    }
}
