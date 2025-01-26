/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean;

/**
 * 为Bean提供注入程序。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
@FunctionalInterface
public interface BeanInjector {
    /**
     * 为指定Bean注入依赖。
     *
     * @param bean 表示待注入依赖的Bean实例的 {@link Object}。
     */
    void inject(Object bean);
}
