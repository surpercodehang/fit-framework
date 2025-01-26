/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.dispatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpDispatcher;
import modelengine.fit.http.server.HttpHandler;
import modelengine.fit.http.server.HttpHandlerNotFoundException;
import modelengine.fit.http.server.RegisterHttpHandlerException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * {@link HttpDispatcher} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-07-26
 */
@DisplayName("测试 DefaultHttpDispatcher")
public class DefaultHttpDispatcherTest {
    private HttpDispatcher dispatcher;
    private HttpClassicServerRequest request;
    private HttpHandler handler;

    @BeforeEach
    void setup() {
        this.dispatcher = HttpDispatcher.create();
        this.request = mock(HttpClassicServerRequest.class);
        this.handler = mock(HttpHandler.class);
        Address remoteAddress = mock(Address.class);
        when(this.request.remoteAddress()).thenReturn(remoteAddress);
        when(this.request.remoteAddress().hostAddress()).thenReturn("127.0.0.1");
    }

    @AfterEach
    void teardown() {
        this.dispatcher = null;
        this.request = null;
        this.handler = null;
    }

    @Test
    @DisplayName("当转发请求时，抛出 HttpHandlerNotFoundException")
    void throwExceptionWhenDispatching() {
        when(this.request.method()).thenReturn(HttpRequestMethod.GET);
        when(this.request.path()).thenReturn("/a");
        HttpHandlerNotFoundException exception =
                catchThrowableOfType(() -> this.dispatcher.dispatch(this.request, null),
                        HttpHandlerNotFoundException.class);
        assertThat(exception).isNotNull().hasMessage("No http handler for http request. [method=GET, path=/a]");
    }

    @Test
    @DisplayName("可以成功获取所有注册的处理器")
    void shouldReturnAllRegisteredHandlers() {
        Map<HttpRequestMethod, List<HttpHandler>> mapping = this.dispatcher.getHttpHandlersMapping();
        assertThat(mapping).isEmpty();
    }

    @Nested
    @DisplayName("注册了根路径的处理器后")
    class AfterRegisteringRootPathPatternHandler {
        @BeforeEach
        void setup() {
            when(DefaultHttpDispatcherTest.this.handler.pathPattern()).thenReturn("/");
            this.dispatcher().register(HttpRequestMethod.GET.name(), DefaultHttpDispatcherTest.this.handler);
        }

        @Test
        @DisplayName("当转发根路径的请求时，请求被注册的处理器处理")
        void requestIsHandledWhenDispatchingRootPath() {
            when(DefaultHttpDispatcherTest.this.request.method()).thenReturn(HttpRequestMethod.GET);
            when(DefaultHttpDispatcherTest.this.request.path()).thenReturn("/");
            HttpHandler httpHandler = this.dispatcher().dispatch(DefaultHttpDispatcherTest.this.request, null);
            assertThat(httpHandler).isEqualTo(DefaultHttpDispatcherTest.this.handler);
        }

        @Test
        @DisplayName("当转发非根路径的请求时，抛出 HttpHandlerNotFoundException")
        void throwExceptionWhenDispatchingNonRootPath() {
            when(DefaultHttpDispatcherTest.this.request.method()).thenReturn(HttpRequestMethod.GET);
            when(DefaultHttpDispatcherTest.this.request.path()).thenReturn("/b");
            HttpHandlerNotFoundException exception =
                    catchThrowableOfType(() -> this.dispatcher().dispatch(DefaultHttpDispatcherTest.this.request, null),
                            HttpHandlerNotFoundException.class);
            assertThat(exception).isNotNull().hasMessage("No http handler for http request. [method=GET, path=/b]");
        }

        @Test
        @DisplayName("当再注册一个根路径的处理器时，抛出 RegisterHttpHandlerException")
        void throwExceptionWhenRegisteringRootPathPatternHandler() {
            RegisterHttpHandlerException exception = catchThrowableOfType(() -> this.dispatcher()
                            .register(HttpRequestMethod.GET.name(), DefaultHttpDispatcherTest.this.handler),
                    RegisterHttpHandlerException.class);
            assertThat(exception).isNotNull().hasMessage("Http handler has been registered. [method=GET, pattern=/]");
        }

