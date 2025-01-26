/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;

import java.util.List;
import java.util.Objects;

/**
 * 为 {@link BeanInjector} 提供组合。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public class BeanInjectorComposite implements BeanInjector {
    private final List<BeanInjector> injectors;

    /**
     * 使用待组合的注入程序初始化 {@link BeanInjectorComposite} 类的新实例。
     *
     * @param injectors 表示待组合的注入程序的列表的 {@link List}{@code <}{@link BeanInjector}{@code >}。
     * @throws IllegalArgumentException {@code injectors} 为 {@code null} 或空列表，或包含为 {@code null} 的元素。
     */
    public BeanInjectorComposite(List<BeanInjector> injectors) {
        Validation.notNull(injectors, "The injectors to combine cannot be null.");
        if (injectors.isEmpty()) {
            throw new IllegalArgumentException("No injectors to combine.");
        } else if (injectors.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Any injector in injectors to combine cannot be null.");
        } else {
            this.injectors = injectors;
        }
    }

    @Override
    public void inject(Object bean) {
        for (BeanInjector injector : this.injectors) {
            injector.inject(bean);
        }
    }
}
