/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.ValueSupplier;

import java.lang.reflect.Type;

/**
 * 表示对所需注入的依赖的需求。
 *
 * @author 梁济时
 * @since 2022-12-26
 */
abstract class DependencyRequirement {
    private final BeanMetadata source;

    /**
     * 使用待注入依赖的 Bean 的元数据初始化 {@link DependencyRequirement} 类的新实例。
     *
     * @param source 表示待注入依赖的 Bean 的元数据的 {@link BeanMetadata}。
     * @throws IllegalArgumentException {@code source} 为 {@code null}。
     */
    DependencyRequirement(BeanMetadata source) {
        this.source = source;
    }

    /**
     * 获取待注入依赖的 Bean 的元数据。
     *
     * @return 表示待注入依赖的 Bean 的元数据的 {@link BeanMetadata}。
     */
    final BeanMetadata source() {
        return this.source;
    }

    /**
     * 结合所需的类型，生成用以获取依赖的方法。
     *
     * @param targetType 表示所需的依赖的类型的 {@link Type}。
     * @param annotations 表示所需的依赖所在位置的注解元数据的 {@link AnnotationMetadata}。
     * @return 表示用以获取所依赖对象的方法的 {@link ValueSupplier}。
     */
    abstract ValueSupplier withType(Type targetType, AnnotationMetadata annotations);
}
