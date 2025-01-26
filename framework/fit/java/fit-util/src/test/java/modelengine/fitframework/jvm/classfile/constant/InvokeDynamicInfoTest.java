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
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link InvokeDynamicInfo} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-10
 */
@DisplayName("测试 InvokeDynamicInfo 类")
class InvokeDynamicInfoTest {
    private static final int DYNAMIC_TEST_VALUE = 0;

    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = InvokeDynamicInfo.TAG;

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 InvokeDynamicInfo 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            InvokeDynamicInfo invokeDynamicInfo =
                    ClassInfoTest.getConstant(InvokeDynamicInfoTest.this.url, InvokeDynamicInfoTest.this.tag);
            assertThat(invokeDynamicInfo.equals(invokeDynamicInfo)).isTrue();
        }

        @Test
        @DisplayName("提供 InvokeDynamicInfo 类 equals 方法与相同类型的新对象比较时，返回 false")
        void givenSameTypeShouldReturnFalse() {
            InvokeDynamicInfo invokeDynamicInfo =
                    ClassInfoTest.getConstant(InvokeDynamicInfoTest.this.url, InvokeDynamicInfoTest.this.tag);
            InvokeDynamicInfo newInvokeDynamicInfo =
                    ClassInfoTest.getConstant(InvokeDynamicInfoTest.this.url, InvokeDynamicInfoTest.this.tag);
            assertThat(invokeDynamicInfo.equals(newInvokeDynamicInfo)).isFalse();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 InvokeDynamicInfo 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            InvokeDynamicInfo invokeDynamicInfo =
                    ClassInfoTest.getConstant(InvokeDynamicInfoTest.this.url, InvokeDynamicInfoTest.this.tag);
            ClassInfo classInfo = ClassInfoTest.getConstant(InvokeDynamicInfoTest.this.url, ClassInfo.TAG);
            assertThat(invokeDynamicInfo.equals(classInfo)).isFalse();
        }
    }

    @Test
    @DisplayName("提供 InvokeDynamicInfo 类 hasCode 方法与相同类型的新对象比较时，返回正常结果")
    void givenInvokeDynamicInfoShouldReturnHasCode() {
        InvokeDynamicInfo invokeDynamicInfo = ClassInfoTest.getConstant(this.url, this.tag);
        InvokeDynamicInfo newInvokeDynamicInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(invokeDynamicInfo.hashCode()).isNotEqualTo(newInvokeDynamicInfo.hashCode());
    }

    @Test
    @DisplayName("提供 InvokeDynamicInfo 类 bootstrapMethodAttrIndex 方法时，返回正常结果")
    void givenInvokeDynamicInfoShouldReturnBootstrapValue() {
        InvokeDynamicInfo invokeDynamicInfo = ClassInfoTest.getConstant(this.url, this.tag);
        int bootstrap = invokeDynamicInfo.bootstrapMethodAttrIndex().intValue();
        assertThat(bootstrap).isEqualTo(DYNAMIC_TEST_VALUE);
    }

    @Test
    @DisplayName("提供 InvokeDynamicInfo 类 stringIndex 方法时，返回正常结果")
    void givenInvokeDynamicInfoShouldReturnStringValue1() {
        InvokeDynamicInfo invokeDynamicInfo = ClassInfoTest.getConstant(this.url, this.tag);
        ConstantPool pool = invokeDynamicInfo.pool();
        Constant index = pool.get(invokeDynamicInfo.nameAndTypeIndex());
        NameAndTypeInfo info = ObjectUtils.cast(index);
        String type = pool.get(info.descriptorIndex()).toString();
        assertThat(type).isEqualTo("()Ljava/util/function/Predicate;");
    }
}
