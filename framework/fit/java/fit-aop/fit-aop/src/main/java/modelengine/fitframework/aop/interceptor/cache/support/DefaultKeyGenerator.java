/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.cache.support;

import modelengine.fitframework.aop.interceptor.cache.CacheKey;
import modelengine.fitframework.aop.interceptor.cache.KeyGenerator;
import modelengine.fitframework.inspection.Nonnull;

import java.lang.reflect.Method;

/**
 * 表示 {@link KeyGenerator} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-12-13
 */
public class DefaultKeyGenerator implements KeyGenerator {
    /** 表示空的缓存键生成器实现。 */
    public static final KeyGenerator EMPTY = (target, method, params) -> CacheKey.empty();

    @Override
    public CacheKey generate(Object target, @Nonnull Method method, @Nonnull Object... params) {
        return CacheKey.combine(params);
    }
}
