/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * {@link Fit} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-02-18
 */
@DisplayName("验证 Fit 注解")
public class FitTest {
    @Nested
    @DisplayName("当 Fit 注解未设置任何值时")
    class GivenDefaultFitAnnotation {
        @SuppressWarnings("unused")
        @Fit
        private Object obj;

        private Fit defaultFit;

        @BeforeEach
        void setup() throws NoSuchFieldException {
            Field field = GivenDefaultFitAnnotation.class.getDeclaredField("obj");
            this.defaultFit = field.getDeclaredAnnotation(Fit.class);
        }

        @AfterEach
        void teardown() {
            this.defaultFit = null;
        }

        @Test
        @DisplayName("别名默认为空字符串")
        void thenAliasIsEmpty() {
            String actual = this.defaultFit.alias();
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("路由策略默认为默认路由策略")
        void thenPolicyIsDefault() {
            String actual = this.defaultFit.policy();
            assertThat(actual).isEqualTo("default");
        }

        @Test
        @DisplayName("重试次数默认为 0")
        void thenRetryIs0() {
            int actual = this.defaultFit.retry();
            assertThat(actual).isEqualTo(0);
        }

        @Test
        @DisplayName("超时时间默认为 3000")
        void thenTimeoutIs3000() {
            int actual = this.defaultFit.timeout();
            assertThat(actual).isEqualTo(3000);
        }

        @Test
        @DisplayName("超时时间单位默认为毫秒")
        void thenTimeoutUnitIsMillisecond() {
            TimeUnit actual = this.defaultFit.timeunit();
            assertThat(actual).isEqualTo(TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 用于避免增加 @SuppressWarnings("unused") 注解。
     */
    @Nested
    @DisplayName("验证 Fit 注解提供的策略常量")
    class TestConstant {
        @Test
        @DisplayName("别名路由策略的常量为 'alias'")
        void givenPolicyAliasThenReturnAlias() {
            assertThat(Fit.POLICY_ALIAS).isEqualTo("alias");
        }

        @Test
        @DisplayName("规则路由策略的常量为 'rule'")
        void givenPolicyRuleThenReturnRule() {
            assertThat(Fit.POLICY_RULE).isEqualTo("rule");
        }
    }
}
