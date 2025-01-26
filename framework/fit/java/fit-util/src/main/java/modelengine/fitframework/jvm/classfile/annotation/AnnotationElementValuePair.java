/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.annotation;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.jvm.classfile.AttributeInfo;
import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.jvm.classfile.constant.Utf8Info;
import modelengine.fitframework.jvm.classfile.lang.U2;

import java.io.IOException;
import java.io.InputStream;

/**
 * 为注解中的元素提供键值对定义。
 *
 * @author 梁济时
 * @since 2022-10-29
 */
public final class AnnotationElementValuePair {
    private final AnnotationElementValuePairList list;
    private final U2 nameIndex;
    private final AnnotationElementValue value;

    /**
     * 构造一个表示注解元素值对的对象。
     *
     * @param list 表示所属的注解元素值对列表的 {@link AnnotationElementValuePairList}。
     * @param in 表示包含注解元素值对的输入流的 {@link InputStream}。
     * @throws IOException 如果无法从输入流中读取数据，则抛出此异常。
     */
    public AnnotationElementValuePair(AnnotationElementValuePairList list, InputStream in) throws IOException {
        this.list = notNull(list, "The owning list of a annotation element value pair cannot be null.");
        notNull(in, "The input stream that contains annotation element value pair cannot be null.");
        this.nameIndex = U2.read(in);
        this.value = AnnotationElementValue.read(this, in);
    }

    /**
     * 获取键值对所属的注解。
     *
     * @return 表示所属注解的 {@link AnnotationInfo}。
     */
    public AnnotationInfo annotation() {
        return this.list.annotation();
    }

    /**
     * 获取键值对所属的属性。
     *
     * @return 表示所属属性的 {@link AttributeInfo}。
     */
    public AttributeInfo attribute() {
        return this.annotation().attribute();
    }

    /**
     * 获取键值对所属的类文件。
     *
     * @return 表示所属类文件的 {@link ClassFile}。
     */
    public ClassFile file() {
        return this.annotation().file();
    }

    /**
     * 获取元素的键在常量池中的索引。常量池中该索引处的元素必然是一个 {@link Utf8Info}。
     *
     * @return 表示键在常量池中索引的 {@link U2}。
     */
    public U2 nameIndex() {
        return this.nameIndex;
    }

    /**
     * 获取元素的值。
     *
     * @return 表示元素值的 {@link AnnotationElementValue}。
     */
    public AnnotationElementValue value() {
        return this.value;
    }
}
