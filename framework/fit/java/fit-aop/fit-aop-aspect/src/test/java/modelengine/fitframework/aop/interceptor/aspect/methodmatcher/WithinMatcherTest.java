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
import modelengine.fitframework.aop.interceptor.aspect.parser.support.WithinParser;
import modelengine.fitframework.aop.interceptor.aspect.test.TestExecutionAspect;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Method;

/**
 * {@link WithinParser} 单元测试。
 *
 * @author 白鹏坤
 * @since 2023-03-29
 */
@DisplayName("测试 within 表达式")
public class WithinMatcherTest {
    private Method service1M2Method;

    @BeforeEach
    void setup() throws NoSuchMethodException {
        this.service1M2Method = TestService1.class.getDeclaredMethod("m2", String.class);
    }

    @AfterEach
    void teardown() {
        this.service1M2Method = null;
    }

    @DisplayName("当表达式包含 within 范围匹配时，对象匹配成功")
    @ParameterizedTest
    @CsvSource({
            "within(modelengine.fitframework.aop.interceptor.aspect.test.TestService1)",
            "within(modelengine.fitframework.aop.interceptor..TestService1)",
            "within(modelengine.fitframework.aop.interceptor..*)",
            "within(modelengine.fitframework.aop.*.aspect.test.*)",
            "within(modelengine.fitframework.aop..aspect.test.*Service1)",
            "within(modelengine.fitframework.aop.interceptor.aspect.test.TestService*)"
    })
    void givenExpressionContainsWithinThenProxyMatches(String exp) {
        PointcutParameter[] parameters = new PointcutParameter[1];
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.service1M2Method);
        assertThat(isMatch.matches()).isTrue();
    }
}
