/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.constant;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.jvm.classfile.lang.U1;
import modelengine.fitframework.util.Convert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * {@link FloatInfo} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-09
 */
@DisplayName("测试 FloatInfo 类以及相关类")
class FloatInfoTest {
    private static final Float FLOAT_TEST_VALUE = 11.2f;

    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = FloatInfo.TAG;

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 FloatInfo 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            FloatInfo floatInfo = ClassInfoTest.getConstant(FloatInfoTest.this.url, FloatInfoTest.this.tag);
            assertThat(floatInfo.equals(floatInfo)).isTrue();
        }

        @Test
        @DisplayName("提供 FloatInfo 类 equals 方法与相同类型的新对象比较时，返回 false")
        void givenSameTypeShouldReturnFalse() {
            FloatInfo floatInfo = ClassInfoTest.getConstant(FloatInfoTest.this.url, FloatInfoTest.this.tag);
            FloatInfo newFloatInfo = ClassInfoTest.getConstant(FloatInfoTest.this.url, FloatInfoTest.this.tag);
            assertThat(floatInfo.equals(newFloatInfo)).isFalse();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 FloatInfo 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            FloatInfo floatInfo = ClassInfoTest.getConstant(FloatInfoTest.this.url, FloatInfoTest.this.tag);
            ClassInfo classInfo = ClassInfoTest.getConstant(FloatInfoTest.this.url, ClassInfo.TAG);
            assertThat(floatInfo.equals(classInfo)).isFalse();
        }
    }

    @Test
    @DisplayName("提供 FloatInfo 类 hasCode 方法与相同类型的新对象比较时，返回正常结果")
    void givenFloatInfoShouldReturnHasCode() {
        FloatInfo floatInfo = ClassInfoTest.getConstant(this.url, this.tag);
        FloatInfo newFloatInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(floatInfo.hashCode()).isNotEqualTo(newFloatInfo.hashCode());
    }

    @Test
    @DisplayName("提供 FloatInfo 类 toString 方法与相同值的新对象比较时，返回正常结果")
    void givenFloatInfoShouldReturnStringValue() {
        FloatInfo floatInfo = ClassInfoTest.getConstant(this.url, this.tag);
        FloatInfo newFloatInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(floatInfo.toString()).isEqualTo(newFloatInfo.toString());
    }

    @Test
    @DisplayName("提供 FloatInfo 类 write 方法写入时，返回正常结果")
    void givenFloatInfoWhenWriteThenReturnFloatValue() throws IOException {
        FloatInfo floatInfo = ClassInfoTest.getConstant(this.url, this.tag);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            floatInfo.write(out);
            byte[] outBytes = out.toByteArray();
            byte[] bytes = Arrays.copyOfRange(outBytes, 1, outBytes.length);
            Float toFloat = Convert.toFloat(bytes);
            assertThat(toFloat).isEqualTo(FLOAT_TEST_VALUE);
        }
    }

    @Test
    @DisplayName("提供 FloatInfo 类 data 方法时，返回正常结果")
    void givenDoubleInfoShouldReturnData() {
        FloatInfo floatInfo = ClassInfoTest.getConstant(this.url, this.tag);
        Float value = Float.intBitsToFloat(floatInfo.data().intValue());
        assertThat(value).isEqualTo(FLOAT_TEST_VALUE);
    }
}
