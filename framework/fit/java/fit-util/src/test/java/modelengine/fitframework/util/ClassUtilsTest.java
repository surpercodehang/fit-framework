/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.util.data.Child;
import modelengine.fitframework.util.data.Parent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URL;

/**
 * {@link ClassUtils} 的单元测试。
 *
 * @author 季聿阶
 * @since 2021-02-24
 */
class ClassUtilsTest {
    @Nested
    @DisplayName("when isAssignableFrom(targetClass, fromClass)")
    class WhenIsAssignableFrom {
        @SuppressWarnings("ConstantConditions")
        @Nested
        @DisplayName("given target class is null")
        class GivenTargetClassIsNull {
            private final Class<?> targetClass = null;

            @Test
            @DisplayName("and from class is not null then return false")
            void andFromClassIsNotNullThenReturnFalse() {
                boolean result = ClassUtils.isAssignableFrom(this.targetClass, Object.class);
                assertThat(result).isFalse();
            }

            @Test
            @DisplayName("and from class is null then return false")
            void andFromClassIsNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ClassUtils.isAssignableFrom(this.targetClass, null),
                                IllegalArgumentException.class);
                assertThat(exception).isNotNull().hasMessage("The fromClass cannot be null.");
            }
        }

        @Nested
        @DisplayName("given target class is Parent.class")
        class GivenTargetClassIsParent {
            private final Class<?> targetClass = Parent.class;

            @Test
            @DisplayName("and from class is Parent.class then return true")
            void andFromClassIsParentThenReturnTrue() {
                boolean result = ClassUtils.isAssignableFrom(this.targetClass, Parent.class);
                assertThat(result).isTrue();
            }

            @Test
            @DisplayName("and from class is Child.class then return true")
            void andFromClassIsChildThenReturnTrue() {
                boolean result = ClassUtils.isAssignableFrom(this.targetClass, Child.class);
                assertThat(result).isTrue();
            }

            @Test
            @DisplayName("and from class is null then throw IllegalArgumentException")
            void andFromClassIsNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ClassUtils.isAssignableFrom(this.targetClass, null),
                                IllegalArgumentException.class);
                assertThat(exception).isNotNull().hasMessage("The fromClass cannot be null.");
            }
        }

        @Nested
        @DisplayName("given target class is Child.class")
        class GivenTargetClassIsChild {
            private final Class<?> targetClass = Child.class;

            @Test
            @DisplayName("and from class is Parent.class then return false")
            void andFromClassIsParentThenReturnFalse() {
                boolean result = ClassUtils.isAssignableFrom(this.targetClass, Parent.class);
                assertThat(result).isFalse();
            }

            @Test
            @DisplayName("and from class is Child.class then return true")
            void andFromClassIsChildThenReturnTrue() {
                boolean result = ClassUtils.isAssignableFrom(this.targetClass, Child.class);
                assertThat(result).isTrue();
            }

            @Test
            @DisplayName("and from class is null then throw IllegalArgumentException")
            void andFromClassIsNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ClassUtils.isAssignableFrom(this.targetClass, null),
                                IllegalArgumentException.class);
                assertThat(exception).isNotNull().hasMessage("The fromClass cannot be null.");
            }
        }
    }

    @Nested
    @DisplayName("测试方法：isLambda(Class<?> clazz)")
    class WhenIsLambda {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("当 clazz 为 null 时，结果为 false")
        void givenClazzIsNullThenReturnFalse() {
            boolean actual = ClassUtils.isLambda(null);
            assertThat(actual).isFalse();
        }

        @Nested
        @DisplayName("当 clazz 不为 null 时")
        class WhenClazzIsNotNull {
            @Test
            @DisplayName("当 clazz 是一个用户定义的类时，结果为 false")
            void givenClazzIsUserDefinedThenReturnFalse() {
                Class<?> clazz = TestAnotherClass.class;
                boolean actual = ClassUtils.isLambda(clazz);
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("当 clazz 的父类不是 Object.class 时，结果为 false")
            void givenClazzParentIsNotObjectThenReturnFalse() {
                Class<?> clazz = TestChild.class;
                boolean actual = ClassUtils.isLambda(clazz);
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("当 clazz 没有实现接口时，结果为 false")
            void givenClazzNotImplementInterfaceThenReturnFalse() {
                Class<?> clazz = TestParent.class;
                boolean actual = ClassUtils.isLambda(clazz);
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("当 clazz 是 Lambda 表示式创建的类时，结果为 true")
            void givenClazzIsLambdaThenReturnTrue() {
                TestLambda lambda = () -> false;
                boolean actual = ClassUtils.isLambda(lambda.getClass());
                assertThat(actual).isTrue();
            }
        }

        /**
         * 测试父类。
         */
        class TestParent {}

        /**
         * 测试子类。
         */
        class TestChild extends TestParent {}

        /**
         * 测试实现了接口的类。
         */
        class TestAnotherClass implements TestLambda {
            @Override
            public boolean test() {
                return false;
            }
        }
    }

    @Nested
    @DisplayName("测试方法：locateOfProtectionDomain(Class<?> clazz)")
    class TestLocateOfProtectionDomain {
        @Test
        @DisplayName("当 clazz 是一个用户定义的类时，返回该类的保护域")
        void givenClazzIsUserDefinedThenReturnProtectionDomain() {
            Class<?> clazz = this.getClass();
            final URL url = ClassUtils.locateOfProtectionDomain(clazz);
            final URL resource = this.getClass().getResource("");
            assertThat(url.getPath()).matches(ele -> resource.getPath().startsWith(ele));
        }

        @Test
        @DisplayName("当 clazz 是 Object 类时，抛出异常")
        void givenClazzIsObjectThenThrowException() {
            Class<?> clazz = Object.class;
            IllegalStateException cause =
                    catchThrowableOfType(() -> ClassUtils.locateOfProtectionDomain(clazz), IllegalStateException.class);
            assertThat(cause).isNotNull();
        }
    }

    @Nested
    @DisplayName("测试方法：tryLoadClass(ClassLoader loader, String className)")
    class TestTryLoadClass {
        @Test
        @DisplayName("当提供类加载器和待加载的类全包名，返回对应类的 Class")
        void givenClassLoaderAndClassNameThenReturnClass() {
            final ClassLoader classLoader = this.getClass().getClassLoader();
            final String className = TestLambda.class.getName();
            final Class<?> tryLoadClass = ClassUtils.tryLoadClass(classLoader, className);
            assertThat(tryLoadClass).isEqualTo(TestLambda.class);
        }

        @Test
        @DisplayName("当提供类加载器和待加载的类简短名称，返回 null")
        void givenClassLoaderAndSimpleClassNameThenReturnNull() {
            final ClassLoader classLoader = this.getClass().getClassLoader();
            final String className = TestLambda.class.getSimpleName();
            final Class<?> tryLoadClass = ClassUtils.tryLoadClass(classLoader, className);
            assertThat(tryLoadClass).isNull();
        }
    }

    /**
     * 测试 Lambda。
     */
    @FunctionalInterface
    interface TestLambda {
        /**
         * 测试方法。
         *
         * @return 表示测试返回值。
         */
        boolean test();
    }
}
