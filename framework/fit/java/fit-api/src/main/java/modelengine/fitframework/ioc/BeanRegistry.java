/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 为Bean提供注册入口。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public interface BeanRegistry {
    /**
     * 将指定的类型注册为Bean类型。
     *
     * @param beanClass 表示Bean的类型的 {@link Class}。
     * @return 表示新注册的Bean的元数据的 {@link List}{@code <}{@link BeanMetadata}{@code >}。
     * @throws IllegalArgumentException {@code beanClass} 为 {@code null}。
     */
    List<BeanMetadata> register(Class<?> beanClass);

    /**
     * 将指定对象注册为Bean。
     *
     * @param bean 表示待注册为Bean的对象的 {@link Object}。
     * @return 表示新注册的Bean的元数据的 {@link List}{@code <}{@link BeanMetadata}{@code >}。
     * @throws IllegalArgumentException {@code bean} 为 {@code null}。
     */
    List<BeanMetadata> register(Object bean);

    /**
     * 将指定的对象注册为Bean。
     *
     * @param bean 表示待注册为Bean的对象的 {@link Object}。
     * @param name 表示Bean的名称的 {@link String}。当为空白字符串时自动生成。
     * @return 表示新注册的Bean的元数据的 {@link List}{@code <}{@link BeanMetadata}{@code >}。
     * @throws IllegalArgumentException {@code bean} 为 {@code null}。
     */
    List<BeanMetadata> register(Object bean, String name);

    /**
     * 将指定的对象注册为Bean。
     *
     * @param bean 表示待注册为Bean的对象的 {@link Object}。
     * @param type 表示Bean的类型的 {@link Type}。当为 {@code null} 时使用 {@link Object#getClass() bean.getClass()}。
     * @return 表示新注册的Bean的元数据的 {@link List}{@code <}{@link BeanMetadata}{@code >}。
     * @throws IllegalArgumentException {@code bean} 为 {@code null}。
     */
    List<BeanMetadata> register(Object bean, Type type);

    /**
     * 使用指定的定义注册 Bean。
     *
     * @param definition 表示待注册的 Bean 的定义的 {@link BeanDefinition}。
     * @return 表示新注册的Bean的元数据的 {@link List}{@code <}{@link BeanMetadata}{@code >}。
     * @throws IllegalArgumentException {@code definition} 为 {@code null}。
     */
    List<BeanMetadata> register(BeanDefinition definition);

    /**
     * 添加一个观察者。
     *
     * @param observer 表示 Bean 被注册时通知的观察者的 {@link BeanRegisteredObserver}。
     */
    void subscribe(BeanRegisteredObserver observer);

    /**
     * 去除一个观察者。
     *
     * @param observer 表示 Bean 被注册时通知的观察者的 {@link BeanRegisteredObserver}。
     */
    void unsubscribe(BeanRegisteredObserver observer);
}
