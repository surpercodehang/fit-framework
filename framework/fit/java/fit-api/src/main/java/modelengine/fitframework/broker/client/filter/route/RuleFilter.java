/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.filter.route;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.broker.client.Router;

/**
 * 使用规则路由的过滤器。
 * <p><b>使用规则路由时，请直接使用 {@link Fit} 注入该过滤器。</b></p>
 *
 * @author 季聿阶
 * @since 2021-06-17
 */
@Genericable
public interface RuleFilter extends Router.Filter {}
