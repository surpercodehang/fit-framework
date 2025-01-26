/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.within;

import modelengine.fitframework.beans.Object1;
import modelengine.fitframework.beans.Object2;
import modelengine.fitframework.beans.Object3;
import modelengine.fitframework.beans.Object4;
import modelengine.fitframework.beans.Object5;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link ObjectUtils} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
@DisplayName("测试 ObjectUtils 工具类")
public class ObjectUtilsTest {
    /**
     * 表示作为较小值的可比较对象。
     */
    private static final Integer COMPARABLE_SMALL = -100;

    /**
     * 表示作为较大值的可比较对象。
     */
    private static final Integer COMPARABLE_BIG = 100;

    /**
     * 表示作为空值的可比较对象。
     */
    private static final Integer COMPARABLE_NULL = null;

    /**
     * 表示将整数映射称为字符串的方法。
     */
    private static final Function<Integer, String> MAPPER = Integer::toHexString;

    /**
     * 表示将 {@link ObjectUtilsTest#COMPARABLE_BIG} 映射成字符串的结果。
     */
    private static final String MAPPED_COMPARABLE_UPPER = "64";

    @Nested
    @DisplayName("Test between")
    class TestBetween {
        /**
         * 目标方法：{@link ObjectUtils#between(Comparable, Comparable, Comparable)}。
         */
        @Nested
        @DisplayName("Test method: between(T value, T min, T max)")
        class TestBetweenWithoutInclude {
            @Test
            @DisplayName("Given 100 and range [-100, 100] then return true")
            void givenValueIsMaxThenReturnTrue() {
                boolean actual = ObjectUtils.between(COMPARABLE_BIG, COMPARABLE_SMALL, COMPARABLE_BIG);
                assertThat(actual).isTrue();
            }
        }

        /**
         * 目标方法：{@link ObjectUtils#between(Comparable, Comparable, Comparable, boolean, boolean)}。
         */
        @Nested
        @DisplayName("Test method: between(T value, T min, T max, boolean includeMin, boolean includeMax)")
        class TestBetweenWithInclude {
            @Nested
            @DisplayName("Given include max")
            class GivenIncludeMax {
                @Test
                @DisplayName("Given 100 and range [-100, 100] then return true")
                void givenValueIsMaxThenReturnTrue() {
                    boolean actual = ObjectUtils.between(COMPARABLE_BIG, COMPARABLE_SMALL, COMPARABLE_BIG, true, true);
                    assertThat(actual).isTrue();
                }

                @Test
                @DisplayName("Given 101 and range [-100, 100] then return false")
                void givenValueIsGreaterThanMaxThenReturnFalse() {
                    boolean actual =
                            ObjectUtils.between(COMPARABLE_BIG + 1, COMPARABLE_SMALL, COMPARABLE_BIG, true, true);
                    assertThat(actual).isFalse();
                }
            }

            @Nested
            @DisplayName("Given exclude max")
            class GivenExcludeMax {
                @Test
                @DisplayName("Given 100 and range [-100, 100) then return false")
                void givenValueIsMaxThenReturnFalse() {
                    boolean actual = ObjectUtils.between(COMPARABLE_BIG, COMPARABLE_SMALL, COMPARABLE_BIG, true, false);
                    assertThat(actual).isFalse();
                }

                @Test
                @DisplayName("Given 99 and range [-100, 100) then return true")
                void givenValueIsLessThanMaxThenReturnTrue() {
                    boolean actual =
                            ObjectUtils.between(COMPARABLE_BIG - 1, COMPARABLE_SMALL, COMPARABLE_BIG, true, false);
                    assertThat(actual).isTrue();
                }
            }

            @Nested
            @DisplayName("Given include min")
            class GivenIncludeMin {
                @Test
                @DisplayName("Given -100 and range [-100, 100] then return true")
                void givenValueIsMinThenReturnTrue() {
                    boolean actual =
                            ObjectUtils.between(COMPARABLE_SMALL, COMPARABLE_SMALL, COMPARABLE_BIG, true, true);
                    assertThat(actual).isTrue();
                }

                @Test
                @DisplayName("Given -101 and range [-100, 100] then return false")
                void givenValueIsLessThanMinThenReturnFalse() {
                    boolean actual =
                            ObjectUtils.between(COMPARABLE_SMALL - 1, COMPARABLE_SMALL, COMPARABLE_BIG, true, true);
                    assertThat(actual).isFalse();
                }
            }

