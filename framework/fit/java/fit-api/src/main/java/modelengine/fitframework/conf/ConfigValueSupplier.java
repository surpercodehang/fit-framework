/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

/**
 * 表示配置的通用获取值的方法。
 *
 * @author 季聿阶
 * @since 2023-06-26
 */
@FunctionalInterface
public interface ConfigValueSupplier {
    /**
     * 获取指定键的配置的值。
     * <p>配置值类型的有效值域为：
     * <ul>
     *     <li>{@link java.math.BigInteger}</li>
     *     <li>{@link java.math.BigDecimal}</li>
     *     <li>{@link java.lang.Boolean}</li>
     *     <li>{@link java.lang.String}</li>
     *     <li>{@link java.util.Date}</li>
     * </ul></p>
     *
     * @param key 表示配置的键的 {@link String}。
     * @return 若存在配置，则为配置的值的 {@link Object}，否则为 {@code null}。
     */
    Object get(String key);

    /**
     * 从指定配置中获取指定键的值。
     *
     * @param conf 表示指定配置的 {@link Config}。
     * @param key 表示指定键的 {@link String}。
     * @return 表示指定键的值的 {@link Object}。
     */
    static Object get(Config conf, String key) {
        if (conf instanceof ConfigValueSupplier) {
            return ((ConfigValueSupplier) conf).get(key);
        } else {
            return null;
        }
    }
}
