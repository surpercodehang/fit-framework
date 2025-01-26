/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;
import modelengine.fitframework.ioc.lifecycle.bean.ValueSupplier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;

/**
 * 为 Bean 提供解析程序。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public interface BeanResolver {
    /**
     * 检查指定的类型是否表示 Bean。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param clazz 表示待检查的类型的 {@link Class}。
     * @return 若指定类型是 Bean，则为其定义的 {@link Optional}{@code <}{@link BeanDefinition}{@code >}；
     * 否则为 {@link Optional#empty()}。
     */
    Optional<BeanDefinition> bean(BeanContainer container, Class<?> clazz);

    /**
     * 检查指定的方法，是否用以创建Bean。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param method 表示待检查的方法的 {@link Method}。
     * @return 若方法用以创建 Bean ，则为其定义的 {@link Optional}{@code <}{@link BeanDefinition}{@code >}；
     * 否则为 {@link Optional#empty()}。
     */
    Optional<BeanDefinition> bean(BeanContainer container, Method method);

    /**
     * 检查指定的构造方法是否是首选构造方法。
     *
     * @param metadata 表示 Bean 的元数据的 {@link BeanMetadata}。
     * @param constructor 表示待检查的构造方法的 {@link Constructor}。
     * @return 若是首选构造方法，则为 {@code true}；否则为 {@code false}。
     */
    boolean preferred(BeanMetadata metadata, Constructor<?> constructor);

    /**
     * 解析参数的值。
     *
     * @param metadata 表示 Bean 的元数据的 {@link BeanMetadata}。
     * @param parameter 表示待解析的参数的 {@link Parameter}。
     * @return 若可以解析该参数，则为表示参数值的获取程序的 {@link Optional}{@code <}{@link ValueSupplier}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    Optional<ValueSupplier> parameter(BeanMetadata metadata, Parameter parameter);

    /**
     * 从指定字段解析 Bean 注入程序。
     *
     * @param metadata 表示 Bean 的元数据的 {@link BeanMetadata}。
     * @param field 表示待解析的字段的 {@link Field}。
     * @return 若可以解析该字段，则为表示解析到的Bean注入程序的 {@link Optional}{@code <}{@link BeanInjector}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    Optional<BeanInjector> injector(BeanMetadata metadata, Field field);

    /**
     * 从指定方法解析 Bean 注入程序。
     *
     * @param metadata 表示 Bean 的元数据的 {@link BeanMetadata}。
     * @param method 表示待解析的方法的 {@link Method}。
     * @return 若可以解析该方法，则为表示解析到的Bean注入程序的 {@link Optional}{@code <}{@link BeanInjector}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    Optional<BeanInjector> injector(BeanMetadata metadata, Method method);

    /**
     * 从指定方法解析 Bean 初始化程序。
     *
     * @param metadata 表示 Bean 的元数据的 {@link BeanMetadata}。
     * @param method 表示待解析的方法的 {@link Method}。
     * @return 若可以解析该方法，则为表示解析到的Bean初始化程序的 {@link Optional}{@code <}{@link BeanInitializer}{@code >}；
     * 否则为 {@link Optional#empty()}。
     */
    Optional<BeanInitializer> initializer(BeanMetadata metadata, Method method);

    /**
     * 从指定方法解析 Bean 销毁程序。
     *
     * @param metadata 表示 Bean 的元数据的 {@link BeanMetadata}。
     * @param method 表示待解析的方法的 {@link Method}。
     * @return 若可以解析该方法，则为表示解析到的Bean销毁程序的 {@link Optional}{@code <}{@link BeanDestroyer}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    Optional<BeanDestroyer> destroyer(BeanMetadata metadata, Method method);

    /**
     * 检查指定 Bean 的优先级。
     *
     * @param metadata 表示检查优先级的 Bean 的元数据的 {@link BeanMetadata}。
     * @return 表示 Bean 的优先级的 32 位整数。
     */
    Optional<Integer> priority(BeanMetadata metadata);

    /**
     * 从指定的 Bean 元数据中解析其所依赖的自动扫描的包。
     *
     * @param metadata 表示待检查的 Bean 的元数据的 {@link BeanMetadata}。
     * @return 表示所依赖的包的路径的集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> packages(BeanMetadata metadata);

    /**
     * 从指定的 Bean 元数据中解析所需要引入的配置。
     *
     * @param metadata 表示待检查 Bean 的元数据的 {@link BeanMetadata}。
     * @return 表示所需引入的配置的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> configurations(BeanMetadata metadata);

    /**
     * 检查指定的 Bean 是否作为工厂使用。
     *
     * @param metadata 表示待检查的 Bean 的元数据的 {@link BeanMetadata}。
     * @return 若Bean作为工厂使用，则为表示工厂信息的 {@link Optional}{@code <}{@link Factory}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    Optional<Factory> factory(BeanMetadata metadata);

    /**
     * 为作为工厂使用的 Bean 提供定义。
     *
     * @author 梁济时
     * @since 2022-07-06
     */
    interface Factory {
        /**
         * 获取待创建的 Bean 的类型。
         *
         * @return 表示 Bean 的实际类型的 {@link Type}。
         */
        Type type();

        /**
         * 通过原始 Bean 创建实际 Bean 。
         *
         * @param bean 表示原始 Bean 的 {@link Object}。
         * @return 表示实际 Bean 的 {@link Object}。
         */
        Object create(Object bean);
    }
}
