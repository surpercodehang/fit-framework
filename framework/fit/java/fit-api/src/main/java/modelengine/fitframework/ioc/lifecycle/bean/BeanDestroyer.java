/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean;

/**
 * 为Bean提供销毁程序。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
@FunctionalInterface
public interface BeanDestroyer {
    /**
     * 销毁指定Bean。
     *
     * @param bean 表示待销毁的Bean的 {@link Object}。
     */
    void destroy(Object bean);
}
