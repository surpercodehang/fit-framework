/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import modelengine.fitframework.ioc.BeanDependency;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * 为 {@link BeanDependency} 提供默认实现。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-05-10
 */
public class DefaultBeanDependency implements BeanDependency {
    private final BeanMetadata source;
    private final String name;
    private final Type type;
    private final boolean required;
    private final AnnotationMetadata annotations;

    /**
     * 使用所依赖的 Bean 的名称和类型，以及指示是否为必须的值初始化 {@link DefaultBeanDependency} 类的新实例。
     *
     * @param source 表示源 Bean 的元数据的 {@link BeanMetadata}。
     * @param name 表示 Bean 的名称的 {@link String}。
     * @param type 表示 Bean 的类型的 {@link Type}。
     * @param annotations 表示所依赖的 Bean 所在位置的注解信息的 {@link AnnotationMetadata}。
     * @param required 若为 {@code true}，则所依赖的Bean是必须的；否则是可选的。
     */
    public DefaultBeanDependency(BeanMetadata source, String name, Type type, boolean required,
            AnnotationMetadata annotations) {
        this.source = source;
        this.name = name;
        this.type = type;
        this.required = required;
        this.annotations = annotations;
    }

    @Override
    public BeanMetadata source() {
        return this.source;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public boolean required() {
        return this.required;
    }

    @Override
    public AnnotationMetadata annotations() {
        return this.annotations;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.source(), this.name(), this.type(), this.required()});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof BeanDependency) {
            BeanDependency another = (BeanDependency) obj;
            return Objects.equals(this.source(), another.source()) && Objects.equals(this.name(), another.name())
                    && Objects.equals(this.type(), another.type()) && Objects.equals(this.required(),
                    another.required());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return StringUtils.format("[source={0}, name={1}, type={2}, required={3}]",
                this.source(),
                this.name(),
                this.type(),
                this.required());
    }
}
