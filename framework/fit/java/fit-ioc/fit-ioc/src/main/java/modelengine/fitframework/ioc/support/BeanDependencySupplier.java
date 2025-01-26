/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import modelengine.fitframework.ioc.BeanDependency;
import modelengine.fitframework.ioc.DependencyNotFoundException;
import modelengine.fitframework.ioc.DependencyResolvingResult;
import modelengine.fitframework.ioc.lifecycle.bean.ValueSupplier;
import modelengine.fitframework.util.StringUtils;

/**
 * 为 {@link ValueSupplier} 提供基于 {@link BeanDependency} 的实现。
 *
 * @author 梁济时
 * @since 2022-06-27
 */
class BeanDependencySupplier implements ValueSupplier {
    private final BeanDependency dependency;

    /**
     * 使用依赖初始化 {@link BeanDependencySupplier} 类的新实例。
     *
     * @param dependency 表示依赖的 {@link BeanDependency}。
     */
    public BeanDependencySupplier(BeanDependency dependency) {
        this.dependency = dependency;
    }

    @Override
    public Object get() {
        DependencyResolvingResult result = this.dependency.resolve();
        Object value = result.resolved() ? result.get() : null;
        if (this.dependency.required() && value == null) {
            throw new DependencyNotFoundException(StringUtils.format(
                    "Dependency required but not found. [type={0}, name={1}]",
                    this.dependency.type().getTypeName(), this.dependency.name()));
        } else {
            return value;
        }
    }
}