        @Test
        @DisplayName("可以成功获取所有注册的处理器")
        void shouldReturnAllRegisteredHandlers() {
            Map<HttpRequestMethod, List<HttpHandler>> mapping = this.dispatcher().getHttpHandlersMapping();
            assertThat(mapping).hasSize(1);
            assertThat(mapping.get(HttpRequestMethod.GET)).containsExactly(DefaultHttpDispatcherTest.this.handler);
        }

        private HttpDispatcher dispatcher() {
            return DefaultHttpDispatcherTest.this.dispatcher;
        }
    }

    @Nested
    @DisplayName("注册了一个没有路径变量的处理器后")
    class AfterRegistering1NoPathVariableHandler {
        @BeforeEach
        void setup() {
            when(DefaultHttpDispatcherTest.this.handler.pathPattern()).thenReturn("/a");
            this.dispatcher().register(HttpRequestMethod.GET.name(), DefaultHttpDispatcherTest.this.handler);
        }

        @Test
        @DisplayName("当转发路径完全匹配的请求时，请求被注册的处理器处理")
        void requestIsHandledWhenDispatchingMatchedPath() {
            when(DefaultHttpDispatcherTest.this.request.method()).thenReturn(HttpRequestMethod.GET);
            when(DefaultHttpDispatcherTest.this.request.path()).thenReturn("/a");
            HttpHandler httpHandler = this.dispatcher().dispatch(DefaultHttpDispatcherTest.this.request, null);
            assertThat(httpHandler).isEqualTo(DefaultHttpDispatcherTest.this.handler);
        }

        @Test
        @DisplayName("当转发路径不匹配的请求时，抛出 HttpHandlerNotFoundException")
        void throwExceptionWhenDispatchingNotMatchedPath() {
            when(DefaultHttpDispatcherTest.this.request.method()).thenReturn(HttpRequestMethod.GET);
            when(DefaultHttpDispatcherTest.this.request.path()).thenReturn("/b");
            HttpHandlerNotFoundException exception =
                    catchThrowableOfType(() -> this.dispatcher().dispatch(DefaultHttpDispatcherTest.this.request, null),
                            HttpHandlerNotFoundException.class);
            assertThat(exception).isNotNull().hasMessage("No http handler for http request. [method=GET, path=/b]");
        }

        @Test
        @DisplayName("可以成功获取所有注册的处理器")
        void shouldReturnAllRegisteredHandlers() {
            Map<HttpRequestMethod, List<HttpHandler>> mapping = this.dispatcher().getHttpHandlersMapping();
            assertThat(mapping).hasSize(1);
            assertThat(mapping.get(HttpRequestMethod.GET)).containsExactly(DefaultHttpDispatcherTest.this.handler);
        }

        private HttpDispatcher dispatcher() {
            return DefaultHttpDispatcherTest.this.dispatcher;
        }
    }

    @Nested
    @DisplayName("注册了一个有路径变量的处理器后")
    class AfterRegistering1PathVariableHandler {
        @BeforeEach
        void setup() {
            when(DefaultHttpDispatcherTest.this.handler.pathPattern()).thenReturn("/{value}");
            this.dispatcher().register(HttpRequestMethod.GET.name(), DefaultHttpDispatcherTest.this.handler);
        }

        @Test
        @DisplayName("当转发路径匹配的请求时，请求被注册的处理器处理")
        void requestIsHandledWhenDispatchingMatchedPath() {
            when(DefaultHttpDispatcherTest.this.request.method()).thenReturn(HttpRequestMethod.GET);
            when(DefaultHttpDispatcherTest.this.request.path()).thenReturn("/a");
            HttpHandler httpHandler = this.dispatcher().dispatch(DefaultHttpDispatcherTest.this.request, null);
            assertThat(httpHandler).isEqualTo(DefaultHttpDispatcherTest.this.handler);
        }

