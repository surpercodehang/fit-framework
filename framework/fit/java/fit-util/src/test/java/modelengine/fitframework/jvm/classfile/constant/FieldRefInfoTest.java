/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.constant;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.jvm.classfile.ConstantPool;
import modelengine.fitframework.jvm.classfile.lang.U1;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link FieldRefInfo} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-09
 */
@DisplayName("测试 FieldRefInfo 类以及相关类")
class FieldRefInfoTest {
    private static final String TEST_CLASS_PATH = "modelengine/fitframework/jvm/test/AttributeTarget";

    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = FieldRefInfo.TAG;

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 FieldRefInfo 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            FieldRefInfo fieldRefInfo = ClassInfoTest.getConstant(FieldRefInfoTest.this.url, FieldRefInfoTest.this.tag);
            assertThat(fieldRefInfo.equals(fieldRefInfo)).isTrue();
        }

        @Test
        @DisplayName("提供 FieldRefInfo 类 equals 方法与相同类型的新对象比较时，返回 false")
        void givenSameTypeShouldReturnFalse() throws IOException {
            FieldRefInfo fieldRefInfo = ClassInfoTest.getConstant(FieldRefInfoTest.this.url, FieldRefInfoTest.this.tag);
            ConstantPool pool = fieldRefInfo.pool();
            try (InputStream in = ClassInfoTest.class.getClassLoader().getResourceAsStream(FieldRefInfoTest.this.url)) {
                FieldRefInfo refInfo = new FieldRefInfo(pool, in);
                assertThat(fieldRefInfo.equals(refInfo)).isFalse();
            }
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 FieldRefInfo 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            FieldRefInfo fieldRefInfo = ClassInfoTest.getConstant(FieldRefInfoTest.this.url, FieldRefInfoTest.this.tag);
            ClassInfo otherClassInfo = ClassInfoTest.getConstant(FieldRefInfoTest.this.url, ClassInfo.TAG);
            assertThat(fieldRefInfo.equals(otherClassInfo)).isFalse();
        }
    }

    @Test
    @DisplayName("提供 FieldRefInfo 类 hasCode 方法与相同类型的新对象比较时，返回正常结果")
    void givenFieldRefInfoThenReturnHasCode() {
        FieldRefInfo fieldRefInfo = ClassInfoTest.getConstant(this.url, this.tag);
        FieldRefInfo newFieldRefInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(fieldRefInfo.hashCode()).isNotEqualTo(newFieldRefInfo.hashCode());
    }

    @Test
    @DisplayName("提供 FieldRefInfo 类 classIndex 方法时，返回类路径")
    void givenFieldRefInfoShouldReturnClassPath() {
        FieldRefInfo fieldRefInfo = ClassInfoTest.getConstant(this.url, this.tag);
        ClassInfo classInfo = ObjectUtils.cast(fieldRefInfo.pool().get(fieldRefInfo.classIndex()));
        String path = fieldRefInfo.pool().get(classInfo.nameIndex()).toString();
        assertThat(path).isEqualTo(TEST_CLASS_PATH);
    }

    @Test
    @DisplayName("提供 FieldRefInfo 类 nameAndTypeIndex 方法时，返回类命名")
    void givenFieldRefInfoShouldReturnName() {
        FieldRefInfo fieldRefInfo = ClassInfoTest.getConstant(this.url, this.tag);
        NameAndTypeInfo nameAndTypeInfo = ObjectUtils.cast(fieldRefInfo.pool().get(fieldRefInfo.nameAndTypeIndex()));
        String value = fieldRefInfo.pool().get(nameAndTypeInfo.nameIndex()).toString();
        assertThat(value).isEqualTo("byteTest");
    }
}
