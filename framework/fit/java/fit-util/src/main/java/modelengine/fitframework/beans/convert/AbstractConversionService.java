/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans.convert;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.beans.BeanAccessor;
import modelengine.fitframework.util.EnumUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link ConversionService} 的抽象实现类。
 *
 * @author 季聿阶
 * @since 2024-02-25
 */
public abstract class AbstractConversionService implements ConversionService {
    /**
     * 表示标量的类型。
     */
    protected static final Set<Class<?>> SCALAR_TYPES = Stream.of(BigInteger.class,
            BigDecimal.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Boolean.class,
            String.class,
            Date.class).collect(Collectors.toSet());

    private final List<ValueConverter> converters;
    private volatile List<ValueConverter> current;

    /**
     * 初始化 {@link AbstractConversionService} 的新实例。
     */
    protected AbstractConversionService() {
        this.converters = new LinkedList<>();
        this.discover(BuiltinValueConverters.class);
    }

    @Override
    public void discover(Class<?> clazz) {
        notNull(clazz, "The class to discover converters cannot be null.");
        List<ValueConverter> cache = new LinkedList<>();
        discover(cache, clazz);
        if (cache.isEmpty()) {
            return;
        }
        this.register(cache);
    }

    @Override
    public void register(ValueConverter converter) {
        this.register(Collections.singleton(converter));
    }

    @Override
    public void register(Iterable<ValueConverter> converters) {
        if (converters == null) {
            return;
        }
        List<ValueConverter> actual = new LinkedList<>();
        for (ValueConverter converter : converters) {
            if (converter != null) {
                actual.add(converter);
            }
        }
        synchronized (this.converters) {
            this.converters.addAll(actual);
            this.current = null;
        }
    }

