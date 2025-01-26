/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.annotation.AnnotationProperty;
import modelengine.fitframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;

/**
 * 为 {@link AnnotationProperty} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-05-04
 */
public class DefaultAnnotationProperty implements AnnotationProperty {
    private final Class<? extends Annotation> annotation;
    private final String name;

    /**
     * 使用注解类型和属性名称初始化 {@link DefaultAnnotationProperty} 类的新实例。
     *
     * @param annotation 表示注解类型的 {@link Class}。
     * @param name 表示属性名称的 {@link String}。
     * @throws IllegalArgumentException {@code annotation} 或 {@code name} 为 {@code null}。
     */
    public DefaultAnnotationProperty(Class<? extends Annotation> annotation, String name) {
        this.annotation = Validation.notNull(annotation, "The annotation of a property cannot be null.");
        this.name = Validation.notNull(name, "The name of a property cannot be null.");
    }

    @Override
    public Class<? extends Annotation> annotation() {
        return this.annotation;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.annotation(), this.name()});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof AnnotationProperty) {
            AnnotationProperty another = (AnnotationProperty) obj;
            return Objects.equals(this.annotation(), another.annotation()) && Objects.equals(this.name(),
                    another.name());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return StringUtils.format("{0}.{1}()", this.annotation().getName(), this.name());
    }
}
