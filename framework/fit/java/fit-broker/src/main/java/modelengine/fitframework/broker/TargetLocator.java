/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import java.util.List;

/**
 * 服务的注册中心。
 *
 * @author 梁济时
 * @author 张越
 * @author 季聿阶
 * @since 2020-09-02
 */
public interface TargetLocator {
    /**
     * 根据指定的服务定义唯一标识，服务实现唯一标识来寻找具体的服务的地址列表。
     *
     * @param id 表示指定的服务实现唯一标识的 {@link UniqueFitableId}。
     * @return 表示指定服务实现的地址列表的 {@link List}{@code <}{@link Target}{@code >}。
     */
    List<Target> lookup(UniqueFitableId id);

    /**
     * 获取本地提供服务的地址列表。
     *
     * @return 表示本地提供服务的地址列表的 {@link List}{@code <}{@link Target}{@code >}。
     */
    Target local();
}