            @Nested
            @DisplayName("Given exclude min")
            class GivenExcludeMin {
                @Test
                @DisplayName("Given -100 and range (-100, 100] then return false")
                void givenValueIsMinThenReturnFalse() {
                    boolean actual =
                            ObjectUtils.between(COMPARABLE_SMALL, COMPARABLE_SMALL, COMPARABLE_BIG, false, true);
                    assertThat(actual).isFalse();
                }

                @Test
                @DisplayName("Given -99 and range (-100, 100] then return true")
                void givenValueIsGreaterThanMinThenReturnTrue() {
                    boolean actual =
                            ObjectUtils.between(COMPARABLE_SMALL + 1, COMPARABLE_SMALL, COMPARABLE_BIG, false, true);
                    assertThat(actual).isTrue();
                }
            }
        }
    }

    @Nested
    @DisplayName("Test compare")
    class TestCompare {
        /**
         * 目标方法：{@link ObjectUtils#compare(Comparable, Comparable)}。
         */
        @Nested
        @DisplayName("Test method: compare(T obj1, T obj2)")
        class TestCompareWithoutComparator {
            @Test
            @DisplayName("Given 100 vs -100 then return positive")
            void given100VsMinus100ThenReturnPositive() {
                int actual = ObjectUtils.compare(COMPARABLE_BIG, COMPARABLE_SMALL);
                assertThat(actual).isGreaterThan(0);
            }

            @Test
            @DisplayName("Given -100 vs 100 then return negative")
            void givenMinus100Vs100ThenReturnNegative() {
                int actual = ObjectUtils.compare(COMPARABLE_SMALL, COMPARABLE_BIG);
                assertThat(actual).isLessThan(0);
            }

            @Test
            @DisplayName("Given 100 vs 100 then return 0")
            void given100Vs100ThenReturn0() {
                int actual = ObjectUtils.compare(COMPARABLE_BIG, COMPARABLE_BIG);
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Given null vs 100 then negative")
            void givenNullVs100ThenReturnNegative() {
                int actual = ObjectUtils.compare(COMPARABLE_NULL, COMPARABLE_BIG);
                assertThat(actual).isLessThan(0);
            }

            @Test
            @DisplayName("Given 100 vs null then return positive")
            void given100VsNullThenReturnPositive() {
                int actual = ObjectUtils.compare(COMPARABLE_BIG, COMPARABLE_NULL);
                assertThat(actual).isGreaterThan(0);
            }

            @Test
            @DisplayName("Given null vs null then return 0")
            void givenNullVsNullThenReturn0() {
                int actual = ObjectUtils.compare(COMPARABLE_NULL, COMPARABLE_NULL);
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link ObjectUtils#compare(Object, Object, Comparator)}。
         */
        @Nested
        @DisplayName("Test method: compare(T obj1, T obj2, Comparator<T> comparator)")
        class TestCompareWithComparator {
            @Nested
            @DisplayName("Given comparator is null")
            class GivenComparatorNull {
                @Test
                @DisplayName("Given -100 vs 100 then throw IllegalArgumentException")
                void given2ComparablesThenThrowException() {
                    IllegalArgumentException exception =
                            catchThrowableOfType(() -> ObjectUtils.compare(COMPARABLE_SMALL, COMPARABLE_BIG, null),
                                    IllegalArgumentException.class);
                    assertThat(exception).isNotNull().hasMessage("The comparator to compare objects cannot be null.");
                }
            }
        }
    }

    /**
     * 目标方法：{@link ObjectUtils#defaultValue(Class)}。
     */
    @Nested
    @DisplayName("Test method: defaultValue(Class<T> clazz)")
    class TestDefaultValue {
        @Nested
        @DisplayName("Given class is null")
        class GivenClassNull {
            @Test
            @DisplayName("Throw IllegalArgumentException")
            void throwException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ObjectUtils.defaultValue(null), IllegalArgumentException.class);
                assertThat(exception).isNotNull().hasMessage("The class to look up default value cannot be null.");
            }
        }

