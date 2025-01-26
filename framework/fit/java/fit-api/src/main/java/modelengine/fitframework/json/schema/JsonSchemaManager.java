/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.json.schema;

import modelengine.fitframework.json.schema.support.DefaultJsonSchemaManager;
import modelengine.fitframework.json.schema.support.ObjectSchema;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * 表示 {@link JsonSchema} 的管理器。
 *
 * @author 季聿阶
 * @since 2024-03-31
 */
public interface JsonSchemaManager {
    /**
     * 创建一个默认的 Json 格式规范的管理器。
     *
     * @return 表示创建出来的默认的 Json 格式规范管理器。
     */
    static JsonSchemaManager create() {
        return new DefaultJsonSchemaManager();
    }

    /**
     * 对指定类型创建一个 Json 格式规范。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @return 表示创建出来的 Json 格式规范的 {@link JsonSchema}。
     */
    JsonSchema createSchema(Type type);

    /**
     * 对指定类型集合创建 Json 格式规范集合。
     * <p>如果他们之间存在引用关系，则会一起创建引用关系。</p>
     *
     * @param types 表示指定类型集合的 {@link Set}{@code <}{@link Type}{@code >}。
     * @return 表示创建的 Json 格式规范集合的 {@link Map}{@code <}{@link Type}{@code , JsonSchema}{@link }{@code >}。
     */
    Map<Type, JsonSchema> createSchemas(Set<Type> types);

    /**
     * 对指定类型集合创建 Json 格式规范集合。
     * <p>如果他们之间存在引用关系，则会一起创建引用关系。</p>
     *
     * @param types 表示指定类型集合的 {@link Set}{@code <}{@link Type}{@code >}。
     * @param referencedPrefix 表示指定引用前缀的 {@link String}。
     * @return 表示创建的 Json 格式规范集合的 {@link Map}{@code <}{@link Type}{@code , JsonSchema}{@link }{@code >}。
     */
    Map<Type, JsonSchema> createSchemas(Set<Type> types, String referencedPrefix);

    /**
     * 将指定方法的参数创建 Json 格式规范。
     * <p>方法的多个参数会形成一个 {@link ObjectSchema} 类型的格式规范。</p>
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示创建的格式规范的 {@link JsonSchema}。
     */
    JsonSchema createSchema(Method method);
}
