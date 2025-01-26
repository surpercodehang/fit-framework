/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client;

import modelengine.fit.client.support.DefaultRequest;
import modelengine.fitframework.serialization.RequestMetadata;

import java.lang.reflect.Type;

/**
 * 表示请求。
 *
 * @author 季聿阶
 * @since 2022-09-19
 */
public interface Request {
    /**
     * 获取请求的协议。
     *
     * @return 表示请求协议的 {@link String}。
     */
    String protocol();

    /**
     * 获取请求。
     *
     * @return 表示请求地址的 {@link Address}。
     */
    Address address();

    /**
     * 获取请求元数据信息。
     *
     * @return 表示请求元数据的 {@link RequestMetadata}。
     */
    RequestMetadata metadata();

    /**
     * 获取请求数据的类型数组。
     *
     * @return 表示请求数据的类型数组的 {@link Type}{@code []}。
     */
    Type[] dataTypes();

    /**
     * 获取请求数据。
     *
     * @return 表示请求数据的 {@code byte[]}。
     */
    Object[] data();

    /**
     * 获取响应数据的类型。
     *
     * @return 表示响应数据类型的 {@link Type}。
     */
    Type returnType();

    /**
     * 获取请求的上下文信息。
     *
     * @return 表示请求上下文的 {@link RequestContext}。
     */
    RequestContext context();

    /**
     * 表示 {@link Request} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置请求的协议。
         *
         * @param protocol 表示待设置的请求协议的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder protocol(String protocol);

        /**
         * 向当前构建器中设置请求的地址。
         *
         * @param address 表示待设置的请求地址的 {@link Address}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder address(Address address);

        /**
         * 向当前构建器中设置请求的元数据。
         *
         * @param metadata 表示待设置的请求元数据的 {@link RequestMetadata}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder metadata(RequestMetadata metadata);

        /**
         * 向当前构建器中设置请求参数的类型列表。
         *
         * @param types 表示待设置的请求参数的类型列表的 {@link Type}{@code []}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder dataTypes(Type[] types);

        /**
         * 向当前构建器中设置请求的数据列表。
         *
         * @param data 表示待设置的请求数据列表的 {@link Object}{@code []}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder data(Object[] data);

        /**
         * 向当前构建器中设置请求的返回值类型。
         *
         * @param type 表示待设置的请求返回值类型的 {@link Type}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder returnType(Type type);

        /**
         * 向当前构建器中设置请求的上下文。
         *
         * @param context 表示待设置的请求上下文的 {@link RequestContext}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder context(RequestContext context);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Request}。
         */
        Request build();
    }

    /**
     * 获取 {@link Request} 的构建器。
     *
     * @return 表示 {@link Request} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return custom(null);
    }

    /**
     * 获取 {@link Request} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link Request}。
     * @return 表示 {@link Request} 的构建器的 {@link Builder}。
     */
    static Builder custom(Request value) {
        return new DefaultRequest.Builder(value);
    }
}
