/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.constant;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.jvm.classfile.Constant;
import modelengine.fitframework.jvm.classfile.lang.U1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link StringInfo} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-10
 */
@DisplayName("测试 StringInfo 类")
class StringInfoTest {
    private static final String STRING_TEST_VALUE = "aaaa";

    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = StringInfo.TAG;

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 StringInfo 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            StringInfo stringInfo = ClassInfoTest.getConstant(StringInfoTest.this.url, StringInfoTest.this.tag);
            assertThat(stringInfo.equals(stringInfo)).isTrue();
        }

        @Test
        @DisplayName("提供 StringInfo 类 equals 方法与相同类型的新对象比较时，返回 false")
        void givenSameTypeShouldReturnFalse() {
            StringInfo stringInfo = ClassInfoTest.getConstant(StringInfoTest.this.url, StringInfoTest.this.tag);
            StringInfo newStringInfo = ClassInfoTest.getConstant(StringInfoTest.this.url, StringInfoTest.this.tag);
            assertThat(stringInfo.equals(newStringInfo)).isFalse();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 StringInfo 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            StringInfo stringInfo = ClassInfoTest.getConstant(StringInfoTest.this.url, StringInfoTest.this.tag);
            ClassInfo classInfo = ClassInfoTest.getConstant(StringInfoTest.this.url, ClassInfo.TAG);
            assertThat(stringInfo.equals(classInfo)).isFalse();
        }
    }

    @Test
    @DisplayName("提供 StringInfo 类 hasCode 方法与相同类型的新对象比较时，返回正常结果")
    void givenStringInfoShouldReturnHasCode() {
        StringInfo stringInfo = ClassInfoTest.getConstant(this.url, this.tag);
        StringInfo newStringInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(stringInfo.hashCode()).isNotEqualTo(newStringInfo.hashCode());
    }

    @Test
    @DisplayName("提供 StringInfo 类 stringIndex 方法时，返回正常结果")
    void givenStringInfoShouldReturnStringValue() {
        StringInfo stringInfo = ClassInfoTest.getConstant(this.url, this.tag);
        Constant constant = stringInfo.pool().get(stringInfo.stringIndex());
        assertThat(constant.toString()).isEqualTo(STRING_TEST_VALUE);
    }
}
