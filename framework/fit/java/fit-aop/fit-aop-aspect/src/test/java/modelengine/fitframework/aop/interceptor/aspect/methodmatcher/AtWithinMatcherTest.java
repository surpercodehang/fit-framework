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
import modelengine.fitframework.aop.interceptor.aspect.parser.support.AtWithinParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.DefaultPointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.test.TestAnnotation;
import modelengine.fitframework.aop.interceptor.aspect.test.TestExecutionAspect;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService1;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService2;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Method;

/**
 * {@link AtWithinParser} 单元测试。
 *
 * @author 白鹏坤
 * @since 2023-03-29
 */
@DisplayName("测试 @within 表达式")
public class AtWithinMatcherTest {
    private Method hasAnnotationMethod;
    private Method noAnnotationMethod;
    private Method m1Method;
    private Method m2Method;
    private Method selfMethod;

    @BeforeEach
    void setup() throws NoSuchMethodException {
        this.hasAnnotationMethod = TestService1.class.getDeclaredMethod("m1");
        this.noAnnotationMethod = TestService2.class.getDeclaredMethod("m1");
        this.m1Method = TestService3.class.getDeclaredMethod("m1");
        this.m2Method = TestService3.class.getMethod("m2", String.class);
        this.selfMethod = TestService3.class.getDeclaredMethod("selfMethod");
    }

    @AfterEach
    void teardown() {
        this.hasAnnotationMethod = null;
        this.noAnnotationMethod = null;
        this.m1Method = null;
        this.m2Method = null;
        this.selfMethod = null;
    }

    @DisplayName("范围匹配：当类有表达式中注解，或子类直接继承并没有重写的方法，匹配器匹配成功")
    @ParameterizedTest
    @CsvSource({"has_annotation", "m3_extends"})
    void givenExpressionContainsAnnotationThenMethodMatchesSuccessfully(String methodName) {
        String exp = "@within(modelengine.fitframework.aop.interceptor.aspect.test.TestAnnotation)";
        AspectMethodMatcher methodMatcher =
                new AspectMethodMatcher(exp, TestExecutionAspect.class, new PointcutParameter[0]);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService3.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.getMethod(methodName));
        assertThat(isMatch.matches()).isTrue();
    }

    @DisplayName("范围匹配：当类没有表达式中注解，或子类继承重写的方法和子类自己的方法，匹配器匹配失败")
    @ParameterizedTest
    @CsvSource({"no_annotation", "m3_override", "m3_selfMethod"})
    void givenExpressionContainsAnnotationThenMethodMatchesFailed(String methodName) {
        String exp = "@within(modelengine.fitframework.aop.interceptor.aspect.test.TestAnnotation)";
        AspectMethodMatcher methodMatcher =
                new AspectMethodMatcher(exp, TestExecutionAspect.class, new PointcutParameter[0]);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService3.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.getMethod(methodName));
        assertThat(isMatch.matches()).isFalse();
    }

    @DisplayName("数据绑定：当类有表达式中注解，或子类直接继承并没有重写的方法，匹配器匹配成功")
    @ParameterizedTest
    @CsvSource({"has_annotation", "m3_extends"})
    void givenExpressionContainsAnnotationBindingThenMethodMatchesFailed(String methodName) {
        String exp = "@within(an)";
        PointcutParameter[] parameters = new PointcutParameter[1];
        parameters[0] = new DefaultPointcutParameter("an", TestAnnotation.class);
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService3.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.getMethod(methodName));
        assertThat(isMatch.matches()).isTrue();
    }

    private Method getMethod(String methodName) {
        switch (methodName) {
            case "has_annotation":
                return this.hasAnnotationMethod;
            case "no_annotation":
                return this.noAnnotationMethod;
            case "m3_extends":
                return this.m2Method;
            case "m3_override":
                return this.m1Method;
            case "m3_selfMethod":
                return this.selfMethod;
            default:
                return null;
        }
    }
}
