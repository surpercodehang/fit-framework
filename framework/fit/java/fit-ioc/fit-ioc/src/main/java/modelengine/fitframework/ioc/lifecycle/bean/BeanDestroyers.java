/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean;

import modelengine.fitframework.ioc.lifecycle.bean.support.BeanDestroyerComposite;
import modelengine.fitframework.ioc.lifecycle.bean.support.MethodBeanDestroyer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 Bean 销毁器的工具类集合。
 *
 * @author 梁济时
 * @since 2022-06-01
 */
public class BeanDestroyers {
    /**
     * 创建使用指定方法进行销毁的Bean销毁程序。
     *
     * @param method 表示用以销毁Bean的方法的 {@link Method}。
     * @return 表示使用该方法对Bean进行销毁的Bean销毁程序的 {@link BeanDestroyer}。
     * @throws IllegalArgumentException {@code method} 为 {@code null}。
     * @throws modelengine.fitframework.ioc.BeanDefinitionException {@code method} 被 {@code static} 修饰。
     */
    public static BeanDestroyer method(Method method) {
        return new MethodBeanDestroyer(method);
    }

    /**
     * 将指定的Bean销毁程序组合成为一个以对外呈现。
     * <ul>
     *     <li>若 {@code destroyers} 为 {@code null}，则返回 {@code null}</li>
     *     <li>若数组中不存在任何非 {@code null} 的销毁程序，则返回 {@code null}</li>
     *     <li>若数组中仅有一个非 {@code null} 的销毁程序，则直接返回该销毁程序</li>
     *     <li>否则返回一个销毁程序的组合，包含所有非 {@code null} 的销毁程序</li>
     * </ul>
     *
     * @param destroyers 表示待组合的销毁程序的 {@link BeanInjector}{@code []}。
     * @return 表示组合后的Bean销毁程序的 {@link BeanInjector}。
     */
    public static BeanDestroyer combine(BeanDestroyer... destroyers) {
        if (destroyers == null) {
            return null;
        }
        List<BeanDestroyer> actualDestroyers =
                Stream.of(destroyers).filter(Objects::nonNull).collect(Collectors.toList());
        if (actualDestroyers.isEmpty()) {
            return null;
        } else if (actualDestroyers.size() > 1) {
            return new BeanDestroyerComposite(actualDestroyers);
        } else {
            return actualDestroyers.get(0);
        }
    }
}
