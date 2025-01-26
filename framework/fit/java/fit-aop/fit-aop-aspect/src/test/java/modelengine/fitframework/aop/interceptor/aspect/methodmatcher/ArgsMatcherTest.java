/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.methodmatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import modelengine.fitframework.aop.interceptor.MethodInvocation;
import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.aop.interceptor.aspect.interceptor.AspectMethodMatcher;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.ArgsParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.DefaultPointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.test.TestExecutionAspect;
import modelengine.fitframework.aop.interceptor.aspect.test.TestParam;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService1;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService2;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService3;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@link ArgsParser} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-03-29
 */
@DisplayName("测试 Args 表达式")
public class ArgsMatcherTest {
    private Method service1M1Method;
    private Method service1M2Method;
    private Method service1M3Method;
    private Method service1M4Method;
    private Method service1M5Method;
    private Method service2M1Method;
    private Method service3M1Method;

    @BeforeEach
    void setup() throws NoSuchMethodException {
        this.service1M1Method = TestService1.class.getDeclaredMethod("m1");
        this.service1M2Method = TestService1.class.getDeclaredMethod("m2", String.class);
        this.service1M3Method = TestService1.class.getDeclaredMethod("m3");
        this.service1M4Method = TestService1.class.getDeclaredMethod("m4", TestParam.class);
        this.service1M5Method =
                TestService1.class.getDeclaredMethod("m5", TestParam.class, List.class, int[].class, Map.class);
        this.service2M1Method = TestService2.class.getDeclaredMethod("m1");
        this.service3M1Method = TestService3.class.getDeclaredMethod("m1");
    }

    @AfterEach
    void teardown() {
        this.service1M1Method = null;
        this.service1M2Method = null;
        this.service1M3Method = null;
        this.service1M4Method = null;
        this.service2M1Method = null;
        this.service3M1Method = null;
    }

    @Nested
    @DisplayName("测试匹配方法")
    class TestMatchMethod {
        @DisplayName("当表达式包含 args 数据绑定时，返回信息匹配成功")
        @ParameterizedTest
        @CsvSource({
                "'',m1_1", "s1,m1_2", "*,m1_2", "..,m1_1", "..,m1_2", "..,m1_3", "..,m1_4", "'s1,s2,s3,s4',m1_5"
        })
        void givenParametersBindingMatchThenReturnMatches(String expressContent, String methodName) {
            String exp = String.format("args(%s)", expressContent);
            Method method = this.getMethod(methodName);
            PointcutParameter[] parameters = this.getParams(expressContent, method);
            AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
            // 类匹配
            boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
            assertThat(isCouldMatch).isTrue();
            // 方法匹配
            MethodMatcher.MatchResult isMatch = methodMatcher.match(method);
            assertThat(isMatch.matches()).isTrue();
        }

        @DisplayName("当表达式包含 args 范围匹配时，返回信息匹配成功")
        @ParameterizedTest
        @CsvSource({
                "java.lang.String,m1_2",
                "'modelengine.fitframework.aop.interceptor.aspect.test.TestParam,java.util.List,int[],java.util.Map',"
                        + "m1_5"
        })
        void givenParametersFilterMatchThenReturnMatches(String expressContent, String methodName) {
            String exp = String.format("args(%s)", expressContent);
            Method method = this.getMethod(methodName);
            AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, null);
            // 类匹配
            boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
            assertThat(isCouldMatch).isTrue();
            // 方法匹配
            MethodMatcher.MatchResult isMatch = methodMatcher.match(method);
            assertThat(isMatch.matches()).isTrue();
        }

        private PointcutParameter[] getParams(String expressContent, Method method) {
            if (StringUtils.isBlank(expressContent) || Objects.equals(expressContent, "..")) {
                return new PointcutParameter[0];
            }
            String[] params = expressContent.split(",");
            PointcutParameter[] parameters = new PointcutParameter[params.length];
            for (int i = 0; i < parameters.length; i++) {
                Class<?> parameterType = method.getParameterTypes()[i];
                parameters[i] = new DefaultPointcutParameter(params[i], parameterType);
            }
            return parameters;
        }

