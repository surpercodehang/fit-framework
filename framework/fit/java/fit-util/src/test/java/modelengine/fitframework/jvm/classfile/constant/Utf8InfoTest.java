/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.constant;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.jvm.classfile.lang.U1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link Utf8Info} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-10
 */
@DisplayName("测试 Utf8Info 类")
class Utf8InfoTest {
    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = Utf8Info.TAG;

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 Utf8Info 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            Utf8Info utf8Info = ClassInfoTest.getConstant(Utf8InfoTest.this.url,
                    Utf8InfoTest.this.tag);
            assertThat(utf8Info.equals(utf8Info)).isTrue();
        }

        @Test
        @DisplayName("提供 Utf8Info 类 equals 方法与相同类型的新对象比较时，返回 false")
        void givenSameTypeShouldReturnFalse() {
            Utf8Info utf8Info = ClassInfoTest.getConstant(Utf8InfoTest.this.url, Utf8InfoTest.this.tag);
            Utf8Info newUtf8Info = ClassInfoTest.getConstant(Utf8InfoTest.this.url,
                    Utf8InfoTest.this.tag);
            assertThat(utf8Info.equals(newUtf8Info)).isFalse();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 Utf8Info 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            Utf8Info utf8Info = ClassInfoTest.getConstant(Utf8InfoTest.this.url,
                    Utf8InfoTest.this.tag);
            ClassInfo classInfo = ClassInfoTest.getConstant(Utf8InfoTest.this.url, ClassInfo.TAG);
            assertThat(utf8Info.equals(classInfo)).isFalse();
        }
    }

    @Test
    @DisplayName("提供 Utf8Info 类 hasCode 方法与相同类型的新对象比较时，返回正常结果")
    void givenUtf8InfoShouldReturnHasCode() {
        Utf8Info utf8Info = ClassInfoTest.getConstant(this.url, this.tag);
        Utf8Info newUtf8Info = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(utf8Info.hashCode()).isNotEqualTo(newUtf8Info.hashCode());
    }
}