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
 * {@link NameAndTypeInfo} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-10
 */
@DisplayName("测试 NameAndTypeInfo 类")
class NameAndTypeInfoTest {
    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = NameAndTypeInfo.TAG;

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 NameAndTypeInfo 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            NameAndTypeInfo nameAndTypeInfo = ClassInfoTest.getConstant(NameAndTypeInfoTest.this.url,
                    NameAndTypeInfoTest.this.tag);
            assertThat(nameAndTypeInfo.equals(nameAndTypeInfo)).isTrue();
        }

        @Test
        @DisplayName("提供 NameAndTypeInfo 类 equals 方法与相同类型的新对象比较时，返回 false")
        void givenSameTypeShouldReturnFalse() {
            NameAndTypeInfo nameAndTypeInfo = ClassInfoTest.getConstant(NameAndTypeInfoTest.this.url,
                    NameAndTypeInfoTest.this.tag);
            NameAndTypeInfo newNameAndTypeInfo = ClassInfoTest.getConstant(NameAndTypeInfoTest.this.url,
                    NameAndTypeInfoTest.this.tag);
            assertThat(nameAndTypeInfo.equals(newNameAndTypeInfo)).isFalse();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 NameAndTypeInfo 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            NameAndTypeInfo nameAndTypeInfo = ClassInfoTest.getConstant(NameAndTypeInfoTest.this.url,
                    NameAndTypeInfoTest.this.tag);
            ClassInfo classInfo = ClassInfoTest.getConstant(NameAndTypeInfoTest.this.url, ClassInfo.TAG);
            assertThat(nameAndTypeInfo.equals(classInfo)).isFalse();
        }
    }

    @Test
    @DisplayName("提供 NameAndTypeInfo 类 hasCode 方法与相同类型的新对象比较时，返回正常结果")
    void givenNameAndTypeInfoShouldReturnHasCode() {
        NameAndTypeInfo nameAndTypeInfo = ClassInfoTest.getConstant(this.url, this.tag);
        NameAndTypeInfo newNameAndTypeInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(nameAndTypeInfo.hashCode()).isNotEqualTo(newNameAndTypeInfo.hashCode());
    }
}