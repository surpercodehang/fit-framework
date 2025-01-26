/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.methodmatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.aop.interceptor.MethodInvocation;
import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.aop.interceptor.aspect.interceptor.AspectMethodMatcher;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.AtTargetParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.DefaultPointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.test.TestAnnotation;
import modelengine.fitframework.aop.interceptor.aspect.test.TestExecutionAspect;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * {@link AtTargetParser} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-03-29
 */
@DisplayName("测试 @target 表达式")
public class AtTargetMatcherTest {
    private Method service1M1Method;

    @BeforeEach
    void setup() throws NoSuchMethodException {
        this.service1M1Method = TestService1.class.getDeclaredMethod("m1");
    }

    @AfterEach
    void teardown() {
        this.service1M1Method = null;
    }

    @DisplayName("当表达式包含 @target 数据绑定时，返回信息匹配成功")
    @Test
    void givenParametersBindingMatchThenReturnMatches() {
        String exp = "@target(s1)";
        Method method = AtTargetMatcherTest.this.service1M1Method;
        PointcutParameter[] parameters = {new DefaultPointcutParameter("s1", TestAnnotation.class)};
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        // 类匹配
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        // 方法匹配
        MethodMatcher.MatchResult isMatch = methodMatcher.match(method);
        assertThat(isMatch.matches()).isTrue();

        // 连接点匹配
        methodMatcher.choose(AtTargetMatcherTest.this.service1M1Method, isMatch);
        MethodInvocation proxy = mock(MethodInvocation.class);
        MethodInvocation proxied = mock(MethodInvocation.class);
        when(proxied.getMethod()).thenReturn(method);
        PointcutParameter[] parametersBindings =
                methodMatcher.matchJoinPoint(AtTargetMatcherTest.this.service1M1Method, null, proxy, proxied);
        assertThat(parametersBindings).hasSize(1);
        assertThat(parametersBindings[0].getBinding()).isInstanceOf(TestAnnotation.class);
    }

    @DisplayName("当表达式包含 @target 范围匹配时，返回信息匹配成功")
    @Test
    void givenParametersFilterMatchThenReturnMatches() {
        String exp = "@target(modelengine.fitframework.aop.interceptor.aspect.test.TestAnnotation)";
        Method method = AtTargetMatcherTest.this.service1M1Method;
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, null);
        // 类匹配
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        // 方法匹配
        MethodMatcher.MatchResult isMatch = methodMatcher.match(method);
        assertThat(isMatch.matches()).isTrue();
    }
}
