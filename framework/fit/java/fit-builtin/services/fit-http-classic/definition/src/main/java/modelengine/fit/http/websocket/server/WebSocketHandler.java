/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.websocket.server;

import modelengine.fit.http.HttpResourceSupplier;
import modelengine.fit.http.server.HttpClassicServer;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.websocket.Session;
import modelengine.fitframework.pattern.builder.BuilderFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 表示 WebSocket 消息的处理器。
 *
 * @author 季聿阶
 * @since 2023-12-07
 */
public interface WebSocketHandler extends HttpResourceSupplier {
    /**
     * 获取处理器所对应的路径样式。
     *
     * @return 表示处理器所对应的路径样式的 {@link String}。
     */
    String pathPattern();

    /**
     * 当一个新的 WebSocket 会话打开时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     */
    void onOpen(Session session);

    /**
     * 当收到 WebSocket 文本消息时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     * @param message 表示收到的 WebSocket 文本消息的 {@link String}。
     */
    void onMessage(Session session, String message);

    /**
     * 当收到 WebSocket 二进制消息时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     * @param message 表示收到的 WebSocket 二进制消息的 {@link String}。
     */
    void onMessage(Session session, byte[] message);

    /**
     * 当 WebSocket 会话关闭时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     */
    void onClose(Session session);

    /**
     * 当 WebSocket 会话过程发生异常时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     * @param cause 表示会话过程发生异常的原因的 {@link Throwable}。
     */
    void onError(Session session, Throwable cause);

    /**
     * 表示 WebSocket 处理器的相关信息。
     */
    interface Info {
        /**
         * 获取 WebSocket 处理器的路径样式。
         * <p>路径样式中如果出现变量，需要在其两侧加上 {@code '{' '}'} 符号。</p>
         * <p>例如：{@code /a/{pathVariable}/c}，其中，{@code {pathVariable}} 就是路径变量。</p>
         *
         * @return 表示 WebSocket 处理器的路径样式的 {@link String}。
         */
        String pathPattern();

        /**
         * 获取 WebSocket 处理器所属的服务器。
         *
         * @return 表示 WebSocket 处理器所属服务器的 {@link HttpClassicServer}。
         */
        HttpClassicServer httpServer();

        /**
         * 获取 WebSocket 处理器的执行对象。
         *
         * @return 表示 WebSocket 处理器的执行对象的 {@link Object}。
         */
        Object target();

        /**
         * 获取 WebSocket 连接开启时调用的方法。
         *
         * @return 表示 WebSocket 连接开启时调用的方法的 {@link Method}。
         */
        Method openMethod();

        /**
         * 获取 {@link #openMethod()} 的参数映射器列表。
         *
         * @return 表示 {@link #openMethod()} 的参数映射器列表的 {@link List}{@code <}{@link PropertyValueMapper}{@code >}。
         */
        List<PropertyValueMapper> openMethodMappers();

        /**
         * 获取 WebSocket 收到消息时调用的方法。
         *
         * @return 表示 WebSocket 收到消息时调用的方法的 {@link Method}。
         */
        Method messageMethod();

        /**
         * 获取 {@link #messageMethod()} 的参数映射器列表。
         *
         * @return 表示 {@link #messageMethod()} 的参数映射器列表的 {@link List}{@code <}{@link PropertyValueMapper}{@code
         * >}。
         */
        List<PropertyValueMapper> messageMethodMappers();

        /**
         * 获取 WebSocket 发生错误时调用的方法。
         *
         * @return 表示 WebSocket 发生错误时调用的方法的 {@link Method}。
         */
        Method errorMethod();

        /**
         * 获取 {@link #errorMethod()} 的参数映射器列表。
         *
         * @return 表示 {@link #errorMethod()} 的参数映射器列表的 {@link List}{@code <}{@link PropertyValueMapper}{@code >}。
         */
        List<PropertyValueMapper> errorMethodMappers();

        /**
         * 获取 WebSocket 连接关闭时调用的方法。
         *
         * @return 表示 WebSocket 连接关闭时调用的方法的 {@link Method}。
         */
        Method closeMethod();

        /**
         * 获取 {@link #closeMethod()} 的参数映射器列表。
         *
         * @return 表示 {@link #closeMethod()} 的参数映射器列表的 {@link List}{@code <}{@link PropertyValueMapper}{@code >}。
         */
        List<PropertyValueMapper> closeMethodMappers();

