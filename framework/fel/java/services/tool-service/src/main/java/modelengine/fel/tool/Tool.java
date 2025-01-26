/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.tool;

import modelengine.fel.core.tool.ToolInfo;
import modelengine.fel.tool.support.DefaultToolMetadata;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表示大模型的工具。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
public interface Tool {
    /**
     * 获取工具的基本信息。
     *
     * @return 表示商品信息的 {@link Info}。
     */
    Info info();

    /**
     * 获取工具的元数据。
     *
     * @return 表示工具元数据的 {@link Metadata}。
     */
    Metadata metadata();

    /**
     * 调用工具。
     *
     * @param args 表示调用工具的参数列表的 {@link Object}{@code []}。
     * @return 表示调用工具的结果的 {@link Object}。
     */
    Object execute(Object... args);

    /**
     * 使用 Json 格式的参数调用工具。
     * <p>该调用的参数实际上是将 {@link #executeWithJsonObject(Map)} 方法的参数进行了一次 Json 序列化。</p>
     * <p>该调用的返回值实际上是将工具调用的原始返回值进行了一次 Json 序列化。</p>
     *
     * @param jsonArgs 表示调用工具的 Json 格式的参数的 {@link String}。
     * @return 表示调用工具的 Json 结果的 {@link Object}。
     */
    Object executeWithJson(String jsonArgs);

    /**
     * 使用 Json 格式的对象参数调用工具。
     * <p>该调用的参数实际上是将 {@link #execute(Object...)} 方法的参数按照参数名加值的方式，拼装成了一个对象。</p>
     * <p>该调用的返回值就是工具调用的原始返回值。</p>
     *
     * @param jsonObject 表示调用工具的 Json 格式的对象参数的 {@link Map}{@code <}{@link String}{@code , }{@link
     * Object}{@code >}。
     * @return 表示调用工具的结果的 {@link Object}。
     */
    Object executeWithJsonObject(Map<String, Object> jsonObject);

    /**
     * 表示工具信息。
     */
    interface Info extends ToolInfo {
        /**
         * 获取工具的格式规范描述。
         *
         * @return 表示工具的格式规范描述的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
         */
        Map<String, Object> schema();

        /**
         * 获取工具的运行规范描述。
         *
         * @return 表示工具的格式规范描述的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
         */
        Map<String, Object> runnables();
    }

    /**
     * 表示工具的元数据信息。
     */
    interface Metadata {
        /**
         * 获取工具的参数类型列表。
         *
         * @return 表示工具的参数类型列表的 {@link List}{@code <}{@link Type}{@code >}。
         */
        List<Type> parameterTypes();

        /**
         * 获取工具的参数名字序列，按照参数定义顺序排序。
         *
         * @return 表示工具的参数名字列表的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> parameterOrder();

        /**
         * 获取工具参数列表中指定名字的参数的默认值。
         *
         * @param name 表示指定参数名字的 {@link String}。
         * @return 表示指定名字参数的默认值的 {@link Object}。
         */
        Object parameterDefaultValue(String name);

        /**
         * 获取工具的参数列表中，必须的参数名字列表。
         *
         * @return 表示必须的参数名字列表的 {@link List}{@code <}{@link String}{@code >}。
         */
        Set<String> requiredParameters();

        /**
         * 获取工具的返回值类型。
         *
         * @return 表示工具返回值类型 JsonSchema 格式的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         */
        Map<String, Object> returnType();

        /**
         * 获取输出转换器的工具方法。
         *
         * @return 表示输出转换器工具方法的 {@link String}。
         */
        String returnConverter();

        /**
         * 通过工具的格式规范创建一个可配置的元数据信息。
         *
         * @param schema 表示工具的格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         * @return 表示通过工具的格式规范创建一个可配置的元数据信息的 {@link Metadata}。
         * @throws IllegalArgumentException 当 {@code schema} 为 {@code null} 时。
         */
        static Metadata from(Map<String, Object> schema) {
            return new DefaultToolMetadata(schema);
        }
    }
}
