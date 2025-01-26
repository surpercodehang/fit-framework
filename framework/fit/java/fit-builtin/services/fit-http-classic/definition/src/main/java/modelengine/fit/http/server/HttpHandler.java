/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import modelengine.fit.http.HttpResourceSupplier;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.PropertyValueMetadata;
import modelengine.fitframework.pattern.builder.BuilderFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 表示 Http 请求处理器。
 *
 * @author 季聿阶
 * @since 2022-07-18
 */
public interface HttpHandler extends HttpResourceSupplier {
    /**
     * 获取处理器所对应的路径样式。
     *
     * @return 表示处理器所对应的路径样式的 {@link String}。
     */
    String pathPattern();

    /**
     * 获取处理器的前置过滤器列表。
     *
     * @return 表示处理器的前置过滤器列表的 {@link List}{@code <}{@link HttpServerFilter}{@code >}。
     */
    List<HttpServerFilter> preFilters();

    /**
     * 执行处理器。
     *
     * @param request 表示当前 Http 请求的 {@link HttpClassicServerRequest}。
     * @param response 表示当前 Http 响应的 {@link HttpClassicServerResponse}。
     * @throws DoHttpHandlerException 当执行过程中发生异常时。
     */
    void handle(HttpClassicServerRequest request, HttpClassicServerResponse response) throws DoHttpHandlerException;

    /**
     * 表示 Http 处理器的相关静态信息。
     */
    interface StaticInfo {
        /**
         * 获取 Http 处理器的路径样式。
         * <p>路径样式中如果出现变量，需要在其两侧加上 {@code '{' '}'} 符号。</p>
         * <p>例如：{@code /a/{pathVariable}/c}，其中，{@code {pathVariable}} 就是路径变量。</p>
         *
         * @return 表示 Http 处理器的路径样式的 {@link String}。
         */
        String pathPattern();

        /**
         * 获取 Http 处理器的相关状态码。
         *
         * @return 表示 Http 处理器的相关状态码的 {@code int}。
         */
        int statusCode();

        /**
         * 获取 Http 处理器的元数据列表。
         *
         * @return 表示 Http 处理器的元数据列表的 {@link List}{@code <}{@link PropertyValueMetadata}{@code >}。
         */
        List<PropertyValueMetadata> propertyValueMetadata();

        /**
         * 获取 Http 处理器是否被忽略文档。
         *
         * @return 如果忽略文档，则返回 {@code true}，否则，返回 {@code false}。
         */
        boolean isDocumentIgnored();

        /**
         * 获取 Http 处理器的简短摘要。
         *
         * @return 表示 Http 处理器的简短摘要的 {@link String}。
         */
        String summary();

        /**
         * 获取 Http 处理器的描述信息。
         *
         * @return 表示 Http 处理器的描述信息的 {@link String}。
         */
        String description();

        /**
         * 获取 Http 处理器的返回值的描述信息。
         *
         * @return 表示 Http 处理器的返回值的描述信息的 {@link String}。
         */
        String returnDescription();

        /**
         * {@link StaticInfo} 的构建器。
         */
        interface Builder {
            /**
             * 向当前构建器中设置 Http 处理器的路径样式。
             *
             * @param pathPattern 表示待设置的 Http 处理器的路径样式的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder pathPattern(String pathPattern);

            /**
             * 向当前构建器中设置 Http 处理器的相关状态码。
             *
             * @param statusCode 表示待设置的 Http 处理器的相关状态码的 {@code int}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder statusCode(int statusCode);

            /**
             * 向当前构建器中设置 Http 处理器的元数据列表。
             *
             * @param propertyValueMetadata 表示待设置的 Http 处理器的元数据列表 {@link List}{@code
             * <}{@link PropertyValueMetadata}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder propertyValueMetadata(List<PropertyValueMetadata> propertyValueMetadata);

            /**
             * 向当前构建器中设置 Http 处理器是否忽略文档的标记。
             *
             * @param ignored 表示待设置的 Http 处理器是否忽略文档的标记的 {@code boolean}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder isDocumentIgnored(boolean ignored);

            /**
             * 向当前构建器中设置 Http 处理器的简短摘要。
             *
             * @param summary 表示待设置的 Http 处理器的简短摘要的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder summary(String summary);

            /**
             * 向当前构建器中设置 Http 处理器的描述信息。
             *
             * @param description 表示待设置的 Http 处理的描述信息的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder description(String description);

            /**
             * 向当前构建器中设置 Http 处理器的返回值的描述信息。
             *
             * @param returnDescription 表示待设置的 Http 处理器的返回值的描述信息的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder returnDescription(String returnDescription);

            /**
             * 构建对象。
             *
             * @return 表示构建出来的对象的 {@link StaticInfo}。
             */
            StaticInfo build();
        }

