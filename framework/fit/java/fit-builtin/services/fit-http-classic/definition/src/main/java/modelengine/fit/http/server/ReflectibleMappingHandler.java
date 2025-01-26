/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import modelengine.fit.http.server.handler.PropertyValueMetadata;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 表示 Http 请求的资源存储处理器。
 *
 * @author 邬涨财
 * @since 2022-12-13
 */
public interface ReflectibleMappingHandler extends HttpHandler {
    /**
     * 获取 Http 请求调用的方法的所属对象。
     *
     * @return 表示 Http 请求调用的方法的所属对象的 {@link Object}。
     */
    Object target();

    /**
     * 获取 Http 请求调用的方法。
     *
     * @return 表示 Http 请求调用的方法的 {@link Method}。
     */
    Method method();

    /**
     * 获取 Http 处理器的元数据列表。
     *
     * @return 表示 Http 处理器的元数据列表的 {@link List}{@code <}{@link PropertyValueMetadata}{@code >}。
     */
    List<PropertyValueMetadata> propertyValueMetadata();

    /**
     * 获取 Http 请求调用的返回状态码。
     *
     * @return Http 请求调用的返回状态码的 {@code int}。
     */
    int statusCode();

    /**
     * 判断 Http 处理器是否忽略文档。
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
     * 设置 Http 处理器所属的分组的名字。
     *
     * @param groupName 表示 Http 处理器所属的分组的名字的 {@link String}。
     */
    void group(String groupName);

    /**
     * 获取 Http 处理器所属的分组的名字。
     *
     * @return 表示 Http 处理器所属的分组的名字的 {@link String}。
     */
    String group();
}