        @Test
        @DisplayName("当转发路径不匹配的请求时，抛出 HttpHandlerNotFoundException")
        void throwExceptionWhenDispatchingNotMatchedPath() {
            when(DefaultHttpDispatcherTest.this.request.method()).thenReturn(HttpRequestMethod.GET);
            when(DefaultHttpDispatcherTest.this.request.path()).thenReturn("/a/b");
            HttpHandlerNotFoundException exception =
                    catchThrowableOfType(() -> this.dispatcher().dispatch(DefaultHttpDispatcherTest.this.request, null),
                            HttpHandlerNotFoundException.class);
            assertThat(exception).isNotNull().hasMessage("No http handler for http request. [method=GET, path=/a/b]");
        }

        @Test
        @DisplayName("当再注册一个相同的有路径变量的处理器时，抛出 RegisterHttpHandlerException")
        void throwExceptionWhenRegisteringAnotherSamePathPatternHandler() {
            RegisterHttpHandlerException exception = catchThrowableOfType(() -> this.dispatcher()
                            .register(HttpRequestMethod.GET.name(), DefaultHttpDispatcherTest.this.handler),
                    RegisterHttpHandlerException.class);
            assertThat(exception).isNotNull().hasMessage("Http handler has been registered. [method=GET, pattern=/*]");
        }

        @Test
        @DisplayName("可以成功获取所有注册的处理器")
        void shouldReturnAllRegisteredHandlers() {
            Map<HttpRequestMethod, List<HttpHandler>> mapping = this.dispatcher().getHttpHandlersMapping();
            assertThat(mapping).hasSize(1);
            assertThat(mapping.get(HttpRequestMethod.GET)).containsExactly(DefaultHttpDispatcherTest.this.handler);
        }

        private HttpDispatcher dispatcher() {
            return DefaultHttpDispatcherTest.this.dispatcher;
        }
    }

    @Nested
    @DisplayName("注册了一个有 '**' 的路径样式的处理器后")
    class AfterRegistering1WildcardHandler {
        @BeforeEach
        void setup() {
            when(DefaultHttpDispatcherTest.this.handler.pathPattern()).thenReturn("/a/**");
            this.dispatcher().register(HttpRequestMethod.GET.name(), DefaultHttpDispatcherTest.this.handler);
        }

        @Test
        @DisplayName("当转发路径匹配的请求时，请求被注册的处理器处理")
        void requestIsHandledWhenDispatchingMatchedPath() {
            when(DefaultHttpDispatcherTest.this.request.method()).thenReturn(HttpRequestMethod.GET);
            when(DefaultHttpDispatcherTest.this.request.path()).thenReturn("/a/b/c");
            HttpHandler httpHandler = this.dispatcher().dispatch(DefaultHttpDispatcherTest.this.request, null);
            assertThat(httpHandler).isEqualTo(DefaultHttpDispatcherTest.this.handler);
        }

        @Test
        @DisplayName("当转发路径不匹配的请求时，抛出 HttpHandlerNotFoundException")
        void throwExceptionWhenDispatchingNotMatchedPath() {
            when(DefaultHttpDispatcherTest.this.request.method()).thenReturn(HttpRequestMethod.GET);
            when(DefaultHttpDispatcherTest.this.request.path()).thenReturn("/b/b/c");
            HttpHandlerNotFoundException exception =
                    catchThrowableOfType(() -> this.dispatcher().dispatch(DefaultHttpDispatcherTest.this.request, null),
                            HttpHandlerNotFoundException.class);
            assertThat(exception).isNotNull().hasMessage("No http handler for http request. [method=GET, path=/b/b/c]");
        }

        @Test
        @DisplayName("当再注册一个相同的有路径变量的处理器时，抛出 RegisterHttpHandlerException")
        void throwExceptionWhenRegisteringAnotherSamePathPatternHandler() {
            RegisterHttpHandlerException exception = catchThrowableOfType(() -> this.dispatcher()
                            .register(HttpRequestMethod.GET.name(), DefaultHttpDispatcherTest.this.handler),
                    RegisterHttpHandlerException.class);
            assertThat(exception).isNotNull()
                    .hasMessage("Http handler has been registered. [method=GET, pattern=/a/**]");
        }

