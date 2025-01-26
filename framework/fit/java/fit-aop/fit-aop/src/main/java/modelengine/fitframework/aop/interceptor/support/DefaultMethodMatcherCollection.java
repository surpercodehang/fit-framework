/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.support;

import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.aop.interceptor.MethodMatcherCollection;
import modelengine.fitframework.inspection.Validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link MethodMatcherCollection} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-05-11
 */
public class DefaultMethodMatcherCollection implements MethodMatcherCollection {
    private final List<MethodMatcher> methodMatchers = new ArrayList<>();

    @Override
    public void add(MethodMatcher matcher) {
        Validation.notNull(matcher, "The method matcher to add to method matcher collection cannot be null.");
        this.methodMatchers.add(matcher);
    }

    @Override
    public List<MethodMatcher> all() {
        return Collections.unmodifiableList(this.methodMatchers);
    }
}
