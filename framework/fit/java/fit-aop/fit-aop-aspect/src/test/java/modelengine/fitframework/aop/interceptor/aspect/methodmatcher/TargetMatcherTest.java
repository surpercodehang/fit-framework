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
import modelengine.fitframework.aop.interceptor.aspect.parser.support.DefaultPointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.TargetParser;
import modelengine.fitframework.aop.interceptor.aspect.test.TestExecutionAspect;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * {@link TargetParser} 单元测试。
 *
 * @author 白鹏坤
 * @since 2023-03-29
 */
@DisplayName("测试 target 表达式")
public class TargetMatcherTest {
    private Method service1M2Method;

    @BeforeEach
    void setup() throws NoSuchMethodException {
        this.service1M2Method = TestService1.class.getDeclaredMethod("m2", String.class);
    }

    @AfterEach
    void teardown() {
        this.service1M2Method = null;
    }

    @Test
    @DisplayName("当表达式包含 target 数据绑定时，被代理对象匹配成功")
    void givenExpressionContainsTargetBindingThenProxiedMatches() {
        String exp = "target(proxied)";
        PointcutParameter[] parameters = new PointcutParameter[1];
        parameters[0] = new DefaultPointcutParameter("proxied", Object.class);
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.service1M2Method);
        assertThat(isMatch.matches()).isTrue();
        methodMatcher.choose(this.service1M2Method, isMatch);
        MethodInvocation proxy = mock(MethodInvocation.class);
        MethodInvocation proxied = mock(MethodInvocation.class);
        Object proxyTest = new TestService1();
        when(proxied.getTarget()).thenReturn(proxyTest);
        PointcutParameter[] parametersBindings =
                methodMatcher.matchJoinPoint(this.service1M2Method, new Object[] {"Hello"}, proxy, proxied);
        assertThat(parametersBindings).hasSize(1);
        assertThat(parametersBindings[0].getBinding()).isEqualTo(proxyTest);
    }

    @Test
    @DisplayName("当表达式包含 target 范围匹配时，被代理对象匹配成功")
    void givenExpressionContainsTargetThenProxyMatches() {
        String exp = "target(modelengine.fitframework.aop.interceptor.aspect.test.TestService1)";
        PointcutParameter[] parameters = new PointcutParameter[1];
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.service1M2Method);
        assertThat(isMatch.matches()).isTrue();
    }
}
