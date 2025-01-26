/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.support.authorization;

import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fitframework.inspection.Nonnull;

/**
 * 鉴权信息管理的空实现。
 *
 * @author 王攀博
 * @since 2024-12-12
 */
public class EmptyAuthorization extends AbstractAuthorization {
    @Override
    public void assemble(RequestBuilder builder) {}

    @Override
    protected void setValue(String key, @Nonnull Object value) {}
}
