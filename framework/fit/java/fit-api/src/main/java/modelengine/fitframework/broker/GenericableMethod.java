/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import modelengine.fitframework.inspection.Nullable;

import java.lang.reflect.Method;

/**
 * 表示服务方法。
 *
 * @author 季聿阶
 * @since 2023-03-29
 */
public interface GenericableMethod {
    /**
     * 获取服务的方法。
     *
     * @return 表示服务方法的 {@link Method}。
     */
    @Nullable
    Method method();
}
