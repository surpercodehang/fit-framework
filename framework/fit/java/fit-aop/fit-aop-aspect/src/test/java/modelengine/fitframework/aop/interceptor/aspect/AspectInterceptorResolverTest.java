/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.aop.interceptor.MethodInterceptor;
import modelengine.fitframework.aop.interceptor.MethodInterceptorResolver;
import modelengine.fitframework.aop.interceptor.aspect.interceptor.AspectInterceptorResolver;
import modelengine.fitframework.aop.interceptor.aspect.test.TestBeanContainer;
import modelengine.fitframework.aop.interceptor.aspect.test.TestExecutionAspect;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService1;
import modelengine.fitframework.aop.interceptor.support.BeforeInterceptor;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

/**
 * {@link AspectInterceptorResolver} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-05-15
 */
public class AspectInterceptorResolverTest {
    private Method m1;

    private Method before1;

    @BeforeEach
    void setup() throws NoSuchMethodException {
        this.m1 = TestService1.class.getDeclaredMethod("m1");
        this.before1 = TestExecutionAspect.class.getDeclaredMethod("before1");
    }

    @AfterEach
    void teardown() {
        this.m1 = null;
        this.before1 = null;
    }

    @Nested
    @DisplayName("当存在带有 @Aspect 注解的切面定义类时")
    class GivenClassWithAspectAnnotation {
        @Test
        @DisplayName("当 @Before 定义的表达式匹配了一个 bean 的方法时，则生成一个前置方法拦截器")
        void givenQualifiedBeforeAnnotationThenReturnBeforeInterceptor() throws NoSuchMethodException {
            BeanContainer container = new TestBeanContainer();
            MethodInterceptorResolver resolver = new AspectInterceptorResolver();
            BeanMetadata beanMetadata = mock(BeanMetadata.class);
            when(beanMetadata.container()).thenReturn(container);
            Object bean = new TestService1();
            List<MethodInterceptor> methodInterceptors = resolver.resolve(beanMetadata, bean);
            assertThat(methodInterceptors).hasSize(1);

            MethodInterceptor methodInterceptor0 = methodInterceptors.get(0);
            assertThat(methodInterceptor0).isInstanceOf(BeforeInterceptor.class);
            BeforeInterceptor beforeInterceptor = ObjectUtils.cast(methodInterceptor0);
            assertThat(beforeInterceptor.getAdvisorMethod()).isEqualTo(AspectInterceptorResolverTest.this.before1);
            assertThat(beforeInterceptor.getAdvisorTarget()).isInstanceOf(TestExecutionAspect.class);
            assertThat(beforeInterceptor.getPointCut().methods()).hasSize(1)
                    .containsExactly(AspectInterceptorResolverTest.this.m1);
        }
    }
}