        @Nested
        @DisplayName("Given class is not null")
        class GivenClassNotNull {
            @Test
            @DisplayName("Given class is Integer.class then return null")
            void givenClassIntegerThenReturnNull() {
                Integer actual = ObjectUtils.defaultValue(Integer.class);
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Given class is int.class then return 0")
            void givenClassIntThenReturn0() {
                int actual = ObjectUtils.defaultValue(int.class);
                assertThat(actual).isEqualTo(0);
            }
        }
    }

    /**
     * 目标方法：{@link ObjectUtils#getIfNull(Object, Supplier)}。
     */
    @Nested
    @DisplayName("Test method: getIfNull(T value, Supplier<T> factory)")
    class TestGetIfNull {
        @Nested
        @DisplayName("Given value is null")
        class GivenValueIsNull {
            @Test
            @DisplayName("Given factory is () -> 1 then return 1")
            void givenFactoryIsNotNullThenReturnFactoryGet() {
                int actual = ObjectUtils.getIfNull(null, () -> 1);
                assertThat(actual).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("Given value is 0")
        class GivenValueIsNotNull {
            @Test
            @DisplayName("Given factory is null then throw IllegalArgumentException")
            void givenFactoryIsNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ObjectUtils.getIfNull(0, null), IllegalArgumentException.class);
                assertThat(exception).isNotNull().hasMessage("The factory to create default value cannot be null.");
            }

            @Test
            @DisplayName("Given factory is () -> 1 then return 0")
            void givenFactoryIsNotNullThenReturn0() {
                int actual = ObjectUtils.getIfNull(0, () -> 1);
                assertThat(actual).isEqualTo(0);
            }
        }
    }

    /**
     * 目标方法：{@link ObjectUtils#mapIfNotNull(Object, Function)}。
     */
    @Nested
    @DisplayName("Test method: mapIfNotNull(T value, Function<T, R> mapper)")
    class TestMapIfNotNull {
        @Nested
        @DisplayName("Given value is null")
        class GivenValueIsNull {
            @Test
            @DisplayName("Given mapper is Integer::toHexString then return null")
            void givenMapperIsNotNullThenReturnNull() {
                String actual = ObjectUtils.mapIfNotNull(COMPARABLE_NULL, MAPPER);
                assertThat(actual).isNull();
            }
        }

        @Nested
        @DisplayName("Given value is 100")
        class GivenValueIsNotNull {
            @Test
            @DisplayName("Given mapper is Integer::toHexString then return 64")
            void givenMapperIsNotNullThenReturnMappedValue() {
                String actual = ObjectUtils.mapIfNotNull(COMPARABLE_BIG, MAPPER);
                assertThat(actual).isEqualTo(MAPPED_COMPARABLE_UPPER);
            }

            @Test
            @DisplayName("Given mapper is null then throw IllegalArgumentException")
            void givenMapperIsNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ObjectUtils.mapIfNotNull(COMPARABLE_NULL, null),
                                IllegalArgumentException.class);
                assertThat(exception).isNotNull().hasMessage("The mapper cannot be null.");
            }
        }
    }

    /**
     * 目标方法：{@link ObjectUtils#nullIf(Object, Object)}。
     */
    @Nested
    @DisplayName("Test method: nullIf(T value, T defaultValue)")
    class TestNullIf {
        @Test
        @DisplayName("Given value is -100 and default value is 100 then return -100")
        void givenValueIsNotNullThenReturnValue() {
            int actual = ObjectUtils.nullIf(COMPARABLE_SMALL, COMPARABLE_BIG);
            assertThat(actual).isEqualTo(COMPARABLE_SMALL);
        }

        @Test
        @DisplayName("Given value is null and default value is 100 then return 100")
        void givenValueIsNullThenReturnDefaultValue() {
            Integer actual = ObjectUtils.nullIf(COMPARABLE_NULL, COMPARABLE_BIG);
            assertThat(actual).isEqualTo(COMPARABLE_BIG);
        }
    }

    /**
     * 目标方法：{@link ObjectUtils#toNormalizedString(Object)}。
     */
    @Nested
    @DisplayName("Test method: normalize(Object obj)")
    class TestNormalize {
        @Test
        @DisplayName("Input is null, output is ''")
        void givenNullThenReturnEmpty() {
            String actual = ObjectUtils.toNormalizedString(null);
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is 'Hello World', output is 'Hello World'")
        void givenNormalStringThenReturnOrigin() {
            String actual = ObjectUtils.toNormalizedString("Hello World");
            assertThat(actual).isEqualTo("Hello World");
        }
    }