        private Method getMethod(String methodName) {
            switch (methodName) {
                case "m1_1":
                    return ArgsMatcherTest.this.service1M1Method;
                case "m1_2":
                    return ArgsMatcherTest.this.service1M2Method;
                case "m1_3":
                    return ArgsMatcherTest.this.service1M3Method;
                case "m1_4":
                    return ArgsMatcherTest.this.service1M4Method;
                case "m1_5":
                    return ArgsMatcherTest.this.service1M5Method;
                case "m2_1":
                    return ArgsMatcherTest.this.service2M1Method;
                default:
                    return ArgsMatcherTest.this.service3M1Method;
            }
        }
    }

    @Nested
    @DisplayName("测试匹配连接点")
    class TestMatchJoinPoint {
        @Test
        @DisplayName("当匹配方法的参数只有 1 个 String 时，参数匹配成功")
        void givenArgsHasOnly1StringThenParameterMatches() {
            String exp = "args(s)";
            PointcutParameter[] parameters = new PointcutParameter[1];
            parameters[0] = new DefaultPointcutParameter("s", String.class);
            AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
            MethodMatcher.MatchResult isMatch = methodMatcher.match(ArgsMatcherTest.this.service1M2Method);
            assertThat(isMatch.matches()).isTrue();
            methodMatcher.choose(ArgsMatcherTest.this.service1M2Method, isMatch);
            MethodInvocation proxy = mock(MethodInvocation.class);
            MethodInvocation proxied = mock(MethodInvocation.class);
            PointcutParameter[] parametersBindings = methodMatcher.matchJoinPoint(ArgsMatcherTest.this.service1M2Method,
                    new Object[] {"Hello"},
                    proxy,
                    proxied);
            assertThat(parametersBindings).hasSize(1);
            assertThat(parametersBindings[0].getBinding()).isEqualTo("Hello");
        }

        @Test
        @DisplayName("当匹配方法的参数有多个不同类型时，参数匹配成功")
        void givenArgsHasMoreTypeThenParameterMatches() {
            String exp = "args(s1,s2,s3,s4)";
            PointcutParameter[] parameters = new PointcutParameter[4];
            parameters[0] = new DefaultPointcutParameter("s1", TestParam.class);
            parameters[1] = new DefaultPointcutParameter("s2", List.class);
            parameters[2] = new DefaultPointcutParameter("s3", int[].class);
            parameters[3] = new DefaultPointcutParameter("s4", Map.class);
            AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
            MethodMatcher.MatchResult isMatch = methodMatcher.match(ArgsMatcherTest.this.service1M5Method);
            assertThat(isMatch.matches()).isTrue();
            methodMatcher.choose(ArgsMatcherTest.this.service1M5Method, isMatch);
            MethodInvocation proxy = mock(MethodInvocation.class);
            MethodInvocation proxied = mock(MethodInvocation.class);
            TestParam param1 = new TestParam();
            List param2 = Arrays.asList(parameters);
            int[] param3 = {1, 2, 3};
            Map<String, String> param4 = MapBuilder.<String, String>get().put("key", "value").build();
            Object[] param = {param1, param2, param3, param4};
            PointcutParameter[] parametersBindings =
                    methodMatcher.matchJoinPoint(ArgsMatcherTest.this.service1M5Method, param, proxy, proxied);
            assertThat(parametersBindings).hasSize(4);
            assertThat(parametersBindings[0].getBinding()).isEqualTo(param1);
            assertThat(parametersBindings[1].getBinding()).isEqualTo(param2);
            assertThat(parametersBindings[2].getBinding()).isEqualTo(param3);
            assertThat(parametersBindings[3].getBinding()).isEqualTo(param4);
        }

        @Test
        @DisplayName("当匹配方法的参数有通配符时，参数匹配成功")
        void givenArgsHasWildcardThenParameterMatches() {
            String exp = "args(*,s2,s3,*)";
            PointcutParameter[] parameters = new PointcutParameter[2];
            parameters[0] = new DefaultPointcutParameter("s2", List.class);
            parameters[1] = new DefaultPointcutParameter("s3", int[].class);
            AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
            MethodMatcher.MatchResult isMatch = methodMatcher.match(ArgsMatcherTest.this.service1M5Method);
            assertThat(isMatch.matches()).isTrue();
            methodMatcher.choose(ArgsMatcherTest.this.service1M5Method, isMatch);
            MethodInvocation proxy = mock(MethodInvocation.class);
            MethodInvocation proxied = mock(MethodInvocation.class);
            TestParam param1 = new TestParam();
            List<PointcutParameter> param2 = Arrays.asList(parameters);
            int[] param3 = {1, 2, 3};
            Map<String, String> param4 = MapBuilder.<String, String>get().put("key", "value").build();
            Object[] param = {param1, param2, param3, param4};
            PointcutParameter[] parametersBindings =
                    methodMatcher.matchJoinPoint(ArgsMatcherTest.this.service1M5Method, param, proxy, proxied);
            assertThat(parametersBindings).hasSize(2);
            assertThat(parametersBindings[0].getBinding()).isEqualTo(param2);
            assertThat(parametersBindings[1].getBinding()).isEqualTo(param3);
        }
    }
}