        @Test
        @DisplayName("可以成功获取所有注册的处理器")
        void shouldReturnAllRegisteredHandlers() {
            Map<HttpRequestMethod, List<HttpHandler>> mapping = this.dispatcher().getHttpHandlersMapping();
            assertThat(mapping).hasSize(1);
            assertThat(mapping.get(HttpRequestMethod.GET)).containsExactly(DefaultHttpDispatcherTest.this.handler);
        }

        private HttpDispatcher dispatcher() {
            return DefaultHttpDispatcherTest.this.dispatcher;
        }
    }

    @Nested
    @DisplayName("注册了一个有需转义字符的路径样式的处理器后")
    class AfterRegistering1EscapeHandler {
        @BeforeEach
        void setup() {
            when(DefaultHttpDispatcherTest.this.handler.pathPattern()).thenReturn("/a/ \"#%&()+,/:;<=>?@\\|");
            this.dispatcher().register(HttpRequestMethod.GET.name(), DefaultHttpDispatcherTest.this.handler);
        }

        @Test
        @DisplayName("当转发路径匹配的请求时，请求被注册的处理器处理")
        void requestIsHandledWhenDispatchingMatchedPath() {
            when(DefaultHttpDispatcherTest.this.request.method()).thenReturn(HttpRequestMethod.GET);
            when(DefaultHttpDispatcherTest.this.request.path()).thenReturn(
                    "/a/%20%22%23%25%26%28%29%2B%2C%2F%3A%3B%3C%3D%3E%3F%40%5C%7C");
            HttpHandler httpHandler = this.dispatcher().dispatch(DefaultHttpDispatcherTest.this.request, null);
            assertThat(httpHandler).isEqualTo(DefaultHttpDispatcherTest.this.handler);
        }

        @Test
        @DisplayName("当转发路径不匹配的请求时，抛出 HttpHandlerNotFoundException")
        void throwExceptionWhenDispatchingNotMatchedPath() {
            when(DefaultHttpDispatcherTest.this.request.method()).thenReturn(HttpRequestMethod.GET);
            when(DefaultHttpDispatcherTest.this.request.path()).thenReturn("/b/b/c");
            HttpHandlerNotFoundException exception =
                    catchThrowableOfType(() -> this.dispatcher().dispatch(DefaultHttpDispatcherTest.this.request, null),
                            HttpHandlerNotFoundException.class);
            assertThat(exception).isNotNull().hasMessage("No http handler for http request. [method=GET, path=/b/b/c]");
        }

        @Test
        @DisplayName("当再注册一个相同的有路径变量的处理器时，抛出 RegisterHttpHandlerException")
        void throwExceptionWhenRegisteringAnotherSamePathPatternHandler() {
            RegisterHttpHandlerException exception = catchThrowableOfType(() -> this.dispatcher()
                            .register(HttpRequestMethod.GET.name(), DefaultHttpDispatcherTest.this.handler),
                    RegisterHttpHandlerException.class);
            assertThat(exception).isNotNull()
                    .hasMessage("Http handler has been registered. [method=GET, pattern=/a/ \"#%&()+,/:;<=>?@\\|]");
        }

        @Test
        @DisplayName("可以成功获取所有注册的处理器")
        void shouldReturnAllRegisteredHandlers() {
            Map<HttpRequestMethod, List<HttpHandler>> mapping = this.dispatcher().getHttpHandlersMapping();
            assertThat(mapping).hasSize(1);
            assertThat(mapping.get(HttpRequestMethod.GET)).containsExactly(DefaultHttpDispatcherTest.this.handler);
        }

        private HttpDispatcher dispatcher() {
            return DefaultHttpDispatcherTest.this.dispatcher;
        }
    }
}
