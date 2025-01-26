/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.external;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.ohscript.external.support.DefaultHttpExecutor;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;

import java.util.Map;

/**
 * 表示 Http 调用的执行器。
 *
 * @author 季聿阶
 * @since 2023-12-21
 */
@FunctionalInterface
public interface HttpExecutor {
    /**
     * 表示 Http 请求中消息头的键。
     */
    String REQUEST_HEADERS = ".headers";

    /**
     * 表示 Http 请求中消息体的键。
     */
    String REQUEST_ENTITY = ".body";

    /**
     * 表示 Http 请求中消息体中类型的键。
     */
    String REQUEST_ENTITY_TYPE = ".type";

    /**
     * 表示 Http 请求中消息体中数据的键。
     */
    String REQUEST_ENTITY_DATA = ".data";

    /**
     * 创建一个新的 Http 执行器。
     *
     * @param httpClient 表示 Http 客户端的 {@link HttpClassicClient}。
     * @return 表示创建出来的 Http 执行器的 {@link HttpExecutor}。
     */
    static HttpExecutor create(HttpClassicClient httpClient) {
        return new DefaultHttpExecutor(httpClient);
    }

    /**
     * 使用 Http 客户端，向指定地址发送请求。
     * <p>参数中可能出现的参数如下：
     * <ul>
     *     <li>{@link #REQUEST_HEADERS}：表示消息头</li>
     *     <li>{@link #REQUEST_ENTITY}：表示消息体</li>
     *     <li>{@link #REQUEST_ENTITY_TYPE}：在消息体的值内，表示消息体的类型</li>
     *     <li>{@link #REQUEST_ENTITY_DATA}：在消息体的值内，表示消息体的数据</li>
     * </ul>
     * 参数样例如下：
     * <pre>
     *     let request = entity{
     *         .headers = entity{
     *             .h1 = "v4";
     *          };
     *         .body = entity{
     *             .type = "form";
     *             .data = entity{
     *                 .f1 = "v1";
     *                 .f2 = "v2";
     *             };
     *         };
     *     };
     * </pre>
     * </p>
     *
     * @param method 表示 Http 调用的方法的 {@link String}。
     * @param url 表示 Http 调用地址的 {@link String}。
     * @param args 表示 Http 调用的参数的 {@link Map}{@code <}{@link String}{@code , }{@link ReturnValue}{@code >}。
     * @return 表示 Http 调用的响应的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> execute(String method, String url, Map<String, ReturnValue> args);
}
