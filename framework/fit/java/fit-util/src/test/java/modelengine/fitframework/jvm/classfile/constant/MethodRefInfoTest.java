/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.constant;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.jvm.classfile.Constant;
import modelengine.fitframework.jvm.classfile.ConstantPool;
import modelengine.fitframework.jvm.classfile.lang.U1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link MethodRefInfo} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-10
 */
@DisplayName("测试 MethodRefInfo 类")
class MethodRefInfoTest {
    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = MethodRefInfo.TAG;

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 MethodRefInfo 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            MethodRefInfo methodRefInfo = ClassInfoTest.getConstant(MethodRefInfoTest.this.url,
                    MethodRefInfoTest.this.tag);
            assertThat(methodRefInfo.equals(methodRefInfo)).isTrue();
        }

        @Test
        @DisplayName("提供 MethodRefInfo 类 equals 方法与相同类型的新对象比较时，返回 false")
        void givenSameTypeShouldReturnFalse() {
            MethodRefInfo methodRefInfo = ClassInfoTest.getConstant(MethodRefInfoTest.this.url,
                    MethodRefInfoTest.this.tag);
            MethodRefInfo newMethodRefInfo = ClassInfoTest.getConstant(MethodRefInfoTest.this.url,
                    MethodRefInfoTest.this.tag);
            assertThat(methodRefInfo.equals(newMethodRefInfo)).isFalse();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 MethodRefInfo 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            MethodRefInfo methodRefInfo = ClassInfoTest.getConstant(MethodRefInfoTest.this.url,
                    MethodRefInfoTest.this.tag);
            ClassInfo classInfo = ClassInfoTest.getConstant(MethodRefInfoTest.this.url, ClassInfo.TAG);
            assertThat(methodRefInfo.equals(classInfo)).isFalse();
        }
    }

    @Test
    @DisplayName("提供 MethodRefInfo 类 hasCode 方法与相同类型的新对象比较时，返回正常结果")
    void givenMethodRefInfoShouldReturnHasCode() {
        MethodRefInfo methodRefInfo = ClassInfoTest.getConstant(this.url, this.tag);
        MethodRefInfo newMethodRefInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(methodRefInfo.hashCode()).isNotEqualTo(newMethodRefInfo.hashCode());
    }

    @Test
    @DisplayName("提供 MethodRefInfo 类 classIndex 方法时，返回正常结果")
    void givenStringInfoShouldReturnClassType() {
        MethodRefInfo methodRefInfo = ClassInfoTest.getConstant(this.url, this.tag);
        ConstantPool pool = methodRefInfo.pool();
        ClassInfo classIndex = pool.get(methodRefInfo.classIndex());
        Constant constant = pool.get(classIndex.nameIndex());
        assertThat(constant.toString()).isEqualTo("java/lang/Object");
    }

    @Test
    @DisplayName("提供 MethodRefInfo 类 nameAndTypeIndex 方法时，返回正常结果")
    void givenStringInfoShouldReturnStringValue() {
        MethodRefInfo methodRefInfo = ClassInfoTest.getConstant(this.url, this.tag);
        ConstantPool pool = methodRefInfo.pool();
        NameAndTypeInfo nameAndTypeIndex = pool.get(methodRefInfo.nameAndTypeIndex());
        Constant constant = pool.get(nameAndTypeIndex.nameIndex());
        assertThat(constant.toString()).isEqualTo("<init>");
    }
}