        /**
         * 获取 WebSocket 处理器的值映射器的列表。
         *
         * @return 表示 WebSocket 处理器的值映射器列表的 {@link List}{@code <}{@link PropertyValueMapper}{@code >}。
         */
        List<PropertyValueMapper> httpMappers();

        /**
         * {@link Info} 的构建器。
         */
        interface Builder {
            /**
             * 向当前构建器中设置 WebSocket 处理器的路径样式。
             *
             * @param pathPattern 表示待设置的 WebSocket 处理器的路径样式的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder pathPattern(String pathPattern);

            /**
             * 向当前构建器中设置 WebSocket 处理器所属的服务器。
             *
             * @param httpServer 表示待设置的 WebSocket 处理器所属服务器的 {@link HttpClassicServer}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder httpServer(HttpClassicServer httpServer);

            /**
             * 向当前构建器中设置 WebSocket 处理器的执行对象。
             *
             * @param target 表示待设置的 WebSocket 处理器的执行对象的 {@link Object}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder target(Object target);

            /**
             * 向当前构建器中设置 WebSocket 连接开启时调用的方法。
             *
             * @param method 表示待设置的 WebSocket 连接开启时调用的方法的 {@link Method}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder openMethod(Method method);

            /**
             * 向当前构建器中设置 {@link #openMethod()} 的参数映射器列表。
             *
             * @param mappers 表示待设置的 {@link #openMethod()} 的参数映射器列表的
             * {@link List}{@code <}{@link PropertyValueMapper}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder openMethodMappers(List<PropertyValueMapper> mappers);

            /**
             * 向当前构建器中设置 WebSocket 收到消息时调用的方法。
             *
             * @param method 表示待设置的 WebSocket 收到消息时调用的方法的 {@link Method}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder messageMethod(Method method);

            /**
             * 向当前构建器中设置 {@link #messageMethod()} 的参数映射器列表。
             *
             * @param mappers 表示待设置的 {@link #messageMethod()} 的参数映射器列表的
             * {@link List}{@code <}{@link PropertyValueMapper}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder messageMethodMappers(List<PropertyValueMapper> mappers);

            /**
             * 向当前构建器中设置 WebSocket 发生错误时调用的方法。
             *
             * @param method 表示待设置的 WebSocket 发生错误时调用的方法的 {@link Method}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder errorMethod(Method method);

            /**
             * 向当前构建器中设置 {@link #errorMethod()} 的参数映射器列表。
             *
             * @param mappers 表示待设置的 {@link #errorMethod()} 的参数映射器列表的
             * {@link List}{@code <}{@link PropertyValueMapper}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder errorMethodMappers(List<PropertyValueMapper> mappers);

            /**
             * 向当前构建器中设置 WebSocket 连接关闭时调用的方法。
             *
             * @param method 表示待设置的 WebSocket 连接关闭时调用的方法的 {@link Method}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder closeMethod(Method method);

            /**
             * 向当前构建器中设置 {@link #closeMethod()} 的参数映射器列表。
             *
             * @param mappers 表示待设置的 {@link #closeMethod()} 的参数映射器列表的
             * {@link List}{@code <}{@link PropertyValueMapper}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder closeMethodMappers(List<PropertyValueMapper> mappers);

            /**
             * 向当前构建器中设置 WebSocket 处理器的值映射器列表。
             *
             * @param propertyValueMappers 表示待设置的 WebSocket 处理器的值映射器列表的 {@link List}{@code <}{@link
             * PropertyValueMapper}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder httpMappers(List<PropertyValueMapper> propertyValueMappers);

            /**
             * 构建对象。
             *
             * @return 表示构建出来的对象的 {@link Info}。
             */
            Info build();
        }

        /**
         * 获取 {@link Info} 的构建器。
         *
         * @return 表示 {@link Info} 的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return custom(null);
        }

        /**
         * 获取 {@link Info} 的构建器，同时将指定对象的值进行填充。
         *
         * @param value 表示指定对象的 {@link Info}。
         * @return 表示 {@link Info} 的构建器的 {@link Builder}。
         */
        static Builder custom(Info value) {
            return BuilderFactory.get(Info.class, Builder.class).create(value);
        }
    }
}
