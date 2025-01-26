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
 * {@link LongInfo} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-09
 */
@DisplayName("测试 LongInfo 类以及相关类")
class LongInfoTest {
    private static final Long LONG_TEST_VALUE = 11L;

    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = LongInfo.TAG;

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 LongInfo 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            LongInfo longInfo = ClassInfoTest.getConstant(LongInfoTest.this.url, LongInfoTest.this.tag);
            assertThat(longInfo.equals(longInfo)).isTrue();
        }

        @Test
        @DisplayName("提供 LongInfo 类 equals 方法与相同类型的新对象比较时，返回 false")
        void givenSameTypeShouldReturnFalse() {
            LongInfo longInfo = ClassInfoTest.getConstant(LongInfoTest.this.url, LongInfoTest.this.tag);
            LongInfo newLongInfo = ClassInfoTest.getConstant(LongInfoTest.this.url, LongInfoTest.this.tag);
            assertThat(longInfo.equals(newLongInfo)).isFalse();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 LongInfo 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            LongInfo longInfo = ClassInfoTest.getConstant(LongInfoTest.this.url, LongInfoTest.this.tag);
            ClassInfo classInfo = ClassInfoTest.getConstant(LongInfoTest.this.url, ClassInfo.TAG);
            assertThat(longInfo.equals(classInfo)).isFalse();
        }
    }

    @Test
    @DisplayName("提供 LongInfo 类 hasCode 方法与相同类型的新对象比较时，返回正常结果")
    void givenLongInfoShouldReturnHasCode() {
        LongInfo longInfo = ClassInfoTest.getConstant(this.url, this.tag);
        LongInfo newLongInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(longInfo.hashCode()).isNotEqualTo(newLongInfo.hashCode());
    }

    @Test
    @DisplayName("提供 LongInfo 类 toString 方法与相同值的新对象比较时，返回正常结果")
    void givenLongInfoShouldReturnStringValue() {
        LongInfo longInfo = ClassInfoTest.getConstant(this.url, this.tag);
        LongInfo newLongInfo = ClassInfoTest.getConstant(this.url, this.tag);
        assertThat(longInfo.toString()).isEqualTo(newLongInfo.toString());
    }

    @Test
    @DisplayName("提供 LongInfo 类 write 方法写入时，返回正常结果")
    void givenLongInfoWhenWriteThenLongValue() throws IOException {
        LongInfo longInfo = ClassInfoTest.getConstant(this.url, this.tag);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            longInfo.write(out);
            byte[] outBytes = out.toByteArray();
            byte[] bytes = Arrays.copyOfRange(outBytes, 1, outBytes.length);
            Long toLong = Convert.toLong(bytes);
            assertThat(toLong).isEqualTo(LONG_TEST_VALUE);
        }
    }

    @Test
    @DisplayName("提供 LongInfo 类 data 方法时，返回正常结果")
    void givenLongInfoShouldReturnData() {
        LongInfo longInfo = ClassInfoTest.getConstant(this.url, this.tag);
        Long value = longInfo.data().longValue();
        assertThat(value).isEqualTo(LONG_TEST_VALUE);
    }
}
