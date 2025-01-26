/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 为 {@link StringUtils} 提供单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 1.0
 */
public class StringUtilsTest {
    /** 表示一个泛空格符。 */
    private static final String WHITE_SPACE = " \n\t";

    @Nested
    @DisplayName("测试方法：blankIf(String value, String defaultValue)")
    class TestBlankIf {
        @Test
        @DisplayName("当提供非空字符串，返回原非空字符串")
        void givenNotBlankStringThenReturnOriginal() {
            final String blankIf = StringUtils.blankIf("notBlankString", "defaultString");
            assertThat(blankIf).isEqualTo("notBlankString");
        }

        @Test
        @DisplayName("当提供空字符串，返回给定默认值")
        void givenBlankStringThenReturnDefaultValue() {
            final String blankIf = StringUtils.blankIf("  ", "defaultString");
            assertThat(blankIf).isEqualTo("defaultString");
        }

        @Test
        @DisplayName("当提供 null 时，返回给定默认值")
        void givenNullThenReturnDefaultValue() {
            final String blankIf = StringUtils.blankIf(null, "defaultString");
            assertThat(blankIf).isEqualTo("defaultString");
        }
    }

    /**
     * 目标方法：{@link StringUtils#compare(String, String)}。
     */
    @Nested
    @DisplayName("Test method: compare(String str1, String str2)")
    class TestCompare {
        @Test
        @DisplayName("Input is (null, null), output is 0")
        void givenNullAndNullThenReturn0() {
            int actual = StringUtils.compare(null, null);
            assertThat(actual).isEqualTo(0);
        }

        @Test
        @DisplayName("Input is (null, 'Hello'), output is less than 0")
        void givenNullAndHelloThenReturnNegative() {
            int actual = StringUtils.compare(null, "Hello");
            assertThat(actual).isLessThan(0);
        }

        @Test
        @DisplayName("Input is ('Hello', null), output is greater than 0")
        void givenHelloAndNullThenReturnPositive() {
            int actual = StringUtils.compare("Hello", null);
            assertThat(actual).isGreaterThan(0);
        }

        @Test
        @DisplayName("Input is ('Hello', 'HELLO'), output is greater than 0")
        void givenFirstGreaterThanSecondThenReturnPositive() {
            int actual = StringUtils.compare("Hello", "HELLO");
            assertThat(actual).isGreaterThan(0);
        }
    }

    /**
     * 目标方法：{@link StringUtils#compareIgnoreCase(String, String)}。
     */
    @Nested
    @DisplayName("Test method: compareIgnoreCase(String str1, String str2)")
    class TestCompareIgnoreCase {
        @Test
        @DisplayName("Input is (null, null), output is 0")
        void givenNullAndNullThenReturn0() {
            int actual = StringUtils.compareIgnoreCase(null, null);
            assertThat(actual).isEqualTo(0);
        }

        @Test
        @DisplayName("Input is (null, 'Hello'), output is less than 0")
        void givenNullAndHelloThenReturnNegative() {
            int actual = StringUtils.compareIgnoreCase(null, "Hello");
            assertThat(actual).isLessThan(0);
        }

        @Test
        @DisplayName("Input is ('Hello', null), output is greater than 0")
        void givenHelloAndNullThenReturnPositive() {
            int actual = StringUtils.compareIgnoreCase("Hello", null);
            assertThat(actual).isGreaterThan(0);
        }

        @Test
        @DisplayName("Input is ('Hello', 'HELLO'), output is 0")
        void givenFirstGreaterThanSecondThenReturnPositive() {
            int actual = StringUtils.compareIgnoreCase("Hello", "HELLO");
            assertThat(actual).isEqualTo(0);
        }
    }

