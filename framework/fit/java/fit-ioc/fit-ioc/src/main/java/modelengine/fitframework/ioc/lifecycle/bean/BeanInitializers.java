/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean;

import modelengine.fitframework.ioc.lifecycle.bean.support.BeanInitializerComposite;
import modelengine.fitframework.ioc.lifecycle.bean.support.MethodBeanInitializer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 Bean 初始化器的工具类集合。
 *
 * @author 梁济时
 * @since 2022-06-01
 */
public class BeanInitializers {
    /**
     * 创建使用指定方法进行初始化的Bean初始化程序。
     *
     * @param method 表示用以初始化Bean的方法的 {@link Method}。
     * @return 表示使用该方法对Bean进行初始化的Bean初始化程序的 {@link BeanInitializer}。
     * @throws IllegalArgumentException {@code method} 为 {@code null}。
     * @throws modelengine.fitframework.ioc.BeanDefinitionException {@code method} 被 {@code static} 修饰。
     */
    public static BeanInitializer method(Method method) {
        return new MethodBeanInitializer(method);
    }

    /**
     * 将指定的Bean初始化程序组合成为一个以对外呈现。
     * <ul>
     *     <li>若 {@code initializers} 为 {@code null}，则返回 {@code null}</li>
     *     <li>若数组中不存在任何非 {@code null} 的初始化程序，则返回 {@code null}</li>
     *     <li>若数组中仅有一个非 {@code null} 的初始化程序，则直接返回该初始化程序</li>
     *     <li>否则返回一个初始化程序的组合，包含所有非 {@code null} 的初始化程序</li>
     * </ul>
     *
     * @param initializers 表示待组合的初始化程序的 {@link BeanInjector}{@code []}。
     * @return 表示组合后的Bean初始化程序的 {@link BeanInjector}。
     */
    public static BeanInitializer combine(BeanInitializer... initializers) {
        if (initializers == null) {
            return null;
        }
        List<BeanInitializer> actualInitializers =
                Stream.of(initializers).filter(Objects::nonNull).collect(Collectors.toList());
        if (actualInitializers.isEmpty()) {
            return null;
        } else if (actualInitializers.size() > 1) {
            return new BeanInitializerComposite(actualInitializers);
        } else {
            return actualInitializers.get(0);
        }
    }
}
