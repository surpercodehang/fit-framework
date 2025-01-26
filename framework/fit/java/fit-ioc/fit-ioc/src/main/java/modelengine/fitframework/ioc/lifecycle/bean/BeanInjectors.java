/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean;

import modelengine.fitframework.ioc.lifecycle.bean.support.BeanInjectorComposite;
import modelengine.fitframework.ioc.lifecycle.bean.support.FieldBeanInjector;
import modelengine.fitframework.ioc.lifecycle.bean.support.MethodBeanInjector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 Bean 注入器的工具类集合。
 *
 * @author 梁济时
 * @since 2022-06-01
 */
public class BeanInjectors {
    /**
     * 创建用以注入指定字段的注入程序。
     *
     * @param field 表示待注入的字段的 {@link Field}。
     * @param supplier 表示待注入的值的提供程序的 {@link ValueSupplier}。
     * @return 表示用以向指定字段设置值的Bean注入程序的 {@link BeanInjector}。
     * @throws IllegalArgumentException {@code field} 为 {@code null}。
     * @throws modelengine.fitframework.ioc.BeanDefinitionException
     *         {@code field} 被 {@code final} 或 {@code static} 修饰。
     */
    public static BeanInjector field(Field field, ValueSupplier supplier) {
        return new FieldBeanInjector(field, supplier);
    }

    /**
     * 创建用以注入指定方法的注入程序。
     *
     * @param method 表示待注入的方法的 {@link Method}。
     * @param values 表示待注入的值的 {@link Object}{@code []}。
     * @return 表示用以向指定方法注入值的Bean注入程序的 {@link BeanInjector}。
     * @throws IllegalArgumentException {@code method} 为 {@code null}。
     * @throws modelengine.fitframework.ioc.BeanDefinitionException {@code method} 被 {@code static} 修饰。
     */
    public static BeanInjector method(Method method, Object... values) {
        return new MethodBeanInjector(method, values);
    }

    /**
     * 将指定的Bean注入程序组合成为一个以对外呈现。
     * <ul>
     *     <li>若 {@code injectors} 为 {@code null}，则返回 {@code null}</li>
     *     <li>若数组中不存在任何非 {@code null} 的注入程序，则返回 {@code null}</li>
     *     <li>若数组中仅有一个非 {@code null} 的注入程序，则直接返回该注入程序</li>
     *     <li>否则返回一个注入程序的组合，包含所有非 {@code null} 的注入程序</li>
     * </ul>
     *
     * @param injectors 表示待组合的注入程序的 {@link BeanInjector}{@code []}。
     * @return 表示组合后的Bean注入程序的 {@link BeanInjector}。
     */
    public static BeanInjector combine(BeanInjector... injectors) {
        if (injectors == null) {
            return null;
        }
        List<BeanInjector> actualInjectors = Stream.of(injectors).filter(Objects::nonNull).collect(Collectors.toList());
        if (actualInjectors.isEmpty()) {
            return null;
        } else if (actualInjectors.size() > 1) {
            return new BeanInjectorComposite(actualInjectors);
        } else {
            return actualInjectors.get(0);
        }
    }
}
