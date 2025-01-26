/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.support.authorization;

import modelengine.fit.http.client.proxy.Authorization;
import modelengine.fit.http.client.proxy.AuthorizationFactory;

import java.util.Map;

/**
 * 鉴权信息管理空实现的工厂。
 *
 * @author 王攀博
 * @since 2024-12-12
 */
public class EmptyAuthorizationFactory implements AuthorizationFactory {
    /**
     * 表示鉴权的类型。
     */
    public static final String TYPE = "NoAuth";

    @Override
    public Authorization create(Map<String, Object> authorization) {
        return Authorization.createEmpty();
    }
}
