/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.attribute;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.jvm.classfile.AttributeInfo;
import modelengine.fitframework.jvm.classfile.AttributeList;
import modelengine.fitframework.jvm.classfile.annotation.AnnotationList;
import modelengine.fitframework.jvm.classfile.constant.Utf8Info;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 为 {@link AbstractAttribute} 提供用以表示可见的运行时注解的实现。
 *
 * @author 梁济时
 * @since 2022-10-29
 */
public class RuntimeVisibleAnnotationsAttribute extends AbstractAttribute {
    private static final String NAME = "RuntimeVisibleAnnotations";

    private final AnnotationList annotations;

    /**
     * 为属性内容提供基类。
     *
     * @param attribute 表示所属的属性的 {@link AttributeInfo}。
     * @param in 表示包含属性数据的输入流的 {@link InputStream}。
     * @throws IOException 当读取过程发生输入输出异常时。
     */
    public RuntimeVisibleAnnotationsAttribute(AttributeInfo attribute, InputStream in) throws IOException {
        super(attribute);
        notNull(in, "The input stream to read runtime visible annotations cannot be null.");
        this.annotations = new AnnotationList(attribute, in);
    }

    /**
     * 获取包含的注解的列表。
     *
     * @return 表示注解列表的 {@link AnnotationList}。
     */
    public AnnotationList annotations() {
        return this.annotations;
    }

    /**
     * 在指定属性列表中查找所有当前类型的属性。
     *
     * @param attributes 表示属性列表的 {@link AttributeList}。
     * @return 表示当前类型的属性列表的 {@link List}{@code <}{@link RuntimeVisibleAnnotationsAttribute}{@code >}。
     * @throws IOException 读取过程发生输入输出异常。
     */
    public static List<RuntimeVisibleAnnotationsAttribute> list(AttributeList attributes) throws IOException {
        if (attributes == null) {
            return Collections.emptyList();
        }
        List<RuntimeVisibleAnnotationsAttribute> results = new LinkedList<>();
        for (AttributeInfo attribute : attributes) {
            String name = attribute.file().constants().<Utf8Info>get(attribute.attributeNameIndex()).stringValue();
            if (StringUtils.equalsIgnoreCase(name, NAME)) {
                try (InputStream in = attribute.read()) {
                    results.add(new RuntimeVisibleAnnotationsAttribute(attribute, in));
                }
            }
        }
        return results;
    }

    /**
     * 从属性列表中找到运行时可见的注解属性。
     *
     * @param attributes 表示属性列表的 {@link AttributeList}。
     * @return 表示找到的运行时可见的注解属性的 {@link RuntimeVisibleAnnotationsAttribute}。
     * @throws IOException 当寻找过程中发生异常时。
     */
    public static RuntimeVisibleAnnotationsAttribute lookup(AttributeList attributes) throws IOException {
        if (attributes == null) {
            return null;
        }
        for (AttributeInfo attribute : attributes) {
            String name = attribute.file().constants().<Utf8Info>get(attribute.attributeNameIndex()).stringValue();
            if (StringUtils.equalsIgnoreCase(name, NAME)) {
                try (InputStream in = attribute.read()) {
                    return new RuntimeVisibleAnnotationsAttribute(attribute, in);
                }
            }
        }
        return null;
    }
}
