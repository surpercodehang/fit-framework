/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import modelengine.fitframework.util.Disposable;

/**
 * 为 Bean 提供工厂。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public interface BeanFactory extends Disposable {
    /**
     * 获取所管理的 Bean 的定义。
     *
     * @return 表示 Bean 定义的 {@link BeanMetadata}。
     */
    BeanMetadata metadata();

    /**
     * 获取工厂所属的容器。
     *
     * @return 表示所属容器的 {@link BeanContainer}。
     */
    default BeanContainer container() {
        return this.metadata().container();
    }

    /**
     * 获取 Bean 实例。
     *
     * @param arguments 表示 Bean 的初始化参数的 {@link Object}{@code []}。
     * @return 表示 Bean 实例的 {@link Object}。
     */
    <T> T get(Object... arguments);
}
