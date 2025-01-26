/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;

import java.util.List;
import java.util.Objects;

/**
 * 为 {@link BeanInitializer} 提供组合。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public class BeanInitializerComposite implements BeanInitializer {
    private final List<BeanInitializer> initializers;

    /**
     * 使用待组合的初始化程序初始化 {@link BeanInjectorComposite} 类的新实例。
     *
     * @param initializers 表示待组合的初始化程序的列表的 {@link List}{@code <}{@link BeanInitializer}{@code >}。
     * @throws IllegalArgumentException {@code initializers} 为 {@code null} 或空列表，或包含为 {@code null} 的元素。
     */
    public BeanInitializerComposite(List<BeanInitializer> initializers) {
        Validation.notNull(initializers, "The initializers to combine cannot be null.");
        if (initializers.isEmpty()) {
            throw new IllegalArgumentException("No initializers to combine.");
        } else if (initializers.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Any initializer in initializers to combine cannot be null.");
        } else {
            this.initializers = initializers;
        }
    }

    @Override
    public void initialize(Object bean) {
        for (BeanInitializer initializer : this.initializers) {
            initializer.initialize(bean);
        }
    }
}