    @Override
    public boolean scalar(Type type) {
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            return SCALAR_TYPES.stream().anyMatch(scalar -> scalar.isAssignableFrom(clazz));
        } else {
            return false;
        }
    }

    private static void discover(List<ValueConverter> cache, Class<?> clazz) {
        ValueConverter converter = instantiate(clazz);
        if (converter != null) {
            cache.add(converter);
        }
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(ConverterMethod.class)) {
                converter = ValueConverter.of(method);
                cache.add(converter);
            }
        }
        Class<?>[] nestedClasses = clazz.getDeclaredClasses();
        for (Class<?> nestedClass : nestedClasses) {
            discover(cache, nestedClass);
        }
    }

    private static ValueConverter instantiate(Class<?> clazz) {
        if (!ValueConverter.class.isAssignableFrom(clazz)) {
            return null;
        }
        if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
            return null;
        }
        if (clazz.getDeclaringClass() != null && !Modifier.isStatic(clazz.getModifiers())) {
            return null;
        }
        Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The concrete class of a converter must contain a default constructor. [class={0}]",
                    clazz.getName()));
        }
        Object converter;
        try {
            converter = constructor.newInstance();
        } catch (InstantiationException e) {
            // never occurs
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to access default constructor of converter class. [class={0}]",
                    clazz.getName()), e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to instantiate converter with default constructor. [class={0}]",
                    constructor.getName()), e.getCause());
        }
        return cast(converter);
    }

    private ValueConverter lookupScalarConverter(Class<?> source, Class<?> target) {
        for (ValueConverter converter : this.converters()) {
            if (converter.source().isAssignableFrom(source) && target.isAssignableFrom(converter.target())) {
                return converter;
            }
        }
        throw new IllegalStateException(StringUtils.format("Cannot convert value from {0} to {1}.",
                source.getName(),
                target.getName()));
    }

    private List<ValueConverter> converters() {
        List<ValueConverter> actual;
        if ((actual = this.current) == null) {
            synchronized (this.converters) {
                if ((actual = this.current) == null) {
                    this.current = new ArrayList<>(this.converters);
                    actual = this.current;
                }
            }
        }
        return actual;
    }

    /**
     * 将指定值转换为指定类型。
     *
     * @param value 表示指定值的 {@link Object}。
     * @param type 表示指定类型的 {@link Class}{@code <?>}。
     * @return 表示转换后的值的 {@link Object}。
     */
    protected Object as(Object value, Class<?> type) {
        if (type == void.class || type == Void.class) {
            return null;
        }
        if (value == null) {
            if (type.isPrimitive()) {
                throw new IllegalArgumentException(StringUtils.format(
                        "Cannot convert null to a primitive class. [target={0}]",
                        type.getName()));
            } else {
                return null;
            }
        }
        Class<?> actualType = ReflectionUtils.ignorePrimitiveClass(type);
        if (actualType == byte[].class && value instanceof byte[]) {
            return value;
        }
        if (actualType.isArray()) {
            return this.toArray(value, actualType.getComponentType());
        }
        if (actualType.isEnum()) {
            String enumValue = ObjectUtils.cast(this.as(value, String.class));
            return toEnum(actualType, enumValue);
        }
        Object actual = this.transform(value, type);
        if (actual == null) {
            return null;
        }
        if (actualType.isInstance(actual)) {
            return cast(actual);
        }
        Class<?> source = actual.getClass();
        if (SCALAR_TYPES.contains(source)) {
            ValueConverter converter = this.lookupScalarConverter(source, actualType);
            return converter.convert(actual);
        } else if (actual instanceof Map) {
            return BeanAccessor.of(actualType, this).instantiate(cast(actual));
        } else {
            throw new IllegalStateException(StringUtils.format(
                    "Cannot convert a non-object value to a bean. [value={0}, beanClass={1}]",
                    value,
                    actualType.getName()));
        }
    }

    /**
     * 将转换前的值再进行一次特殊转换。
     * <p>默认不转换，但是如果需要，允许做值内容的转换。</p>
     *
     * @param value 表示转换前指的 {@link Object}。
     * @param type 表示指定类型的 {@link Class}{@code <?>}。
     * @return 表示转换后值的 {@link Object}。
     */
    protected Object transform(Object value, Class<?> type) {
        return value;
    }

    /**
     * 将指定值转换为指定类型。
     *
     * @param value 表示指定值的 {@link Object}。
     * @param type 表示指定类型的 {@link ParameterizedType}。
     * @return 表示转换后的值的 {@link Object}。
     */
    protected Object as(Object value, ParameterizedType type) {
        Class<?> rawClass = (Class<?>) type.getRawType();
        if (rawClass == List.class) {
            return this.toList(value, type.getActualTypeArguments()[0]);
        } else if (rawClass == Set.class) {
            return this.toSet(value, type.getActualTypeArguments()[0]);
        } else if (rawClass == Map.class) {
            return this.toMap(value, type.getActualTypeArguments()[0], type.getActualTypeArguments()[1]);
        } else {
            return this.toCustomObject(value, type);
        }
    }

    /**
     * 将指定值转换成自定义类型。
     *
     * @param value 表示指定值的 {@link Object}。
     * @param type 表示指定泛型的 {@link ParameterizedType}。
     * @return 表示转换后的自定义类型的值的 {@link Object}。
     */
    protected abstract Object toCustomObject(Object value, ParameterizedType type);

    private static Object toEnum(Class<?> enumClass, String value) {
        Class<? extends Enum<?>> actualClass = cast(enumClass);
        Predicate<Enum> predicate = enumConstant -> StringUtils.equalsIgnoreCase(enumConstant.toString(), value);
        return EnumUtils.firstOrDefault(ObjectUtils.cast(actualClass), predicate);
    }

    private List<?> toList(Object source, Type elementType) {
        List<?> actual = this.listOf(source);
        List<Object> list = new ArrayList<>(actual.size());
        this.accept(actual, elementType, list::add);
        return list;
    }

    private Set<?> toSet(Object source, Type elementType) {
        List<?> actual = this.listOf(source);
        Set<Object> set = new HashSet<>(actual.size());
        this.accept(actual, elementType, set::add);
        return set;
    }

    private List<?> listOf(Object value) {
        if (value instanceof List) {
            return (List<?>) value;
        } else if (value == null) {
            return Collections.emptyList();
        } else if (value.getClass().isArray()) {
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < Array.getLength(value); i++) {
                list.add(Array.get(value, i));
            }
            return list;
        } else {
            return transformToList(value);
        }
    }

    /**
     * 将指定值转换成列表。
     * <p>默认不转换。</p>
     *
     * @param value 表示指定值的 {@link Object}。
     * @return 表示转换后的列表的 {@link List}{@code <?>}。
     */
    protected List<?> transformToList(Object value) {
        throw new IllegalArgumentException(StringUtils.format("Cannot convert value to List. [source={0}]",
                value.getClass().getName()));
    }

    private void accept(List<?> source, Type elementType, Consumer<Object> consumer) {
        for (Object item : source) {
            Object element = this.convert(item, elementType);
            consumer.accept(element);
        }
    }

    private Map<?, ?> toMap(Object source, Type keyType, Type valueType) {
        if (source instanceof Map) {
            Map<?, ?> sourceMap = (Map<?, ?>) source;
            Map<Object, Object> targetMap = new HashMap<>(sourceMap.size());
            for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
                Object key = this.convert(entry.getKey(), keyType);
                Object value = this.convert(entry.getValue(), valueType);
                targetMap.put(key, value);
            }
            return targetMap;
        } else if (source == null) {
            return Collections.emptyMap();
        } else {
            throw new IllegalStateException(StringUtils.format(
                    "Cannot convert non-object value to object. [value={0}, required=Map<{1}, {2}>]",
                    source,
                    keyType.getTypeName(),
                    valueType.getTypeName()));
        }
    }

    private Object toArray(Object source, Class<?> elementType) {
        List<?> list = this.toList(source, elementType);
        Object array = Array.newInstance(elementType, list.size());
        int index = 0;
        for (Object item : list) {
            Array.set(array, index++, item);
        }
        return array;
    }
}
