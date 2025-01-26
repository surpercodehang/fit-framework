/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表示 {@link ConfigConversionService} 的单元测试。
 *
 * @author 梁济时
 * @since 2022-12-28
 */
@DisplayName("测试类型转换方法")
class ConfigConversionServiceTest {
    @Nested
    @DisplayName("测试 null 值转换")
    class NullTest {
        @Test
        @DisplayName("当将 null 转为基本类型时抛出异常")
        void should_throw_when_convert_to_primitive() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> ConversionService.forConfig().convert(null, int.class));
            assertEquals("Cannot convert null to a primitive class. [target=int]", exception.getMessage());
        }

        @Test
        @DisplayName("当将 null 转为引用类型时，返回 null")
        void should_return_null_when_convert_to_reference_class() {
            Object value = ConversionService.forConfig().convert(null, String.class);
            assertNull(value);
        }
    }

    @Nested
    @DisplayName("测试枚举转换")
    class EnumTest {
        Class<?> enumClass = DemoEnum.class;

        @Test
        @DisplayName("当字符串为小写时，转化枚举")
        void getEnumWhenLowerCase() {
            Object resEnum = ConversionService.forConfig().convert("shared", enumClass);
            assertThat(resEnum.toString()).isEqualTo("SHARED");
        }

        @Test
        @DisplayName("当字符串为大写时，转化枚举")
        void getEnumWhenUpperCase() {
            Object resEnum = ConversionService.forConfig().convert("SHARED", enumClass);
            assertThat(resEnum.toString()).isEqualTo("SHARED");
        }

        @Test
        @DisplayName("当字符串忽略大小写，仍不匹配时，转化枚举")
        void getEnumWhenNotMatched() {
            Object resEnum = ConversionService.forConfig().convert("hello", enumClass);
            assertThat(resEnum).isNull();
        }
    }

    @Nested
    @DisplayName("测试转换标量")
    class ConvertToScalarTest {
        @Test
        @DisplayName("将数字转为 8 位整数")
        void shouldConvertByteFromNumber() {
            BigInteger value = BigInteger.valueOf(Byte.MIN_VALUE);
            byte result = ConversionService.forConfig().convert(value, byte.class);
            assertEquals(Byte.MIN_VALUE, result);
        }

        @Test
        @DisplayName("将字符串转为 8 位整数")
        void shouldConvertByteFromString() {
            String value = Byte.toString(Byte.MIN_VALUE);
            byte result = ConversionService.forConfig().convert(value, byte.class);
            assertEquals(Byte.MIN_VALUE, result);
        }

        @Test
        @DisplayName("将数字转为 16 位整数")
        void shouldConvertShortFromNumber() {
            BigInteger value = BigInteger.valueOf(Short.MIN_VALUE);
            short result = ConversionService.forConfig().convert(value, short.class);
            assertEquals(Short.MIN_VALUE, result);
        }

        @Test
        @DisplayName("将字符串转为 16 位整数")
        void shouldConvertShortFromString() {
            String value = Short.toString(Short.MIN_VALUE);
            short result = ConversionService.forConfig().convert(value, short.class);
            assertEquals(Short.MIN_VALUE, result);
        }

        @Test
        @DisplayName("将数字转为 32 位整数")
        void shouldConvertIntegerFromNumber() {
            BigInteger value = BigInteger.valueOf(Integer.MIN_VALUE);
            int result = ConversionService.forConfig().convert(value, int.class);
            assertEquals(Integer.MIN_VALUE, result);
        }

        @Test
        @DisplayName("将字符串转为 32 位整数")
        void shouldConvertIntegerFromString() {
            String value = Integer.toString(Integer.MIN_VALUE);
            int result = ConversionService.forConfig().convert(value, int.class);
            assertEquals(Integer.MIN_VALUE, result);
        }

        @Test
        @DisplayName("将数字转为 64 位整数")
        void shouldConvertLongFromNumber() {
            BigInteger value = new BigInteger(Long.toUnsignedString(Long.MIN_VALUE));
            long result = ConversionService.forConfig().convert(value, long.class);
            assertEquals(Long.MIN_VALUE, result);
        }

        @Test
        @DisplayName("将字符串转为 64 位整数")
        void shouldConvertLongFromString() {
            String value = Long.toString(Long.MIN_VALUE);
            long result = ConversionService.forConfig().convert(value, long.class);
            assertEquals(Long.MIN_VALUE, result);
        }

        @Test
        @DisplayName("将数字转为单精度浮点数")
        void shouldConvertFloatFromNumber() {
            float result = ConversionService.forConfig().convert(BigInteger.TEN, float.class);
            assertEquals(10f, result);
        }

        @Test
        @DisplayName("将字符串转为单精度浮点数")
        void shouldConvertFloatFromString() {
            String value = "100.00";
            float result = ConversionService.forConfig().convert(value, float.class);
            assertEquals(100f, result);
        }

        @Test
        @DisplayName("将数字转为双精度浮点数")
        void shouldConvertDoubleFromNumber() {
            double result = ConversionService.forConfig().convert(BigInteger.TEN, double.class);
            assertEquals(10d, result);
        }

        @Test
        @DisplayName("将字符串转为双精度浮点数")
        void shouldConvertDoubleFromString() {
            String value = "100.00";
            double result = ConversionService.forConfig().convert(value, double.class);
            assertEquals(100d, result);
        }

        @Test
        @DisplayName("将大十进制数转换为大整数")
        void shouldConvertBigIntegerFromBigDecimal() {
            BigDecimal value = new BigDecimal("1.1");
            BigInteger result = ConversionService.forConfig().convert(value, BigInteger.class);
            assertEquals(BigInteger.ONE, result);
        }

        @Test
        @DisplayName("将字符串转换为大整数")
        void shouldConvertBigIntegerFromString() {
            String value = "1";
            BigInteger result = ConversionService.forConfig().convert(value, BigInteger.class);
            assertEquals(BigInteger.ONE, result);
        }

        @Test
        @DisplayName("将大整数转换为大十进制数")
        void shouldConvertBigDecimalFromBigInteger() {
            BigInteger value = BigInteger.TEN;
            BigDecimal result = ConversionService.forConfig().convert(value, BigDecimal.class);
            assertEquals(BigDecimal.TEN, result);
        }

        @Test
        @DisplayName("将字符串换为大十进制数")
        void shouldConvertBigDecimalFromString() {
            String value = "10";
            BigDecimal result = ConversionService.forConfig().convert(value, BigDecimal.class);
            assertEquals(BigDecimal.TEN, result);
        }
    }

    @Nested
    @DisplayName("测试将值转为列表")
    class ConvertToListTest {
        @Test
        @DisplayName("应对原始列表中的每个元素进行转换")
        void shouldConvertAllElementInList() {
            List<Object> list = Arrays.asList("1", BigInteger.valueOf(2), BigDecimal.valueOf(3.0));
            @SuppressWarnings("unchecked") Set<Long> result = (Set<Long>) ConversionService.forConfig()
                    .convert(list, TypeUtils.parameterized(Set.class, new Type[] {Long.class}));
            assertEquals(3, result.size());
            assertTrue(result.contains(1L));
            assertTrue(result.contains(2L));
            assertTrue(result.contains(3L));
        }

        @Test
        @DisplayName("将一个标量值转为列表")
        void shouldConvertValueToList() {
            @SuppressWarnings("unchecked") List<Double> result = (List<Double>) ConversionService.forConfig()
                    .convert("1.5", TypeUtils.parameterized(List.class, new Type[] {Double.class}));
            assertEquals(1, result.size());
            assertEquals(1.5d, result.get(0));
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
            Set<String> result = ObjectUtils.cast(ConversionService.forConfig().convert(list, type));
            assertEquals(3, result.size());
            assertTrue(result.contains("1"));
            assertTrue(result.contains("2"));
            assertTrue(result.contains("3.0"));
        }

        @Test
        @DisplayName("可以将标量转为集合")
        void shouldConvertScalarToSet() {
            Object value = "2023-01-04 15:32:33.123";
            ParameterizedType type = TypeUtils.parameterized(Set.class, new Type[] {Date.class});
            Set<Date> result = ObjectUtils.cast(ConversionService.forConfig().convert(value, type));
            assertEquals(1, result.size());
            Calendar calendar = new GregorianCalendar();
            calendar.set(Calendar.YEAR, 2023);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            calendar.set(Calendar.DAY_OF_MONTH, 4);
            calendar.set(Calendar.HOUR_OF_DAY, 15);
            calendar.set(Calendar.MINUTE, 32);
            calendar.set(Calendar.SECOND, 33);
            calendar.set(Calendar.MILLISECOND, 123);
            Date expected = calendar.getTime();
            Date actual = result.iterator().next();
            assertEquals(expected, actual);
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
            Map<Character, Boolean> result = ObjectUtils.cast(ConversionService.forConfig().convert(map, type));
            assertEquals(2, result.size());
            assertFalse(result.get('a'));
            assertTrue(result.get('b'));
        }
    }

    @Nested
    @DisplayName("测试将值转为 JavaBean")
    class ConvertToBeanTest {
        @Test
        @DisplayName("将对象转为 JavaBean")
        void should_convert_object_to_java_bean() {
            Map<String, Object> department = new HashMap<>();
            department.put("name", "FIT");
            Map<String, Object> employee = new HashMap<>();
            employee.put("id", "100");
            employee.put("name", "Maria");
            employee.put("online", "true");
            employee.put("department", department);
            Employee bean = ConversionService.forConfig().convert(employee, Employee.class);
            assertEquals(100L, bean.getId());
            assertEquals("Maria", bean.getName());
            assertTrue(bean.isOnline());
            assertNotNull(bean.getDepartment());
            assertEquals("FIT", bean.getDepartment().getName());
        }
    }

    @Nested
    @DisplayName("测试将值转为 void")
    class ConvertToVoidTest {
        @Test
        @DisplayName("转换值为 null")
        void shouldConvertKeyAndValueInMap() {
            Type type = Void.TYPE;
            Object object = ObjectUtils.cast(ConversionService.forConfig().convert(null, type));
            assertThat(object).isNull();
        }
    }

    enum DemoEnum {
        /** 表示第一个枚举项。 */
        EXCLUSIVE,

        /** 表示第二个枚举项。 */
        SHARED;
    }
}
