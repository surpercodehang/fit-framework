/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.util;

/**
 * 为Java对象提供工具方法。
 *
 * @author 梁济时
 * @since 1.0
 */
public final class ObjectUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private ObjectUtils() {}

    /**
     * 当指定对象为 {@code null} 时使用默认对象，否则继续使用指定对象。
     *
     * @param value 表示指定的对象。
     * @param defaultValue 表示当指定对象为 {@code null} 时使用的默认对象。
     * @param <T> 表示对象的实际类型。
     * @return 若 {@code value} 为 {@code null}，则为 {@code defaultValue}；否则为 {@code value}。
     */
    public static <T> T nullIf(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
