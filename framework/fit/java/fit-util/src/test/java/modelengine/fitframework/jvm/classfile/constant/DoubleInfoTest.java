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
 * {@link DoubleInfo} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-09
 */
@DisplayName("测试 DoubleInfo 类")
class DoubleInfoTest {
    private static final Double DOUBLE_TEST_VALUE = 12.1;

    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = DoubleInfo.TAG;

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 DoubleInfo 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            DoubleInfo doubleInfo = ClassInfoTest.getConstant(DoubleInfoTest.this.url, DoubleInfoTest.this.tag);
            assertThat(doubleInfo.equals(doubleInfo)).isTrue();
        }

        @Test
        @DisplayName("提供 DoubleInfo 类 equals 方法与相同类型的新对象比较时，返回 false")
        void givenSameTypeShouldReturnFalse() {
            DoubleInfo doubleInfo = ClassInfoTest.getConstant(DoubleInfoTest.this.url, DoubleInfoTest.this.tag);
            DoubleInfo newDoubleInfo = ClassInfoTest.getConstant(DoubleInfoTest.this.url, DoubleInfoTest.this.tag);
            assertThat(doubleInfo.equals(newDoubleInfo)).isFalse();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 DoubleInfo 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            DoubleInfo doubleInfo = ClassInfoTest.getConstant(DoubleInfoTest.this.url, DoubleInfoTest.this.tag);
            ClassInfo classInfo = ClassInfoTest.getConstant(DoubleInfoTest.this.url, ClassInfo.TAG);
            assertThat(doubleInfo.equals(classInfo)).isFalse();
        }
    }

    @Test
    @DisplayName("提供 DoubleInfo 类 hasCode 方法与相同类型的新对象比较时，返回正常结果")
    void givenDoubleInfoShouldReturnHasCode() {
        DoubleInfo doubleInfo = ClassInfoTest.getConstant(this.url, this.tag);
        DoubleInfo newDoubleInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(doubleInfo.hashCode()).isNotEqualTo(newDoubleInfo.hashCode());
    }

    @Test
    @DisplayName("提供 DoubleInfo 类 toString 方法与相同值的新对象比较时，返回正常结果")
    void givenDoubleInfoShouldReturnStringValue() {
        DoubleInfo doubleInfo = ClassInfoTest.getConstant(this.url, this.tag);
        DoubleInfo newDoubleInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(doubleInfo.toString()).isEqualTo(newDoubleInfo.toString());
    }

    @Test
    @DisplayName("提供 DoubleInfo 类 write 方法写入时，返回正常结果")
    void givenDoubleInfoWhenWriteThenReturnDoubleValue() throws IOException {
        DoubleInfo doubleInfo = ClassInfoTest.getConstant(this.url, this.tag);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            doubleInfo.write(out);
            byte[] outBytes = out.toByteArray();
            byte[] bytes = Arrays.copyOfRange(outBytes, 1, outBytes.length);
            double toDouble = Convert.toDouble(bytes);
            assertThat(toDouble).isEqualTo(DOUBLE_TEST_VALUE);
        }
    }

    @Test
    @DisplayName("提供 DoubleInfo 类 data 方法时，返回正常结果")
    void givenDoubleInfoShouldReturnData() {
        DoubleInfo doubleInfo = ClassInfoTest.getConstant(this.url, this.tag);
        double value = Double.longBitsToDouble(doubleInfo.data().longValue());
        assertThat(value).isEqualTo(DOUBLE_TEST_VALUE);
    }
}
