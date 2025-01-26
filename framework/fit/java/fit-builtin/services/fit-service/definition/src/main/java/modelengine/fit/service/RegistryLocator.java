/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service;

import modelengine.fitframework.broker.Target;

import java.util.List;

/**
 * 表示注册中心的定位器。
 *
 * @author 季聿阶
 * @since 2022-09-12
 */
public interface RegistryLocator {
    /**
     * 获取注册中心的地址列表。
     *
     * @return 表示注册中心的地址列表的 {@link List}{@code <}{@link Target}{@code >}。
     */
    List<Target> targets();
}
