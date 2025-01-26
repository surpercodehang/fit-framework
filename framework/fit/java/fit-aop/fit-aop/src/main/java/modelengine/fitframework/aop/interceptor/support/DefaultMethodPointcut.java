/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.support;

import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.aop.interceptor.MethodMatcherCollection;
import modelengine.fitframework.aop.interceptor.MethodPointcut;
import modelengine.fitframework.inspection.Validation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Stream;

/**
 * {@link MethodPointcut} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-05-11
 */
public class DefaultMethodPointcut implements MethodPointcut {
    private final Set<Method> methods = new HashSet<>();
    private final MethodMatcherCollection methodMatcherCollection = new DefaultMethodMatcherCollection();

    @Override
    public Set<Method> methods() {
        return Collections.unmodifiableSet(this.methods);
    }

    @Override
    public MethodMatcherCollection matchers() {
        return this.methodMatcherCollection;
    }

    @Override
    public boolean add(Class<?> clazz) {
        for (MethodMatcher matcher : this.methodMatcherCollection.all()) {
            if (!matcher.couldMatch(clazz)) {
                // 所有的方法匹配器中，只要有 1 个不能匹配，整体就不能匹配。
                return false;
            }
        }
        boolean matches = false;
        List<Method> allMethods = this.getMethods(clazz);
        for (Method method : allMethods) {
            if (this.add(method)) {
                matches = true;
            }
        }
        return matches;
    }

    private List<Method> getMethods(Class<?> clazz) {
        List<Method> allMethods = new ArrayList<>();
        Stack<Class<?>> stack = new Stack<>();
        stack.push(clazz);
        while (!stack.empty()) {
            Class<?> current = stack.pop();
            if (current == Object.class) {
                continue;
            }
            allMethods.addAll(Arrays.asList(current.getDeclaredMethods()));
            if (current.getSuperclass() != null) {
                stack.push(current.getSuperclass());
            }
            Stream.of(current.getInterfaces()).forEach(stack::push);
        }
        return allMethods;
    }

    private boolean add(Method method) {
        List<MethodMatcher.MatchResult> matchResults = new ArrayList<>();
        for (MethodMatcher matcher : this.methodMatcherCollection.all()) {
            MethodMatcher.MatchResult matchResult = matcher.match(method);
            if (matchResult.matches()) {
                matchResults.add(matchResult);
            } else {
                // 对于指定方法，所有的方法匹配器只要有 1 个匹配失败，添加方法就会失败。
                return false;
            }
        }
        this.validateMethod(method);
        for (int i = 0; i < this.methodMatcherCollection.all().size(); i++) {
            MethodMatcher matcher = this.methodMatcherCollection.all().get(i);
            matcher.choose(method, matchResults.get(i));
        }
        this.methods.add(method);
        return true;
    }

    private void validateMethod(Method method) {
        Validation.isFalse(Modifier.isFinal(method.getModifiers()),
                "The target method for AOP cannot be 'final'. [method={0}]",
                method.getName());
        Validation.isFalse(Modifier.isPrivate(method.getModifiers()),
                "The target method for AOP cannot be 'private'. [method={0}]",
                method.getName());
    }
}
