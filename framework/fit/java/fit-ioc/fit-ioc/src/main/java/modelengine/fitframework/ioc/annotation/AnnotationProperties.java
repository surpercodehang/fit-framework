/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation;

import modelengine.fitframework.ioc.annotation.support.DefaultAnnotationProperty;

import java.lang.annotation.Annotation;

/**
 * 为 {@link AnnotationProperty} 提供工具方法。
 *
 * @author 梁济时
 * @since 2022-08-16
 */
public final class AnnotationProperties {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private AnnotationProperties() {}

    /**
     * 为指定类型注解的指定名称的属性创建属性信息实例。
     *
     * @param annotation 表示注解类型的 {@link Class}。
     * @param name 表示属性名称的 {@link String}。
     * @return 表示注解属性信息的 {@link AnnotationProperty}。
     * @throws IllegalArgumentException {@code annotation} 或 {@code name} 为 {@code null}。
     */
    public static AnnotationProperty create(Class<? extends Annotation> annotation, String name) {
        return new DefaultAnnotationProperty(annotation, name);
    }
}
