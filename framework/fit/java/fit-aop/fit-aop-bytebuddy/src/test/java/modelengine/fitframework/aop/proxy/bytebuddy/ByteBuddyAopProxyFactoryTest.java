/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.proxy.bytebuddy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fitframework.aop.interceptor.MethodInterceptor;
import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.aop.interceptor.support.AfterInterceptor;
import modelengine.fitframework.aop.interceptor.support.BeforeInterceptor;
import modelengine.fitframework.aop.proxy.AopProxyFactory;
import modelengine.fitframework.aop.proxy.InterceptSupport;
import modelengine.fitframework.aop.proxy.support.DefaultInterceptSupport;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link ByteBuddyAopProxyFactory} 的单元测试。
 *
 * @author 詹高扬
 * @since 2022-08-30
 */
@DisplayName("测试 ByteBuddyAopProxyFactory 类")
public class ByteBuddyAopProxyFactoryTest {
    @Test
    @DisplayName("当使用 bytebuddy 创建相同类的两个代理时，两个代理的类型一致")
    void ShouldCreateSameTypeProxyWhenGivenSameClass() {
        InterceptSupport support =
                new DefaultInterceptSupport(TestTargetClass.class, TestTargetClass::new, Collections.emptyList());
        AopProxyFactory aopProxyFactory = new ByteBuddyAopProxyFactory();
        Object proxy1 = aopProxyFactory.createProxy(support);
        Object proxy2 = aopProxyFactory.createProxy(support);
        assertThat(proxy1.getClass()).isEqualTo(proxy2.getClass());
    }

    @Nested
    @DisplayName("当父类没有无参构造方法时")
    class GivenParentClassHasNotNoArgConstructor {
        @Test
        @DisplayName("使用 bytebuddy 创建代理成功")
        void shouldReturnProxy() {
            InterceptSupport support = new DefaultInterceptSupport(Original.class,
                    () -> new Original("original"),
                    Collections.emptyList());
            AopProxyFactory aopProxyFactory = new ByteBuddyAopProxyFactory();
            Object proxy = aopProxyFactory.createProxy(support);
            assertThat(proxy).isNotNull().isInstanceOf(Original.class);
            Original actual = ObjectUtils.cast(proxy);
            assertThat(actual.getName()).isEqualTo("original");
        }
    }

    @Test
    @DisplayName("使用 bytebuddy 创建一个 Aop 代理，验证 before、after 拦截成功")
    void ShouldInterceptSuccessfullyWhenCreateAopProxy() throws NoSuchMethodException {
        // given
        TestAspect aspect = mock(TestAspect.class);
        BeanFactory aspectFactory = mock(BeanFactory.class);
        when(aspectFactory.get()).thenReturn(aspect);
        Method before1 = TestAspect.class.getDeclaredMethod("before1");
        Method before2 = TestAspect.class.getDeclaredMethod("before2");
        Method after1 = TestAspect.class.getDeclaredMethod("after1");
        List<MethodInterceptor> methodInterceptors = new ArrayList<>();
        MethodInterceptor methodInterceptor1 = new BeforeInterceptor(aspectFactory, before1);
        MethodInterceptor methodInterceptor2 = new BeforeInterceptor(aspectFactory, before2);
        MethodInterceptor methodInterceptor3 = new AfterInterceptor(aspectFactory, after1);
        methodInterceptors.add(methodInterceptor1);
        methodInterceptors.add(methodInterceptor2);
        methodInterceptors.add(methodInterceptor3);
        MethodMatcher hello1Matcher = new Hello1MethodMatcher();
        MethodMatcher hello2Matcher = new Hello2MethodMatcher();
        methodInterceptor1.getPointCut().matchers().add(hello1Matcher);
        methodInterceptor2.getPointCut().matchers().add(hello2Matcher);
        methodInterceptor3.getPointCut().matchers().add(hello1Matcher);
        methodInterceptor1.getPointCut().add(TestTargetClass.class);
        methodInterceptor2.getPointCut().add(TestTargetClass.class);
        methodInterceptor3.getPointCut().add(TestTargetClass.class);
        TestTargetClass targetBean = new TestTargetClass();
        InterceptSupport interceptSupport =
                new DefaultInterceptSupport(TestTargetClass.class, () -> targetBean, methodInterceptors);

        // when
        AopProxyFactory aopProxyFactory = new ByteBuddyAopProxyFactory();
        TestTargetClass proxy = ObjectUtils.cast(aopProxyFactory.createProxy(interceptSupport));
        String message1 = targetBean.hello1("Tom");
        String message2 = proxy.hello1("Jerry");
        String message3 = proxy.hello1("Tom");

        // then
        assertThat(message1).isEqualTo("Hello, Tom! This is the 1 times.");
        assertThat(message2).isEqualTo("Hello, Jerry! This is the 2 times.");
        assertThat(message3).isEqualTo("Hello, Tom! This is the 3 times.");
        verify(aspect, times(2)).before1();
        verify(aspect, times(0)).before2();
        verify(aspect, times(2)).after1();

        // when
        String message4 = targetBean.hello2("Tom");
        String message5 = proxy.hello2("Jerry");
        String message6 = proxy.hello2("Tom");

        // then
        assertThat(message4).isEqualTo("Hello, Tom! This is the 4 times.");
        assertThat(message5).isEqualTo("Hello, Jerry! This is the 5 times.");
        assertThat(message6).isEqualTo("Hello, Tom! This is the 6 times.");
        verify(aspect, times(2)).before1();
        verify(aspect, times(2)).before2();
        verify(aspect, times(2)).after1();
    }

