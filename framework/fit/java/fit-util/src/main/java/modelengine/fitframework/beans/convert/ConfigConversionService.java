/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans.convert;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * 为 {@link ConversionService} 提供配置系统的实现。
 *
 * @author 梁济时
 * @since 2022-12-28
 */
final class ConfigConversionService extends AbstractConversionService {
    /** 获取当前类型的单例实现。 */
    static final ConversionService INSTANCE = new ConfigConversionService();

    private ConfigConversionService() {}

    @Override
    public Object convert(Object value, Type type) {
        if (type == null) {
            return value;
        } else if (type instanceof Class) {
            Type withDefault = TypeUtils.withDefault((Class<?>) type);
            if (type == withDefault) {
                return this.as(value, (Class<?>) type);
            } else {
                return this.convert(value, withDefault);
            }
        } else if (type instanceof ParameterizedType) {
            return this.as(value, (ParameterizedType) type);
        } else {
            throw new IllegalArgumentException(StringUtils.format("Cannot convert value to target type. [target={0}]",
                    type.getTypeName()));
        }
    }

    @Override
    protected Object transform(Object value, Class<?> type) {
        if (type == Object.class) {
            return value;
        }
        if (value instanceof List) {
            List<?> list = cast(value);
            return list.isEmpty() ? null : list.get(0);
        }
        return value;
    }

    @Override
    protected Object toCustomObject(Object value, ParameterizedType type) {
        throw new IllegalStateException(StringUtils.format("Unsupported parameterized type. [target={0}]",
                type.getTypeName()));
    }

    @Override
    protected List<?> transformToList(Object value) {
        return Collections.singletonList(value);
    }
}
