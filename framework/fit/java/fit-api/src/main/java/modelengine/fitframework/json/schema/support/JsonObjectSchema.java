/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.json.schema.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import java.util.Map;

/**
 * 表示 {@link modelengine.fitframework.json.schema.JsonSchema} 的原生 Json 对象实现。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
public class JsonObjectSchema extends AbstractJsonSchema {
    private final Map<String, Object> jsonObject;

    /**
     * 通过规范名字、描述、Json 对象和 Json 序列化器来初始化 {@link JsonObjectSchema} 的新实例。
     *
     * @param name 表示规范名字的 {@link String}。
     * @param description 表示规范描述的 {@link String}。
     * @param jsonObject 表示 Json 对象的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public JsonObjectSchema(String name, String description, Map<String, Object> jsonObject) {
        super(Map.class, name, description);
        this.jsonObject = notNull(jsonObject, "The json object cannot be null.");
    }

    @Override
    public Map<String, Object> toJsonObject() {
        return this.jsonObject;
    }
}
