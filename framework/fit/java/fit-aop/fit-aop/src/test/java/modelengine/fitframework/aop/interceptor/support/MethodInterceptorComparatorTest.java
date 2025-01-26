/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.support;

import static modelengine.fitframework.annotation.Order.HIGH;
import static modelengine.fitframework.annotation.Order.LOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.aop.interceptor.MethodInterceptor;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.annotation.support.DefaultAnnotationMetadataResolver;
import modelengine.fitframework.runtime.FitRuntime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link MethodInterceptorComparator} 的单元测试。
 *
 * @author 詹高扬
 * @since 2022-07-28
 */
@DisplayName("测试 MethodInterceptorComparator")
public class MethodInterceptorComparatorTest {
    private List<MethodInterceptor> methodInterceptors;
    private MethodInterceptor methodInterceptor1;
    private MethodInterceptor methodInterceptor2;
    private MethodInterceptor methodInterceptor3;
    private MethodInterceptor methodInterceptor4;
    private MethodInterceptor methodInterceptor5;

    @Nested
    @DisplayName("在添加五种不同类型拦截器之后")
    class AfterAddMultiInterceptors {
        @BeforeEach
        void setup() throws NoSuchMethodException {
            AdvisorClass1 advisorClass1 = mock(AdvisorClass1.class);
            BeanFactory advisorClass1Factory = mock(BeanFactory.class);
            when(advisorClass1Factory.get()).thenReturn(advisorClass1);
            Method before1 = AdvisorClass1.class.getDeclaredMethod("before1");
            Method after1 = AdvisorClass1.class.getDeclaredMethod("after1");
            Method around1 = AdvisorClass1.class.getDeclaredMethod("around1");
            Method afterReturning1 = AdvisorClass1.class.getDeclaredMethod("afterReturning1");
            Method afterThrowing1 = AdvisorClass1.class.getDeclaredMethod("afterThrowing1");
            methodInterceptor2 = new BeforeInterceptor(advisorClass1Factory, before1);
            methodInterceptor3 = new AfterInterceptor(advisorClass1Factory, after1);
            methodInterceptor5 = new AfterThrowingInterceptor(advisorClass1Factory, afterThrowing1);
            methodInterceptor4 = new AfterReturningInterceptor(advisorClass1Factory, afterReturning1);
            methodInterceptor1 = new AroundInterceptor(advisorClass1Factory, around1);
            methodInterceptors = new ArrayList<>();
            methodInterceptors.add(methodInterceptor1);
            methodInterceptors.add(methodInterceptor2);
            methodInterceptors.add(methodInterceptor3);
            methodInterceptors.add(methodInterceptor4);
            methodInterceptors.add(methodInterceptor5);
            BeanContainer beanContainer = mock(BeanContainer.class);
            FitRuntime runtime = mock(FitRuntime.class);
            when(beanContainer.runtime()).thenReturn(runtime);
            when(runtime.resolverOfAnnotations()).thenReturn(new DefaultAnnotationMetadataResolver());
        }

        @Test
        @DisplayName("当对拦截器进行排序时，返回顺序：around、before、after、afterReturn、afterThrow")
        void shouldReturnInterceptorByType() {
            List<MethodInterceptor> orderedInterceptors =
                    methodInterceptors.stream().sorted(new MethodInterceptorComparator()).collect(Collectors.toList());

            assertThat(orderedInterceptors).containsExactly(methodInterceptor1,
                    methodInterceptor2,
                    methodInterceptor3,
                    methodInterceptor4,
                    methodInterceptor5);
        }

        @Nested
        @DisplayName("在添加优先级更高的不同切面的前置拦截器之后")
        class AfterAddHigherBeforeMethodInterceptorOfOtherClass {
            private BeforeInterceptor methodInterceptor6;

            @BeforeEach
            void setup() throws NoSuchMethodException {
                AdvisorClass2 advisorClass2 = mock(AdvisorClass2.class);
                Method before2 = AdvisorClass2.class.getDeclaredMethod("before2");
                this.methodInterceptor6 = Mockito.mock(BeforeInterceptor.class);
                when(this.methodInterceptor6.getAdvisorMethod()).thenReturn(before2);
                when(this.methodInterceptor6.getAdvisorTarget()).thenReturn(advisorClass2);
                methodInterceptors.add(this.methodInterceptor6);
            }

            @Test
            @DisplayName("当对拦截器进行排序时，返回顺序：around、beforeHigherOrder、before、after、afterReturn、afterThrow")
            void shouldReturnHigherOrderInterceptorBeforeOtherSameTypeInterceptor() {
                List<MethodInterceptor> methodInterceptorList = methodInterceptors.stream()
                        .sorted(new MethodInterceptorComparator())
                        .collect(Collectors.toList());
                assertThat(methodInterceptorList).containsExactly(methodInterceptor1,
                        this.methodInterceptor6,
                        methodInterceptor2,
                        methodInterceptor3,
                        methodInterceptor4,
                        methodInterceptor5);
            }
        }

        @Nested
        @DisplayName("在添加优先级更低的不同切面的环绕拦截器之后")
        class AfterAddLowerAroundMethodInterceptorOfOtherClass {
            private AroundInterceptor methodInterceptor7;

            @BeforeEach
            void setup() throws NoSuchMethodException {
                AdvisorClass2 advisorClass2 = mock(AdvisorClass2.class);
                Method before2 = AdvisorClass2.class.getDeclaredMethod("around2");
                this.methodInterceptor7 = Mockito.mock(AroundInterceptor.class);
                when(this.methodInterceptor7.getAdvisorMethod()).thenReturn(before2);
                when(this.methodInterceptor7.getAdvisorTarget()).thenReturn(advisorClass2);
                methodInterceptors.add(this.methodInterceptor7);
            }

            @Test
            @DisplayName("当对拦截器进行排序时，返回顺序：around、aroundLowerOrder、before、after、afterReturn、afterThrow")
            void shouldReturnLowerOrderInterceptorAfterOtherSameTypeInterceptor() {
                List<MethodInterceptor> methodInterceptorList = methodInterceptors.stream()
                        .sorted(new MethodInterceptorComparator())
                        .collect(Collectors.toList());
                assertThat(methodInterceptorList).containsExactly(methodInterceptor1,
                        this.methodInterceptor7,
                        methodInterceptor2,
                        methodInterceptor3,
                        methodInterceptor4,
                        methodInterceptor5);
            }
        }
    }

    /**
     * 测试代理的拦截方法类1。
     */
    public static class AdvisorClass1 {
        /**
         * 表示一个方法前置拦截器。
         */
        public void before1() {}

        /**
         * 表示一个方法后置拦截器。
         */
        public void after1() {}

        /**
         * 表示一个方法异常拦截器。
         */
        public void afterThrowing1() {}

        /**
         * 表示一个方法返回拦截器。
         */
        public void afterReturning1() {}

        /**
         * 表示一个方法环绕拦截器。
         */
        public void around1() {}
    }

    /**
     * 测试代理的拦截方法类2。
     */
    @Order(HIGH)
    public static class AdvisorClass2 {
        /**
         * 表示一个方法前置拦截器。
         */
        public void before2() {}

        /**
         * 表示一个方法环绕拦截器。
         */
        @Order(LOW)
        public void around2() {}
    }
}
