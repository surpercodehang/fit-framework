/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger;

import java.util.Map;

/**
 * 表示可序列化为 JSON 对象的接口。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public interface Serializable {
    /**
     * 将当前对象转换成 JSON 格式。
     *
     * @return 表示 JSON 格式的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> toJson();
}
