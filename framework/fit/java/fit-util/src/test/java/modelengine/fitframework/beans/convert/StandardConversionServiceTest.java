/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表示 {@link StandardConversionService} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-02-25
 */
@DisplayName("测试类型转换方法")
class StandardConversionServiceTest {
    @Nested
    @DisplayName("测试 null 值转换")
    class NullTest {
        @Test
        @DisplayName("当将 null 转为基本类型时抛出异常")
        void shouldThrowWhenConvert2Primitive() {
            IllegalArgumentException actual =
                    catchThrowableOfType(() -> ConversionService.forStandard().convert(null, int.class),
                            IllegalArgumentException.class);
            assertThat(actual).hasMessage("Cannot convert null to a primitive class. [target=int]");
        }

        @Test
        @DisplayName("当将 null 转为引用类型时，返回 null")
        void shouldReturnNullWhenConvert2ReferenceClass() {
            Object actual = ConversionService.forStandard().convert(null, String.class);
            assertThat(actual).isNull();
        }
    }

    @Nested
    @DisplayName("测试转换标量")
    class ConvertToScalarTest {
        @Test
        @DisplayName("将数字转为 8 位整数")
        void shouldConvertByteFromNumber() {
            BigInteger value = BigInteger.valueOf(Byte.MIN_VALUE);
            byte actual = ConversionService.forStandard().convert(value, byte.class);
            assertThat(actual).isEqualTo(Byte.MIN_VALUE);
        }

        @Test
        @DisplayName("将字符串转为 8 位整数")
        void shouldConvertByteFromString() {
            String value = Byte.toString(Byte.MIN_VALUE);
            byte actual = ConversionService.forStandard().convert(value, byte.class);
            assertThat(actual).isEqualTo(Byte.MIN_VALUE);
        }

        @Test
        @DisplayName("将数字转为 16 位整数")
        void shouldConvertShortFromNumber() {
            BigInteger value = BigInteger.valueOf(Short.MIN_VALUE);
            short actual = ConversionService.forStandard().convert(value, short.class);
            assertThat(actual).isEqualTo(Short.MIN_VALUE);
        }

        @Test
        @DisplayName("将字符串转为 16 位整数")
        void shouldConvertShortFromString() {
            String value = Short.toString(Short.MIN_VALUE);
            short actual = ConversionService.forStandard().convert(value, short.class);
            assertThat(actual).isEqualTo(Short.MIN_VALUE);
        }

        @Test
        @DisplayName("将数字转为 32 位整数")
        void shouldConvertIntegerFromNumber() {
            BigInteger value = BigInteger.valueOf(Integer.MIN_VALUE);
            int actual = ConversionService.forStandard().convert(value, int.class);
            assertThat(actual).isEqualTo(Integer.MIN_VALUE);
        }

        @Test
        @DisplayName("将字符串转为 32 位整数")
        void shouldConvertIntegerFromString() {
            String value = Integer.toString(Integer.MIN_VALUE);
            int actual = ConversionService.forStandard().convert(value, int.class);
            assertThat(actual).isEqualTo(Integer.MIN_VALUE);
        }

        @Test
        @DisplayName("将数字转为 64 位整数")
        void shouldConvertLongFromNumber() {
            BigInteger value = new BigInteger(Long.toUnsignedString(Long.MIN_VALUE));
            long actual = ConversionService.forStandard().convert(value, long.class);
            assertThat(actual).isEqualTo(Long.MIN_VALUE);
        }

        @Test
        @DisplayName("将字符串转为 64 位整数")
        void shouldConvertLongFromString() {
            String value = Long.toString(Long.MIN_VALUE);
            long actual = ConversionService.forStandard().convert(value, long.class);
            assertThat(actual).isEqualTo(Long.MIN_VALUE);
        }

        @Test
        @DisplayName("将数字转为单精度浮点数")
        void shouldConvertFloatFromNumber() {
            float actual = ConversionService.forStandard().convert(BigInteger.TEN, float.class);
            assertThat(actual).isEqualTo(10f);
        }

        @Test
        @DisplayName("将字符串转为单精度浮点数")
        void shouldConvertFloatFromString() {
            String value = "100.00";
            float actual = ConversionService.forStandard().convert(value, float.class);
            assertThat(actual).isEqualTo(100f);
        }

        @Test
        @DisplayName("将数字转为双精度浮点数")
        void shouldConvertDoubleFromNumber() {
            double actual = ConversionService.forStandard().convert(BigInteger.TEN, double.class);
            assertThat(actual).isEqualTo(10d);
        }

        @Test
        @DisplayName("将字符串转为双精度浮点数")
        void shouldConvertDoubleFromString() {
            String value = "100.00";
            double actual = ConversionService.forStandard().convert(value, double.class);
            assertThat(actual).isEqualTo(100d);
        }