    /**
     * 目标方法：{@link StringUtils#concat(Function, Iterator)}。
     */
    @Nested
    @DisplayName("Test method: concat(Function<T, String> mapper, Iterator<T> iterator)")
    class TestConcatIteratorWithMapper {
        @Test
        @DisplayName("Input is null, output is '' (mapper: Integer::toHexString)")
        void givenNoIteratorThenReturnEmpty() {
            String actual = StringUtils.concat(Integer::toHexString, (Iterator<Integer>) null);
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is [], output is '' (mapper: Integer::toHexString)")
        void givenEmptyIteratorThenReturnEmpty() {
            String actual = StringUtils.concat(Integer::toHexString, ArrayUtils.iterator());
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is [null], output is 'null' (mapper: custom toUpperCase)")
        void givenIteratorWithElementNullAndCustomMapperThenReturnNullString() {
            String actual = StringUtils.concat(str -> {
                if (str == null) {
                    return "NULL";
                } else {
                    return str.toUpperCase();
                }
            }, ArrayUtils.iterator(ObjectUtils.<String>cast(null)));
            assertThat(actual).isEqualTo("NULL");
        }

        @Test
        @DisplayName("Input is [null], output is '' (mapper: null)")
        void givenIteratorWithElementNullThenReturnEmpty() {
            String actual = StringUtils.concat(null, ArrayUtils.iterator(ObjectUtils.<String>cast(null)));
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is [1, 2], output is '34' (mapper: i -> String.valueOf(i + 2))")
        void givenNormalIteratorThenReturnCorrectResult() {
            String actual = StringUtils.concat(aInt -> String.valueOf(aInt + 2), ArrayUtils.iterator(1, 2));
            assertThat(actual).isEqualTo("34");
        }
    }

    /**
     * 目标方法：{@link StringUtils#concat(Function, List)}。
     */
    @Nested
    @DisplayName("Test method: concat(Function<T, String> mapper, List<T> list)")
    class TestConcatListWithMapper {
        @Test
        @DisplayName("Input is [1, 2], output is '34' (mapper: i -> String.valueOf(i + 2))")
        void givenNormalListThenReturnCorrectResult() {
            String actual = StringUtils.concat(i -> String.valueOf(i + 2), Arrays.asList(1, 2));
            assertThat(actual).isEqualTo("34");
        }
    }

    /**
     * 目标方法：{@link StringUtils#concat(Function, Object[])}。
     */
    @Nested
    @DisplayName("Test method: concat(Function<T, String> mapper, T... array)")
    class TestConcatArrayWithMapper {
        @Test
        @DisplayName("Input is [1, 2], output is '34' (mapper: i -> String.valueOf(i + 2))")
        void givenNormalListThenReturnCorrectResult() {
            String actual = StringUtils.concat(i -> String.valueOf(i + 2), 1, 2);
            assertThat(actual).isEqualTo("34");
        }
    }

    /**
     * 目标方法：{@link StringUtils#concat(Iterator)}。
     */
    @Nested
    @DisplayName("Test method: concat(Iterator<T> iterator)")
    class TestConcatIterator {
        @Test
        @DisplayName("Input is [1, 2], output is '12'")
        void givenNormalIteratorThenReturnCorrectResult() {
            String actual = StringUtils.concat(ArrayUtils.iterator(1, 2));
            assertThat(actual).isEqualTo("12");
        }
    }

    /**
     * 目标方法：{@link StringUtils#concat(List)}。
     */
    @Nested
    @DisplayName("Test method: concat(List<T> list)")
    class TestConcatList {
        @Test
        @DisplayName("Input is [1, 2], output is '12'")
        void givenNormalListThenReturnCorrectResult() {
            String actual = StringUtils.concat(Arrays.asList(1, 2));
            assertThat(actual).isEqualTo("12");
        }
    }

    /**
     * 目标方法：{@link StringUtils#concat(Object[])}。
     */
    @Nested
    @DisplayName("Test method: concat(T... array)")
    class TestConcatArray {
        @Test
        @DisplayName("Input is [1, 2], output is '12'")
        void givenNormalListThenReturnCorrectResult() {
            String actual = StringUtils.concat(1, 2);
            assertThat(actual).isEqualTo("12");
        }
    }

    /**
     * 目标方法：{@link StringUtils#endsWithIgnoreCase(String, String)}。
     */
    @Nested
    @DisplayName("Test method: endsWithIgnoreCase(String source, String suffix)")
    class TestEndsWithIgnoreCase {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is true (suffix: null)")
        void givenNullAndSuffixNullThenReturnTrue() {
            boolean actual = StringUtils.endsWithIgnoreCase(null, null);
            assertThat(actual).isTrue();
        }

        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is false (suffix: '')")
        void givenNullAndSuffixEmptyThenReturnFalse() {
            boolean actual = StringUtils.endsWithIgnoreCase(null, StringUtils.EMPTY);
            assertThat(actual).isFalse();
        }

        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is 'Hello', output is false (suffix: null)")
        void givenHelloAndSuffixNullThenReturnFalse() {
            boolean actual = StringUtils.endsWithIgnoreCase("Hello", null);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is 'Hello', output is false (suffix: 'Hello World')")
        void givenHelloAndSuffixHelloWorldThenReturnFalse() {
            boolean actual = StringUtils.endsWithIgnoreCase("Hello", "Hello World");
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is 'Hello', output is true (suffix: 'Hello')")
        void givenHelloAndSuffixTheSameThenReturnTrue() {
            boolean actual = StringUtils.endsWithIgnoreCase("Hello", "Hello");
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is 'Hello', output is true (suffix: 'HELLO')")
        void givenHelloAndSuffixIgnoreCaseThenReturnTrue() {
            boolean actual = StringUtils.endsWithIgnoreCase("Hello", "HELLO");
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is 'Hello World', output is true (suffix: 'World')")
        void givenHelloWorldAndSuffixWorldThenReturnTrue() {
            boolean actual = StringUtils.endsWithIgnoreCase("Hello World", "World");
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is 'Hello World', output is false (suffix: 'God')")
        void givenHelloWorldAndSuffixGodThenReturnFalse() {
            boolean actual = StringUtils.endsWithIgnoreCase("Hello World", "God");
            assertThat(actual).isFalse();
        }
    }

    /**
     * 目标方法：{@link StringUtils#equalizer(boolean)}。
     */
    @Nested
    @DisplayName("Test method: equalizer(boolean ignoreCase)")
    class TestEqualizer {
        @Test
        @DisplayName("Input is ('Hello', 'HELLO'), output is true (ignoreCase: true)")
        void given2StringWithDifferentCaseAndIgnoreCaseThenReturnTrue() {
            Equalizer<String> equalizer = StringUtils.equalizer(true);
            boolean actual = equalizer.equals("Hello", "HELLO");
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is ('Hello', 'HELLO'), output is false (ignoreCase: false)")
        void given2StringWithDifferentCaseAndNotIgnoreCaseThenReturnFalse() {
            Equalizer<String> equalizer = StringUtils.equalizer(false);
            boolean actual = equalizer.equals("Hello", "HELLO");
            assertThat(actual).isFalse();
        }
    }

    /**
     * 目标方法：{@link StringUtils#equals(String, String)}。
     */
    @Nested
    @DisplayName("Test method: equals(String str1, String str2)")
    class TestEquals {
        @Test
        @DisplayName("Input is (null, null), output is true")
        void givenNullAndNullThenReturnTrue() {
            boolean actual = StringUtils.equals(null, null);
            assertThat(actual).isTrue();
        }

        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is (null, 'Hello'), output is false")
        void givenNullAndHelloThenReturnFalse() {
            boolean actual = StringUtils.equals(null, "Hello");
            assertThat(actual).isFalse();
        }

        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is ('Hello', null), output is false")
        void givenHelloAndNullThenReturnFalse() {
            boolean actual = StringUtils.equals("Hello", null);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is (Integer.toHexString(123), Integer.toHexString(123)), output is true")
        void givenTheSame2StringThenReturnTrue() {
            String string1 = Integer.toHexString(123);
            String string2 = Integer.toHexString(123);
            boolean actual = StringUtils.equals(string1, string2);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is ('Hello', 'HELLO'), output is false")
        void givenHelloAndUpperCaseHelloThenReturnTrue() {
            boolean actual = StringUtils.equals("Hello", "HELLO");
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is ('Hello', 'World'), output is false")
        void givenDifferentStringThenReturnFalse() {
            boolean actual = StringUtils.equals("Hello", "World");
            assertThat(actual).isFalse();
        }
    }

    /**
     * 目标方法：{@link StringUtils#equalsIgnoreCase(String, String)}。
     */
    @Nested
    @DisplayName("Test method: equalsIgnoreCase(String str1, String str2)")
    class TestEqualsIgnoreCase {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is (null, null), output is true")
        void givenNullAndNullThenReturnTrue() {
            boolean actual = StringUtils.equalsIgnoreCase(null, null);
            assertThat(actual).isTrue();
        }

        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is (null, 'Hello'), output is false")
        void givenNullAndHelloThenReturnFalse() {
            boolean actual = StringUtils.equalsIgnoreCase(null, "Hello");
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is ('Hello', null), output is false")
        void givenHelloAndNullThenReturnFalse() {
            boolean actual = StringUtils.equalsIgnoreCase("Hello", null);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is (Integer.toHexString(123), Integer.toHexString(123)), output is true")
        void givenTheSame2StringThenReturnTrue() {
            String string1 = Integer.toHexString(123);
            String string2 = Integer.toHexString(123);
            boolean actual = StringUtils.equalsIgnoreCase(string1, string2);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is ('Hello', 'HELLO'), output is true")
        void givenHelloAndUpperCaseHelloThenReturnTrue() {
            boolean actual = StringUtils.equalsIgnoreCase("Hello", "HELLO");
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is ('Hello', 'World'), output is false")
        void givenDifferentStringThenReturnFalse() {
            boolean actual = StringUtils.equalsIgnoreCase("Hello", "World");
            assertThat(actual).isFalse();
        }
    }

    /**
     * 目标方法：{@link StringUtils#format(String, Object...)}。
     */
    @Nested
    @DisplayName("Test method: format(String format, Object... args)")
    class TestFormat {
        @Test
        @DisplayName("Input is ' \n\t', output is ' \n\t'")
        void givenWhiteSpaceThenReturnOrigin() {
            String actual = StringUtils.format(WHITE_SPACE);
            assertThat(actual).isEqualTo(WHITE_SPACE);
        }

        @Test
        @DisplayName("Input is 'item1={0};item2={1}', output is 'item1=123;item2=456' (args: [123, 456])")
        void givenFormatAndArgsThenReturnFormattedString() {
            String actual = StringUtils.format("item1={0};item2={1}", 123, 456);
            assertThat(actual).isEqualTo("item1=123;item2=456");
        }

        @Test
        @DisplayName("Input is 'item1={0};item2={1}', output is 'item1=123;item2=' (args: [123, null])")
        void givenFormatAndArgsWithNullThenReturnFormattedStringWithEmpty() {
            String actual = StringUtils.format("item1={0};item2={1}", 123, null);
            assertThat(actual).isEqualTo("item1=123;item2=");
        }

        @Test
        @DisplayName("Input is '/{///}', output is '{/}'")
        void givenFormatWithEscapeCharacterThenReturnStringWithoutEscapeCharacterOnly() {
            String actual = StringUtils.format("/{///}");
            assertThat(actual).isEqualTo("{/}");
        }
    }

    /**
     * 目标方法：{@link StringUtils#isBlank(String)}。
     */
    @Nested
    @DisplayName("Test method: isBlank(String source)")
    class TestIsBlank {
        @Test
        @DisplayName("Input is null, output is true")
        void givenNullThenReturnTrue() {
            boolean actual = StringUtils.isBlank(null);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is '', output is true")
        void givenEmptyThenReturnTrue() {
            boolean actual = StringUtils.isBlank(StringUtils.EMPTY);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is ' \n\t', output is true")
        void givenWhiteSpaceThenReturnTrue() {
            boolean actual = StringUtils.isBlank(WHITE_SPACE);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is 'Hello World', output is false")
        void givenNonWhiteSpaceThenReturnFalse() {
            boolean actual = StringUtils.isBlank("Hello World");
            assertThat(actual).isFalse();
        }
    }

    /**
     * 目标方法：{@link StringUtils#isEmpty(String)}。
     */
    @Nested
    @DisplayName("Test method: isEmpty(String source)")
    class TestIsEmpty {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is true")
        void givenNullThenReturnTrue() {
            boolean actual = StringUtils.isEmpty(null);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is '', output is true")
        void givenEmptyThenReturnTrue() {
            boolean actual = StringUtils.isEmpty(StringUtils.EMPTY);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is ' \n\t', output is false")
        void givenWhiteSpaceThenReturnFalse() {
            boolean actual = StringUtils.isEmpty(WHITE_SPACE);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is 'Hello World', output is false")
        void givenNonWhiteSpaceThenReturnFalse() {
            boolean actual = StringUtils.isEmpty("Hello World");
            assertThat(actual).isFalse();
        }
    }

    /**
     * 目标方法：{@link StringUtils#isNotBlank(String)}。
     */
    @Nested
    @DisplayName("Test method: isNotBlank(String source)")
    class TestIsNotBlank {
        @Test
        @DisplayName("Input is null, output is false")
        void givenNullThenReturnFalse() {
            boolean actual = StringUtils.isNotBlank(null);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is '', output is false")
        void givenEmptyThenReturnFalse() {
            boolean actual = StringUtils.isNotBlank(StringUtils.EMPTY);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is ' \n\t', output is false")
        void givenWhiteSpaceThenReturnFalse() {
            boolean actual = StringUtils.isNotBlank(WHITE_SPACE);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is 'Hello World', output is true")
        void givenNonWhiteSpaceThenReturnTrue() {
            boolean actual = StringUtils.isNotBlank("Hello World");
            assertThat(actual).isTrue();
        }
    }

    /**
     * 目标方法：{@link StringUtils#isNotEmpty(String)}。
     */
    @Nested
    @DisplayName("Test method: isNotEmpty(String source)")
    class TestIsNotEmpty {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is false")
        void givenNullThenReturnFalse() {
            boolean actual = StringUtils.isNotEmpty(null);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is '', output is false")
        void givenEmptyThenReturnFalse() {
            boolean actual = StringUtils.isNotEmpty(StringUtils.EMPTY);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is ' \n\t', output is true")
        void givenWhiteSpaceThenReturnTrue() {
            boolean actual = StringUtils.isNotEmpty(WHITE_SPACE);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is 'Hello World', output is true")
        void givenNonWhiteSpaceThenReturnTrue() {
            boolean actual = StringUtils.isNotEmpty("Hello World");
            assertThat(actual).isTrue();
        }
    }

    /**
     * 目标方法：{@link StringUtils#join(String, Function, Iterator)}。
     */
    @Nested
    @DisplayName("Test method: join(String separator, Function<T, String> mapper, Iterator<T> iterator)")
    class TestJoinIteratorWithMapperAndStringSeparator {
        @Test
        @DisplayName("Input is null, output is '' (separator: ',', mapper: Integer::toHexString)")
        void givenNoIteratorThenReturnEmpty() {
            String actual = StringUtils.join(",", Integer::toHexString, (Iterator<Integer>) null);
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is [], output is '' (separator: ',', mapper: Integer::toHexString)")
        void givenEmptyIteratorThenReturnEmpty() {
            String actual = StringUtils.join(",", Integer::toHexString, ArrayUtils.iterator());
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is [null, 'str'], output is 'NULL,STR' (separator: ',', mapper: custom toUpperCase)")
        void givenIteratorWithElementNullAndCustomMapperThenReturnStringWithNull() {
            String actual = StringUtils.join(",", str -> {
                if (str == null) {
                    return "NULL";
                } else {
                    return str.toUpperCase();
                }
            }, ArrayUtils.iterator(null, "str"));
            assertThat(actual).isEqualTo("NULL,STR");
        }

        @Test
        @DisplayName("Input is [null], output is '' (separator: ',', mapper: null)")
        void givenIteratorWithElementNullThenReturnEmpty() {
            String actual = StringUtils.join(",", null, ArrayUtils.iterator(ObjectUtils.cast(null)));
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is [1, 2], output is '3,4' (separator: ',', mapper: i -> String.valueOf(i + 2))")
        void givenNormalIteratorThenReturnCorrectResult() {
            String actual = StringUtils.join(",", aInt -> String.valueOf(aInt + 2), ArrayUtils.iterator(1, 2));
            assertThat(actual).isEqualTo("3,4");
        }
    }

    /**
     * 目标方法：{@link StringUtils#join(String, Function, List)}。
     */
    @Nested
    @DisplayName("Test method: join(String separator, Function<T, String> mapper, List<T> list)")
    class TestJoinListWithMapperAndStringSeparator {
        @Test
        @DisplayName("Input is [1, 2], output is '3,4' (separator: ',', mapper: i -> String.valueOf(i + 2))")
        void givenNormalListThenReturnCorrectResult() {
            String actual = StringUtils.join(",", i -> String.valueOf(i + 2), Arrays.asList(1, 2));
            assertThat(actual).isEqualTo("3,4");
        }
    }

    /**
     * 目标方法：{@link StringUtils#join(String, Function, Object[])}。
     */
    @Nested
    @DisplayName("Test method: join(String separator, Function<T, String> mapper, T... array)")
    class TestJoinArrayWithMapperAndStringSeparator {
        @Test
        @DisplayName("Input is [1, 2], output is '3,4' (separator: ',', mapper: i -> String.valueOf(i + 2))")
        void givenNormalListThenReturnCorrectResult() {
            String actual = StringUtils.join(",", i -> String.valueOf(i + 2), 1, 2);
            assertThat(actual).isEqualTo("3,4");
        }
    }

    /**
     * 目标方法：{@link StringUtils#join(String, Iterator)}。
     */
    @Nested
    @DisplayName("Test method: join(String separator, Iterator<T> iterator)")
    class TestJoinIteratorWithStringSeparator {
        @Test
        @DisplayName("Input is [1, 2], output is '1,2' (separator: ',')")
        void givenNormalIteratorThenReturnCorrectResult() {
            String actual = StringUtils.join(",", ArrayUtils.iterator(1, 2));
            assertThat(actual).isEqualTo("1,2");
        }
    }

    /**
     * 目标方法：{@link StringUtils#join(String, List)}。
     */
    @Nested
    @DisplayName("Test method: join(String separator, List<T> list)")
    class TestJoinListWithStringSeparator {
        @Test
        @DisplayName("Input is [1, 2], output is '1,2' (separator: ',')")
        void givenNormalListThenReturnCorrectResult() {
            String actual = StringUtils.join(",", Arrays.asList(1, 2));
            assertThat(actual).isEqualTo("1,2");
        }
    }

    /**
     * 目标方法：{@link StringUtils#join(String, Object[])}。
     */
    @Nested
    @DisplayName("Test method: join(String separator, T... array)")
    class TestJoinArrayWithStringSeparator {
        @Test
        @DisplayName("Input is [1, 2], output is '1,2' (separator: ',')")
        void givenNormalListThenReturnCorrectResult() {
            String actual = StringUtils.join(",", 1, 2);
            assertThat(actual).isEqualTo("1,2");
        }
    }

    /**
     * 目标方法：{@link StringUtils#join(char, Function, Iterator)}。
     */
    @Nested
    @DisplayName("Test method: join(char separator, Function<T, String> mapper, Iterator<T> iterator)")
    class TestJoinIteratorWithMapperAndCharSeparator {
        @Test
        @DisplayName("Input is null, output is '' (separator: ',', mapper: Integer::toHexString)")
        void givenNoIteratorThenReturnEmpty() {
            String actual = StringUtils.join(',', Integer::toHexString, (Iterator<Integer>) null);
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is [], output is '' (separator: ',', mapper: Integer::toHexString)")
        void givenEmptyIteratorThenReturnEmpty() {
            String actual = StringUtils.join(',', Integer::toHexString, ArrayUtils.iterator());
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is [null, 'str'], output is 'NULL,STR' (separator: ',', mapper: custom toUpperCase)")
        void givenIteratorWithElementNullAndCustomMapperThenReturnStringWithNull() {
            String actual = StringUtils.join(',', str -> {
                if (str == null) {
                    return "NULL";
                } else {
                    return str.toUpperCase();
                }
            }, ArrayUtils.iterator(null, "str"));
            assertThat(actual).isEqualTo("NULL,STR");
        }

        @Test
        @DisplayName("Input is [null], output is '' (separator: ',', mapper: null)")
        void givenIteratorWithElementNullThenReturnEMPTY() {
            String actual = StringUtils.join(',', null, ArrayUtils.iterator(ObjectUtils.cast(null)));
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is [1, 2], output is '3,4' (separator: ',', mapper: i -> String.valueOf(i + 2))")
        void givenNormalIteratorThenReturnCorrectResult() {
            String actual = StringUtils.join(',', aInt -> String.valueOf(aInt + 2), ArrayUtils.iterator(1, 2));
            assertThat(actual).isEqualTo("3,4");
        }
    }

    /**
     * 目标方法：{@link StringUtils#join(char, Function, List)}。
     */
    @Nested
    @DisplayName("Test method: join(char separator, Function<T, String> mapper, List<T> list)")
    class TestJoinListWithMapperAndCharSeparator {
        @Test
        @DisplayName("Input is [1, 2], output is '3,4' (separator: ',', mapper: i -> String.valueOf(i + 2))")
        void givenNormalListThenReturnCorrectResult() {
            String actual = StringUtils.join(',', i -> String.valueOf(i + 2), Arrays.asList(1, 2));
            assertThat(actual).isEqualTo("3,4");
        }
    }

    /**
     * 目标方法：{@link StringUtils#join(char, Function, Object[])}。
     */
    @Nested
    @DisplayName("Test method: join(char separator, Function<T, String> mapper, T... array)")
    class TestJoinArrayWithMapperAndCharSeparator {
        @Test
        @DisplayName("Input is [1, 2], output is '3,4' (separator: ',', mapper: i -> String.valueOf(i + 2))")
        void givenNormalListThenReturnCorrectResult() {
            String actual = StringUtils.join(',', i -> String.valueOf(i + 2), 1, 2);
            assertThat(actual).isEqualTo("3,4");
        }
    }

    /**
     * 目标方法：{@link StringUtils#join(char, Iterator)}。
     */
    @Nested
    @DisplayName("Test method: join(char separator, Iterator<T> iterator)")
    class TestJoinIteratorWithCharSeparator {
        @Test
        @DisplayName("Input is [1, 2], output is '1,2' (separator: ',')")
        void givenNormalIteratorThenReturnCorrectResult() {
            String actual = StringUtils.join(',', ArrayUtils.iterator(1, 2));
            assertThat(actual).isEqualTo("1,2");
        }
    }

    /**
     * 目标方法：{@link StringUtils#join(char, List)}。
     */
    @Nested
    @DisplayName("Test method: join(char separator, List<T> list)")
    class TestJoinListWithCharSeparator {
        @Test
        @DisplayName("Input is [1, 2], output is '1,2' (separator: ',')")
        void givenNormalListThenReturnCorrectResult() {
            String actual = StringUtils.join(',', Arrays.asList(1, 2));
            assertThat(actual).isEqualTo("1,2");
        }
    }

    /**
     * 目标方法：{@link StringUtils#join(char, Object[])}。
     */
    @Nested
    @DisplayName("Test method: join(char separator, T... array)")
    class TestJoinArrayWithCharSeparator {
        @Test
        @DisplayName("Input is [1, 2], output is '1,2' (separator: ',')")
        void givenNormalListThenReturnCorrectResult() {
            String actual = StringUtils.join(',', 1, 2);
            assertThat(actual).isEqualTo("1,2");
        }
    }

    /**
     * 目标方法：{@link StringUtils#lengthBetween(String, int, int)}。
     */
    @Nested
    @DisplayName("Test method: lengthBetween(String source, int min, int max)")
    class TestLengthBetweenMinAllowedMaxNotAllowed {
        @Test
        @DisplayName("Input is 'Hello', output is false (min: 0, max: 5)")
        void givenHelloWithMin0Max5ThenReturnFalse() {
            boolean actual = StringUtils.lengthBetween("Hello", 0, 5);
            assertThat(actual).isFalse();
        }
    }

    /**
     * 目标方法：{@link StringUtils#lengthBetween(String, int, int, boolean, boolean)}。
     */
    @Nested
    @DisplayName(
            "Test method: lengthBetween(String source, int min, int max, boolean allowMinimum, boolean allowMaximum)")
    class TestLengthBetween {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is false (min: 0, max: 5, allowMinimum: true, allowMaximum: true)")
        void givenNullThenReturnFalse() {
            boolean actual = StringUtils.lengthBetween(null, 0, 5, true, true);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is 'Hello', output is true (min: 0, max: 5, allowMinimum: true, allowMaximum: true)")
        void givenHelloWithMin0Max5ThenReturnFalse() {
            boolean actual = StringUtils.lengthBetween("Hello", 0, 5, true, true);
            assertThat(actual).isTrue();
        }
    }

    /**
     * 目标方法：{@link StringUtils#mapIfNotBlank(String, Function)}
     */
    @Nested
    @DisplayName("目标方法：mapIfNotBlank(String, Function)")
    class TestMapIfNotBlank {
        @Test
        @DisplayName("当映射程序为null时，抛出异常")
        void givenNullMapperThenThrowException() {
            ThrowableAssert.ThrowingCallable execution = () -> StringUtils.mapIfNotBlank("1", null);
            IllegalArgumentException ex = catchThrowableOfType(execution, IllegalArgumentException.class);
            assertThat(ex).isNotNull();
        }

        @Test
        @DisplayName("当给定了空白字符串时，返回null")
        void givenBlankStringThenReturnNull() {
            Integer result = StringUtils.mapIfNotBlank(null, value -> {
                if (value == null) {
                    return 0;
                } else {
                    return Integer.parseInt(value);
                }
            });
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("当给定了非空白字符串时，返回映射得到的结果")
        void givenNotBlankStringThenReturnMappedValue() {
            Integer result = StringUtils.mapIfNotBlank("1", Integer::parseInt);
            assertThat(result).isEqualTo(1);
        }
    }

    /**
     * 目标方法：{@link StringUtils#normalize(String)}。
     */
    @Nested
    @DisplayName("Test method: normalize(String source)")
    class TestNormalize {
        @Test
        @DisplayName("Input is null, output is ''")
        void givenNullThenReturnEmpty() {
            String actual = StringUtils.normalize(null);
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is 'Hello World', output is 'Hello World'")
        void givenNormalStringThenReturnOrigin() {
            String actual = StringUtils.normalize("Hello World");
            assertThat(actual).isEqualTo("Hello World");
        }
    }

    /**
     * 目标方法：{@link StringUtils#padLeft(String, char, int)}。
     */
    @Nested
    @DisplayName("Test method: padLeft(String source, char padding, int length)")
    class TestPadLeft {
        @Test
        @DisplayName("Input is null, output is null (padding: ' ', length: -1)")
        void givenNullWithLengthMinus1ThenReturnNull() {
            String actual = StringUtils.padLeft(null, ' ', -1);
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is null, output is '' (padding: ' ', length: 0)")
        void givenNullWithLength0ThenReturnEmpty() {
            String actual = StringUtils.padLeft(null, ' ', 0);
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is 'Hello', output is '     Hello' (padding: ' ', length: 10)")
        void givenHelloWithLength10ThenReturnPaddedHello() {
            String actual = StringUtils.padLeft("Hello", ' ', 10);
            assertThat(actual).isEqualTo("     Hello");
        }
    }

    /**
     * 目标方法：{@link StringUtils#padRight(String, char, int)}。
     */
    @Nested
    @DisplayName("Test method: padRight(String source, char padding, int length)")
    class TestPadRight {
        @Test
        @DisplayName("Input is null, output is null (padding: ' ', length: -1)")
        void givenNullWithLengthMinus1ThenReturnNull() {
            String actual = StringUtils.padRight(null, ' ', -1);
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is null, output is '' (padding: ' ', length: 0)")
        void givenNullWithLength0ThenReturnEmpty() {
            String actual = StringUtils.padRight(null, ' ', 0);
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is 'Hello', output is 'Hello     ' (padding: ' ', length: 10)")
        void givenHelloWithLength10ThenReturnPaddedHello() {
            String actual = StringUtils.padRight("Hello", ' ', 10);
            assertThat(actual).isEqualTo("Hello     ");
        }
    }

    /**
     * 目标方法：{@link StringUtils#replace(String, char, char)}。
     */
    @Nested
    @DisplayName("Test method: replace(String source, char oldChar, char newChar)")
    class TestReplace {
        @Test
        @DisplayName("Input is null, output is null (oldChar: 'a', newChar: 'b')")
        void givenNullThenReturnNull() {
            String actual = StringUtils.replace(null, 'a', 'b');
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is '', output is '' (oldChar: 'a', newChar: 'b')")
        void givenEmptyThenReturnEmpty() {
            String actual = StringUtils.replace(StringUtils.EMPTY, 'a', 'b');
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is 'Hello World', output is 'Hell! W!rld' (oldChar: 'o', newChar: '!')")
        void givenNormalStringThenReturnReplacedString() {
            String actual = StringUtils.replace("Hello World", 'o', '!');
            assertThat(actual).isEqualTo("Hell! W!rld");
        }
    }

    @Nested
    @DisplayName("Test method: replace(String source, String oldStr, String newStr)")
    class TestReplaceString {
        @Test
        @DisplayName("Source is null, output is null")
        void givenNullSourceThenReturnNull() {
            String actual = StringUtils.replace(null, "a", "b");
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Target is null, output is Source")
        void givenNullTargetThenReturnOrigin() {
            String source = "a";
            String actual = StringUtils.replace(source, null, "b");
            assertThat(actual).isEqualTo(source);
        }

        @Test
        @DisplayName("Replace is null, output is Source")
        void givenNullReplaceThenReturnOrigin() {
            String source = "a";
            String actual = StringUtils.replace(source, "b", null);
            assertThat(actual).isEqualTo(source);
        }

        @Test
        @DisplayName("Target is miss, output is Source")
        void givenMissTargetThenReturnOrigin() {
            String source = "Hello World";
            String actual = StringUtils.replace(source, "foo", "bar");
            assertThat(actual).isEqualTo(source);
        }

        @Test
        @DisplayName("Input is 'Hello World', output is 'Hel!l!o Worl!d' (oldStr: 'l', newStr: 'l!')")
        void givenNormalStringThenReturnReplacedString() {
            String actual = StringUtils.replace("Hello World", "l", "l!");
            assertThat(actual).isEqualTo("Hel!l!o Worl!d");
        }
    }

    /**
     * 目标方法：{@link StringUtils#split(String, char)}。
     */
    @Nested
    @DisplayName("Test method: split(String source, char separator)")
    class TestSimpleSplit {
        @Test
        @DisplayName("To array: input is 'boo:and:foo', separator is ':', output is ['boo', 'and', 'foo']")
        void givenNormalStringAndSeparatorColonThenReturnCorrectResult() {
            String[] array = StringUtils.split("boo:and:foo", ':');
            assertThat(array).hasSize(3).containsSequence("boo", "and", "foo");
        }
    }

    /**
     * 目标方法：{@link StringUtils#split(String, char, Supplier, Predicate)}。
     */
    @Nested
    @DisplayName("Test method: "
            + "split(String source, char separator, Supplier<C> collectionSupplier, Predicate<String> partPredicator)")
    class TestSplit {
        @Nested
        @DisplayName("Separator is ':' and partPredicator is null")
        class GivenSeparatorColonAndNoPredicator {
            private static final char SEPARATOR = ':';

            @Test
            @DisplayName("To list: input is null, output is IllegalArgumentException")
            void givenNoOriginalStringThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> StringUtils.split(null, SEPARATOR, ArrayList::new, null),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("The string to be split cannot be null.");
            }

            @Test
            @DisplayName("To unknown collection: input is '', output is IllegalArgumentException")
            void givenNoCollectionSupplierThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> StringUtils.split(StringUtils.EMPTY, SEPARATOR, null, null),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("The collectionSupplier cannot be null.");
            }

            @Test
            @DisplayName("To list: input is '', output is ['']")
            void givenEmptyThenReturnEmpty() {
                List<String> list = StringUtils.split(StringUtils.EMPTY, SEPARATOR, ArrayList::new, null);
                assertThat(list).hasSize(1).containsSequence(StringUtils.EMPTY);
            }

            @Test
            @DisplayName("To list: input is ':', output is ['', '']")
            void givenOnlyColonThenReturn2EmptyString() {
                List<String> list = StringUtils.split(":", SEPARATOR, ArrayList::new, null);
                assertThat(list).hasSize(2).containsSequence(StringUtils.EMPTY, StringUtils.EMPTY);
            }

            @Test
            @DisplayName("To list: input is ':: :', output is ['', '', ' ', '']")
            void givenContinuousColonsThenReturnContinuousEmpty() {
                List<String> list = StringUtils.split(":: :", SEPARATOR, ArrayList::new, null);
                assertThat(list).hasSize(4)
                        .containsSequence(StringUtils.EMPTY, StringUtils.EMPTY, " ", StringUtils.EMPTY);
            }

            @Test
            @DisplayName("To list: input is 'boo:and:foo', output is ['boo', 'and', 'foo']")
            void givenNormalStringThenReturnCorrectResult() {
                List<String> list = StringUtils.split("boo:and:foo", SEPARATOR, ArrayList::new, null);
                assertThat(list).hasSize(3).containsSequence("boo", "and", "foo");
            }
        }

        @Nested
        @DisplayName("Separator is 'o'")
        class GivenSeparatorO {
            private static final char SEPARATOR = 'o';

            @Test
            @DisplayName("To list: input is 'boo:and:foo', output is ['b', '', ':and:f', '', ''] (no predicator)")
            void givenNormalStringThenReturnCorrectResult() {
                List<String> list = StringUtils.split("boo:and:foo", SEPARATOR, ArrayList::new, null);
                assertThat(list).hasSize(5)
                        .containsSequence("b", StringUtils.EMPTY, ":and:f", StringUtils.EMPTY, StringUtils.EMPTY);
            }

            @Test
            @DisplayName("To list: input is 'boo:and:foo', output is ['b', ':and:f']")
            void givenNormalStringAndPredicatorThenReturnCorrectResult() {
                List<String> list =
                        StringUtils.split("boo:and:foo", SEPARATOR, ArrayList::new, StringUtils::isNotBlank);
                assertThat(list).hasSize(2).containsSequence("b", ":and:f");
            }
        }
    }

    /**
     * 目标方法：{@link StringUtils#split(String, String)}。
     */
    @Nested
    @DisplayName("Test method: split(String source, String separator)")
    class TestSimpleSplitWithStringSeparator {
        @Test
        @DisplayName("To array: input is 'boo:and:foo', separator is ':', output is ['boo', 'and', 'foo']")
        void givenNormalStringAndSeparatorColonThenReturnCorrectResult() {
            String[] array = StringUtils.split("boo:and:foo", ":");
            assertThat(array).hasSize(3).containsSequence("boo", "and", "foo");
        }
    }

    /**
     * 目标方法：{@link StringUtils#split(String, String, Supplier, Predicate)}。
     */
    @Nested
    @DisplayName("Test method: split(source, separator, collectionSupplier, partPredicator)")
    class TestSplitWithStringSeparator {
        @Nested
        @DisplayName("Separator is ':' and partPredicator is null")
        class GivenSeparatorColonAndNoPredicator {
            private static final String SEPARATOR = ":";

            @Test
            @DisplayName("To list: input is null, output is IllegalArgumentException")
            void givenNoOriginalStringThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> StringUtils.split(null, SEPARATOR, ArrayList::new, null),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("The string to be split cannot be null.");
            }

            @Test
            @DisplayName("To unknown collection: input is '', output is IllegalArgumentException")
            void givenNoCollectionSupplierThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> StringUtils.split(StringUtils.EMPTY, SEPARATOR, null, null),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("The collectionSupplier cannot be null.");
            }

            @Test
            @DisplayName("To list: input is '', output is ['']")
            void givenEmptyThenReturnEmpty() {
                List<String> list = StringUtils.split(StringUtils.EMPTY, SEPARATOR, ArrayList::new, null);
                assertThat(list).hasSize(1).containsSequence(StringUtils.EMPTY);
            }

            @Test
            @DisplayName("To list: input is ':', output is ['', '']")
            void givenOnlyColonThenReturn2EmptyString() {
                List<String> list = StringUtils.split(":", SEPARATOR, ArrayList::new, null);
                assertThat(list).hasSize(2).containsSequence(StringUtils.EMPTY, StringUtils.EMPTY);
            }

            @Test
            @DisplayName("To list: input is ':: :', output is ['', '', ' ', '']")
            void givenContinuousColonsThenReturnContinuousEmpty() {
                List<String> list = StringUtils.split(":: :", SEPARATOR, ArrayList::new, null);
                assertThat(list).hasSize(4)
                        .containsSequence(StringUtils.EMPTY, StringUtils.EMPTY, " ", StringUtils.EMPTY);
            }

            @Test
            @DisplayName("To list: input is 'boo:and:foo', output is ['boo', 'and', 'foo']")
            void givenNormalStringThenReturnCorrectResult() {
                List<String> list = StringUtils.split("boo:and:foo", SEPARATOR, ArrayList::new, null);
                assertThat(list).hasSize(3).containsSequence("boo", "and", "foo");
            }
        }

        @Nested
        @DisplayName("Separator is 'o'")
        class GivenSeparatorO {
            private static final String SEPARATOR = "o";

            @Test
            @DisplayName("To list: input is 'boo:and:foo', output is ['b', '', ':and:f', '', ''] (no predicator)")
            void givenNormalStringThenReturnCorrectResult() {
                List<String> list = StringUtils.split("boo:and:foo", SEPARATOR, ArrayList::new, null);
                assertThat(list).hasSize(5)
                        .containsSequence("b", StringUtils.EMPTY, ":and:f", StringUtils.EMPTY, StringUtils.EMPTY);
            }

            @Test
            @DisplayName("To list: input is 'boo:and:foo', output is ['b', ':and:f']")
            void givenNormalStringAndPredicatorThenReturnCorrectResult() {
                List<String> list =
                        StringUtils.split("boo:and:foo", SEPARATOR, ArrayList::new, StringUtils::isNotBlank);
                assertThat(list).hasSize(2).containsSequence("b", ":and:f");
            }
        }

        @Nested
        @DisplayName("Separator is empty")
        class GivenEmptySeparator {
            private static final String SEPARATOR = "";

            @Test
            @DisplayName("To list: input is 'boo:and:foo', output is ['b', 'o', 'o', ':' 'a', 'n', 'd', ':', 'f', 'o', "
                    + "'o'] (no predicator)")
            void givenNormalStringThenReturnCorrectResult() {
                List<String> list = StringUtils.split("boo:and:foo", SEPARATOR, ArrayList::new, null);
                assertThat(list).hasSize(11).containsSequence("b", "o", "o", ":", "a", "n", "d", ":", "f", "o", "o");
            }

            @Test
            @DisplayName(
                    "To list: input is ' boo:and:foo ', output is [['b', 'o', 'o', ':' 'a', 'n', 'd', ':', 'f', 'o', "
                            + "'o']")
            void givenNormalStringAndPredicatorThenReturnCorrectResult() {
                List<String> list =
                        StringUtils.split(" boo:and:foo ", SEPARATOR, ArrayList::new, StringUtils::isNotBlank);
                assertThat(list).hasSize(11).containsSequence("b", "o", "o", ":", "a", "n", "d", ":", "f", "o", "o");
            }
        }

        @Nested
        @DisplayName("Separator is 'oo'")
        class GivenSeparatorOo {
            private static final String SEPARATOR = "oo";

            @Test
            @DisplayName("To list: input is 'boo:and:foo', output is ['b', ':and:f', ''] (no predicator)")
            void givenNormalStringThenReturnCorrectResult() {
                List<String> list = StringUtils.split("boo:and:foo", SEPARATOR, ArrayList::new, null);
                assertThat(list).hasSize(3).containsSequence("b", ":and:f", StringUtils.EMPTY);
            }

            @Test
            @DisplayName("To list: input is 'boo:and:foo', output is ['b', ':and:f']")
            void givenNormalStringAndPredicatorThenReturnCorrectResult() {
                List<String> list =
                        StringUtils.split("boo:and:foo", SEPARATOR, ArrayList::new, StringUtils::isNotBlank);
                assertThat(list).hasSize(2).containsSequence("b", ":and:f");
            }
        }
    }

    /**
     * 目标方法：{@link StringUtils#splitToList(String, char)}。
     */
    @Nested
    @DisplayName("Test method: splitToList(String source, char separator)")
    class TestSplitToList {
        @Test
        @DisplayName("To list: input is 'a\\b\\c', separator is '\\', output is ['a', 'b', 'c']")
        void givenStringConcatByBackSlashThenReturnCorrectResult() {
            List<String> list = StringUtils.splitToList("a\\b\\c", '\\');
            assertThat(list).hasSize(3).containsSequence("a", "b", "c");
        }

        @Test
        @DisplayName("To list: input is 'a/b', separator is '/', output is ['a', 'b']")
        void givenStringConcatBySlashThenReturnCorrectResult() {
            List<String> list = StringUtils.splitToList("a/b", '/');
            assertThat(list).hasSize(2).containsSequence("a", "b");
        }
    }

    /**
     * 目标方法：{@link StringUtils#splitToList(String, String)}。
     */
    @Nested
    @DisplayName("Test method: splitToList(String source, String separator)")
    class TestSplitToListWithStringSeparator {
        @Test
        @DisplayName("To list: input is 'a\\b\\c', separator is '\\', output is ['a', 'b', 'c']")
        void givenStringConcatByBackSlashThenReturnCorrectResult() {
            List<String> list = StringUtils.splitToList("a\\b\\c", "\\");
            assertThat(list).hasSize(3).containsSequence("a", "b", "c");
        }

        @Test
        @DisplayName("To list: input is 'a/b', separator is '/', output is ['a', 'b']")
        void givenStringConcatBySlashThenReturnCorrectResult() {
            List<String> list = StringUtils.splitToList("a/b", "/");
            assertThat(list).hasSize(2).containsSequence("a", "b");
        }
    }

    /**
     * 目标方法：{@link StringUtils#splitToSet(String, char)}。
     */
    @Nested
    @DisplayName("Test method: splitToSet(String source, char separator)")
    class TestSplitToSet {
        @Test
        @DisplayName("To set: input is 'a\\b\\b', separator is '\\', output is ['a', 'b']")
        void givenStringConcatByBackSlashThenReturnCorrectResult() {
            Set<String> set = StringUtils.splitToSet("a\\b\\b", '\\');
            assertThat(set).hasSize(2).containsSequence("a", "b");
        }
    }

    /**
     * 目标方法：{@link StringUtils#splitToSet(String, String)}。
     */
    @Nested
    @DisplayName("Test method: splitToSet(String source, String separator)")
    class TestSplitToSetWithStringSeparator {
        @Test
        @DisplayName("To set: input is 'a\\b\\b', separator is '\\', output is ['a', 'b']")
        void givenStringConcatByBackSlashThenReturnCorrectResult() {
            Set<String> set = StringUtils.splitToSet("a\\b\\b", "\\");
            assertThat(set).hasSize(2).containsSequence("a", "b");
        }

        @Test
        @DisplayName("To set: input is 'a\\b\\b', separator is '', output is ['a', 'b', '\\']")
        void givenStringConcatByEmptySeparatorThenReturnCorrectResult() {
            Set<String> set = StringUtils.splitToSet("a\\b\\b", "");
            assertThat(set).hasSize(3).containsSequence("a", "b", "\\");
        }
    }

    /**
     * 目标方法：{@link StringUtils#substring(String, int, int)}。
     */
    @Nested
    @DisplayName("Test method: substring(String source, int startIndex, int endIndex)")
    class TestSubstring {
        @Test
        @DisplayName("Input is null, output is IllegalArgumentException")
        void givenNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> StringUtils.substring(null, 0, 0), IllegalArgumentException.class);
            assertThat(exception).isNotNull().hasMessage("The source string is null.");
        }

        @Test
        @DisplayName("Input is 'Hello World', output is IllegalStateException (startIndex: -12, endIndex: 5)")
        void givenHelloWorldAndBetweenMinus12And5ThenThrowException() {
            IllegalStateException exception = catchThrowableOfType(() -> StringUtils.substring("Hello World", -12, 5),
                    IllegalStateException.class);
            assertThat(exception).isNotNull().hasMessage("The start index is out of range: -12.");
        }

        @Test
        @DisplayName("Input is 'Hello World', output is IllegalStateException (startIndex: 0, endIndex: 12)")
        void givenHelloWorldAndBetween0And12ThenThrowException() {
            IllegalStateException exception = catchThrowableOfType(() -> StringUtils.substring("Hello World", 0, 12),
                    IllegalStateException.class);
            assertThat(exception).isNotNull().hasMessage("The end index is out of range: 12.");
        }

        @Test
        @DisplayName("Input is 'Hello World', output is IllegalStateException (startIndex: -1, endIndex: 1)")
        void givenHelloWorldAndBetweenMinus1And1ThenThrowException() {
            IllegalStateException exception = catchThrowableOfType(() -> StringUtils.substring("Hello World", -1, 1),
                    IllegalStateException.class);
            assertThat(exception).isNotNull()
                    .hasMessage("The canonical start index is greater than the canonical end index. "
                            + "[startIndex=-1, endIndex=1, canonicalStartIndex=10, canonicalEndIndex=1]");
        }

        @Test
        @DisplayName("Input is 'Hello World', output is 'Hello' (startIndex: 0, endIndex: 5)")
        void givenHelloWorldAndBetween0And5ThenReturnHello() {
            String actual = StringUtils.substring("Hello World", 0, 5);
            assertThat(actual).isEqualTo("Hello");
        }
    }

    /**
     * 目标方法：{@link StringUtils#substringAfter(String, String)}。
     */
    @Nested
    @DisplayName("Test method: substringAfter(String source, String separator)")
    class TestSubstringAfter {
        @Test
        @DisplayName("Input is null, output is null (separator: ': ')")
        void givenNullThenReturnNull() {
            String actual = StringUtils.substringAfter(null, ": ");
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is '', output is '' (separator: ': ')")
        void givenEmptyThenReturnEmpty() {
            String actual = StringUtils.substringAfter(StringUtils.EMPTY, ": ");
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is 'Hello World', output is 'Hello World' (separator: null)")
        void givenHelloWorldWithSeparatorNullThenReturnOrigin() {
            String actual = StringUtils.substringAfter("Hello World", null);
            assertThat(actual).isEqualTo("Hello World");
        }

        @Test
        @DisplayName("Input is 'Hello World', output is 'Hello World' (separator: '')")
        void givenHelloWorldWithSeparatorEmptyThenReturnOrigin() {
            String actual = StringUtils.substringAfter("Hello World", "");
            assertThat(actual).isEqualTo("Hello World");
        }

        @Test
        @DisplayName("Input is 'Hello World', output is '' (separator: ': ')")
        void givenHelloWorldWithSeparatorNotExistThenReturnEmpty() {
            String actual = StringUtils.substringAfter("Hello World", ": ");
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is 'Hello World', output is 'World' (separator: ' ')")
        void givenHelloWorldWithSeparatorExistThenReturnCorrectResult() {
            String actual = StringUtils.substringAfter("Hello World", " ");
            assertThat(actual).isEqualTo("World");
        }
    }

    /**
     * 目标方法：{@link StringUtils#surround(String, char)}。
     */
    @Nested
    @DisplayName("Test method: surround(String source, char surroundWith)")
    class TestSurround {
        @Test
        @DisplayName("Input is null, output is '##' (surroundWith is '#')")
        void givenNullThenReturnOnlySurroundedChar() {
            String actual = StringUtils.surround(null, '#');
            assertThat(actual).isEqualTo("##");
        }
    }

    /**
     * 目标方法：{@link StringUtils#surround(String, char, char)}。
     */
    @Nested
    @DisplayName("Test method: surround(String source, char prefix, char suffix)")
    class TestSurroundWithBothSide {
        @Test
        @DisplayName("Input is 'different-string', output is '[different-string]' (prefix is '[', suffix is ']')")
        void givenNormalStringThenReturnSurroundedString() {
            String actual = StringUtils.surround("different-string", '[', ']');
            assertThat(actual).isEqualTo("[different-string]");
        }
    }

    /**
     * 目标方法：{@link StringUtils#toLowerCase(String)}。
     */
    @Nested
    @DisplayName("Test method: toLowerCase(String source)")
    class TestToLowerCase {
        @Test
        @DisplayName("Input is 'HELLO', output is 'hello'")
        void givenUpperCaseThenReturnLowerCase() {
            String actual = StringUtils.toLowerCase("HELLO");
            assertThat(actual).isEqualTo("hello");
        }
    }

    /**
     * 目标方法：{@link StringUtils#toLowerCase(String, Locale)}。
     */
    @Nested
    @DisplayName("Test method: toLowerCase(String source, Locale locale)")
    class TestToLowerCaseWithLocale {
        @Test
        @DisplayName("Input is null, output is null (locale is Locale.ROOT)")
        void givenNullThenReturnNull() {
            String actual = StringUtils.toLowerCase(null, Locale.ROOT);
            assertThat(actual).isNull();
        }
    }

    /**
     * 目标方法：{@link StringUtils#toUpperCase(String)}。
     */
    @Nested
    @DisplayName("Test method: toUpperCase(String source)")
    class TestToUpperCase {
        @Test
        @DisplayName("Input is 'hello', output is 'HELLO'")
        void givenLowerCaseThenReturnUpperCase() {
            String actual = StringUtils.toUpperCase("hello");
            assertThat(actual).isEqualTo("HELLO");
        }
    }

    /**
     * 目标方法：{@link StringUtils#toUpperCase(String, Locale)}。
     */
    @Nested
    @DisplayName("Test method: toUpperCase(String source, Locale locale)")
    class TestToUpperCaseWithLocale {
        @Test
        @DisplayName("Input is null, output is null (locale is Locale.ROOT)")
        void givenNullThenReturnNull() {
            String actual = StringUtils.toUpperCase(null, Locale.ROOT);
            assertThat(actual).isNull();
        }
    }

    /**
     * 目标方法：{@link StringUtils#trim(String)}。
     */
    @Nested
    @DisplayName("Test method: trim(String source)")
    class TestTrimWhiteSpace {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is null")
        void givenNullThenReturnNull() {
            String actual = StringUtils.trim(null);
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is ' \n\t', output is ''")
        void givenBlankThenReturnEmpty() {
            String actual = StringUtils.trim(WHITE_SPACE);
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }
    }

    /**
     * 目标方法：{@link StringUtils#trim(String, char)}。
     */
    @Nested
    @DisplayName("Test method: trim(String source, char ch)")
    class TestTrimChar {
        @Test
        @DisplayName("Input is null, output is null (ch: ':')")
        void givenNullThenReturnNull() {
            String actual = StringUtils.trim(null, ':');
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is ':', output is '' (ch: ':')")
        void givenColonThenReturnEmpty() {
            String actual = StringUtils.trim(":", ':');
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is ':abc:', output is 'abc' (ch: ':')")
        void givenStringWithBothSideColonThenReturnStringWithNoColon() {
            String actual = StringUtils.trim(":abc:", ':');
            assertThat(actual).isEqualTo("abc");
        }
    }

    /**
     * 目标方法：{@link StringUtils#trim(String, char, char)}。
     */
    @Nested
    @DisplayName("Test method: trim(String source, char startCh, char endCh)")
    class TestTrimBothSideChar {
        @Test
        @DisplayName("Input is null, output is null (startCh: 'a', endCh: 'b')")
        void givenNullThenReturnNull() {
            String actual = StringUtils.trim(null, 'a', 'b');
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is 'ab', output is '' (startCh: 'a', endCh: 'b')")
        void givenNormalStringThenReturnCorrectString() {
            String actual = StringUtils.trim("ab", 'a', 'b');
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is 'ab', output is 'ab' (startCh: 'b', endCh: 'a')")
        void givenNormalStringWithoutLeadingAndTrailingCharThenReturnOrigin() {
            String actual = StringUtils.trim("ab", 'b', 'a');
            assertThat(actual).isEqualTo("ab");
        }
    }

    /**
     * 目标方法：{@link StringUtils#trimEnd(String)}。
     */
    @Nested
    @DisplayName("Test method: trimEnd(String source)")
    class TestTrimEndWhiteSpace {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is null")
        void givenNullThenReturnNull() {
            String actual = StringUtils.trimEnd(null);
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is ' \n\t', output is ''")
        void givenBlankThenReturnEmpty() {
            String actual = StringUtils.trimEnd(WHITE_SPACE);
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is ' abc ', output is ' abc'")
        void givenNormalStringThenReturnCorrectString() {
            String actual = StringUtils.trimEnd(" abc ");
            assertThat(actual).isEqualTo(" abc");
        }
    }

    /**
     * 目标方法：{@link StringUtils#trimEnd(String, char)}。
     */
    @Nested
    @DisplayName("Test method: trimEnd(String source, char ch)")
    class TestTrimEndChar {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is null (ch: ':')")
        void givenNullThenReturnNull() {
            String actual = StringUtils.trimEnd(null, ':');
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is ':', output is '' (ch: ':')")
        void givenColonThenReturnEmpty() {
            String actual = StringUtils.trimEnd(":", ':');
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is ':abc:', output is ':abc' (ch: ':')")
        void givenNormalStringThenReturnCorrectString() {
            String actual = StringUtils.trimEnd(":abc:", ':');
            assertThat(actual).isEqualTo(":abc");
        }

        @Test
        @DisplayName("Input is ':abc#', output is ':abc#' (ch: ':')")
        void givenNormalStringWithoutTrailingColonThenReturnOrigin() {
            String actual = StringUtils.trimEnd(":abc#", ':');
            assertThat(actual).isEqualTo(":abc#");
        }
    }

    /**
     * 目标方法：{@link StringUtils#trimStart(String)}。
     */
    @Nested
    @DisplayName("Test method: trimStart(String source)")
    class TestTrimStartWhiteSpace {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is null")
        void givenNullThenReturnNull() {
            String actual = StringUtils.trimStart(null);
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is ' \n\t', output is ''")
        void givenBlankThenReturnEmpty() {
            String actual = StringUtils.trimStart(WHITE_SPACE);
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is ' abc ', output is 'abc '")
        void givenNormalStringThenReturnCorrectString() {
            String actual = StringUtils.trimStart(" abc ");
            assertThat(actual).isEqualTo("abc ");
        }
    }

    /**
     * 目标方法：{@link StringUtils#trimStart(String, char)}。
     */
    @Nested
    @DisplayName("Test method: trimStart(String source, char ch)")
    class TestTrimStartChar {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is null (ch: ':')")
        void givenNullThenReturnNull() {
            String actual = StringUtils.trimStart(null, ':');
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is ':', output is '' (ch: ':')")
        void givenColonThenReturnEmpty() {
            String actual = StringUtils.trimStart(":", ':');
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is ':abc:', output is 'abc:' (ch: ':')")
        void givenNormalStringThenReturnCorrectString() {
            String actual = StringUtils.trimStart(":abc:", ':');
            assertThat(actual).isEqualTo("abc:");
        }

        @Test
        @DisplayName("Input is '#abc:', output is '#abc:' (ch: ':')")
        void givenNormalStringWithoutLeadingColonThenReturnOrigin() {
            String actual = StringUtils.trimStart("#abc:", ':');
            assertThat(actual).isEqualTo("#abc:");
        }
    }

    @Nested
    @DisplayName("测试方法：isUtf8()")
    class TestIsUtf8 {
        @Test
        @DisplayName("当提供字节数组包含有效 utf8 数据，返回 true")
        void givenBytesWithUtf8ThenReturnTrue() {
            final boolean isUtf8 = StringUtils.isUtf8(new byte[] {1, 2, 3});
            assertThat(isUtf8).isTrue();
        }

        @Test
        @DisplayName("当提供字节数组不包含有效 utf8 数据，返回 false")
        void givenBytesWithNotUtf8ThenReturnFalse() {
            final boolean isUtf8 = StringUtils.isUtf8(new byte[] {-1, -2});
            assertThat(isUtf8).isFalse();
        }

        @Test
        @DisplayName("当提供输入流包含有效 utf8 数据，返回 true")
        void givenInputStreamWithUtf8ThenReturnTrue() throws IOException {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[] {1, 2, 3});
            final boolean isUtf8 = StringUtils.isUtf8(inputStream);
            assertThat(isUtf8).isTrue();
        }

        @Test
        @DisplayName("当提供输入流不包含有效 utf8 数据，返回 false")
        void givenInputStreamWithNotUtf8ThenReturnFalse() throws IOException {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[] {-1, -2});
            final boolean isUtf8 = StringUtils.isUtf8(inputStream);
            assertThat(isUtf8).isFalse();
        }
    }

    @Nested
    @DisplayName("测试方法：isAscii()")
    class TestIsAscii {
        @Test
        @DisplayName("当输入全部都是 ASCII 字符时，返回 true")
        void shouldReturnTrue() {
            boolean actual = StringUtils.isAscii("hello world.txt");
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("当输入不全部都是 ASCII 字符时，返回 false")
        void shouldReturnFalse() {
            boolean actual = StringUtils.isAscii("你好.txt");
            assertThat(actual).isFalse();
        }
    }
}
