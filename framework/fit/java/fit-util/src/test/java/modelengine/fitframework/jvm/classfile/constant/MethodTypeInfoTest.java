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
 * {@link MethodTypeInfo} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-10
 */
@DisplayName("测试 MethodTypeInfo 类")
class MethodTypeInfoTest {
    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = MethodTypeInfo.TAG;

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 MethodTypeInfo 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            MethodTypeInfo methodTypeInfo = ClassInfoTest.getConstant(MethodTypeInfoTest.this.url,
                    MethodTypeInfoTest.this.tag);
            assertThat(methodTypeInfo.equals(methodTypeInfo)).isTrue();
        }

        @Test
        @DisplayName("提供 MethodTypeInfo 类 equals 方法与相同类型的新对象比较时，返回 false")
        void givenSameTypeShouldReturnFalse() {
            MethodTypeInfo methodTypeInfo = ClassInfoTest.getConstant(MethodTypeInfoTest.this.url,
                    MethodTypeInfoTest.this.tag);
            MethodTypeInfo newMethodTypeInfo = ClassInfoTest.getConstant(MethodTypeInfoTest.this.url,
                    MethodTypeInfoTest.this.tag);
            assertThat(methodTypeInfo.equals(newMethodTypeInfo)).isFalse();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 MethodTypeInfo 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            MethodTypeInfo methodTypeInfo = ClassInfoTest.getConstant(MethodTypeInfoTest.this.url,
                    MethodTypeInfoTest.this.tag);
            ClassInfo classInfo = ClassInfoTest.getConstant(MethodTypeInfoTest.this.url, ClassInfo.TAG);
            assertThat(methodTypeInfo.equals(classInfo)).isFalse();
        }
    }

    @Test
    @DisplayName("提供 MethodTypeInfo 类 hasCode 方法与相同类型的新对象比较时，返回正常结果")
    void givenDoubleInfoShouldReturnHasCode() {
        MethodTypeInfo methodTypeInfo = ClassInfoTest.getConstant(this.url, this.tag);
        MethodTypeInfo newMethodTypeInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(methodTypeInfo.hashCode()).isNotEqualTo(newMethodTypeInfo.hashCode());
    }

    @Test
    @DisplayName("提供 MethodTypeInfo 类 toString 方法与相同值的新对象比较时，返回正常结果")
    void givenDoubleInfoShouldReturnStringValue() {
        MethodTypeInfo methodTypeInfo = ClassInfoTest.getConstant(this.url, this.tag);
        MethodTypeInfo newMethodTypeInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(methodTypeInfo.toString()).isEqualTo(newMethodTypeInfo.toString());
    }

    @Test
    @DisplayName("提供 MethodTypeInfo 类 descriptorIndex 方法时，返回正常结果")
    void givenMethodTypeInfoShouldReturnDescriptorValue() {
        MethodTypeInfo methodTypeInfo = ClassInfoTest.getConstant(this.url, this.tag);
        Constant constant = methodTypeInfo.pool().get(methodTypeInfo.descriptorIndex());
        assertThat(constant.toString()).isEqualTo("(Ljava/lang/Object;)Z");
    }
}