        @Test
        @DisplayName("将大十进制数转换为大整数")
        void shouldConvertBigIntegerFromBigDecimal() {
            BigDecimal value = new BigDecimal("1.1");
            BigInteger actual = ConversionService.forStandard().convert(value, BigInteger.class);
            assertThat(actual).isEqualTo(BigInteger.ONE);
        }

        @Test
        @DisplayName("将字符串转换为大整数")
        void shouldConvertBigIntegerFromString() {
            String value = "1";
            BigInteger actual = ConversionService.forStandard().convert(value, BigInteger.class);
            assertThat(actual).isEqualTo(BigInteger.ONE);
        }

        @Test
        @DisplayName("将大整数转换为大十进制数")
        void shouldConvertBigDecimalFromBigInteger() {
            BigInteger value = BigInteger.TEN;
            BigDecimal actual = ConversionService.forStandard().convert(value, BigDecimal.class);
            assertThat(actual).isEqualTo(BigDecimal.TEN);
        }

        @Test
        @DisplayName("将字符串换为大十进制数")
        void shouldConvertBigDecimalFromString() {
            String value = "10";
            BigDecimal actual = ConversionService.forStandard().convert(value, BigDecimal.class);
            assertThat(actual).isEqualTo(BigDecimal.TEN);
        }
    }

    @Nested
    @DisplayName("测试将值转为列表")
    class ConvertToListTest {
        @Test
        @DisplayName("应对原始列表中的每个元素进行转换")
        void shouldConvertAllElementInList() {
            List<Object> list = Arrays.asList("1", BigInteger.valueOf(2), BigDecimal.valueOf(3.0));
            Set<Long> actual = ObjectUtils.cast(ConversionService.forStandard()
                    .convert(list, TypeUtils.parameterized(Set.class, new Type[] {Long.class})));
            assertThat(actual).hasSize(3).contains(1L, 2L, 3L);
        }

        @Test
        @DisplayName("将一个标量值转为列表，抛出异常")
        void shouldThrowExceptionWhenConvertSingleValueToList() {
            IllegalArgumentException actual = catchThrowableOfType(() -> ConversionService.forStandard()
                            .convert("1.5", TypeUtils.parameterized(List.class, new Type[] {Double.class})),
                    IllegalArgumentException.class);
            assertThat(actual).hasMessage("Cannot convert value to List. [source=java.lang.String]");
        }
    }

    @Nested
    @DisplayName("测试将值转为集合")
    class ConvertToSetTest {
        @Test
        @DisplayName("可以将列表类型的值转为集合")
        void shouldConvertListToSet() {
            List<?> list = Arrays.asList("1", BigInteger.valueOf(2), BigDecimal.valueOf(3.0));
            ParameterizedType type = TypeUtils.parameterized(Set.class, new Type[] {String.class});
            Set<String> actual = ObjectUtils.cast(ConversionService.forStandard().convert(list, type));
            assertThat(actual).hasSize(3).contains("1", "2", "3.0");
        }
    }

    @Nested
    @DisplayName("测试将值转为映射")
    class ConvertToMapTest {
        @Test
        @DisplayName("转换映射的键和值")
        void shouldConvertKeyAndValueInMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("a", BigInteger.ZERO);
            map.put("b", BigDecimal.ONE);
            Type type = TypeUtils.parameterized(Map.class, new Type[] {Character.class, Boolean.class});
            Map<Character, Boolean> actual = ObjectUtils.cast(ConversionService.forStandard().convert(map, type));
            assertThat(actual).hasSize(2).containsEntry('a', false).containsEntry('b', true);
        }
    }

    @Nested
    @DisplayName("测试将值转为 JavaBean")
    class ConvertToBeanTest {
        @Test
        @DisplayName("将对象转为 JavaBean")
        void shouldConvertObject2JavaBean() {
            Map<String, Object> department = new HashMap<>();
            department.put("name", "FIT");
            Map<String, Object> employee = new HashMap<>();
            employee.put("id", "100");
            employee.put("name", "Maria");
            employee.put("online", "true");
            employee.put("department", department);
            Employee actual = ConversionService.forStandard().convert(employee, Employee.class);
            assertThat(actual).isNotNull()
                    .returns(100L, Employee::getId)
                    .returns("Maria", Employee::getName)
                    .returns(true, Employee::isOnline);
            assertThat(actual.getDepartment()).isNotNull().returns("FIT", Department::getName);
        }
    }

    @Nested
    @DisplayName("测试将值转为 void")
    class ConvertToVoidTest {
        @Test
        @DisplayName("转换值为 null")
        void shouldConvertKeyAndValueInMap() {
            Type type = Void.TYPE;
            Object object = ObjectUtils.cast(ConversionService.forStandard().convert(null, type));
            assertThat(object).isNull();
        }
    }
}
