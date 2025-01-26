/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.methodmatcher;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.aop.interceptor.aspect.interceptor.AspectMethodMatcher;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.DefaultPointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.ReferenceParser;
import modelengine.fitframework.aop.interceptor.aspect.test.TestExecutionAspect;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService1;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * {@link ReferenceParser} 单元测试。
 *
 * @author 白鹏坤
 * @since 2023-03-31
 */
@DisplayName("测试 pointcut 表达式")
public class ReferenceMatcherTest {
    private Method service1M1Method;
    private Method service1M2Method;
    private Method service2M1Method;

    @BeforeEach
    void setup() throws NoSuchMethodException {
        this.service1M1Method = TestService1.class.getDeclaredMethod("m1");
        this.service1M2Method = TestService1.class.getDeclaredMethod("m2", String.class);
        this.service2M1Method = TestService2.class.getDeclaredMethod("m1");
    }

    @AfterEach
    void teardown() {
        this.service1M1Method = null;
        this.service1M2Method = null;
        this.service2M1Method = null;
    }

    @Test
    @DisplayName("当表达式包含 pointcut 与关系匹配时，对象方法匹配成功")
    void givenExpressionContainsPointcutAndThenProxyMatches() {
        String exp = "pointcut1() && pointcut2(service1,name)";
        PointcutParameter[] parameters = new PointcutParameter[2];
        parameters[0] = new DefaultPointcutParameter("service1", TestService1.class);
        parameters[1] = new DefaultPointcutParameter("name", String.class);
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.service1M2Method);
        assertThat(isMatch.matches()).isTrue();
    }

    @Test
    @DisplayName("当表达式包含 pointcut 或关系匹配时，对象方法匹配成功")
    void givenExpressionContainsPointcutOrThenProxyMatches() {
        String exp = "pointcut1() || pointcut2(service1,name)";
        PointcutParameter[] parameters = new PointcutParameter[2];
        parameters[0] = new DefaultPointcutParameter("service1", TestService1.class);
        parameters[1] = new DefaultPointcutParameter("name", String.class);
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.service1M1Method);
        assertThat(isMatch.matches()).isTrue();
    }

    @Test
    @DisplayName("当表达式包含 pointcut 非关系匹配时，对象方法匹配成功")
    void givenExpressionContainsPointcutNotThenProxyMatches() {
        String exp = "!pointcut1()";
        PointcutParameter[] parameters = new PointcutParameter[0];
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService2.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.service2M1Method);
        assertThat(isMatch.matches()).isTrue();
    }
}
