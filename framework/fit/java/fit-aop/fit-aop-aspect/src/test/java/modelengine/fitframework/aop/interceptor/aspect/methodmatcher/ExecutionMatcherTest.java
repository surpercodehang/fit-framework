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
import modelengine.fitframework.aop.interceptor.aspect.parser.support.ExecutionParser;
import modelengine.fitframework.aop.interceptor.aspect.test.TestExecutionAspect;
import modelengine.fitframework.aop.interceptor.aspect.test.TestParam;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * {@link ExecutionParser} 单元测试。
 *
 * @author 白鹏坤
 * @since 2023-03-29
 */
@DisplayName("测试 execution 表达式")
public class ExecutionMatcherTest {
    private Method service1M1Method;
    private Method service1M2Method;
    private Method service1M5Method;

    @BeforeEach
    void setup() throws NoSuchMethodException {
        this.service1M1Method = TestService1.class.getDeclaredMethod("m1");
        this.service1M2Method = TestService1.class.getDeclaredMethod("m2", String.class);
        this.service1M5Method =
                TestService1.class.getDeclaredMethod("m5", TestParam.class, List.class, int[].class, Map.class);
    }

    @AfterEach
    void teardown() {
        this.service1M1Method = null;
        this.service1M2Method = null;
        this.service1M5Method = null;
    }

    @DisplayName("当表达式没有参数时，方法匹配器匹配成功")
    @ParameterizedTest
    @CsvSource({
            "execution(public String modelengine.fitframework.aop.interceptor.aspect.test.TestService1.m1())",
            "execution(* modelengine.fitframework.aop.interceptor.aspect.test.*.m1(..))",
            "execution(java.lang.String m*())", "execution(java.lang.String *1())", "execution(* m1())"
    })
    void givenExpressionContainsExecutionWithNoParamThenProxyMatches(String exp) {
        PointcutParameter[] parameters = new PointcutParameter[0];
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.service1M1Method);
        assertThat(isMatch.matches()).isTrue();
    }

    @DisplayName("当表达式有一个参数时，方法匹配器匹配成功")
    @ParameterizedTest
    @CsvSource({
            "execution(public String modelengine.fitframework.aop.interceptor.aspect.test.TestService1.m2(String))",
            "execution(* * modelengine.fitframework.aop.interceptor.aspect.test.TestService1.*(*))",
            "execution(* modelengine.fitframework.aop.interceptor.aspect.test.*.m2(..))",
            "execution(java.lang.String m*(java.lang.String))", "execution(java.lang.String *2(String))",
            "execution(* m2(..))"
    })
    void givenExpressionContainsExecutionWithOneParamThenProxyMatches(String exp) {
        PointcutParameter[] parameters = new PointcutParameter[1];
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.service1M2Method);
        assertThat(isMatch.matches()).isTrue();
    }

    @DisplayName("当表达式有多个复杂参数时，方法匹配器匹配成功")
    @Test
    void givenExpressionContainsExecutionWithMultiParamsThenProxyMatches() {
        String[] expressions = {
                "execution(public modelengine.fitframework.aop.interceptor.aspect.test.TestParam"
                        + " modelengine.fitframework.aop.interceptor.aspect.test.TestService1.m5("
                        + "modelengine.fitframework.aop.interceptor.aspect.test.TestParam, " + "java.util.List,"
                        + "int[]," + "java.util.Map))",
                "execution(* * modelengine.fitframework.aop.interceptor.aspect.test.TestService1.*(" + "*, "
                        + "java.util.List," + "int[]," + "java.util.Map))", "execution(* m5(..))"
        };
        PointcutParameter[] parameters = new PointcutParameter[4];
        for (String expression : expressions) {
            AspectMethodMatcher methodMatcher =
                    new AspectMethodMatcher(expression, TestExecutionAspect.class, parameters);
            boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
            assertThat(isCouldMatch).isTrue();
            MethodMatcher.MatchResult isMatch = methodMatcher.match(this.service1M5Method);
            assertThat(isMatch.matches()).isTrue();
        }
    }
}
