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
import modelengine.fitframework.jvm.classfile.descriptor.ClassDescriptor;
import modelengine.fitframework.jvm.classfile.lang.U2;

import java.io.IOException;
import java.io.InputStream;

/**
 * 表示注解的定义。
 *
 * @author 梁济时
 * @since 2022-10-29
 */
public final class AnnotationInfo {
    private final AttributeInfo attribute;
    private final U2 typeIndex;
    private final AnnotationElementValuePairList elements;

    /**
     * 使用所属的属性与包含注解数据的输入流初始化 {@link AnnotationInfo} 类的新实例。
     *
     * @param attribute 表示注解所属的属性的 {@link AttributeInfo}。
     * @param in 表示包含注解信息的输入流的 {@link InputStream}。
     * @throws IOException 读取注解信息过程发生输入输出异常。
     */
    public AnnotationInfo(AttributeInfo attribute, InputStream in) throws IOException {
        this.attribute = notNull(attribute, "The owning attribute of annotation cannot be null.");
        this.typeIndex = U2.read(notNull(in, "The input stream that contains annotation cannot be null."));
        this.elements = new AnnotationElementValuePairList(this, in);
    }

    /**
     * 获取注解所属的属性。
     *
     * @return 表示注解所属属性的 {@link AttributeInfo}。
     */
    public AttributeInfo attribute() {
        return this.attribute;
    }

    /**
     * 获取注解所属的类型文件。
     *
     * @return 表示注解所属的类型文件的 {@link ClassFile}。
     */
    public ClassFile file() {
        return this.attribute().list().file();
    }

    /**
     * 获取注解类型在常量池中的索引。
     * <p>该索引处常量池中元素的类型必然是 {@link Utf8Info CONSTANT_Utf8_info}，并且其内容是一个
     * {@link ClassDescriptor field descriptor}。</p>
     *
     * @return 表示注解类型在常量池中索引的 {@link U2}。
     */
    public U2 typeIndex() {
        return this.typeIndex;
    }

    /**
     * 获取所包含元素键值对的列表。
     *
     * @return 表示所包含元素键值对的列表的 {@link AnnotationElementValuePairList}。
     */
    public AnnotationElementValuePairList elements() {
        return this.elements;
    }
}
