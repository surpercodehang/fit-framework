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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link MethodHandleInfo} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-10
 */
@DisplayName("测试 MethodHandleInfo 类")
class MethodHandleInfoTest {
    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = MethodHandleInfo.TAG;

    @Test
    @DisplayName("提供 MethodHandleInfo 类 referenceIndex 方法时，返回正常结果")
    void givenMethodHandleInfoShouldReturnReferenceValue() {
        MethodHandleInfo methodHandleInfo = ClassInfoTest.getConstant(this.url, this.tag);
        ConstantPool pool = methodHandleInfo.pool();
        MethodRefInfo methodRefInfo = pool.get(methodHandleInfo.referenceIndex());
        Constant constant = pool.get(methodRefInfo.nameAndTypeIndex());
        Assertions.assertThat(constant.tag()).isEqualTo(NameAndTypeInfo.TAG);
    }

    @Nested
    @DisplayName("测试方法：referenceKind")
    class TestReferenceKind {
        private ReferenceKind referenceKind;

        @BeforeEach
        @DisplayName("初始化 referenceKind 类")
        void init() {
            MethodHandleInfo methodHandleInfo = ClassInfoTest.getConstant(MethodHandleInfoTest.this.url,
                    MethodHandleInfoTest.this.tag);
            this.referenceKind = methodHandleInfo.referenceKind();
        }

        @Test
        @DisplayName("提供 referenceKind 类 value 方法时，返回正常值")
        void givenMethodHandleInfoShouldReturnValue() {
            U1 value = this.referenceKind.value();
            U1 expect = ReferenceKind.REF_INVOKE_STATIC.value();
            assertThat(value).isEqualTo(expect);
        }

        @Test
        @DisplayName("提供 referenceKind 类 description 方法时，返回正常值")
        void givenMethodHandleInfoShouldReturnDescription() {
            String description = this.referenceKind.description();
            String expect = ReferenceKind.REF_INVOKE_STATIC.description();
            assertThat(description).isEqualTo(expect);
        }
    }
}