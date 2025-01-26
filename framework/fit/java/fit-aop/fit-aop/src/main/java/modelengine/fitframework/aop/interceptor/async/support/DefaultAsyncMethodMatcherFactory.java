/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.async.support;

import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.aop.interceptor.async.AsyncMethodMatcher;
import modelengine.fitframework.aop.interceptor.async.AsyncMethodMatcherFactory;

/**
 * 表示 {@link AsyncMethodMatcherFactory} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-11-13
 */
public class DefaultAsyncMethodMatcherFactory implements AsyncMethodMatcherFactory {
    @Override
    public MethodMatcher create(String asyncExecutorName) {
        return new AsyncMethodMatcher(asyncExecutorName);
    }
}
