/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.aop.interceptor.MethodInvocation;
import modelengine.fitframework.aop.interceptor.MethodJoinPoint;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.conf.runtime.CommunicationProtocol;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.util.LazyLoader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 表示 {@link DynamicRoutingInterceptor} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-07-12
 */
@DisplayName("测试 DynamicRoutingInterceptor")
public class DynamicRoutingInterceptorTest {
    private DynamicRoutingInterceptor interceptor;
    private BrokerClient brokerClient;
    private MethodJoinPoint joinPoint;
    private MethodInvocation invocation;
    private Method testMethod;

    @BeforeEach
    void setup() {
        this.brokerClient = mock(BrokerClient.class);
        Router router = mock(Router.class);
        when(this.brokerClient.getRouter(anyString(), anyBoolean(), any())).thenReturn(router);
        Invoker invoker = mock(Invoker.class);
        when(router.route(any())).thenReturn(invoker);
        when(invoker.retry(anyInt())).thenReturn(invoker);
        when(invoker.timeout(anyLong(), any())).thenReturn(invoker);
        when(invoker.protocol(any())).thenReturn(invoker);
        when(invoker.format(any())).thenReturn(invoker);
        when(invoker.invoke(any())).thenReturn("OK");
        this.joinPoint = mock(MethodJoinPoint.class);
        this.invocation = mock(MethodInvocation.class);
        when(this.joinPoint.getProxiedInvocation()).thenReturn(this.invocation);
        when(this.invocation.getArguments()).thenReturn(new Object[0]);
    }

    @Test
    @DisplayName("当不存在别名时，返回正确的结果")
    void givenAliasNullThenReturnOk() throws Throwable {
        this.testMethod = TestInterface1.class.getDeclaredMethod("testMethod1");
        when(this.invocation.getMethod()).thenReturn(this.testMethod);
        this.interceptor = new DynamicRoutingInterceptor(new LazyLoader<>(() -> this.brokerClient),
                null,
                0,
                0,
                TimeUnit.MILLISECONDS,
                CommunicationProtocol.HTTP,
                SerializationFormat.JSON);
        Object actual = this.interceptor.intercept(this.joinPoint);
        assertThat(actual).isEqualTo("OK");
    }

    @Test
    @DisplayName("当存在别名时，返回正确的结果")
    void givenAliasThenReturnOk() throws Throwable {
        this.testMethod = TestInterface1.class.getDeclaredMethod("testMethod1");
        when(this.invocation.getMethod()).thenReturn(this.testMethod);
        this.interceptor = new DynamicRoutingInterceptor(new LazyLoader<>(() -> this.brokerClient),
                "alias",
                0,
                0,
                TimeUnit.MILLISECONDS,
                CommunicationProtocol.HTTP,
                SerializationFormat.JSON);
        Object actual = this.interceptor.intercept(this.joinPoint);
        assertThat(actual).isEqualTo("OK");
    }

    @Test
    @DisplayName("反复调用相同的方法，返回正确的结果")
    void shouldReturnOkWhenInvokeTwice() throws Throwable {
        this.testMethod = TestInterface1.class.getDeclaredMethod("testMethod1");
        when(this.invocation.getMethod()).thenReturn(this.testMethod);
        this.interceptor = new DynamicRoutingInterceptor(new LazyLoader<>(() -> this.brokerClient),
                null,
                0,
                0,
                TimeUnit.MILLISECONDS,
                CommunicationProtocol.HTTP,
                SerializationFormat.JSON);
        Object first = this.interceptor.intercept(this.joinPoint);
        assertThat(first).isEqualTo("OK");
        Object second = this.interceptor.intercept(this.joinPoint);
        assertThat(second).isEqualTo("OK");
    }

    @Test
    @DisplayName("调用带有 @Genericable 注解的接口，返回正确的结果")
    void shouldReturnOkWhenInvokeMethodWithGenericableAnnotation() throws Throwable {
        this.testMethod = TestInterface1.class.getDeclaredMethod("testMethod2");
        when(this.invocation.getMethod()).thenReturn(this.testMethod);
        this.interceptor = new DynamicRoutingInterceptor(new LazyLoader<>(() -> this.brokerClient),
                null,
                0,
                0,
                TimeUnit.MILLISECONDS,
                CommunicationProtocol.HTTP,
                SerializationFormat.JSON);
        Object actual = this.interceptor.intercept(this.joinPoint);
        assertThat(actual).isEqualTo("OK");
    }

    @Test
    @DisplayName("调用接口类上带有 @Genericable 注解的接口，返回正确的结果")
    void shouldReturnOkWhenInvokeMethodItsInterfaceWithGenericableAnnotation() throws Throwable {
        this.testMethod = TestInterface2.class.getDeclaredMethod("testMethod1");
        when(this.invocation.getMethod()).thenReturn(this.testMethod);
        this.interceptor = new DynamicRoutingInterceptor(new LazyLoader<>(() -> this.brokerClient),
                null,
                0,
                0,
                TimeUnit.MILLISECONDS,
                CommunicationProtocol.HTTP,
                SerializationFormat.JSON);
        Object actual = this.interceptor.intercept(this.joinPoint);
        assertThat(actual).isEqualTo("OK");
    }

    /**
     * 表示第一个测试接口。
     */
    private interface TestInterface1 {
        /**
         * 表示第一个测试方法。
         */
        void testMethod1();

        /**
         * 表示第二个测试方法。
         */
        @Genericable(id = "g")
        void testMethod2();
    }

    /**
     * 表示第二个测试接口。
     */
    @Genericable(id = "g")
    private interface TestInterface2 {
        /**
         * 表示第一个测试方法。
         */
        void testMethod1();
    }
}
