/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;

import java.util.List;
import java.util.Objects;

/**
 * 为 {@link BeanDestroyer} 提供组合。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public class BeanDestroyerComposite implements BeanDestroyer {
    private final List<BeanDestroyer> destroyers;

    /**
     * 使用待组合的Bean销毁程序初始化 {@link BeanDestroyerComposite} 类的新实例。
     *
     * @param destroyers 表示待组合的Bean销毁程序的 {@link List}{@code <}{@link BeanDestroyer}{@code >}。
     * @throws IllegalArgumentException {@code destroyers} 为 {@code null} 或空列表，或者包含为 {@code null} 的元素。
     */
    public BeanDestroyerComposite(List<BeanDestroyer> destroyers) {
        Validation.notNull(destroyers, "The destroyers to combine cannot be null.");
        if (destroyers.isEmpty()) {
            throw new IllegalArgumentException("No destroyers to combine.");
        } else if (destroyers.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Any destroyer in destroyers to combine cannot be null.");
        } else {
            this.destroyers = destroyers;
        }
    }

    @Override
    public void destroy(Object bean) {
        this.destroyers.forEach(destroyer -> destroyer.destroy(bean));
    }
}
