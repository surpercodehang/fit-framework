/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.beans.convert.ConversionService;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 Bean 提供访问程序。
 *
 * @author 梁济时
 * @since 2023-01-06
 */
public final class BeanAccessor {
    private static final Map<Class<?>, BeanAccessor> ACCESSORS = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final Map<String, BeanPropertyAccessor> properties;
    private final Map<String, String> propertiesAliases;
    private final ConversionService conversionService;

    /**
     * 使用 Bean 的类型和对象类型转换服务初始化 {@link BeanAccessor} 类的新实例。
     *
     * @param type 表示 Bean 的类型的 {@link Class}。
     * @param conversionService 表示对象类型转换服务的 {@link ConversionService}。
     * @throws IllegalStateException {@code type} 无法被自省，或属性定义不正确。
     */
    private BeanAccessor(Class<?> type, ConversionService conversionService) {
        this.type = type;
        this.conversionService = conversionService;
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(this.type);
        } catch (IntrospectionException ex) {
            throw new IllegalStateException(StringUtils.format("Failed to introspect class of bean. [type={0}]",
                    this.type.getName()), ex);
        }
        this.properties = Stream.of(info.getPropertyDescriptors())
                .map(descriptor -> BeanPropertyAccessor.of(this, descriptor))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(BeanPropertyAccessor::name, Function.identity()));
        Map<String, String> propertiesRelation = new HashMap<>();
        for (Field field : type.getDeclaredFields()) {
            Property property = field.getDeclaredAnnotation(Property.class);
            if (property != null && StringUtils.isNotBlank(property.name()) && !field.getName()
                .equals(property.name())) {
                propertiesRelation.put(property.name(), field.getName());
                propertiesRelation.put(field.getName(), property.name());
            }
        }
        this.propertiesAliases = propertiesRelation;
    }

    /**
     * 获取 Bean 的类型。
     *
     * @return 表示 Bean 类型的 {@link Class}。
     */
    public Class<?> type() {
        return this.type;
    }

    /**
     * 获取包含属性的名称的集合。
     *
     * @return 表示属性名称的集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public Set<String> properties() {
        return this.properties.keySet();
    }

    /**
     * 获取指定 Bean 的指定名称的属性的值。
     *
     * @param bean 表示待获取属性值的 Bean 的 {@link Object}。
     * @param property 表示属性的名称的 {@link String}。
     * @return 表示属性值的 {@link Object}。
     * @throws UnsupportedOperationException 属性不能被读取。
     * @throws IllegalStateException 未能访问获取属性值的方法。
     */
    public Object get(Object bean, String property) {
        return this.accessor(property).get(bean);
    }

    /**
     * 获取属性的别名。
     *
     * @param property 表示属性的名称的 {@link String}。
     * @return 表示属性的别名的 {@link String}。
     */
    public String getAlias(String property) {
        return this.propertiesAliases.getOrDefault(property, property);
    }

    /**
     * 设置指定 Bean 的指定名称的属性的值。
     *
     * @param bean 表示待设置属性值的 Bean 的 {@link Object}。
     * @param property 表示属性的名称的 {@link String}。
     * @param value 表示属性值的 {@link Object}。
     * @throws UnsupportedOperationException 属性不能被修改。
     * @throws IllegalStateException 未能访问设置属性值的方法。
     */
    public void set(Object bean, String property, Object value) {
        this.accessor(property).set(bean, value);
    }

    /**
     * 将指定映射中保存的信息设置到指定 Bean 中。
     *
     * @param bean 表示待接收数据的 Bean 的 {@link Object}。
     * @param values 表示包含 Bean 数据的映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}。
     */
    public void accept(Object bean, Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            BeanPropertyAccessor property = this.properties.get(this.getAlias(key));
            if (property == null) {
                continue;
            }
            Object value = entry.getValue();
            value = this.conversionService.convert(value, property.type());
            property.set(bean, value);
        }
    }

    /**
     * 使用指定映射中包含的数据，创建当前类型的 Bean 的实例。
     *
     * @param values 表示包含 Bean 数据的映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}。
     * @return 表示新创建的 Bean 实例的 {@link Object}。
     */
    public Object instantiate(Map<String, Object> values) {
        Object bean = ReflectionUtils.instantiate(this.type);
        this.accept(bean, values);
        return bean;
    }

    private BeanPropertyAccessor accessor(String property) {
        notNull(property, "The property of a bean cannot be null.");
        BeanPropertyAccessor accessor = this.properties.get(property);
        if (accessor == null) {
            throw new IllegalStateException(StringUtils.format(
                    "Property with specific name not found. [bean={0}, property={1}]",
                    this.type.getName(),
                    property));
        } else {
            return accessor;
        }
    }

    /**
     * 返回一个 Bean 访问程序，用以访问指定类型的 Bean。
     *
     * @param beanClass 表示 Bean 的类型的 {@link Class}。
     * @return 表示 Bean 的访问程序的 {@link BeanAccessor}。
     */
    public static BeanAccessor of(Class<?> beanClass) {
        return ACCESSORS.computeIfAbsent(beanClass, bc -> new BeanAccessor(bc, ConversionService.forStandard()));
    }

    /**
     * 返回一个 Bean 访问程序，用以访问指定类型的 Bean。
     *
     * @param beanClass 表示 Bean 的类型的 {@link Class}。
     * @param conversionService 表示指定的类型转换服务的 {@link ConversionService}。
     * @return 表示 Bean 的访问程序的 {@link BeanAccessor}。
     */
    public static BeanAccessor of(Class<?> beanClass, ConversionService conversionService) {
        return ACCESSORS.computeIfAbsent(beanClass, bc -> new BeanAccessor(bc, conversionService));
    }
}
