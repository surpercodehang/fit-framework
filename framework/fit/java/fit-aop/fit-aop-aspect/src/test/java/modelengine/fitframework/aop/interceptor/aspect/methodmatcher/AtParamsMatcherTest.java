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
import modelengine.fitframework.aop.interceptor.aspect.parser.support.AtParamsParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.DefaultPointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.test.TestAnnotation;
import modelengine.fitframework.aop.interceptor.aspect.test.TestExecutionAspect;
import modelengine.fitframework.aop.interceptor.aspect.test.TestParam;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * {@link AtParamsParser} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-03-29
 */
@DisplayName("测试 @params 表达式")
public class AtParamsMatcherTest {
    private Method service1M6Method;

    @BeforeEach
    void setup() throws NoSuchMethodException {
        this.service1M6Method = TestService1.class.getDeclaredMethod("m6", TestParam.class);
    }

    @AfterEach
    void teardown() {
        this.service1M6Method = null;
    }

    @DisplayName("当表达式包含 @params 数据绑定时，返回信息匹配成功")
    @Test
    void givenParametersBindingMatchThenReturnMatches() {
        String exp = "@params(s1)";
        Method method = AtParamsMatcherTest.this.service1M6Method;
        PointcutParameter[] parameters = {new DefaultPointcutParameter("s1", TestAnnotation.class)};
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        // 类匹配
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        // 方法匹配
        MethodMatcher.MatchResult isMatch = methodMatcher.match(method);
        assertThat(isMatch.matches()).isTrue();

        // 连接点匹配
        methodMatcher.choose(AtParamsMatcherTest.this.service1M6Method, isMatch);
        MethodInvocation proxy = mock(MethodInvocation.class);
        MethodInvocation proxied = mock(MethodInvocation.class);
        TestAnnotation annotation = method.getParameterTypes()[0].getAnnotation(TestAnnotation.class);
        when(proxied.getMethod()).thenReturn(method);
        PointcutParameter[] parametersBindings = methodMatcher.matchJoinPoint(AtParamsMatcherTest.this.service1M6Method,
                new Object[] {new TestParam()},
                proxy,
                proxied);
        assertThat(parametersBindings).hasSize(1);
        assertThat(parametersBindings[0].getBinding()).isEqualTo(annotation);
    }

    @DisplayName("当表达式包含 @params 范围匹配时，返回信息匹配成功")
    @Test
    void givenParametersFilterMatchThenReturnMatches() {
        String exp = "@args(modelengine.fitframework.aop.interceptor.aspect.test.TestAnnotation)";
        Method method = AtParamsMatcherTest.this.service1M6Method;
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, null);
        // 类匹配
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        // 方法匹配
        MethodMatcher.MatchResult isMatch = methodMatcher.match(method);
        assertThat(isMatch.matches()).isTrue();
    }
}
