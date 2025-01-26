/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean;

import modelengine.fitframework.ioc.BeanMetadata;

/**
 * 为Bean提供生命周期定义。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public interface BeanLifecycle extends BeanCreator, BeanDecorator, BeanInjector, BeanInitializer, BeanDestroyer {
    /**
     * 获取Bean的元数据。
     *
     * @return 表示Bean的元数据的 {@link BeanMetadata}。
     */
    BeanMetadata metadata();
}
