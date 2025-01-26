/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 为 Bean 的属性提供访问程序。
 *
 * @author 梁济时
 * @since 2023-01-06
 */
final class BeanPropertyAccessor {
    private final BeanAccessor bean;
    private final String name;
    private final Type type;
    private final Method readMethod;
    private final Method writeMethod;

    /**
     * 使用属性所属 Bean 的访问程序、属性的名称、类型及读写方法初始化 {@link BeanPropertyAccessor} 类的新实例。
     *
     * @param bean 表示属性所属的 Bean 的访问程序的 {@link BeanAccessor}。
     * @param name 表示属性的名称的 {@link String}。
     * @param type 表示属性的类型的 {@link Type}。
     * @param readMethod 表示用以读取属性值的方法的 {@link Method}。
     * @param writeMethod 表示用以设置属性值的方法的 {@link Method}。
     */
    private BeanPropertyAccessor(BeanAccessor bean, String name, Type type, Method readMethod, Method writeMethod) {
        this.bean = bean;
        this.name = name;
        this.type = type;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
    }

    /**
     * 获取属性的名称。
     *
     * @return 表示属性名称的 {@link String}。
     */
    public String name() {
        return this.name;
    }

    /**
     * 获取属性的类型。
     *
     * @return 表示属性类型的 {@link Type}。
     */
    Type type() {
        return this.type;
    }

    /**
     * 获取指定 Bean 的当前属性的值。
     *
     * @param bean 表示待获取属性值的 Bean 的 {@link Object}。
     * @return 表示 Bean 的属性值的 {@link Object}。
     */
    Object get(Object bean) {
        if (this.readMethod == null) {
            throw new UnsupportedOperationException(StringUtils.format(
                    "The property is not readable. [bean={0}, property={1}]",
                    this.bean.type().getName(),
                    this.name()));
        }
        try {
            return this.readMethod.invoke(bean);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to access method to read property of bean. [method={0}]",
                    ReflectionUtils.signatureOf(this.readMethod)), ex);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof Error) {
                throw ObjectUtils.<Error>cast(cause);
            } else {
                throw ObjectUtils.<RuntimeException>cast(cause);
            }
        }
    }

    /**
     * 设置指定 Bean 的当前属性的值。
     *
     * @param bean 表示待设置属性值的 Bean 的 {@link Object}。
     * @param value 表示新的属性值的 {@link Object}。
     */
    void set(Object bean, Object value) {
        if (this.writeMethod == null) {
            throw new UnsupportedOperationException(StringUtils.format(
                    "The property is not writable. [bean={0}, property={1}]",
                    this.bean.type().getName(),
                    this.name()));
        }
        try {
            this.writeMethod.invoke(bean, value);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to access method to write property of bean. [method={0}]",
                    ReflectionUtils.signatureOf(this.writeMethod)), ex);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof Error) {
                throw ObjectUtils.<Error>cast(cause);
            } else {
                throw ObjectUtils.<RuntimeException>cast(cause);
            }
        }
    }

    @Override
    public String toString() {
        return StringUtils.format("{0}.{1} : {2}", this.bean.type().getName(), this.name(), this.type().getTypeName());
    }

    /**
     * 为指定 Bean 的指定属性创建 Bean 属性访问程序。
     *
     * @param bean 表示所属的 Bean 的访问程序的 {@link BeanAccessor}。
     * @param descriptor 表示属性的描述符的 {@link PropertyDescriptor}。
     * @return 若属性即不可读又不可写，则为 {@code null}，否则为属性的访问程序的 {@link BeanPropertyAccessor}。
     * @throws IllegalStateException 读取方法或设置方法的定义不正确，或抛出了受检异常，或其所对应的属性类型不一致。
     */
    static BeanPropertyAccessor of(BeanAccessor bean, PropertyDescriptor descriptor) {
        Method descriptorReadMethod = descriptor.getReadMethod();
        Method descriptorWriteMethod = descriptor.getWriteMethod();
        if (descriptorReadMethod == null && descriptorWriteMethod == null) {
            return null;
        }
        Type propertyType = obtainPropertyType(descriptorReadMethod, descriptorWriteMethod);
        return new BeanPropertyAccessor(bean,
                descriptor.getName(),
                propertyType,
                descriptorReadMethod,
                descriptorWriteMethod);
    }

    private static Type obtainPropertyType(Method readMethod, Method writeMethod) {
        Type propertyType = null;
        if (readMethod != null) {
            propertyType = obtainPropertyTypeFromReadMethod(readMethod);
        }
        if (writeMethod != null) {
            Type anotherPropertyType = obtainPropertyTypeFromWriteMethod(writeMethod);
            if (propertyType != null && !Objects.equals(propertyType, anotherPropertyType)) {
                throw new IllegalStateException(StringUtils.format(
                        "Different property types from read and write methods. [readMethod={0}, writeMethod={1}]",
                        ReflectionUtils.signatureOf(readMethod),
                        ReflectionUtils.signatureOf(writeMethod)));
            } else {
                propertyType = anotherPropertyType;
            }
        }
        return propertyType;
    }

    private static Type obtainPropertyTypeFromReadMethod(Method method) {
        if (method.getParameterCount() > 0) {
            throw new IllegalStateException(StringUtils.format(
                    "The method to read property of bean cannot have any parameter. [method={0}]",
                    ReflectionUtils.signatureOf(method)));
        } else if (Stream.of(method.getExceptionTypes()).anyMatch(ReflectionUtils::isCheckedException)) {
            throw new IllegalStateException(StringUtils.format(
                    "The method to read property of bean cannot throw any checked exception. [method={0}]",
                    ReflectionUtils.signatureOf(method)));
        } else {
            return method.getGenericReturnType();
        }
    }

    private static Type obtainPropertyTypeFromWriteMethod(Method method) {
        if (method.getParameterCount() != 1) {
            throw new IllegalStateException(StringUtils.format(
                    "The method to write property of bean must have and only have 1 parameter. [method={0}]",
                    ReflectionUtils.signatureOf(method)));
        } else if (Stream.of(method.getExceptionTypes()).anyMatch(ReflectionUtils::isCheckedException)) {
            throw new IllegalStateException(StringUtils.format(
                    "The method to write property of bean cannot throw any checked exception. [method={0}]",
                    ReflectionUtils.signatureOf(method)));
        } else {
            return method.getParameters()[0].getParameterizedType();
        }
    }
}
