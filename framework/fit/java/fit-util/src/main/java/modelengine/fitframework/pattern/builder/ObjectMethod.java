/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.builder;

import java.util.Map;

/**
 * 表示 {@link Object} 的方法。
 *
 * @author 季聿阶
 * @since 2022-06-23
 */
@FunctionalInterface
public interface ObjectMethod {
    /**
     * 调用 {@link Object} 的方法。
     *
     * @param args 表示调用的具体参数的 {@link Object}{@code []}。
     * @param clazz 表示调用方法所属的实际类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @param fields 表示调用方法所属类中的所有属性的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示调用后的返回值的 {@link Object}。
     */
    Object invoke(Object[] args, Class<?> clazz, Map<String, Object> fields);
}
