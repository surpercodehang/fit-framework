/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;
import java.util.function.Function;

@DisplayName("测试异常类")
class ExceptionTest {
    private abstract static class AbstractTest<T extends IocException> {
        private final Function<String, T> creatorWithMessage;
        private final Function<Throwable, T> creatorWithCause;
        private final BiFunction<String, Throwable, T> creatorWithMessageAndCause;

        private AbstractTest(Function<String, T> creatorWithMessage, Function<Throwable, T> creatorWithCause,
                BiFunction<String, Throwable, T> creatorWithMessageAndCause) {
            this.creatorWithMessage = creatorWithMessage;
            this.creatorWithCause = creatorWithCause;
            this.creatorWithMessageAndCause = creatorWithMessageAndCause;
        }

        @Test
        @DisplayName("当只提供异常信息时，异常中包含的信息正确")
        void should_return_correct_values_when_only_message() {
            T exception = this.creatorWithMessage.apply("test message");
            assertEquals("test message", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("当只提供异常原因时，异常中包含的信息正确")
        void should_return_correct_values_when_only_cause() {
            IllegalArgumentException cause = new IllegalArgumentException("test cause");
            T exception = this.creatorWithCause.apply(cause);
            assertEquals(cause.toString(), exception.getMessage());
            assertSame(cause, exception.getCause());
        }

        @Test
        @DisplayName("当同时提供了异常信息和原因时，异常中包含的信息正确")
        void should_return_correct_values_when_both_message_and_cause() {
            IllegalArgumentException cause = new IllegalArgumentException("test cause");
            T exception = this.creatorWithMessageAndCause.apply("test message", cause);
            assertEquals("test message", exception.getMessage());
            assertSame(cause, exception.getCause());
        }
    }

    @Nested
    @DisplayName("测试 BeanCreationException 类")
    class BeanCreationExceptionTest extends AbstractTest<BeanCreationException> {
        private BeanCreationExceptionTest() {
            super(BeanCreationException::new, BeanCreationException::new, BeanCreationException::new);
        }
    }

    @Nested
    @DisplayName("测试 BeanDefinitionException 类")
    class BeanDefinitionExceptionTest extends AbstractTest<BeanDefinitionException> {
        private BeanDefinitionExceptionTest() {
            super(BeanDefinitionException::new, BeanDefinitionException::new, BeanDefinitionException::new);
        }
    }

    @Nested
    @DisplayName("测试 BeanNotFoundException 类")
    class BeanNotFoundExceptionTest extends AbstractTest<BeanNotFoundException> {
        private BeanNotFoundExceptionTest() {
            super(BeanNotFoundException::new, BeanNotFoundException::new, BeanNotFoundException::new);
        }
    }

    @Nested
    @DisplayName("测试 AmbiguousBeanException 类")
    class AmbiguousBeanExceptionTest extends AbstractTest<AmbiguousBeanException> {
        private AmbiguousBeanExceptionTest() {
            super(AmbiguousBeanException::new, AmbiguousBeanException::new, AmbiguousBeanException::new);
        }
    }

    @Nested
    @DisplayName("测试 DependencyNotFoundException 类")
    class DependencyNotFoundExceptionTest extends AbstractTest<DependencyNotFoundException> {
        private DependencyNotFoundExceptionTest() {
            super(DependencyNotFoundException::new, DependencyNotFoundException::new, DependencyNotFoundException::new);
        }
    }

    @Nested
    @DisplayName("测试 CircularDependencyException 类")
    class CircularDependencyExceptionTest extends AbstractTest<CircularDependencyException> {
        private CircularDependencyExceptionTest() {
            super(CircularDependencyException::new, CircularDependencyException::new, CircularDependencyException::new);
        }
    }

    @Nested
    @DisplayName("测试 CircularDependencyException 类")
    class UnresolvableDependencyExceptionTest extends AbstractTest<UnresolvableDependencyException> {
        private UnresolvableDependencyExceptionTest() {
            super(UnresolvableDependencyException::new,
                    UnresolvableDependencyException::new,
                    UnresolvableDependencyException::new);
        }
    }
}
