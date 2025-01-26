/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean;

/**
 * 为Bean提供装饰程序。
 *
 * @author 梁济时
 * @since 2022-08-05
 */
@FunctionalInterface
public interface BeanDecorator {
    /**
     * 装饰指定Bean实例。
     *
     * @param bean 表示被装饰的Bean的 {@link Object}。
     * @return 表示装饰后的Bean的 {@link Object}。
     */
    Object decorate(Object bean);
}
