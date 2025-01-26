/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.RouterFactory;
import modelengine.fitframework.broker.client.RouterRetrievalFailureException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * 用于 {@link DefaultBrokerClient} 进行单元测试。
 *
 * @author 季聿阶
 * @since 2023-07-06
 */
@DisplayName("测试 DefaultBrokerClient")
public class DefaultBrokerClientTest {
    private BrokerClient client;
    private RouterFactory factory;
    private Router router;
    private modelengine.fitframework.broker.Genericable genericable;

    @BeforeEach
    void setup() {
        this.factory = mock(RouterFactory.class);
        this.client = new DefaultBrokerClient(this.factory);
        this.router = mock(Router.class);
        when(this.factory.create(anyString(), anyBoolean(), any())).thenReturn(this.router);
        Invoker invoker = mock(Invoker.class);
        when(this.router.route()).thenReturn(invoker);
        this.genericable = Mockito.mock(modelengine.fitframework.broker.Genericable.class);
        when(invoker.getGenericable()).thenReturn(this.genericable);
    }

    @AfterEach
    void teardown() {
        this.client = null;
        this.factory = null;
        this.router = null;
        this.genericable = null;
    }

    @Nested
    @DisplayName("测试单接口单方法规范")
    class TestSingleInterfaceSingleMethod {
        @Test
        @DisplayName("当调用符合单接口单方法规范的获取动态路由器的接口时，返回正确的动态路由器")
        void shouldReturnRouterWhenGenericableClassIsSingleMethod() {
            Router actual = this.client().getRouter(SingleMethod.class);
            assertThat(actual).isEqualTo(DefaultBrokerClientTest.this.router);
        }

        @Test
        @DisplayName("当调用不符合单接口单方法规范的获取动态路由器的接口时，抛出 RouterRetrievalFailureException")
        void shouldThrowExceptionWhenGenericableClassIsNotSingleMethod() {
            assertThatThrownBy(() -> this.client().getRouter(MultipleMethods.class)).isInstanceOf(
                            RouterRetrievalFailureException.class)
                    .hasMessage("No genericable id declared in class. [class=" + MultipleMethods.class.getName() + "]");
        }

        @Test
        @DisplayName("当调用的接口存在多个 process() 方法时，抛出 RouterRetrievalFailureException")
        void shouldThrowExceptionWhenInterfaceHasMultipleProcessMethods() {
            assertThatThrownBy(() -> this.client().getRouter(MultipleProcessMethod.class)).isInstanceOf(
                    RouterRetrievalFailureException.class);
        }

        @Test
        @DisplayName("当调用的接口为 null 时，抛出 RouterRetrievalFailureException")
        void shouldThrowExceptionWhenGenericableClassIsNull() {
            assertThatThrownBy(() -> DefaultBrokerClientTest.this.client.getRouter((Class<?>) null)).isInstanceOf(
                    RouterRetrievalFailureException.class).hasMessage("The genericable class cannot be null.");
        }

        @Test
        @DisplayName("当调用的类不是接口时，抛出 RouterRetrievalFailureException")
        void shouldThrowExceptionWhenGenericableClassIsNotInterface() {
            assertThatThrownBy(() -> DefaultBrokerClientTest.this.client.getRouter(NotInterface.class)).isInstanceOf(
                            RouterRetrievalFailureException.class)
                    .hasMessage(
                            "The genericable class is not a interface. [class=" + NotInterface.class.getName() + "]");
        }

        @Test
        @DisplayName("当调用的接口不存在 process() 方法时，抛出 RouterRetrievalFailureException")
        void shouldThrowExceptionWhenGenericableClassHasNoProcessMethod() {
            assertThatThrownBy(() -> DefaultBrokerClientTest.this.client.getRouter(NoProcess.class)).isInstanceOf(
                            RouterRetrievalFailureException.class)
                    .hasMessage("No genericable method declared in class. [class=" + NoProcess.class.getName() + "]");
        }

