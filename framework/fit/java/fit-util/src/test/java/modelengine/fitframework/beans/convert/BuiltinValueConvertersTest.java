/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 表示 {@link BuiltinValueConverters} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-14
 */
@DisplayName("测试 BuiltinValueConverters 类")
public class BuiltinValueConvertersTest {
    @Nested
    @DisplayName("调用 toCharacter() 方法")
    class InvokeToCharacter {
        @Test
        @DisplayName("给定一个 Number 值，返回值为 char 类型")
        void givenNumberParameterThenReturnIsInstanceOfChar() {
            Number number = 48;
            Character character = BuiltinValueConverters.toCharacter(number);
            assertThat(character).isEqualTo('0');
        }

        @Test
        @DisplayName("给定一个空的 String 值，返回为 '\0'")
        void givenEmptyStringValueThenReturnZeroOfASCII() {
            String givenValue = "";
            Character character = BuiltinValueConverters.toCharacter(givenValue);
            assertThat(character).isEqualTo('\0');
        }
    }

    @Test
    @DisplayName("给定一个非大小数的 Number 值，返回值为大整数")
    void givenNumberIsNotInstanceBigDecimalThenReturnIsInstanceOfBigInteger() {
        Number number = 186;
        BigInteger bigInteger = BuiltinValueConverters.toBigInteger(number);
        assertThat(bigInteger).isEqualTo(186);
    }

    @Test
    @DisplayName("给定一个非大整数的 Number 值，返回值为大小数")
    void givenNumberIsNotInstanceBigIntegerThenReturnIsInstanceOfBigDecimal() {
        Number number = 186;
        BigDecimal bigDecimal = BuiltinValueConverters.toBigDecimal(number);
        assertThat(bigDecimal).isEqualTo(BigDecimal.valueOf(186.0));
    }

    @Disabled("当前 Converter 不支持转换部分日期")
    @Test
    @DisplayName("给定一个日期格式的 String 值，返回值为日期")
    void givenDateFormatStringValueThenReturnIsInstanceOfSimpleDateFormat() {
        String date = "2023-01-04";
        Date actual = BuiltinValueConverters.toDate(date);
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 4);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date expected = calendar.getTime();
        assertThat(actual).isEqualTo(expected);
    }
    @Test
    @DisplayName("给定一个非日期格式的 String 值，抛出异常")
    void givenNotDateFormatStringValueThenThrowException() {
        String date = "errorDateFormat";
        IllegalStateException illegalStateException =
                catchThrowableOfType(() -> BuiltinValueConverters.toDate(date), IllegalStateException.class);
        assertThat(illegalStateException).hasMessageStartingWith(
                StringUtils.format("Illegal date string. [value={0}]", date));
    }
}
