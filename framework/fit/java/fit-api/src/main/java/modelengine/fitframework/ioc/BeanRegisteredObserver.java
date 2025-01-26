/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

/**
 * 为Bean被注册提供观察者。
 *
 * @author 梁济时
 * @since 2022-06-30
 */
@FunctionalInterface
public interface BeanRegisteredObserver {
    /**
     * 当Bean被注册时被通知。
     *
     * @param metadata 表示新注册的Bean的元数据的 {@link BeanMetadata}。
     */
    void onBeanRegistered(BeanMetadata metadata);
}