    /**
     * 目标方法：{@link ObjectUtils#toString(Object)}。
     */
    @Nested
    @DisplayName("Test method: toString(Object obj)")
    class TestToString {
        @Test
        @DisplayName("Given obj is null then return null")
        void givenObjIsNullThenReturnNull() {
            Object actual = ObjectUtils.toString(null);
            assertThat(actual).isNull();
        }
    }

    @Nested
    @DisplayName("测试 cast(Object obj) 方法")
    class WhenCast {
        @Test
        @DisplayName("当参数是 1 个字符串时，返回值可以被成功的强转为字符串")
        void givenInputIsStringThenOutputIsString() {
            String actual = ObjectUtils.cast("Hello");
            assertThat(actual).isInstanceOf(String.class).isEqualTo("Hello");
        }
    }

    @Nested
    @DisplayName("测试方法: combine")
    class TestCombine {
        private static final String HELLO = "Hello";
        private static final String WORLD = "World";

        @Nested
        @DisplayName("测试 combine(T object1, T object2, BinaryOperator<T> combiner)方法")
        class CombineWith2Object {
            @Test
            @DisplayName("当参数是 1 个字符串 1 个 null 和字符串连接方法时，返回值是非 null 字符串")
            void givenOneObjIsNullThenReturnNull() {
                String combine1 = ObjectUtils.combine(null, WORLD, String::concat);
                String combine2 = ObjectUtils.combine(HELLO, null, String::concat);
                assertThat(combine1).isEqualTo(WORLD);
                assertThat(combine2).isEqualTo(HELLO);
            }

            @Test
            @DisplayName("当参数是 2 个待连接字符串和 null 时，抛出异常")
            void given2ObjIsNullThenReturnNull() {
                assertThatThrownBy(() -> ObjectUtils.combine(HELLO,
                        WORLD,
                        null)).isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("The combiner to combine two objects cannot be null.");
            }

            @Test
            @DisplayName("当参数是 2 个待连接字符串和字符串连接方法时，返回值是字符串拼接后的结果")
            void given2ObjectThenReturnCombinedObject() {
                String combine = ObjectUtils.combine(HELLO, WORLD, String::concat);
                assertThat(combine).isEqualTo(HELLO + WORLD);
            }
        }

        @Nested
        @DisplayName("测试 combine(Iterable<T> objects, BinaryOperator<T> combiner)方法")
        class CombineWithMoreObject {
            @Test
            @DisplayName("当参数是 null 时，返回值是 null")
            void givenIteratorIsNullThenReturnNull() {
                String combine = ObjectUtils.combine(null, String::concat);
                assertThat(combine).isNull();
            }

            @Test
            @DisplayName("当参数是一组对象时，返回值是一个组合后的对象")
            void givenIteratorIsNotNullThenReturnNull() {
                final List<Integer> list = Arrays.asList(1, 2, 3);
                final Integer combine = ObjectUtils.combine(list, Integer::sum);
                assertThat(combine).isEqualTo(6);
            }
        }
    }

    @Nested
    @DisplayName("测试方法: toJavaObject(Object obj)")
    class TestToJavaObject {
        @SuppressWarnings("ConstantValue")
        @Test
        @DisplayName("当输入为 null 时，返回 null")
        void shouldReturnNull() {
            Object javaObject = ObjectUtils.toJavaObject(null);
            assertThat(javaObject).isNull();
        }

        @Test
        @DisplayName("当输入为基础类型时，返回基础类型")
        void shouldReturnPrimitive() {
            Object javaObject = ObjectUtils.toJavaObject(0);
            assertThat(javaObject).isEqualTo(0);
        }

        @Test
        @DisplayName("当输入为基础类型的包装类时，返回基础类型的包装类")
        void shouldReturnPrimitiveWrapper() {
            Long source = 100L;
            Object javaObject = ObjectUtils.toJavaObject(source);
            assertThat(javaObject).isEqualTo(100L);
        }

