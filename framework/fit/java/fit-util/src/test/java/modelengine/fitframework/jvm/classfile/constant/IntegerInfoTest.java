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
 * {@link IntegerInfo} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-09
 */
@DisplayName("测试 IntegerInfo 类以及相关类")
class IntegerInfoTest {
    private static final Integer INTEGER_TEST_VALUE = 10;

    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = IntegerInfo.TAG;

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 IntegerInfo 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            IntegerInfo integerInfo = ClassInfoTest.getConstant(IntegerInfoTest.this.url, IntegerInfoTest.this.tag);
            assertThat(integerInfo.equals(integerInfo)).isTrue();
        }

        @Test
        @DisplayName("提供 IntegerInfo 类 equals 方法与相同类型的新对象比较时，返回 false")
        void givenSameTypeShouldReturnFalse() {
            IntegerInfo integerInfo = ClassInfoTest.getConstant(IntegerInfoTest.this.url, IntegerInfoTest.this.tag);
            IntegerInfo newIntegerInfo = ClassInfoTest.getConstant(IntegerInfoTest.this.url, IntegerInfoTest.this.tag);
            assertThat(integerInfo.equals(newIntegerInfo)).isFalse();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 IntegerInfo 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            IntegerInfo integerInfo = ClassInfoTest.getConstant(IntegerInfoTest.this.url, IntegerInfoTest.this.tag);
            ClassInfo classInfo = ClassInfoTest.getConstant(IntegerInfoTest.this.url, ClassInfo.TAG);
            assertThat(integerInfo.equals(classInfo)).isFalse();
        }
    }

    @Test
    @DisplayName("提供 IntegerInfo 类 hasCode 方法与相同类型的新对象比较时，返回正常结果")
    void givenIntegerInfoShouldReturnHasCode() {
        IntegerInfo integerInfo = ClassInfoTest.getConstant(this.url, this.tag);
        IntegerInfo newIntegerInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(integerInfo.hashCode()).isNotEqualTo(newIntegerInfo.hashCode());
    }

    @Test
    @DisplayName("提供 IntegerInfo 类 toString 方法与相同值的新对象比较时，返回正常结果")
    void givenIntegerInfoShouldReturnStringValue() {
        IntegerInfo integerInfo = ClassInfoTest.getConstant(this.url, this.tag);
        IntegerInfo newIntegerInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(integerInfo.toString()).isEqualTo(newIntegerInfo.toString());
    }

    @Test
    @DisplayName("提供 IntegerInfo 类 write 方法写入时，返回正常结果")
    void givenIntegerInfoWhenWriteThenReturnIntegerValue() throws IOException {
        IntegerInfo integerInfo = ClassInfoTest.getConstant(this.url, this.tag);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            integerInfo.write(out);
            byte[] outBytes = out.toByteArray();
            byte[] bytes = Arrays.copyOfRange(outBytes, 1, outBytes.length);
            int toInteger = Convert.toInteger(bytes);
            assertThat(toInteger).isEqualTo(INTEGER_TEST_VALUE);
        }
    }

    @Test
    @DisplayName("提供 IntegerInfo 类 data 方法时，返回正常结果")
    void givenIntegerInfoShouldReturnData() {
        IntegerInfo integerInfo = ClassInfoTest.getConstant(this.url, this.tag);
        int value = integerInfo.data().intValue();
        assertThat(value).isEqualTo(INTEGER_TEST_VALUE);
    }
}