    /**
     * 测试代理的目标类。
     * <p>该测试类中携带状态信息，即每次都用都会对下一次调用产生影响。</p>
     */
    public static class TestTargetClass {
        private int count = 0;

        /**
         * 测试代理的目标方法1。
         *
         * @param name 表示测试参数的 {@link String}。
         * @return 表示测试返回值的 {@link String}。
         */
        public String hello1(String name) {
            this.count++;
            return "Hello, " + name + "! This is the " + this.count + " times.";
        }

        /**
         * 测试代理的目标方法2。
         *
         * @param name 表示测试参数的 {@link String}。
         * @return 表示测试返回值的 {@link String}。
         */
        public String hello2(String name) {
            this.count++;
            return "Hello, " + name + "! This is the " + this.count + " times.";
        }
    }

    /**
     * 表示测试使用的切面类。
     */
    public static class TestAspect {
        /**
         * 表示第一个方法前置拦截器，拦截的目标方法是 {@link TestTargetClass#hello1(String)}。
         */
        public void before1() {}

        /**
         * 表示第二个方法前置拦截器，拦截的目标方法是 {@link TestTargetClass#hello2(String)}。
         */
        public void before2() {}

        /**
         * 表示第一个方法后置拦截器，拦截的目标方法是 {@link TestTargetClass#hello1(String)}。
         */
        public void after1() {}
    }

    /**
     * 匹配方法 {@link TestTargetClass#hello1(String)} 的方法匹配器。
     */
    public static class Hello1MethodMatcher implements MethodMatcher {
        @Override
        public boolean couldMatch(Class<?> clazz) {
            return true;
        }

        @Override
        public TestMatcherResult match(@Nonnull Method method) {
            return new TestMatcherResult(method.getName().contains("hello1"));
        }

        @Override
        public void choose(Method method, MatchResult result) {}
    }

    /**
     * 匹配方法 {@link TestTargetClass#hello2(String)} 的方法匹配器。
     */
    public static class Hello2MethodMatcher implements MethodMatcher {
        @Override
        public boolean couldMatch(Class<?> clazz) {
            return true;
        }

        @Override
        public TestMatcherResult match(@Nonnull Method method) {
            return new TestMatcherResult(method.getName().contains("hello2"));
        }

        @Override
        public void choose(Method method, MatchResult result) {}
    }

    /**
     * 测试匹配结果。
     */
    public static class TestMatcherResult implements MethodMatcher.MatchResult {
        private final boolean matches;

        /**
         * 使用匹配结果实例化 {@link TestMatcherResult}。
         *
         * @param matches 表示匹配结果的 {@code boolean}。
         */
        public TestMatcherResult(boolean matches) {
            this.matches = matches;
        }

        @Override
        public boolean matches() {
            return this.matches;
        }

        @Override
        public Object getResult() {
            return null;
        }
    }

    /**
     * 测试携带 final 属性的父类型。
     */
    public static class Original {
        private final String name;

        /**
         * 表示测试构造方法。
         *
         * @param name 表示测试参数的 {@link String}。
         */
        public Original(String name) {
            this.name = name;
        }

        /**
         * 测试方法。
         *
         * @return 表示测试结果的 {@link String}。
         */
        public String getName() {
            return this.name;
        }
    }
}