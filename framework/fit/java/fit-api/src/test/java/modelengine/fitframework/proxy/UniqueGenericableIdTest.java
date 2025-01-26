/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.broker.UniqueGenericableId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * {@link UniqueGenericableId} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-23
 */
@DisplayName("测试 UniqueGenericableId 类以及相关类")
class UniqueGenericableIdTest {
    private final String genericableId = "9588e5fc63cc4f1fbdcf2567bce0a454";
    private final String genericableVersion = "1.0.0";
    private UniqueGenericableId genericable;

    @BeforeEach
    @DisplayName("创建 UniqueGenericableId 类")
    void setup() {
        this.genericable = UniqueGenericableId.create(this.genericableId, this.genericableVersion);
    }

    @Test
    @DisplayName("提供 UniqueGenericableId 类 create 方法时，返回正常信息")
    void givenUniqueGenericableIdWhenCreateThenReturnGenericableInfo() {
        UniqueGenericableId uniqueGenericableId =
                UniqueGenericableId.create(this.genericableId, this.genericableVersion);
        assertThat(uniqueGenericableId).isEqualTo(this.genericable);
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    @DisplayName("提供 UniqueGenericableId 类 equals 方法与自身比较时，返回 true")
    void givenSelfUniqueGenericableIdReturnTrue() {
        assertThat(this.genericable.equals(this.genericable)).isTrue();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    @DisplayName("提供 UniqueGenericableId 类 equals 方法与空比较时，返回 false")
    void givenNullUniqueGenericableIdReturnFalse() {
        assertThat(Objects.equals(this.genericable, null)).isFalse();
    }

    @Test
    @DisplayName("提供 UniqueGenericableId 类 hasCode 方法比较时，返回正常信息")
    void givenUniqueGenericableIdHasCodeReturnHasCode() {
        int actual = this.genericable.hashCode();
        UniqueGenericableId uniqueGenericableId =
                UniqueGenericableId.create(this.genericableId, this.genericableVersion);
        int expected = uniqueGenericableId.hashCode();
        assertThat(actual).isEqualTo(expected);
    }

    @Nested
    @DisplayName("测试 compareTo 方法")
    class TestCompare {
        @Test
        @DisplayName("提供 UniqueGenericableId 类 GenericableId 比较时，小于零")
        void givenGenericableIdShouldLessThanZero() {
            UniqueGenericableId defaultGenericable = UniqueGenericableIdTest.this.genericable;
            UniqueGenericableId uniqueGenericableId =
                    UniqueGenericableId.create("genericableId", UniqueGenericableIdTest.this.genericableVersion);
            assertThat(defaultGenericable.compareTo(uniqueGenericableId)).isLessThan(0);
        }

        @Test
        @DisplayName("提供 UniqueGenericableId 类 genericableVersion 比较时，小于零")
        void givenGenericableVersionShouldLessThanZero() {
            UniqueGenericableId defaultGenericable = UniqueGenericableIdTest.this.genericable;
            UniqueGenericableId uniqueGenericableId =
                    UniqueGenericableId.create(UniqueGenericableIdTest.this.genericableId, "genericableVersion");
            assertThat(defaultGenericable.compareTo(uniqueGenericableId)).isLessThan(0);
        }
    }
}