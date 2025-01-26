/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.cache.support;

import static modelengine.fitframework.inspection.Validation.greaterThanOrEquals;
import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.lessThan;
import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.aop.interceptor.cache.CacheKey;
import modelengine.fitframework.aop.interceptor.cache.KeyGenerator;
import modelengine.fitframework.inspection.Nonnull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * 表示 {@link KeyGenerator} 的指定参数的缓存键生成器实现。
 *
 * @author 季聿阶
 * @since 2022-12-13
 */
public class SpecifiedParamKeyGenerator implements KeyGenerator {
    /** 表示指定参数名字的固定前缀的 {@link String}。 */
    public static final String KEY_PREFIX = "#";

    private final int index;

    /**
     * 创建指定参数的缓存键生成器。
     *
     * @param keyPattern 表示方法参数名字的样式的 {@link String}。
     * @param method 表示指定方法的 {@link Method}。
     */
    public SpecifiedParamKeyGenerator(String keyPattern, Method method) {
        notBlank(keyPattern, "The key pattern cannot be blank.");
        isTrue(keyPattern.startsWith(KEY_PREFIX),
                "The key pattern must be started with '{0}'. [keyPattern={1}]",
                KEY_PREFIX,
                keyPattern);
        String paramName = keyPattern.substring(1);
        int actualIndex = -1;
        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = method.getParameters()[i];
            if (Objects.equals(paramName, parameter.getName())) {
                actualIndex = i;
                break;
            }
        }
        this.index = greaterThanOrEquals(actualIndex, 0, "No param name matched. [key={0}]", keyPattern);
    }

    public SpecifiedParamKeyGenerator(int index) {
        this.index = greaterThanOrEquals(index, 0, "The param index must be positive. [index={0}]", index);
    }

    @Override
    public CacheKey generate(Object target, @Nonnull Method method, @Nonnull Object... params) {
        lessThan(this.index,
                params.length,
                "The param index is out of range. [index={0}, length={1}]",
                this.index,
                params.length);
        return CacheKey.combine(params[this.index]);
    }
}
