/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans.convert;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Type;

/**
 * 为对象类型转换提供服务。
 *
 * @author 梁济时
 * @since 2022-12-28
 */
public interface ConversionService {
    /**
     * 在指定的类型中发现对象类型转换程序的定义。
     *
     * @param clazz 表示待从中发现转换程序定义的类型的 {@link Class}。
     */
    void discover(Class<?> clazz);

    /**
     * 注册一个转换程序。
     *
     * @param converter 表示待注册的转换程序的 {@link ValueConverter}。
     */
    void register(ValueConverter converter);

    /**
     * 注册一组转换程序。
     *
     * @param converters 表示待注册的转换程序的 {@link Iterable}{@code <}{@link ValueConverter}{@code >}。
     */
    void register(Iterable<ValueConverter> converters);

    /**
     * 检查指定类型是否是一个标量类型。
     *
     * @param type 表示待检查的类型的 {@link Type}。
     * @return 若是标量类型，则为 {@code true}，否则为 {@code false}。
     */
    boolean scalar(Type type);

    /**
     * 将原始值转为目标类型。
     *
     * @param value 表示原始值的 {@link Object}。
     * @param type 表示目标类型的 {@link Class}。
     * @param <T> 表示目标类型。
     * @return 表示转换后的值的 {@link Object}。
     */
    default <T> T convert(Object value, Class<T> type) {
        return cast(this.convert(value, ObjectUtils.<Type>cast(type)));
    }

    /**
     * 将原始值转为目标类型。
     *
     * @param value 表示原始值的 {@link Object}。
     * @param type 表示目标类型的 {@link Type}。
     * @return 表示转换后的值的 {@link Object}。
     */
    Object convert(Object value, Type type);

    /**
     * 获取转换服务的标准实现的实例。
     *
     * @return 表示转换服务的标准实现的实例的 {@link ConversionService}。
     */
    static ConversionService forStandard() {
        return StandardConversionService.INSTANCE;
    }

    /**
     * 获取转换服务的为配置系统实现的实例。
     *
     * @return 表示转换服务为配置系统实现的实例的 {@link ConversionService}。
     */
    static ConversionService forConfig() {
        return ConfigConversionService.INSTANCE;
    }
}
