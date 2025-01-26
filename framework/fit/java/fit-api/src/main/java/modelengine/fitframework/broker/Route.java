/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

/**
 * 表示泛服务的路由信息。
 *
 * @author 季聿阶
 * @since 2023-03-08
 */
public interface Route {
    /**
     * 获取默认路由的服务实现唯一标识。
     *
     * @return 表示默认路由的服务实现唯一标识的 {@link String}。
     */
    String defaultFitable();
}