        @Test
        @DisplayName("当输入为字符串时，返回字符串")
        void shouldReturnString() {
            Object javaObject = ObjectUtils.toJavaObject("Hello");
            assertThat(javaObject).isEqualTo("Hello");
        }

        @Test
        @DisplayName("当输入为二进制数组时，返回二进制数组")
        void shouldReturnBytes() {
            Object javaObject = ObjectUtils.toJavaObject("Hello".getBytes(StandardCharsets.UTF_8));
            assertThat(javaObject).isEqualTo("Hello".getBytes(StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("当输入为键值对时，返回键值对")
        void shouldReturnMap() {
            Map<String, Object> map = MapBuilder.<String, Object>get().put("k1", 1).put("k2", "v2").build();
            Object javaObject = ObjectUtils.toJavaObject(map);
            assertThat(javaObject).isInstanceOf(Map.class)
                    .hasFieldOrPropertyWithValue("k1", 1)
                    .hasFieldOrPropertyWithValue("k2", "v2");
        }

        @Test
        @DisplayName("当输入为列表时，返回列表")
        void shouldReturnList() {
            List<Object> list = Arrays.asList(1, "v2");
            Object javaObject = ObjectUtils.toJavaObject(list);
            assertThat(javaObject).isInstanceOf(List.class).asList().contains(1, "v2");
        }

        @Test
        @DisplayName("当输入为数组时，返回列表")
        void shouldReturnListWhenInputIsArray() {
            Object[] array = new Object[] {1, "v2"};
            Object javaObject = ObjectUtils.toJavaObject(array);
            assertThat(javaObject).isInstanceOf(List.class).asList().contains(1, "v2");
        }

        @Test
        @DisplayName("当对象属性为基本类型时，转换成合适的 Map 对象")
        void shouldReturnCorrectMapWhenAttributesArePrimitive() {
            Object1 o1 = new Object1();
            o1.setF1("Hello");
            o1.setF2(1);
            Map<String, Object> map = ObjectUtils.cast(ObjectUtils.toJavaObject(o1));
            assertThat(map).containsEntry("f1", "Hello").containsEntry("f2", 1);
        }

        @Test
        @DisplayName("当对象属性为自定义对象时，转换成合适的 Map 对象")
        void shouldReturnCorrectMapWhenAttributesHaveCustomObject() {
            Object1 o1 = new Object1();
            o1.setF1("in");
            o1.setF2(1);
            Object3 o3 = new Object3();
            o3.setF1("out");
            o3.setF2(2);
            o3.setO1(o1);
            Map<String, Object> map = ObjectUtils.cast(ObjectUtils.toJavaObject(o3));
            assertThat(map).containsEntry("f1", "out").containsEntry("f2", 2);
            assertThat(map.get("o1")).isNotNull()
                    .hasFieldOrPropertyWithValue("f1", "in")
                    .hasFieldOrPropertyWithValue("f2", 1);
        }

        @Test
        @DisplayName("当对象属性为自定义对象且存在别名时，转换成合适的 Map 对象")
        void shouldReturnAliaMapWhenAttributesHaveCustomObject() {
            Object5 o5 = new Object5();
            o5.setFooBar("out");
            Map<String, Object> map = ObjectUtils.cast(ObjectUtils.toJavaObject(o5));
            assertThat(map).containsEntry("foo_bar", "out");
        }
    }

    @Nested
    @DisplayName("测试方法: toCustomObject(Object obj, Type type)")
    class TestToCustomObject {
        @Test
        @DisplayName("当输入为 null 时，返回 null")
        void shouldReturnNull() {
            Object actual = ObjectUtils.toCustomObject(null, Object.class);
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("当输入为基础类型时，返回基础类型")
        void shouldReturnPrimitive() {
            int actual = ObjectUtils.toCustomObject(0L, int.class);
            assertThat(actual).isEqualTo(0);
        }

        @Test
        @DisplayName("当输入为基础类型的包装类时，返回基础类型的包装类")
        void shouldReturnPrimitiveWrapper() {
            Long source = 100L;
            int actual = ObjectUtils.toCustomObject(source, Integer.class);
            assertThat(actual).isEqualTo(100);
        }

        @Test
        @DisplayName("当输入为字符串时，返回字符串")
        void shouldReturnString() {
            String actual = ObjectUtils.toCustomObject("Hello", String.class);
            assertThat(actual).isEqualTo("Hello");
        }

        @Test
        @DisplayName("当输入为二进制数组时，返回二进制数组")
        void shouldReturnBytes() {
            byte[] actual = ObjectUtils.toCustomObject("Hello".getBytes(StandardCharsets.UTF_8), byte[].class);
            assertThat(actual).isEqualTo("Hello".getBytes(StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("当输入为键值对时，返回键值对对应的类型实例")
        void shouldReturnObject1() {
            Map<String, Object> map =
                    MapBuilder.<String, Object>get().put("f1", "v1").put("f2", 2).put("f3", 0.8f).build();
            Object1 actual = ObjectUtils.toCustomObject(map, Object1.class);
            assertThat(actual).returns("v1", Object1::getF1).returns(2, Object1::getF2);
            assertThat(actual.getF3()).isCloseTo(0.8f, within(0.01));
        }

        @Test
        @DisplayName("当输入为列表时，返回列表")
        void shouldReturnList() {
            List<Object> list = Arrays.asList(1, "v2");
            List<Object> actual =
                    ObjectUtils.toCustomObject(list, TypeUtils.parameterized(List.class, new Type[] {Object.class}));
            assertThat(actual).contains(1, "v2");
        }

        @Test
        @DisplayName("当输入为数组时，返回列表")
        void shouldReturnListWhenInputIsArray() {
            Object[] array = new Object[] {1, "v2"};
            List<Object> actual =
                    ObjectUtils.toCustomObject(array, TypeUtils.parameterized(List.class, new Type[] {Object.class}));
            assertThat(actual).contains(1, "v2");
        }

        @Test
        @DisplayName("当对象属性和自定义对象匹配时，转换成合适的自定义对象")
        void shouldReturnObject3WhenAttributesHaveCustomObjectValues() {
            Map<String, Object> map = MapBuilder.<String, Object>get()
                    .put("f1", "out")
                    .put("f2", 2)
                    .put("o1", MapBuilder.<String, Object>get().put("f1", "in").put("f2", 1).build())
                    .build();
            Object3 obj3 = ObjectUtils.toCustomObject(map, Object3.class);
            assertThat(obj3).returns("out", Object3::getF1).returns(2, Object3::getF2);
            assertThat(obj3.getO1()).isNotNull().returns("in", Object1::getF1).returns(1, Object1::getF2);
        }

        @Test
        @DisplayName("当对象属性和自定义对象存在别名关系时，转换成合适的自定义对象")
        void shouldReturnObject5WhenAttributesHaveCustomObjectValues() {
            Map<String, Object> map = MapBuilder.<String, Object>get()
                .put("foo_bar", "out")
                .build();
            Object5 obj5 = ObjectUtils.toCustomObject(map, Object5.class);
            assertThat(obj5).returns("out", Object5::getFooBar);
        }

        @Test
        @DisplayName("当创建的对象为泛型对象时，正确返回创建的对象")
        void shouldReturnCorrectGenericObject() {
            Map<String, Object> o1 = MapBuilder.<String, Object>get().put("f1", "Hello").put("f2", 1).build();
            Map<String, Object> o2 = MapBuilder.<String, Object>get().put("f1", "World").put("f2", 2).build();
            Map<String, Object> o4 =
                    MapBuilder.<String, Object>get().put("d1", o1).put("d2", o2).put("d3", "all").build();
            Object actual = ObjectUtils.toCustomObject(o4,
                    TypeUtils.parameterized(Object4.class, new Type[] {Object1.class, Object2.class}));
            assertThat(actual).isInstanceOf(Object4.class);
            Object4<Object1, Object2> a4 = ObjectUtils.cast(actual);
            assertThat(a4).returns("all", Object4::getD3);
            assertThat(a4.getD1()).isNotNull().isInstanceOf(Object1.class);
            Object1 a1 = ObjectUtils.cast(a4.getD1());
            assertThat(a1).returns("Hello", Object1::getF1).returns(1, Object1::getF2);
            assertThat(a4.getD2()).isNotNull().isInstanceOf(Object2.class);
            Object2 a2 = ObjectUtils.cast(a4.getD2());
            assertThat(a2).returns("World", Object2::getF1).returns(2, Object2::getF2);
        }
    }
}
