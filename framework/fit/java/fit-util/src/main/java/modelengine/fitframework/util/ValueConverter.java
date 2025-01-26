/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import modelengine.fitframework.inspection.Validation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 为配置值的类型转换提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-08-26
 */
public class ValueConverter {
    private static final Map<Class<?>, Function<String, Object>> PARSERS = new HashMap<>();

    static {
        PARSERS.put(Byte.class, str -> tryParse(str, Byte::parseByte));
        PARSERS.put(Short.class, str -> tryParse(str, Short::parseShort));
        PARSERS.put(Integer.class, str -> tryParse(str, Integer::parseInt));
        PARSERS.put(Long.class, str -> tryParse(str, Long::parseLong));
        PARSERS.put(Float.class, str -> tryParse(str, Float::parseFloat));
        PARSERS.put(Double.class, str -> tryParse(str, Double::parseDouble));
        PARSERS.put(Character.class, str -> {
            String actual = StringUtils.trim(str);
            if (actual.length() == 1) {
                return str.charAt(0);
            } else {
                return null;
            }
        });
        PARSERS.put(Boolean.class, str -> {
            String actual = StringUtils.trim(str);
            if (StringUtils.equalsIgnoreCase(actual, "true")) {
                return true;
            } else if (StringUtils.equalsIgnoreCase(actual, "false")) {
                return false;
            } else {
                return null;
            }
        });
        PARSERS.put(Date.class, str -> tryParse(str, DateUtils::parse));
        PARSERS.put(String.class, str -> str);
    }

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private ValueConverter() {}

    /**
     * 将字符串类型的配置值转换成为目标类型的值。
     *
     * @param source 表示配置值的字符串表现形式的 {@link String}。
     * @param targetClass 表示目标的配置值的类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 表示配置值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code targetClass} 为 {@code null} 时。
     * @throws ClassCastException 当不支持将值转为目标类型时。
     */
    public static Object convert(String source, Class<?> targetClass) {
        Validation.notNull(targetClass, "Target class to convert to cannot be null.");
        if (source == null) {
            return null;
        }
        Class<?> actualTargetClass = ReflectionUtils.ignorePrimitiveClass(targetClass);
        Function<String, Object> parser = PARSERS.get(actualTargetClass);
        Validation.notNull(parser,
                () -> new ClassCastException(StringUtils.format(
                        "Not supported class to resolve configuration. [targetClass={0}]",
                        targetClass.getSimpleName())));
        return parser.apply(source);
    }

    private static <T> T tryParse(String source, Function<String, T> mapper) {
        String actual = StringUtils.trim(source);
        if (StringUtils.isBlank(actual)) {
            return null;
        }
        try {
            return mapper.apply(actual);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
