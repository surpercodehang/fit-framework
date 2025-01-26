/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.DefaultPaths;

import java.util.Map;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#paths-object">OpenAPI 3.1.0
 * </a> 文档中的所有路径信息。
 *
 * @author 季聿阶
 * @since 2023-08-21
 */
public interface Paths extends Serializable {
    /**
     * 判断指定的路径是否存在于所有路径信息中。
     *
     * @param path 表示指定的路径信息的 {@link String}。
     * @return 如果指定路径存在于所有路径中，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean contains(String path);

    /**
     * 将指定路径信息设置进所有路径信息中。
     *
     * @param path 表示指定路径的 {@link String}。
     * @param pathItem 表示指定路径的详细信息的 {@link PathItem}。
     */
    void put(String path, PathItem pathItem);

    /**
     * 获取指定路径的详细信息。
     *
     * @param path 表示指定路径的 {@link String}。
     * @return 表示指定路径的详细信息的 {@link PathItem}。
     */
    PathItem get(String path);

    /**
     * 获取所有路径信息的 {@link Map}{@code <}{@link String}{@code , }{@link PathItem}{@code >}。
     *
     * @return 表示所有路径信息的 {@link Map}{@code <}{@link String}{@code , }{@link PathItem}{@code >}。
     */
    Map<String, PathItem> getPathItems();

    /**
     * 创建一份空的所有路径信息。
     *
     * @return 表示创建出来的空的所有路径信息的 {@link Paths}。
     */
    static Paths create() {
        return new DefaultPaths();
    }
}
