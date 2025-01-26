/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.exception.FieldVisitException;
import modelengine.fitframework.exception.MethodInvocationException;
import modelengine.fitframework.exception.MethodNotFoundException;
import modelengine.fitframework.exception.ObjectInstantiationException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * {@link ReflectionUtils} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-01-17
 */
public class ReflectionUtilsTest {
    @AfterEach
    void teardown() {
        MockClass.f2 = 0;
    }

    /**
     * 目标方法：{@link ReflectionUtils#getDeclaredConstructor(Class, Class[])}。
     */
    @Nested
    @DisplayName("测试方法: getDeclaredConstructor(Class<T> clazz, Class<?>... parameterTypes)")
    class TestGetDeclaredConstructor {
        @Test
        @DisplayName("当 class 类型为 null，入参类型为空时，抛出参数异常")
        void givenNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> ReflectionUtils.getDeclaredConstructor(null),
                            IllegalArgumentException.class);
            assertThat(exception).hasMessage("The class to detect constructor cannot be null.");
        }

        @Test
        @DisplayName("当 class 类型为 MockClass，入参类型为空时，返回该类的默认空参构造方法")
        void givenMockClassClassAndNoParamTypesThenReturnDefaultConstructor()
                throws InvocationTargetException, InstantiationException, IllegalAccessException {
            Constructor<MockClass> actual = ReflectionUtils.getDeclaredConstructor(MockClass.class);
            MockClass actualMockClass = actual.newInstance();
            assertThat(actualMockClass).isNotNull()
                    .hasFieldOrPropertyWithValue("f3", 0L)
                    .hasFieldOrPropertyWithValue("f4", 0L);
        }

        @Test
        @DisplayName("当 class 类型为 MockClass，入参类型与实际入参类型不匹配时，抛出方法未找到异常")
        void givenMockClassClassAndIntegerParamTypeThenThrowException() {
            MethodNotFoundException exception =
                    catchThrowableOfType(() -> ReflectionUtils.getDeclaredConstructor(MockClass.class, Integer.class),
                            MethodNotFoundException.class);
            assertThat(exception).isNotNull().getCause().isInstanceOf(NoSuchMethodException.class);
        }
    }

    @Nested
    @DisplayName("测试方法: lookupField(Class<?> clazz, String name)")
    class TestLookupField {
        private static final String HASH = "hash";

        @Test
        @DisplayName("当提供字符串类和哈希字段名称时, 返回字符串的哈希字段")
        void given2ParamsThenReturnField() {
            final Field field = ReflectionUtils.lookupField(String.class, HASH);
            assertThat(field).isNotNull();
            assertThat(field.getName()).isEqualTo(HASH);
        }

        @Test
        @DisplayName("当提供字符串类、哈希字段名称、正确的哈希字段类型时, 返回字符串的哈希字段")
        void given3ParamsThenReturnField() {
            final Field field = ReflectionUtils.lookupField(String.class, HASH, int.class);
            assertThat(field).isNotNull();
            assertThat(field.getName()).isEqualTo(HASH);
        }

        @Test
        @DisplayName("当提供字符串类、不存在的该类字段名称时，返回为 null")
        void givenUnNormalFieldNameThenReturnNull() {
            final Field field = ReflectionUtils.lookupField(String.class, HASH + "1");
            assertThat(field).isNull();
        }
    }

    @Nested
    @DisplayName("测试方法: lookupMethod(Class<?> clazz, String name)")
    class TestLookupMethod {
        private static final String HASHCODE = "hashCode";

        @Test
        @DisplayName("当提供字符串类、存在的方法名时，返回为字符串的该方法")
        void givenNormalFieldNameThenReturnMethod() {
            final Method method = ReflectionUtils.lookupMethod(String.class, HASHCODE);
            assertThat(method).isNotNull();
            assertThat(method.getName()).isEqualTo(HASHCODE);
        }

        @Test
        @DisplayName("当提供字符串类、不存在的方法名时，返回为 null")
        void givenUnNormalFieldNameThenReturnNull() {
            final Method method = ReflectionUtils.lookupMethod(String.class, HASHCODE + "1");
            assertThat(method).isNull();
        }
    }

    /**
     * 目标方法：{@link ReflectionUtils#getDeclaredConstructors(Class)}。
     */
    @Nested
    @DisplayName("测试方法: getDeclaredConstructors(Class<T> clazz)")
    class TestGetDeclaredConstructors {
        @Test
        @DisplayName("当 class 类型为 null 时，抛出参数异常")
        void givenNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> ReflectionUtils.getDeclaredConstructors(null),
                            IllegalArgumentException.class);
            assertThat(exception).hasMessage("The class to detect constructors cannot be null.");
        }

        @Test
        @DisplayName("当 class 类型为 MockClass 时，返回该类的参构造方法数组")
        void givenMockClassClassThenReturnAllItsConstructors() {
            Constructor<MockClass>[] actual = ReflectionUtils.getDeclaredConstructors(MockClass.class);
            assertThat(actual).isNotNull().hasSize(2);
        }
    }

    /**
     * 目标方法：{@link ReflectionUtils#getDeclaredFields(Class)}。
     */
    @Nested
    @DisplayName("测试方法: getDeclaredFields(Class<?> clazz)")
    class TestGetDeclaredFields {
        @Test
        @DisplayName("当 class 类型为 null 时，抛出参数异常")
        void givenNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> ReflectionUtils.getDeclaredFields(null), IllegalArgumentException.class);
            assertThat(exception).hasMessage("The class to detect fields cannot be null.");
        }

        @Test
        @DisplayName("当 class 类型为 null，返回该类的字段数组")
        void givenMockClassClassThenReturnAllItsFields() {
            Field[] actual = ReflectionUtils.getDeclaredFields(MockClass.class);
            // 当跑覆盖率测试时，会动态添加属性到类中，因此原本的 3 个属性校验会有偏差。
            assertThat(actual).isNotNull().hasSizeGreaterThanOrEqualTo(3);
        }
    }

    /**
     * 目标方法：{@link ReflectionUtils#getDeclaredMethod(Class, String, Class[])}。
     */
    @Nested
    @DisplayName("测试方法: getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterClasses)")
    class TestGetDeclaredMethod {
        @Test
        @DisplayName("当 class 类型为 null，方法名为空字符串，入参为空数组时，抛出参数异常")
        void givenClassNullThenThrowException() {
            IllegalArgumentException exception = catchThrowableOfType(() -> ReflectionUtils.getDeclaredMethod(null, ""),
                    IllegalArgumentException.class);
            assertThat(exception).hasMessage("The class to detect method cannot be null.");
        }

        @Test
        @DisplayName("当 class 类型为 MockClass，方法名为 null，入参为空数组时，抛出参数异常")
        void givenMockClassAndMethodNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> ReflectionUtils.getDeclaredMethod(MockClass.class, null),
                            IllegalArgumentException.class);
            assertThat(exception).hasMessage("The method name cannot be null.");
        }

        @Test
        @DisplayName("当 class 类型为 MockClass，方法名为该类存在的方法名称，入参为空数组时，返回该方法名的方法")
        void givenMockClassAndGetF3ThenReturnGetF3Method() throws InvocationTargetException, IllegalAccessException {
            Method actual = ReflectionUtils.getDeclaredMethod(MockClass.class, "getF3");
            MockClass owner = new MockClass();
            long actualF3 = (long) actual.invoke(owner);
            assertThat(actualF3).isEqualTo(0);
        }

        @Test
        @DisplayName("当 class 类型为 MockClass，方法名为不存在的方法名称，入参为空数组时，返抛出方法未找到异常")
        void givenMockClassAndNotExistMethodThenThrowException() {
            MethodNotFoundException exception =
                    catchThrowableOfType(() -> ReflectionUtils.getDeclaredMethod(MockClass.class, "notExist"),
                            MethodNotFoundException.class);
            assertThat(exception).isNotNull().getCause().isInstanceOf(NoSuchMethodException.class);
        }
    }

    /**
     * 目标方法：{@link ReflectionUtils#getDeclaredMethods(Class)}。
     */
    @Nested
    @DisplayName("测试方法: getDeclaredMethods(Class<?> clazz)")
    class TestGetDeclaredMethods {
        @Test
        @DisplayName("当 class 类型为 null 时，抛出参数异常")
        void givenClassNullThenThrowException() {
            IllegalArgumentException exception = catchThrowableOfType(() -> ReflectionUtils.getDeclaredMethods(null),
                    IllegalArgumentException.class);
            assertThat(exception).hasMessage("The class to detect methods cannot be null.");
        }

        @Test
        @DisplayName("当 class 类型为 MockClass 时，返回该类已声明的方法数组")
        void givenMockClassThenReturnAllItsMethods() {
            Method[] actual = ReflectionUtils.getDeclaredMethods(MockClass.class);
            assertThat(actual).isNotNull().hasSizeGreaterThanOrEqualTo(2);
        }
    }

    @Nested
    @DisplayName("测试方法: getField")
    class TestGetField {
        /**
         * 目标方法：{@link ReflectionUtils#getField(Object, Field)}。
         */
        @Nested
        @DisplayName("测试方法: getField(Object owner, Field field)")
        class TestGetFieldByField {
            @Test
            @DisplayName("当 owner 类型和 field 类型都为 null时，抛出参数异常")
            void givenFieldNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ReflectionUtils.getField(null, ObjectUtils.<Field>cast(null)),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("The field to get value cannot be null.");
            }

            @Test
            @DisplayName("当 owner 类型为 null，字段为非静态字段时，抛出参数异常")
            void givenOwnerNullAndFieldNotStaticThenThrowException() throws NoSuchFieldException {
                Field f3 = MockClass.class.getDeclaredField("f3");
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ReflectionUtils.getField(null, f3), IllegalArgumentException.class);
                assertThat(exception).hasMessage(
                        "The specified owner is null and the field is an instance field. [field=f3]");
            }

            @Test
            @DisplayName("当 owner 类型为 null，字段为静态字段时，返回该字段的值")
            void givenOwnerNullAndFieldStaticThenReturn0() throws NoSuchFieldException {
                Field f2 = MockClass.class.getDeclaredField("f2");
                long actual = (long) ReflectionUtils.getField(null, f2);
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("当 owner 类型为 MockClass 对象，字段为非静态字段时，返回该字段的值")
            void givenDefaultMockClassAndFieldF3ThenReturn0() throws NoSuchFieldException {
                Field f3 = MockClass.class.getDeclaredField("f3");
                long actual = (long) ReflectionUtils.getField(new MockClass(), f3);
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("当 owner 类型为 MockClass 对象，字段为该类不存在字段时，抛出参数异常")
            void givenDefaultMockClassAndOtherClassFieldThenThrowException() throws NoSuchFieldException {
                Field field = AnotherClass.class.getDeclaredField("f3");
                FieldVisitException exception =
                        catchThrowableOfType(() -> ReflectionUtils.getField(new MockClass(), field),
                                FieldVisitException.class);
                assertThat(exception).isNotNull().getCause().isInstanceOf(IllegalArgumentException.class);
            }
        }

        /**
         * 目标方法：{@link ReflectionUtils#getField(Object, String)}。
         */
        @Nested
        @DisplayName("测试方法 getField(Object owner, String fieldName)")
        class TestGetFieldByName {
            @Test
            @DisplayName("当 owner 类型和 field 类型都为 null时，抛出参数异常")
            void givenFieldNameNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ReflectionUtils.getField(null, ObjectUtils.<String>cast(null)),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("The name of field to get value cannot be blank.");
            }

            @Test
            @DisplayName("当 owner 类型为 MockClass 对象，字段为非静态字段时，返回该字段的值")
            void givenDefaultMockClassAndFieldF3ThenReturn0() {
                long actual = (long) ReflectionUtils.getField(new MockClass(), "f3");
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("当 owner 类型为 MockClass 对象，字段为该类不存在字段时，抛出参数异常")
            void givenDefaultMockClassAndFieldNotExistThenThrowException() {
                FieldVisitException exception =
                        catchThrowableOfType(() -> ReflectionUtils.getField(new MockClass(), "notExist"),
                                FieldVisitException.class);
                assertThat(exception).isNotNull().getCause().isInstanceOf(NoSuchFieldException.class);
            }

            @Test
            @DisplayName("当 owner 类型为 MockClass，字段为非静态字段时，抛出参数异常")
            void givenOwnerIsClassThenThrowException() {
                FieldVisitException exception =
                        catchThrowableOfType(() -> ReflectionUtils.getField(MockClass.class, "f3"),
                                FieldVisitException.class);
                assertThat(exception).isNotNull().getCause().isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Nested
    @DisplayName("测试方法：getInterface(Method method)")
    class TestGetInterface {
        @Test
        @DisplayName("当方法所属类是接口时，返回方法所属的接口")
        void givenMethodBelongToInterfaceThenReturnTheInterface() throws NoSuchMethodException {
            Method method = TestInterface.class.getDeclaredMethod("m1");
            Optional<Class<?>> actual = ReflectionUtils.getInterface(method);
            assertThat(actual).isNotEmpty().get().isEqualTo(TestInterface.class);
        }

        @Nested
        @DisplayName("当方法所属类不是接口时")
        class GivenMethodNotBelongToInterface {
            @Nested
            @DisplayName("当方法所属类实现了接口时")
            class GivenDeclaringClassImplementsInterface {
                @Test
                @DisplayName("当方法覆盖了所属类实现的接口时，返回这个实现的接口")
                void givenMethodOverrideInterfaceMethodThenReturnTheInterface() throws NoSuchMethodException {
                    Method method = TestClass1.class.getDeclaredMethod("m1");
                    Optional<Class<?>> actual = ReflectionUtils.getInterface(method);
                    assertThat(actual).isNotEmpty().get().isEqualTo(TestInterface.class);
                }

                @Test
                @DisplayName("当方法覆盖了所属类实现的接口的父接口时，返回这个实现的接口的父接口")
                void givenMethodOverrideParentInterfaceMethodThenReturnTheParentInterface()
                        throws NoSuchMethodException {
                    Method method = TestClass1.class.getDeclaredMethod("m0");
                    Optional<Class<?>> actual = ReflectionUtils.getInterface(method);
                    assertThat(actual).isNotEmpty().get().isEqualTo(TestParentInterface.class);
                }

                @Test
                @DisplayName("当方法没有覆盖接口时，返回空")
                void givenMethodNotOverrideInterfaceMethodThenReturnEmpty() throws NoSuchMethodException {
                    Method method = TestClass1.class.getDeclaredMethod("m2");
                    Optional<Class<?>> actual = ReflectionUtils.getInterface(method);
                    assertThat(actual).isEmpty();
                }
            }

            @Nested
            @DisplayName("当方法所属类继承了类时")
            class GivenDeclaringClassExtendsClass {
                @Test
                @DisplayName("当方法覆盖了所属类实现的接口时，返回这个实现的接口")
                void givenMethodOverrideInterfaceMethodThenReturnTheInterface() throws NoSuchMethodException {
                    Method method = TestClass2.class.getDeclaredMethod("m1");
                    Optional<Class<?>> actual = ReflectionUtils.getInterface(method);
                    assertThat(actual).isNotEmpty().get().isEqualTo(TestInterface.class);
                }

                @Test
                @DisplayName("当方法覆盖了所属类实现的接口的父接口时，返回这个实现的接口的父接口")
                void givenMethodOverrideParentInterfaceMethodThenReturnTheParentInterface()
                        throws NoSuchMethodException {
                    Method method = TestClass2.class.getDeclaredMethod("m0");
                    Optional<Class<?>> actual = ReflectionUtils.getInterface(method);
                    assertThat(actual).isNotEmpty().get().isEqualTo(TestParentInterface.class);
                }

                @Test
                @DisplayName("当方法没有覆盖接口时，返回空")
                void givenMethodNotOverrideInterfaceMethodThenReturnEmpty() throws NoSuchMethodException {
                    Method method = TestClass2.class.getDeclaredMethod("m2");
                    Optional<Class<?>> actual = ReflectionUtils.getInterface(method);
                    assertThat(actual).isEmpty();
                }
            }
        }
    }

    @Nested
    @DisplayName("测试方法：getInterfaceMethod(Method method)")
    class TestGetInterfaceMethod {
        @Test
        @DisplayName("当方法为接口的方法时，返回方法自身")
        void givenMethodBelongToInterfaceThenReturnMethodItself() throws NoSuchMethodException {
            Method method = TestInterface.class.getDeclaredMethod("m1");
            Optional<Method> actual = ReflectionUtils.getInterfaceMethod(method);
            assertThat(actual).isNotEmpty().get().isEqualTo(method);
        }

        @Nested
        @DisplayName("当方法所属类不是接口时")
        class GivenMethodNotBelongToInterface {
            @Nested
            @DisplayName("当方法所属类实现了接口时")
            class GivenDeclaringClassImplementsInterface {
                @Test
                @DisplayName("当方法覆盖了所属类实现的接口时，返回这个实现的接口的同签名的方法")
                void givenMethodOverrideInterfaceMethodThenReturnTheInterfaceMethod() throws NoSuchMethodException {
                    Method method = TestClass1.class.getDeclaredMethod("m1");
                    Optional<Method> actual = ReflectionUtils.getInterfaceMethod(method);
                    assertThat(actual).isNotEmpty().get().isEqualTo(TestInterface.class.getDeclaredMethod("m1"));
                }

                @Test
                @DisplayName("当方法覆盖了所属类实现的接口的父接口时，返回这个实现的接口的父接口的同签名的方法")
                void givenMethodOverrideParentInterfaceMethodThenReturnTheParentInterfaceMethod()
                        throws NoSuchMethodException {
                    Method method = TestClass1.class.getDeclaredMethod("m0");
                    Optional<Method> actual = ReflectionUtils.getInterfaceMethod(method);
                    assertThat(actual).isNotEmpty().get().isEqualTo(TestParentInterface.class.getDeclaredMethod("m0"));
                }

                @Test
                @DisplayName("当方法没有覆盖接口时，返回空")
                void givenMethodNotOverrideInterfaceMethodThenReturnEmpty() throws NoSuchMethodException {
                    Method method = TestClass1.class.getDeclaredMethod("m2");
                    Optional<Method> actual = ReflectionUtils.getInterfaceMethod(method);
                    assertThat(actual).isEmpty();
                }
            }

            @Nested
            @DisplayName("当方法所属类继承了类时")
            class GivenDeclaringClassExtendsClass {
                @Test
                @DisplayName("当方法覆盖了所属类实现的接口时，返回这个实现的接口的同签名的方法")
                void givenMethodOverrideInterfaceMethodThenReturnTheInterfaceMethod() throws NoSuchMethodException {
                    Method method = TestClass2.class.getDeclaredMethod("m1");
                    Optional<Method> actual = ReflectionUtils.getInterfaceMethod(method);
                    assertThat(actual).isNotEmpty().get().isEqualTo(TestInterface.class.getDeclaredMethod("m1"));
                }

                @Test
                @DisplayName("当方法覆盖了所属类实现的接口的父接口时，返回这个实现的接口的父接口的同签名的方法")
                void givenMethodOverrideParentInterfaceMethodThenReturnTheParentInterfaceMethod()
                        throws NoSuchMethodException {
                    Method method = TestClass2.class.getDeclaredMethod("m0");
                    Optional<Method> actual = ReflectionUtils.getInterfaceMethod(method);
                    assertThat(actual).isNotEmpty().get().isEqualTo(TestParentInterface.class.getDeclaredMethod("m0"));
                }

                @Test
                @DisplayName("当方法没有覆盖接口时，返回空")
                void givenMethodNotOverrideInterfaceMethodThenReturnEmpty() throws NoSuchMethodException {
                    Method method = TestClass2.class.getDeclaredMethod("m2");
                    Optional<Method> actual = ReflectionUtils.getInterfaceMethod(method);
                    assertThat(actual).isEmpty();
                }
            }
        }
    }

    /**
     * 目标方法：{@link ReflectionUtils#getParameters(Executable)}。
     */
    @Nested
    @DisplayName("测试方法: getParameters(T method)")
    class TestGetParameters {
        @Test
        @DisplayName("当指定方法为 null时，抛出参数异常")
        void givenNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> ReflectionUtils.getParameters(null), IllegalArgumentException.class);
            assertThat(exception).hasMessage("The method to get parameters cannot be null.");
        }

        @Test
        @DisplayName("当提供 MockClass 的方法时，返回该方法的参数数组")
        void givenMockClassSetF3MethodThenReturnLongParameter() throws NoSuchMethodException {
            Method method = MockClass.class.getDeclaredMethod("setF3", long.class);
            Parameter[] actual = ReflectionUtils.getParameters(method);
            assertThat(actual).isNotNull().hasSize(1);
            assertThat(actual[0].getName()).isEqualTo("f3");
        }
    }

    /**
     * 目标方法：{@link ReflectionUtils#getProperty(Object, String)}。
     */
    @Nested
    @DisplayName("测试方法: getProperty(Object owner, String name)")
    class TestGetProperty {
        @Test
        @DisplayName("当提供 MockClass 的对象和该类的属性名称，属性有 get方法时时，返回该属性的值")
        void givenGetterThenReturnMethodValue() {
            MockClass mockClass = new MockClass();
            long actual = (long) ReflectionUtils.getProperty(mockClass, "f3");
            assertThat(actual).isEqualTo(0);
        }

        @Test
        @DisplayName("当提供 MockClass 的对象和该类的属性名称，属性没有 get方法时，返回该属性的值")
        void givenNoGetterThenReturnFieldValue() {
            MockClass mockClass = new MockClass();
            long actual = (long) ReflectionUtils.getProperty(mockClass, "f4");
            assertThat(actual).isEqualTo(0);
        }
    }

    /**
     * 目标方法：{@link ReflectionUtils#ignorePrimitiveClass(Class)}。
     */
    @Nested
    @DisplayName("测试方法: ignorePrimitiveClass(Class<?> source)")
    class TestIgnorePrimitiveClass {
        @Test
        @DisplayName("当 class 类型为 null时，抛出参数异常")
        void givenNullThenThrowException() {
            IllegalArgumentException exception = catchThrowableOfType(() -> ReflectionUtils.ignorePrimitiveClass(null),
                    IllegalArgumentException.class);
            assertThat(exception).hasMessage("Source class cannot be null.");
        }

        @Test
        @DisplayName("当 class 类型为 int 时，返回包装类型 Integer.class")
        void givenIntThenReturnInteger() {
            Class<?> actual = ReflectionUtils.ignorePrimitiveClass(int.class);
            assertThat(actual).isEqualTo(Integer.class);
        }

        @Test
        @DisplayName("当 class 类型为 String 时，返回原始类型 String.class")
        void givenStringThenReturnString() {
            Class<?> actual = ReflectionUtils.ignorePrimitiveClass(String.class);
            assertThat(actual).isEqualTo(String.class);
        }
    }

    @Nested
    @DisplayName("测试方法: getPrimitiveDefaultValue(Class<?> source)")
    class TestGetPrimitiveDefaultValue {
        @Test
        @DisplayName("当 class 类型为 null时，抛出参数异常")
        void givenNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> ReflectionUtils.getPrimitiveDefaultValue(null),
                            IllegalArgumentException.class);
            assertThat(exception).hasMessage("Source class cannot be null.");
        }

        @Test
        @DisplayName("当 class 类型为 int 时，返回默认值0")
        void givenIntThenReturnZero() {
            final Object defaultValue = ReflectionUtils.getPrimitiveDefaultValue(int.class);
            assertThat(defaultValue).isEqualTo(0);
        }

        @Test
        @DisplayName("当 class 为用户自定义类型时，返回默认值 null")
        void givenUserClassThenReturnNull() {
            final Object defaultValue = ReflectionUtils.getPrimitiveDefaultValue(this.getClass());
            assertThat(defaultValue).isNull();
        }
    }

    @Nested
    @DisplayName("测试方法: isPrimitive(Class<?> source)")
    class TestIsPrimitive {
        @Test
        @DisplayName("当 class 类型为 null 时，抛出参数异常")
        void givenNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> ReflectionUtils.isPrimitive(null), IllegalArgumentException.class);
            assertThat(exception).hasMessage("Source class cannot be null.");
        }

        @Test
        @DisplayName("当 class 类型为 int 时，返回 true")
        void givenIntThenReturnTrue() {
            final boolean isPrimitive = ReflectionUtils.isPrimitive(int.class);
            assertThat(isPrimitive).isTrue();
        }

        @Test
        @DisplayName("当 class 为用户自定义类型时，返回 false")
        void givenUserClassThenReturnFalse() {
            final boolean isPrimitive = ReflectionUtils.isPrimitive(this.getClass());
            assertThat(isPrimitive).isFalse();
        }
    }

    @Nested
    @DisplayName("测试方法: instantiate")
    class TestInstantiate {
        /**
         * 目标方法：{@link ReflectionUtils#instantiate(Class)}。
         */
        @Nested
        @DisplayName("测试方法: instantiate(Class<T> clazz)")
        class TestInstantiateWithClass {
            @Test
            @DisplayName("当 class 类型为 null 时，抛出参数异常")
            void givenNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ReflectionUtils.instantiate(null), IllegalArgumentException.class);
                assertThat(exception).hasMessage("The class to instantiate new object cannot be null.");
            }

            @Test
            @DisplayName("当 class 类型为 MockClass 时，没有实例化参数，返回该类型的实例")
            void givenMockClassThenReturnDefaultInstance() {
                MockClass actual = ReflectionUtils.instantiate(MockClass.class);
                assertThat(actual).isNotNull()
                        .hasFieldOrPropertyWithValue("f3", 0L)
                        .hasFieldOrPropertyWithValue("f4", 0L);
            }

            @Test
            @DisplayName("当 class 类型为没有无参构造的类型时，抛出异常")
            void givenNoDefaultConstructorErrorThenThrowException() {
                ObjectInstantiationException exception =
                        catchThrowableOfType(() -> ReflectionUtils.instantiate(MoDefaultConstructorError.class),
                                ObjectInstantiationException.class);
                assertThat(exception).isNotNull().getCause().isInstanceOf(NoSuchMethodException.class);
            }
        }

        /**
         * 目标方法：{@link ReflectionUtils#instantiate(Constructor, Object...)}。
         */
        @Nested
        @DisplayName("测试方法: instantiate(Constructor<T> constructor, Object... parameters)")
        class TestInstantiateWithConstructor {
            @Test
            @DisplayName("当构造方法为 null，构造方法参数为空数组时，抛出参数异常")
            void givenConstructorNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ReflectionUtils.instantiate((Constructor<?>) null),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("The constructor to instantiate new object cannot be null.");
            }

            @Test
            @DisplayName("当提供有参构造方法和构造方法参数数值时，返回使用该构造方法构造的实例")
            void givenMockClassAndF3Is1ThenReturnCorrectInstance() throws NoSuchMethodException {
                Constructor<MockClass> constructor = MockClass.class.getDeclaredConstructor(long.class);
                MockClass actual = ReflectionUtils.instantiate(constructor, 1);
                assertThat(actual).isNotNull()
                        .hasFieldOrPropertyWithValue("f3", 1L)
                        .hasFieldOrPropertyWithValue("f4", 0L);
            }

            @Test
            @DisplayName("当提供无参构造方法，但构造方法执行中报异常时，抛出实例化对象异常")
            void givenDefaultConstructorErrorThenThrowException() throws NoSuchMethodException {
                Constructor<DefaultConstructorError> constructor =
                        DefaultConstructorError.class.getDeclaredConstructor();
                ObjectInstantiationException exception =
                        catchThrowableOfType(() -> ReflectionUtils.instantiate(constructor),
                                ObjectInstantiationException.class);
                assertThat(exception).isNotNull().getCause().isInstanceOf(IllegalStateException.class);
            }

            @Test
            @DisplayName("当提供有参构造方法和构造方法参数数值，但构造方法执行中报异常时，抛出实例化对象异常")
            void givenNoDefaultConstructorErrorThenThrowException() throws NoSuchMethodException {
                Constructor<MoDefaultConstructorError> constructor =
                        MoDefaultConstructorError.class.getDeclaredConstructor(int.class);
                ObjectInstantiationException exception =
                        catchThrowableOfType(() -> ReflectionUtils.instantiate(constructor),
                                ObjectInstantiationException.class);
                assertThat(exception).isNotNull().getCause().isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    /**
     * 目标方法：{@link ReflectionUtils#invoke(Object, Method, Object...)}。
     */
    @Nested
    @DisplayName("测试方法: invoke(Object owner, Method method, Object... parameters)")
    class TestInvoke {
        @Test
        @DisplayName("当 owner 和方法都为 null 时，抛出参数异常")
        void givenMethodNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> ReflectionUtils.invoke(null, null), IllegalArgumentException.class);
            assertThat(exception).hasMessage("The method to invoke cannot be null.");
        }

        @Test
        @DisplayName("当 owner 为 null，方法为非静态时，抛出参数异常")
        void givenOwnerNullAndMethodNotStaticThenThrowException() throws NoSuchMethodException {
            Method setF3 = MockClass.class.getDeclaredMethod("setF3", long.class);
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> ReflectionUtils.invoke(null, setF3), IllegalArgumentException.class);
            assertThat(exception).hasMessage(
                    "The specified owner is null and the method is an instance method. [method=setF3]");
        }

        @Test
        @DisplayName("当 owner 为 null，方法为静态时，返回该方法执行后的返回值0")
        void givenOwnerNullAndMethodStaticThenInvokeCorrectly() throws NoSuchMethodException {
            Method getF2 = MockClass.class.getDeclaredMethod("getF2");
            long actual = (long) ReflectionUtils.invoke(null, getF2);
            assertThat(actual).isEqualTo(0);
        }

        @Test
        @DisplayName("当 owner 为 MockClass 对象，方法为非静态时，返回该方法执行后的结果")
        void givenDefaultMockClassAndMethodSetF3ThenInvokeCorrectly() throws NoSuchMethodException {
            Method setF3 = MockClass.class.getDeclaredMethod("setF3", long.class);
            MockClass mockClass = new MockClass();
            ReflectionUtils.invoke(mockClass, setF3, 1);
            assertThat(mockClass).isNotNull()
                    .hasFieldOrPropertyWithValue("f3", 1L)
                    .hasFieldOrPropertyWithValue("f4", 0L);
        }

        @Test
        @DisplayName("当 owner 为 MockClass 对象，方法为非静态但执行抛异常时，抛出方法执行失败异常")
        void givenDefaultMockClassAndExceptionMethodThenThrowException() throws NoSuchMethodException {
            Method exceptionMethod = MockClass.class.getDeclaredMethod("exceptionMethod");
            MockClass mockClass = new MockClass();
            MethodInvocationException exception =
                    catchThrowableOfType(() -> ReflectionUtils.invoke(mockClass, exceptionMethod),
                            MethodInvocationException.class);
            assertThat(exception).isNotNull().getCause().isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("当 owner 为 MockClass 对象，方法为非静态但执行出错时，抛出方法执行失败异常")
        void givenDefaultMockClassAndSetF3WithStringParamThenThrowException() throws NoSuchMethodException {
            Method invokeExceptionMethod = MockClass.class.getDeclaredMethod("invokeExceptionMethod");
            MockClass mockClass = new MockClass();
            MethodInvocationException exception =
                    catchThrowableOfType(() -> ReflectionUtils.invoke(mockClass, invokeExceptionMethod),
                            MethodInvocationException.class);
            assertThat(exception).isNotNull().getCause().isInstanceOf(IllegalArgumentException.class);
        }
    }

    /**
     * 目标方法：{@link ReflectionUtils#invokeWithReturnType(Object, Method, Class, Object...)}。
     */
    @Nested
    @DisplayName(
            "测试方法: invokeWithReturnType(Object owner, Method method, Class<T> returnType, Object... parameters)")
    class TestInvokeWithReturnType {
        @Test
        @DisplayName("当 owner 为 MockClass 对象，方法为非静态，返回类型正确，但方法参数为 null 的数组，返回值为 null")
        void givenDefaultMockClassAndGetStringMethodThenInvokeCorrectly() throws NoSuchMethodException {
            Method getString = MockClass.class.getDeclaredMethod("getString", String.class);
            MockClass mockClass = new MockClass();
            Object[] args = new Object[] {null};
            String actual = ReflectionUtils.invokeWithReturnType(mockClass, getString, String.class, args);
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("当 owner 为 MockClass 对象，方法为非静态，返回类型错误，方法参数正确，抛出调用方法失败异常")
        void givenDefaultMockClassAndGetStringWithMismatchReturnTypeThenInvokeIncorrectly()
                throws NoSuchMethodException {
            Method getString = MockClass.class.getDeclaredMethod("getString", String.class);
            MockClass mockClass = new MockClass();
            MethodInvocationException exception = catchThrowableOfType(() -> ReflectionUtils.invokeWithReturnType(
                    mockClass,
                    getString,
                    Long.class,
                    "Hello"), MethodInvocationException.class);
            assertThat(exception).hasMessage(
                    "Return type is mismatch. [returnType=java.lang.Long, actualType=java.lang.String]");
        }

        @Test
        @DisplayName("当 owner 为 MockClass 对象，方法为非静态，返回类型正确，方法参数正确，返回方法执行后的结果")
        void givenDefaultMockClassAndGetStringWithStringReturnTypeThenInvokeCorrectly() throws NoSuchMethodException {
            Method getString = MockClass.class.getDeclaredMethod("getString", String.class);
            MockClass mockClass = new MockClass();
            String actual = ReflectionUtils.invokeWithReturnType(mockClass, getString, String.class, "Hello");
            assertThat(actual).isEqualTo("Hello");
        }
    }

    @Nested
    @DisplayName("测试方法: setField")
    class TestSetField {
        /**
         * 目标方法：{@link ReflectionUtils#setField(Object, Field, Object)}。
         */
        @Nested
        @DisplayName("测试方法: setField(Object owner, Field field, Object value)")
        class TestSetFieldByField {
            @Test
            @DisplayName("当 owner、字段、字段设置值都为 null时，抛出参数异常")
            void givenFieldNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ReflectionUtils.setField(null, ObjectUtils.<Field>cast(null), null),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("The field to set value cannot be null.");
            }

            @Test
            @DisplayName("当 owner 为 null，字段不为 null，字段设置值为 null时，抛出参数异常")
            void givenOwnerNullAndFieldNotStaticThenThrowException() throws NoSuchFieldException {
                Field f3 = MockClass.class.getDeclaredField("f3");
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ReflectionUtils.setField(null, f3, null),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage(
                        "The specified owner is null and the field is an instance field. [field=f3]");
            }

            @Test
            @DisplayName("当 owner 为 null，字段为静态，字段设置值类型匹配时，字段设置成功")
            void givenOwnerNullAndFieldStaticThenReturnCorrectly() throws NoSuchFieldException {
                Field f2 = MockClass.class.getDeclaredField("f2");
                ReflectionUtils.setField(null, f2, 1);
                assertThat(MockClass.f2).isEqualTo(1);
            }

            @Test
            @DisplayName("当 owner 为 MockClass 对象，字段为非静态，字段设置值类型匹配时，字段设置成功")
            void givenDefaultMockClassAndFieldF3ThenReturnCorrectly() throws NoSuchFieldException {
                Field f3 = MockClass.class.getDeclaredField("f3");
                MockClass mockClass = new MockClass();
                ReflectionUtils.setField(mockClass, f3, 1L);
                assertThat(mockClass.f3).isEqualTo(1);
            }

            @Test
            @DisplayName("当 owner 为 MockClass 对象，字段为非静态，字段设置值类型不匹配时，抛出访问字段失败异常")
            void givenDefaultMockClassAndOtherClassFieldThenThrowException() throws NoSuchFieldException {
                Field field = AnotherClass.class.getDeclaredField("f3");
                FieldVisitException exception =
                        catchThrowableOfType(() -> ReflectionUtils.setField(new MockClass(), field, "Hello"),
                                FieldVisitException.class);
                assertThat(exception).isNotNull().getCause().isInstanceOf(IllegalArgumentException.class);
            }
        }

        /**
         * 目标方法：{@link ReflectionUtils#setField(Object, String, Object)}。
         */
        @Nested
        @DisplayName("测试方法: setField(Object owner, String fieldName, Object value)")
        class TestSetFieldByName {
            @Test
            @DisplayName("当 owner、字段名称、字段设置值都为 null时，抛出参数异常")
            void givenFieldNameNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ReflectionUtils.setField(null, ObjectUtils.<String>cast(null), null),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("The name of field to set value cannot be blank.");
            }

            @Test
            @DisplayName("当 owner 为 MockClass 对象，字段名称为存在名称，字段设置值类型匹配时，字段设置成功")
            void givenDefaultMockClassAndFieldF3ThenReturnCorrectly() {
                MockClass mockClass = new MockClass();
                ReflectionUtils.setField(mockClass, "f3", 1);
                assertThat(mockClass.f3).isEqualTo(1);
            }

            @Test
            @DisplayName("当 owner 为 MockClass 对象，字段名称为不存在名称，字段设置值类型匹配时，抛出访问字段失败异常")
            void givenDefaultMockClassAndFieldNotExistThenThrowException() {
                FieldVisitException exception =
                        catchThrowableOfType(() -> ReflectionUtils.setField(new MockClass(), "notExist", 1),
                                FieldVisitException.class);
                assertThat(exception).isNotNull().getCause().isInstanceOf(NoSuchFieldException.class);
            }
        }
    }

    @Nested
    @DisplayName("测试方法: toLongString(Method method), toShortString(Method method)")
    class TestToLongStringAndToShortString {
        @Test
        @DisplayName("当参数为 null 时，返回值为空字符串")
        void givenNullThenReturnEmpty() {
            String actual1 = ReflectionUtils.toLongString(null);
            String actual2 = ReflectionUtils.toShortString(null);
            assertThat(actual1).isEqualTo(StringUtils.EMPTY);
            assertThat(actual2).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("当参数为存在的方法名称时，返回方法的长和短的方法的完整形式")
        void givenExceptionMethodThenReturnMethodString() throws NoSuchMethodException {
            Method method = MockClass.class.getDeclaredMethod("exceptionMethod");
            String actual1 = ReflectionUtils.toLongString(method);
            String actual2 = ReflectionUtils.toShortString(method);
            assertThat(actual1).isEqualTo(
                    "public long modelengine.fitframework.util.ReflectionUtilsTest$MockClass.exceptionMethod()");
            assertThat(actual2).isEqualTo("MockClass.exceptionMethod()");
        }
    }

    /**
     * 目标方法：{@link ReflectionUtils#setProperty(Object, String, Object)}。
     */
    @Nested
    @DisplayName("测试方法: setProperty(Object owner, String name, Object value)")
    class TestSetProperty {
        @Test
        @DisplayName("当 owner 为 MockClass 对象，属性有 set 方法，字段设置值类型匹配时，属性设置成功")
        void givenSetterThenReturnMethodValue() {
            MockClass mockClass = new MockClass();
            ReflectionUtils.setProperty(mockClass, "f3", 1);
            assertThat(mockClass.f3).isEqualTo(1);
        }

        @Test
        @DisplayName("当 owner 为 MockClass 对象，属性没有 set 方法，字段设置值类型匹配时，属性设置成功")
        void givenNoSetterThenReturnCorrectly() {
            MockClass mockClass = new MockClass();
            ReflectionUtils.setProperty(mockClass, "f4", 1);
            assertThat(mockClass.f4).isEqualTo(1);
        }
    }

    /**
     * 目标方法：{@link ReflectionUtils#toString(Method)}。
     */
    @Nested
    @DisplayName("测试方法: toString(Method method)")
    class TestToString {
        @Test
        @DisplayName("当参数为 null 时，返回值为空字符串")
        void givenNullThenReturnEmpty() {
            String actual = ReflectionUtils.toString(null);
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("当参数为存在的方法，但执行会抛出异常时，返回方法的完整字符串形式")
        void givenExceptionMethodThenReturnMethodString() throws NoSuchMethodException {
            Method method = MockClass.class.getDeclaredMethod("exceptionMethod");
            String actual = ReflectionUtils.toString(method);
            assertThat(actual).isEqualTo(
                    "long modelengine.fitframework.util.ReflectionUtilsTest$MockClass.exceptionMethod()");
        }

        @Test
        @DisplayName("当参数为存在的方法，执行正常，方法参数为 1 个字符串，不提供特定格式时，返回方法的完整字符串形式")
        void givenGetStringThenReturnMethodString() throws NoSuchMethodException {
            Method method = MockClass.class.getDeclaredMethod("getString", String.class);
            String actual = ReflectionUtils.toString(method);
            assertThat(actual).isEqualTo(
                    "String modelengine.fitframework.util.ReflectionUtilsTest$MockClass.getString(String)");
        }

        @Test
        @DisplayName("当参数为存在的方法，执行正常，方法参数为 1 个字符串，提供特定格式时，返回方法的特定字符串形式")
        void givenGetStringAndPatternThenReturnMethodString() throws NoSuchMethodException {
            Method method = MockClass.class.getDeclaredMethod("getString", String.class);
            ReflectionUtils.Pattern includeTypeFalsePattern =
                    new ReflectionUtils.Pattern(false, false, false, false, true);
            String actual = ReflectionUtils.toString(method, includeTypeFalsePattern);
            assertThat(actual).isEqualTo("getString(..)");
        }

        @Test
        @DisplayName("当参数为存在的方法，执行正常，方法参数为 1 个字符串数组时，返回方法的完整字符串形式")
        void givenGetArrayThenReturnMethodString() throws NoSuchMethodException {
            Method method = MockClass.class.getDeclaredMethod("getArray", String[].class);
            String actual = ReflectionUtils.toString(method);
            assertThat(actual).isEqualTo(
                    "String modelengine.fitframework.util.ReflectionUtilsTest$MockClass.getArray(String[])");
        }

        @Test
        @DisplayName("当参数为存在的方法，执行正常，方法参数为 2 个时，返回方法的完整字符串形式")
        void givenGetString2ThenReturnMethodString() throws NoSuchMethodException {
            Method method = MockClass.class.getDeclaredMethod("getString", String.class, String.class);
            String actual = ReflectionUtils.toString(method);
            assertThat(actual).isEqualTo(
                    "String modelengine.fitframework.util.ReflectionUtilsTest$MockClass.getString(String,String)");
        }
    }

    @Nested
    @DisplayName("测试方法: signatureOf(Method method)")
    class TestSignatureOf {
        @Test
        @DisplayName("当提供一个有参方法时，返回方法签名的字符串形式")
        void givenMethodHasParamsThenReturnMethodSignature() throws NoSuchMethodException {
            Method method = MockClass.class.getDeclaredMethod("getString", String.class, String.class);
            String actual = ReflectionUtils.signatureOf(method);
            assertThat(actual).isEqualTo("modelengine.fitframework.util.ReflectionUtilsTest$MockClass"
                    + ".getString(java.lang.String, java.lang.String) : java.lang.String");
        }

        @Test
        @DisplayName("当提供一个无参方法时，返回方法签名的字符串形式")
        void givenMethodNoParamsThenReturnMethodSignature() throws NoSuchMethodException {
            Method method = MockClass.class.getDeclaredMethod("getF3");
            String actual = ReflectionUtils.signatureOf(method);
            assertThat(actual).isEqualTo("modelengine.fitframework.util.ReflectionUtilsTest$MockClass.getF3() : long");
        }
    }

    @Nested
    @DisplayName("测试方法: isCheckedException(Class<?> clazz)")
    class TestIsCheckedException {
        @Test
        @DisplayName("当提供非法参数异常类时，返回 false")
        void givenIllegalArgumentExceptionThenReturnFalse() {
            final boolean checkedException = ReflectionUtils.isCheckedException(IllegalArgumentException.class);
            assertThat(checkedException).isFalse();
        }

        @Test
        @DisplayName("当提供不是异常类时，返回 false")
        void givenNoExceptionThenReturnFalse() {
            final boolean checkedException = ReflectionUtils.isCheckedException(Object.class);
            assertThat(checkedException).isFalse();
        }

        @Test
        @DisplayName("当提供 Error 类子类时，返回 false")
        void givenOutOfMemoryErrorThenReturnFalse() {
            final boolean checkedException = ReflectionUtils.isCheckedException(OutOfMemoryError.class);
            assertThat(checkedException).isFalse();
        }

        @Test
        @DisplayName("当提供 IO 异常类时，返回 true")
        void givenIOExceptionThenReturnTrue() {
            final boolean checkedException = ReflectionUtils.isCheckedException(IOException.class);
            assertThat(checkedException).isTrue();
        }
    }

    @SuppressWarnings("unused")
    static class MockClass {
        private static long f2;

        private long f3;
        private long f4;

        public MockClass() {
            this.f3 = 0;
            this.f4 = 0;
        }

        public MockClass(long f3) {
            this.f3 = f3;
        }

        /**
         * 用于测试的静态方法。
         *
         * @return 表示返回值的 {@code long}。
         */
        public static long getF2() {
            return f2;
        }

        public long getF3() {
            return this.f3;
        }

        public void setF3(long f3) {
            this.f3 = f3;
        }

        /**
         * 用于测试异常返回的方法。
         *
         * @return 表示返回值的 {@code long}。
         */
        public long exceptionMethod() {
            throw new IllegalStateException();
        }

        /**
         * 用于测试 1 个参数的方法。
         *
         * @param str 表示第一个参数 {@link String}。
         * @return 表示返回值的 {@link String}。
         */
        public String getString(String str) {
            return str;
        }

        /**
         * 用于测试数组为参数的方法。
         *
         * @param str 表示参数数组的 {@link String}{@code []}。
         * @return 表示返回值的 {@link String}。
         */
        public String getArray(String[] str) {
            return "";
        }

        /**
         * 用于测试 2 个参数的方法。
         *
         * @param str1 表示第一个参数的 {@link String}。
         * @param str2 表示第二个参数的 {@link String}。
         * @return 表示返回值的 {@link String}。
         */
        public String getString(String str1, String str2) {
            return str1 + str2;
        }

        /**
         * 用于测试方法调用异常的方法。
         *
         * @throws InvocationTargetException 表示方法调用异常。
         */
        public void invokeExceptionMethod() throws InvocationTargetException {
            throw new InvocationTargetException(new IllegalArgumentException());
        }
    }

    @SuppressWarnings("unused")
    static class AnotherClass {
        private long f3;
    }

    static class DefaultConstructorError {
        /**
         * 用于测试默认构造函数抛出异常的情况。
         */
        public DefaultConstructorError() {
            throw new IllegalStateException();
        }
    }

    @SuppressWarnings("unused")
    static class MoDefaultConstructorError {
        /**
         * 用于测试异常构造方法的方法。
         *
         * @param i1 表示第一个参数的 {@code int}。
         */
        public MoDefaultConstructorError(int i1) {
            throw new IllegalStateException();
        }
    }

    /**
     * 测试父接口。
     */
    interface TestParentInterface {
        /**
         * 测试父接口的测试方法。
         */
        void m0();
    }

    /**
     * 测试接口。
     */
    interface TestInterface extends TestParentInterface {
        /**
         * 测试接口的测试方法。
         */
        void m1();
    }

    abstract static class TestAbstractClass implements TestInterface {}

    static class TestClass1 implements TestInterface {
        @Override
        public void m0() {}

        @Override
        public void m1() {}

        void m2() {}
    }

    static class TestClass2 extends TestAbstractClass {
        @Override
        public void m0() {}

        @Override
        public void m1() {}

        void m2() {}
    }
}
