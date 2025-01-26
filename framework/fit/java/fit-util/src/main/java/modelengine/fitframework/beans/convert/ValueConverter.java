/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans.convert;

import modelengine.fitframework.util.convert.Converter;

import java.lang.reflect.Method;

/**
 * 为对象提供转换程序。
 *
 * @author 梁济时
 * @since 2022-12-27
 */
public interface ValueConverter extends Converter {
    /**
     * 获取支持转换的源类型。
     *
     * @return 表示源类型的 {@link Class}。
     */
    Class<?> source();

    /**
     * 获取支持转换到的目标类型。
     *
     * @return 表示目标类型的 {@link Class}。
     */
    Class<?> target();

    /**
     * 为指定的方法生成对象类型转换程序。
     *
     * @param method 表示用以转换对象的方法的 {@link Method}。
     * @return 表示使用该方法转换对象的类型转换程序的 {@link ValueConverter}。
     */
    static ValueConverter of(Method method) {
        return new MethodValueConverter(method);
    }
}
