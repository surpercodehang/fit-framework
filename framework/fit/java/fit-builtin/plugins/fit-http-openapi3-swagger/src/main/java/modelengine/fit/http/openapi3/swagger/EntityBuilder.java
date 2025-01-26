/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger;

/**
 * 表示满足 OpenAPI 规范的实体对象的构建器。
 *
 * @param <T> 表示待构建的实体对象的类型的 {@link T}。
 * @author 季聿阶
 * @since 2023-08-23
 */
public interface EntityBuilder<T> {
    /**
     * 构建实体对象。
     *
     * @return 表示构建出来的实体对象的 {@link T}。
     */
    T build();
}
