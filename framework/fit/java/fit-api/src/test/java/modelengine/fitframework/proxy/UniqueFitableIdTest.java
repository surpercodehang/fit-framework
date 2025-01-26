/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.broker.UniqueFitableId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * {@link UniqueFitableId} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-23
 */
@DisplayName("测试 UniqueFitableId 类以及相关类")
class UniqueFitableIdTest {
    private final String genericableId = "9588e5fc63cc4f1fbdcf2567bce0a454";
    private final String genericableVersion = "1.0.0";
    private final String fitableId = "9588e5fc63cc4f1fbdcf2567bce0a459";
    private final String fitableVersion = "1.1.0";
    private UniqueFitableId fitable;

    @BeforeEach
    @DisplayName("创建 UniqueFitableId 类")
    void setup() {
        this.fitable = UniqueFitableId.create(this.genericableId,
                this.genericableVersion,
                this.fitableId,
                this.fitableVersion);
    }

    @Test
    @DisplayName("提供 UniqueFitableId 类 create 方法时，返回正常信息")
    void givenUniqueFitableIdWhenCreateThenReturnFitableInfo() {
        UniqueFitableId uniqueFitableId = UniqueFitableId.create(this.genericableId,
                this.genericableVersion,
                this.fitableId,
                this.fitableVersion);
        assertThat(uniqueFitableId).isEqualTo(this.fitable);
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    @DisplayName("提供 UniqueFitableId 类 equals 方法与自身比较时，返回 true")
    void givenSelfUniqueFitableIdReturnTrue() {
        assertThat(this.fitable.equals(this.fitable)).isTrue();
    }

    @Test
    @DisplayName("提供 UniqueFitableId 类 equals 方法与空比较时，返回 false")
    void givenNullUniqueFitableIdReturnFalse() {
        assertThat(Objects.equals(this.fitable, null)).isFalse();
    }

    @Test
    @DisplayName("提供 UniqueFitableId 类 hasCode 方法比较时，返回正常信息")
    void givenUniqueFitableIdHasCodeReturnHasCode() {
        int actual = this.fitable.hashCode();
        UniqueFitableId uniqueFitableId = UniqueFitableId.create(this.genericableId,
                this.genericableVersion,
                this.fitableId,
                this.fitableVersion);
        int expected = uniqueFitableId.hashCode();
        assertThat(actual).isEqualTo(expected);
    }

    @Nested
    @DisplayName("测试 compareTo 方法")
    class TestCompare {
        @Test
        @DisplayName("提供 UniqueFitableId 类 GenericableId 比较时，小于零")
        void givenGenericableIdShouldLessThanZero() {
            UniqueFitableId defaultFitable = UniqueFitableIdTest.this.fitable;
            UniqueFitableId uniqueFitableId = UniqueFitableId.create("genericableId",
                    UniqueFitableIdTest.this.genericableVersion,
                    UniqueFitableIdTest.this.fitableId,
                    UniqueFitableIdTest.this.fitableVersion);
            assertThat(defaultFitable.compareTo(uniqueFitableId)).isLessThan(0);
        }

        @Test
        @DisplayName("提供 UniqueFitableId 类 genericableVersion 比较时，小于零")
        void givenGenericableVersionShouldLessThanZero() {
            UniqueFitableId defaultFitable = UniqueFitableIdTest.this.fitable;
            UniqueFitableId uniqueFitableId = UniqueFitableId.create(UniqueFitableIdTest.this.genericableId,
                    "genericableVersion",
                    UniqueFitableIdTest.this.fitableId,
                    UniqueFitableIdTest.this.fitableVersion);
            assertThat(defaultFitable.compareTo(uniqueFitableId)).isLessThan(0);
        }

        @Test
        @DisplayName("提供 UniqueFitableId 类 fitableId 比较时，小于零")
        void givenFitableIdShouldLessThanZero() {
            UniqueFitableId defaultFitable = UniqueFitableIdTest.this.fitable;
            UniqueFitableId uniqueFitableId = UniqueFitableId.create(UniqueFitableIdTest.this.genericableId,
                    UniqueFitableIdTest.this.genericableVersion,
                    "fitableId",
                    UniqueFitableIdTest.this.fitableVersion);
            assertThat(defaultFitable.compareTo(uniqueFitableId)).isLessThan(0);
        }

        @Test
        @DisplayName("提供 UniqueFitableId 类 fitableVersion 比较时，小于零")
        void givenFitableVersionShouldLessThanZero() {
            UniqueFitableId defaultFitable = UniqueFitableIdTest.this.fitable;
            UniqueFitableId uniqueFitableId = UniqueFitableId.create(UniqueFitableIdTest.this.genericableId,
                    UniqueFitableIdTest.this.genericableVersion,
                    UniqueFitableIdTest.this.fitableId,
                    "fitableVersion");
            assertThat(defaultFitable.compareTo(uniqueFitableId)).isLessThan(0);
        }
    }
}