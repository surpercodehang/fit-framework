/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.issue;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.ioc.AbstractBeanContainerTest;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanCreationException;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.CircularDependencyException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@DisplayName("循环依赖测试")
class CircularDependencyTest extends AbstractBeanContainerTest {
    @Component
    static class Bean1 {
        private final Bean2 bean2;

        Bean1(Bean2 bean2) {
            this.bean2 = bean2;
        }

        Bean2 bean() {
            return this.bean2;
        }
    }

    @Component
    static class Bean2 {
        private final Bean1 bean1;

        Bean2(Bean1 bean1) {
            this.bean1 = bean1;
        }

        Bean1 bean() {
            return this.bean1;
        }
    }

    @Component
    static class Bean3 {
        @Fit
        private Bean4 bean4;
    }

    @Component
    static class Bean4 {
        @Fit
        private Bean3 bean3;
    }

    @Test
    @DisplayName("当通过构造方法产生循环依赖时，抛出异常")
    void should_throw_when_circular_depended_by_constructor() {
        BeanContainer container = container();
        container.registry().register(Bean1.class);
        container.registry().register(Bean2.class);
        Optional<BeanFactory> factory = container.factory(Bean1.class);
        assertTrue(factory.isPresent());

        BeanCreationException exception = assertThrows(BeanCreationException.class, factory.get()::get);
        assertTrue(exception.getMessage().startsWith("Failed to create bean. [name=$Fit$bean1"));

        assertNotSame(exception, exception.getCause());
        assertTrue(exception.getCause() instanceof BeanCreationException);
        BeanCreationException bean2Exception = (BeanCreationException) exception.getCause();
        assertTrue(bean2Exception.getMessage().startsWith("Failed to create bean. [name=$Fit$bean2"));

        assertNotSame(bean2Exception, bean2Exception.getCause());
        assertTrue(bean2Exception.getCause() instanceof BeanCreationException);
        BeanCreationException bean1Exception = (BeanCreationException) bean2Exception.getCause();
        assertTrue(bean1Exception.getMessage().startsWith("Failed to create bean. [name=$Fit$bean1"));

        assertNotSame(bean1Exception, bean1Exception.getCause());
        assertTrue(bean1Exception.getCause() instanceof CircularDependencyException);
    }

    @Test
    @DisplayName("当通过字段产生循环依赖时，不抛出异常")
    void should_resolve_dependency_when_depended_by_field() {
        BeanContainer container = container();
        container.registry().register(Bean3.class);
        container.registry().register(Bean4.class);

        Optional<BeanFactory> factory = container.factory(Bean3.class);
        assertTrue(factory.isPresent());

        Bean3 bean3 = factory.get().get();

        assertSame(bean3, bean3.bean4.bean3);
    }
}
