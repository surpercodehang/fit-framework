/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.beans.BeanAccessor;
import modelengine.fitframework.beans.convert.ConversionService;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 为 Java 对象提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class ObjectUtils {
    /** 表示基本类型到默认值的映射。 */
    private static final Map<Class<?>, Object> DEFAULT_VALUES = MapBuilder.<Class<?>, Object>get()
            .put(byte.class, (byte) 0)
            .put(short.class, (short) 0)
            .put(int.class, 0)
            .put(long.class, 0L)
            .put(float.class, 0f)
            .put(double.class, 0d)
            .put(char.class, '\u0000')
            .put(boolean.class, false)
            .build();

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private ObjectUtils() {}

    /**
     * 检查指定值是否在指定的左（前）闭右（后）闭有效区间内。
     *
     * @param value 表示待检查的值的 {@link Comparable}。
     * @param min 表示有效区间的最小值的 {@link Comparable}。
     * @param max 表示有效区间的最大值的 {@link Comparable}。
     * @param <T> 表示待检查的值的类型的 {@link T}。
     * @return 若值在有效区间内，则为 {@code true}；否则为 {@code false}。
     */
    public static <T extends Comparable<T>> boolean between(T value, T min, T max) {
        return between(value, min, max, true, true);
    }

    /**
     * 检查指定值是否在指定有效区间内。
     *
     * @param value 表示待检查的值的 {@link Comparable}。
     * @param min 表示有效区间的最小值的 {@link Comparable}。
     * @param max 表示有效区间的最大值的 {@link Comparable}。
     * @param includeMin 若为 {@code true}，则区间的最小值在有效值域内；否则区间的最小值不在有效值域内。
     * @param includeMax 若为 {@code true}，则区间的最大值在有效值域内；否则区间的最大值不在有效值域内。
     * @param <T> 表示待检查的值的类型的 {@link T}。
     * @return 若值在有效区间内，则为 {@code true}；否则为 {@code false}。
     */
    public static <T extends Comparable<T>> boolean between(T value, T min, T max, boolean includeMin,
            boolean includeMax) {
        return compare(value, min) + (includeMin ? 1 : 0) > 0 && compare(value, max) - (includeMax ? 1 : 0) < 0;
    }

    /**
     * 比较两个对象。
     * <p>{@code null} 小于其他任何对象。</p>
     *
     * @param obj1 表示待比较的第一个对象的 {@link T}。
     * @param obj2 表示待比较的第二个对象的 {@link T}。
     * @param <T> 表示待比较对象的类型的 {@link T}。
     * @return 若第一个对象大于第二个对象，则返回一个正数；若第一个对象小于第二个对象，则返回一个负数；否则返回 {@code 0}。
     */
    public static <T extends Comparable<T>> int compare(T obj1, T obj2) {
        return compare(obj1, obj2, Comparator.naturalOrder());
    }

    /**
     * 使用指定比较器来比较两个对象。
     * <p><b>注意：{@code null} 小于非 {@code null} 值。</b></p>
     *
     * @param obj1 表示待比较的第一个对象的 {@link T}。
     * @param obj2 表示待比较的第二个对象的 {@link T}。
     * @param comparator 表示用以比较两个对象的比较器的 {@link Comparator}{@code <}{@link T}{@code >}。
     * <b>其中，传入比较器的所有参数永远不会为 {@code null}。</b>
     * @param <T> 表示待比较对象的类型的 {@link T}。
     * @return 若第一个对象大于第二个对象，则返回一个正数；若第一个对象小于第二个对象，则返回一个负数；否则返回 {@code 0}。
     * @throws IllegalArgumentException 当 {@code comparator} 为 {@code null} 时。
     */
    public static <T> int compare(T obj1, T obj2, Comparator<T> comparator) {
        notNull(comparator, "The comparator to compare objects cannot be null.");
        if (obj1 == null) {
            if (obj2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (obj2 == null) {
                return 1;
            } else {
                return comparator.compare(obj1, obj2);
            }
        }
    }

    /**
     * 获取指定类型的默认值。
     *
     * @param clazz 表示待获取默认值的类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param <T> 表示默认值的类型的 {@link T}。
     * @return 若类型是一个基本类型，则为其默认值的包装类的实例；否则为 {@code null}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时。
     */
    public static <T> T defaultValue(Class<T> clazz) {
        notNull(clazz, "The class to look up default value cannot be null.");
        return cast(DEFAULT_VALUES.get(clazz));
    }

    /**
     * 当 {@code value} 为 {@code null} 时，使用指定的 {@code factory} 获取对象实例，否则继续使用指定对象。
     *
     * @param value 表示指定的对象的 {@link T}。
     * @param factory 表示当指定对象为 {@code null} 时使用的指定 {@link Supplier}{@code <}{@link T}{@code >}。
     * @param <T> 表示对象的实际类型的 {@link T}。
     * @return 若 {@code value} 为 {@code null}，则为 {@link Supplier#get()}；否则为 {@code value}。
     * @throws IllegalArgumentException 当 {@code factory} 为 {@code null} 时。
     */
    public static <T> T getIfNull(T value, Supplier<T> factory) {
        notNull(factory, "The factory to create default value cannot be null.");
        return value == null ? factory.get() : value;
    }

    /**
     * 当 {@code value} 不为 {@code null} 时，使用指定转换方法进行转换，否则不进行转换。
     *
     * @param value 表示待转换的值的 {@link T}。
     * @param mapper 表示转换方法的 {@link Function}{@code <}{@link T}{@code , }{@link R}{@code >}。
     * @param <T> 表示原始值的类型的 {@link T}。
     * @param <R> 表示目标值的类型的 {@link R}。
     * @return 若 {@code value} 为 {@code null}，则为 {@code null}；否则为使用转换方法转换后的值的 {@link R}。
     * @throws IllegalArgumentException 当 {@code mapper} 为 {@code null} 时。
     */
    public static <T, R> R mapIfNotNull(T value, Function<T, R> mapper) {
        notNull(mapper, "The mapper cannot be null.");
        return value == null ? null : mapper.apply(value);
    }

    /**
     * 当 {@code value} 为 {@code null} 时，使用 {@code defaultValue}，否则继续使用 {@code value}。
     *
     * @param value 表示指定的对象的 {@link T}。
     * @param defaultValue 表示当指定对象为 {@code null} 时使用的默认对象的 {@link T}。
     * @param <T> 表示对象的实际类型的 {@link T}。
     * @return 若 {@code value} 为 {@code null}，则为 {@code defaultValue}；否则为 {@code value}。
     */
    public static <T> T nullIf(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * 对指定对象进行标准化操作。
     * <p>标准化操作为如果指定对象为 {@code null}，则返回空字符串，否则，返回指定对象自身的 {@link Object#toString()}。</p>
     *
     * @param obj 表示指定对象的 {@link Object}。
     * @return 表示标准化之后的字符串的 {@link String}。
     * @see #toString(Object)
     * @see StringUtils#normalize(String)
     */
    public static String toNormalizedString(Object obj) {
        return StringUtils.normalize(toString(obj));
    }

    /**
     * 返回指定对象的字符串表现形式。
     *
     * @param obj 表示待转为字符串表现形式的对象的 {@link Object}。
     * @return 若对象为 {@code null}，则为 {@code null}；否则为其{@link Object#toString() 字符串表现形式}。
     */
    public static String toString(Object obj) {
        return mapIfNotNull(obj, Object::toString);
    }

    /**
     * 将对象强制转换为指定类型。
     *
     * @param obj 表示待转换的对象的 {@link Object}。
     * @param <T> 表示强制转换到的目标类型的 {@link T}。
     * @return 表示强制转换后的对象的 {@link Object}。
     */
    public static <T> T cast(Object obj) {
        // noinspection unchecked
        return (T) obj;
    }

    /**
     * 将对象尝试转换成指定类型，如果不能转换，则返回 {@code null}。
     *
     * @param obj 表示待转换的对象的 {@link Object}。
     * @param clazz 表示尝试转换到的目标类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param <T> 表示尝试转换后的类型的 {@link T}。
     * @return 表示转换后的对象的 {@link T}。
     */
    public static <T> T as(Object obj, Class<T> clazz) {
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        } else {
            return null;
        }
    }

    /**
     * 将两个对象组合为一个对象。
     *
     * @param object1 表示待组合的第一个对象的 {@link Object}。
     * @param object2 表示待组合的第二个对象的 {@link Object}。
     * @param combiner 表示将两个对象组合为一个对象的组合程序的 {@link BinaryOperator}。
     * @param <T> 表示待组合对象的实际类型。
     * @return 表示组合后的对象的 {@link Object}。
     */
    public static <T> T combine(T object1, T object2, BinaryOperator<T> combiner) {
        if (object1 == null) {
            return object2;
        } else if (object2 == null) {
            return object1;
        } else {
            return notNull(combiner, "The combiner to combine two objects cannot be null.").apply(object1, object2);
        }
    }

    /**
     * 将一组对象组合为一个对象。
     *
     * @param objects 表示待组合的对象的 {@link Iterable}{@code <}{@link Object}{@code >}。
     * @param combiner 表示将两个对象组合为一个对象的组合程序的 {@link BinaryOperator}。
     * @param <T> 表示待组合对象的实际类型。
     * @return 表示组合后的对象的 {@link Object}。
     */
    public static <T> T combine(Iterable<T> objects, BinaryOperator<T> combiner) {
        T result = null;
        if (objects != null) {
            for (T object : objects) {
                result = combine(result, object, combiner);
            }
        }
        return result;
    }

    /**
     * 将指定对象转换成仅包含 Java 中的类型的对象。
     * <p>如果是自定义结构体，将转换成 {@link LinkedHashMap}，如果是数组，将转换成 {@link LinkedList}。</p>
     *
     * @param obj 表示待转换的指定对象的 {@link Object}。
     * @return 表示转换后的仅包含 Java 中的类型的对象的 {@link Object}。
     */
    public static Object toJavaObject(Object obj) {
        if (obj == null) {
            return null;
        }
        Class<?> clazz = obj.getClass();
        if (clazz.isPrimitive() || ReflectionUtils.isPrimitiveWrapper(clazz)) {
            return obj;
        }
        if (obj instanceof String || obj instanceof byte[]) {
            return obj;
        }
        if (obj instanceof BigInteger || obj instanceof BigDecimal) {
            return obj;
        }
        if (obj instanceof Map) {
            return toJavaObjectFromMap(cast(obj));
        }
        if (obj instanceof List) {
            return toJavaObjectFromList(cast(obj));
        }
        if (clazz.isArray()) {
            return toJavaObjectFromArray(obj);
        }
        return toJavaObjectFromCustomObject(obj);
    }

    private static Object toJavaObjectFromMap(Map<Object, Object> map) {
        Map<Object, Object> newMap = new LinkedHashMap<>();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object key = toJavaObject(entry.getKey());
            Object value = toJavaObject(entry.getValue());
            newMap.put(key, value);
        }
        return newMap;
    }

    private static Object toJavaObjectFromList(List<Object> list) {
        List<Object> newList = new LinkedList<>();
        for (Object obj : list) {
            newList.add(toJavaObject(obj));
        }
        return newList;
    }

    private static Object toJavaObjectFromArray(Object array) {
        int length = Array.getLength(array);
        List<Object> list = new LinkedList<>();
        for (int i = 0; i < length; i++) {
            list.add(toJavaObject(Array.get(array, i)));
        }
        return list;
    }

    private static Map<String, Object> toJavaObjectFromCustomObject(Object obj) {
        BeanAccessor srcAccessor = BeanAccessor.of(obj.getClass());
        List<String> properties = srcAccessor.properties()
                .stream()
                .filter(property -> !Objects.equals(property, "class"))
                .collect(Collectors.toList());
        Map<String, Object> map = new LinkedHashMap<>();
        for (String property : properties) {
            Object value = srcAccessor.get(obj, property);
            if (value == null) {
                continue;
            }
            map.put(srcAccessor.getAlias(property), toJavaObject(value));
        }
        return map;
    }

    /**
     * 将仅包含 Java 中的类型的对象转换成指定类型的对象。
     *
     * @param obj 表示仅包含 Java 中的类型的对象的 {@link Object}。
     * @param type 表示指定类型的 {@link Type}。
     * @param <T> 表示转换后的对象的类型的 {@link T}。
     * @return 表示转换后的对象的 {@link T}。
     */
    public static <T> T toCustomObject(Object obj, Type type) {
        return cast(ConversionService.forStandard().convert(obj, type));
    }

    /**
     * 判断指定对象是否为 Java 中定义的对象。
     *
     * @param obj 表示指定对象的 {@link Object}。
     * @return 如果指定对象是 Java 中定义的对象，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isCustomObject(Object obj) {
        if (obj == null) {
            return false;
        }
        Class<?> clazz = obj.getClass();
        String packageName = clazz.getPackage().getName();
        return !packageName.startsWith("java.") && !packageName.startsWith("javax.");
    }
}
