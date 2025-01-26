/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.support;

import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.inspection.Nonnull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 表示所有可访问方法的匹配器。
 *
 * @author 季聿阶
 * @since 2022-05-28
 */
public class AccessibleMethodMatcher implements MethodMatcher {
    /** 表示 {@link AccessibleMethodMatcher} 的单例。 */
    public static final MethodMatcher INSTANCE = new AccessibleMethodMatcher();

    private AccessibleMethodMatcher() {}

    @Override
    public MatchResult match(@Nonnull Method method) {
        int modifiers = method.getModifiers();
        if (Modifier.isFinal(modifiers) || Modifier.isPrivate(modifiers)) {
            return new DefaultMatchResult(false);
        }
        return new DefaultMatchResult(true);
    }
}
