/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.builder;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 调用 {@link Object} 方法的工具类。
 *
 * @author 季聿阶
 * @since 2022-06-23
 */
public class ObjectMethodUtils {
    private static final Map<String, ObjectMethod> OBJECT_METHODS;
    private static final Method TO_MAP_METHOD;

    static {
        OBJECT_METHODS = MapBuilder.<String, ObjectMethod>get()
                .put("equals", ObjectMethodUtils::invokeObjectEquals)
                .put("hashCode", ObjectMethodUtils::invokeObjectHashCode)
                .put("toString", ObjectMethodUtils::invokeObjectToString)
                .build();
        try {
            TO_MAP_METHOD = ObjectProxy.class.getDeclaredMethod("$toMap");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Method not found. [methodName=$toMap]", e);
        }
    }

    /**
     * 获取 {@link ObjectProxy#$toMap()} 的方法名。
     *
     * @return 表示{@link ObjectProxy#$toMap()} 的方法名的 {@link String}。
     */
    public static String getToMapMethodName() {
        return TO_MAP_METHOD.getName();
    }

    /**
     * 调用 {@link Object} 的方法。
     *
     * @param method 表示调用的具体方法的 {@link Method}。
     * @param args 表示调用的具体参数的 {@link Object}{@code []}。
     * @param clazz 表示调用方法所属的实际类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @param fields 表示调用方法所属类中的所有属性的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示调用后的返回值的 {@link Object}。
     */
    public static Object invokeObjectMethod(Method method, Object[] args, Class<?> clazz, Map<String, Object> fields) {
        ObjectMethod objectMethod = OBJECT_METHODS.get(method.getName());
        if (objectMethod == null) {
            throw new UnsupportedOperationException(StringUtils.format("Not supported method. [methodName={0}]",
                    method.getName()));
        }
        return objectMethod.invoke(args, clazz, fields);
    }

    private static Object invokeObjectEquals(Object[] args, Class<?> clazz, Map<String, Object> fields) {
        Validation.notNull(args, "No args in Object.equals(Object obj).");
        Validation.equals(args.length, 1, "Args mismatch in Object.equals(Object obj). [argsLength={0}]", args.length);
        if (!(args[0] instanceof ObjectProxy)) {
            return false;
        }
        ObjectProxy arg = ObjectUtils.cast(args[0]);
        return fields.equals(arg.$toMap());
    }

    private static Object invokeObjectToString(Object[] args, Class<?> clazz, Map<String, Object> fields) {
        return clazz.getName() + fields.toString();
    }

    private static Object invokeObjectHashCode(Object[] args, Class<?> clazz, Map<String, Object> fields) {
        return fields.hashCode();
    }
}