        /**
         * 获取 {@link StaticInfo} 的构建器。
         *
         * @return 表示 {@link StaticInfo} 的构建器的 {@link Builder}。
         */
        static Builder builder() {
            return builder(null);
        }

        /**
         * 获取 {@link StaticInfo} 的构建器，同时将指定对象的值进行填充。
         *
         * @param value 表示指定对象的 {@link StaticInfo}。
         * @return 表示 {@link StaticInfo} 的构建器的 {@link Builder}。
         */
        static Builder builder(StaticInfo value) {
            return BuilderFactory.get(StaticInfo.class, Builder.class).create(value);
        }
    }

    /**
     * 表示 Http 处理器的相关执行信息。
     */
    interface ExecutionInfo {
        /**
         * 获取 Http 处理器所属的服务器。
         *
         * @return 表示 Http 处理器所属服务器的 {@link HttpClassicServer}。
         */
        HttpClassicServer httpServer();

        /**
         * 获取 Http 处理器的前置过滤器列表。
         *
         * @return 表示 Http 处理器的前置过滤器列表的 {@link List}{@code <}{@link HttpServerFilter}{@code >}。
         */
        List<HttpServerFilter> preFilters();

        /**
         * 获取 Http 处理器的执行对象。
         *
         * @return 表示 Http 处理器的执行对象的 {@link Object}。
         */
        Object target();

        /**
         * 获取 Http 处理器的执行方法。
         *
         * @return 表示 Http 处理器的执行方法的 {@link Method}。
         */
        Method method();

        /**
         * 获取 Http 处理器的值映射器的列表。
         *
         * @return 表示 Http 处理器的值映射器列表的 {@link List}{@code <}{@link PropertyValueMapper}{@code >}。
         */
        List<PropertyValueMapper> httpMappers();

        /**
         * {@link ExecutionInfo} 的构建器。
         */
        interface Builder {
            /**
             * 向当前构建器中设置 Http 处理器所属的服务器。
             *
             * @param httpServer 表示待设置的 Http 处理器所属服务器的 {@link HttpClassicServer}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder httpServer(HttpClassicServer httpServer);

            /**
             * 向当前构建器中设置 Http 处理器的前置过滤器列表。
             *
             * @param preFilters 表示待设置的 Http 处理器的前置过滤器列表的 {@link List}{@code <}{@link
             * HttpServerFilter}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder preFilters(List<HttpServerFilter> preFilters);

            /**
             * 向当前构建器中设置 Http 处理器的执行对象。
             *
             * @param target 表示待设置的 Http 处理器的执行对象的 {@link Object}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder target(Object target);

            /**
             * 向当前构建器中设置 Http 处理器的执行方法。
             *
             * @param method 表示待设置的 Http 处理器的执行方法的 {@link Method}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder method(Method method);

            /**
             * 向当前构建器中设置 Http 处理器的值映射器列表。
             *
             * @param propertyValueMappers 表示待设置的 Http 处理器的值映射器列表的 {@link List}{@code <}{@link
             * PropertyValueMapper}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder httpMappers(List<PropertyValueMapper> propertyValueMappers);

            /**
             * 构建对象。
             *
             * @return 表示构建出来的对象的 {@link ExecutionInfo}。
             */
            ExecutionInfo build();
        }

        /**
         * 获取 {@link ExecutionInfo} 的构建器。
         *
         * @return 表示 {@link ExecutionInfo} 的构建器的 {@link Builder}。
         */
        static Builder builder() {
            return builder(null);
        }

        /**
         * 获取 {@link ExecutionInfo} 的构建器，同时将指定对象的值进行填充。
         *
         * @param value 表示指定对象的 {@link ExecutionInfo}。
         * @return 表示 {@link ExecutionInfo} 的构建器的 {@link Builder}。
         */
        static Builder builder(ExecutionInfo value) {
            return BuilderFactory.get(ExecutionInfo.class, Builder.class).create(value);
        }
    }
}
