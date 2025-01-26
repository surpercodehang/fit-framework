/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link Interval} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class IntervalTest {
    @Nested
    @DisplayName("Test method: create(T minimum, T maximum, boolean minimumAllowed, boolean maximumAllowed)")
    class TestCreateWithAllowed {
        @Test
        @DisplayName("Given allowed min 0 and allowed max 1 then return correct interval")
        void givenAllowedMin0AndAllowedMax1ThenReturnCorrectInterval() {
            Integer minimum = 0;
            Integer maximum = 1;
            Interval<Integer> interval = Interval.create(minimum, maximum, true, true);
            assertThat(interval).isNotNull();
            assertThat(interval.getMinimum()).isEqualTo(minimum);
            assertThat(interval.getMaximum()).isEqualTo(maximum);
            assertThat(interval.isMinimumAllowed()).isTrue();
            assertThat(interval.isMaximumAllowed()).isTrue();
        }

        @Test
        @DisplayName("Given allowed min null and allowed max null then return correct interval")
        void givenAllowedMinNullAndAllowedMaxNullThenReturnCorrectInterval() {
            Interval<Integer> interval = Interval.create(null, null, true, true);
            assertThat(interval).isNotNull();
            assertThat(interval.contains(null)).isTrue();
        }
    }

    @Nested
    @DisplayName("Test method: create(T minimum, T maximum)")
    class TestCreateWithoutAllowed {
        @Test
        @DisplayName("Given min 0 and max 1 then return correct interval")
        void givenMin0AndMax1ThenReturnCorrectInterval() {
            Integer minimum = 0;
            Integer maximum = 1;
            Interval<Integer> interval = Interval.create(minimum, maximum);
            assertThat(interval).isNotNull();
            assertThat(interval.getMinimum()).isEqualTo(minimum);
            assertThat(interval.getMaximum()).isEqualTo(maximum);
            assertThat(interval.isMinimumAllowed()).isTrue();
            assertThat(interval.isMaximumAllowed()).isTrue();
        }

        @Test
        @DisplayName("Given min 0 and max 1 then 0 is in the interval")
        void givenMin0AndMax1Then0IsInTheInterval() {
            Interval<Integer> interval = Interval.create(0, 1);
            assertThat(interval.contains(0)).isTrue();
        }

        @Test
        @DisplayName("Given min 0 and max 1 then 1 is in the interval")
        void givenMin0AndMax1Then1IsInTheInterval() {
            Interval<Integer> interval = Interval.create(0, 1);
            assertThat(interval.contains(1)).isTrue();
        }
    }
}