        @Test
        @DisplayName("当调用符合单接口单方法规范的获取服务的接口时，返回正确的服务")
        void shouldReturnGenericableWhenGenericableClassIsSingleMethod() {
            modelengine.fitframework.broker.Genericable actual = this.client().getGenericable(SingleMethod.class);
            assertThat(actual).isEqualTo(DefaultBrokerClientTest.this.genericable);
        }

        private BrokerClient client() {
            return DefaultBrokerClientTest.this.client;
        }
    }

    @Nested
    @DisplayName("测试单接口多方法规范")
    class TestSingleInterfaceMultipleMethods {
        @Test
        @DisplayName("当调用符合单接口多方法规范的获取动态路由器的接口时，返回正确的动态路由器")
        void shouldReturnRouterWhenGenericableClassIsMultipleMethods() {
            Router actual = DefaultBrokerClientTest.this.client.getRouter(MultipleMethods.class, "MultipleMethods#m1");
            assertThat(actual).isEqualTo(DefaultBrokerClientTest.this.router);
        }

        @Test
        @DisplayName("当调用不符合单接口多方法规范的获取动态路由器的接口时，抛出 RouterRetrievalFailureException")
        void shouldThrowExceptionWhenGenericableClassIsNotMultipleMethods() {
            assertThatThrownBy(() -> DefaultBrokerClientTest.this.client.getRouter(MultipleMethods.class,
                    "NotExist")).isInstanceOf(RouterRetrievalFailureException.class)
                    .hasMessage(
                            "No specified genericable id declared in method. [class=" + MultipleMethods.class.getName()
                                    + ", genericableId=NotExist]");
        }

        @Test
        @DisplayName("当调用的接口为 null 时，抛出 RouterRetrievalFailureException")
        void shouldThrowExceptionWhenGenericableClassIsNull() {
            assertThatThrownBy(() -> DefaultBrokerClientTest.this.client.getRouter(null, "NotExist")).isInstanceOf(
                    RouterRetrievalFailureException.class).hasMessage("The genericable class cannot be null.");
        }

        @Test
        @DisplayName("当调用的类不是接口时，抛出 RouterRetrievalFailureException")
        void shouldThrowExceptionWhenGenericableClassIsNotInterface() {
            assertThatThrownBy(() -> DefaultBrokerClientTest.this.client.getRouter(NotInterface.class,
                    "NotExist")).isInstanceOf(RouterRetrievalFailureException.class)
                    .hasMessage(
                            "The genericable class is not a interface. [class=" + NotInterface.class.getName() + "]");
        }

        @Test
        @DisplayName("当调用的服务唯一标识为 null 时，抛出 RouterRetrievalFailureException")
        void shouldThrowExceptionWhenGenericableIsIsNull() {
            assertThatThrownBy(() -> DefaultBrokerClientTest.this.client.getRouter(MultipleMethods.class,
                    null)).isInstanceOf(RouterRetrievalFailureException.class)
                    .hasMessage("The genericable id cannot be blank.");
        }

        @Test
        @DisplayName("当调用符合单接口多方法规范的获取服务的接口时，返回正确的服务")
        void shouldReturnGenericableWhenGenericableClassIsMultipleMethods() {
            modelengine.fitframework.broker.Genericable actual =
                    DefaultBrokerClientTest.this.client.getGenericable(MultipleMethods.class, "MultipleMethods#m1");
            assertThat(actual).isEqualTo(DefaultBrokerClientTest.this.genericable);
        }
    }

    @Genericable("SingleMethod")
    interface SingleMethod {
        /**
         * 表示测试方法。
         */
        void process();
    }

    @Genericable("MultipleProcessMethod")
    interface MultipleProcessMethod {
        /**
         * 表示测试方法。
         */
        void process();

        /**
         * 表示测试方法。
         *
         * @param param 表示测试的参数的 {@link String}。
         */
        void process(String param);
    }

    interface MultipleMethods {
        /**
         * 表示测试方法。
         */
        @Genericable("MultipleMethods#m1")
        void m1();

        /**
         * 表示测试方法。
         */
        @Genericable("MultipleMethods#m2")
        void m2();
    }

    private static class NotInterface {}

    @Genericable("NoProcess")
    private interface NoProcess {}
}
