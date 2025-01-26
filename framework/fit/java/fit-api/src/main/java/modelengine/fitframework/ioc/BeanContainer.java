/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.Disposable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 为 Bean 提供容器。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public interface BeanContainer extends Disposable {
    /**
     * 获取容器的名称。
     * <p><b>Bean 容器不再提供该方法，应通过 {@link #plugin()} 进行区分。</b></p>
     *
     * @return 表示容器名称的 {@link String}。
     */
    @Nonnull
    @Deprecated
    String name();

    /**
     * 获取容器所属的插件。
     *
     * @return 表示容器所属插件的 {@link Plugin}。
     */
    @Nonnull
    Plugin plugin();

    /**
     * 获取容器的运行时环境。
     *
     * @return 表示容器的运行时环境的 {@link FitRuntime}。
     */
    @Nonnull
    default FitRuntime runtime() {
        return this.plugin().runtime();
    }

    /**
     * 获取容器的 Bean 的注册入口。
     *
     * @return 表示 Bean 的注册入口的 {@link BeanRegistry}。
     */
    @Nonnull
    BeanRegistry registry();

    /**
     * 获取指定名称的 Bean 的工厂。
     *
     * @param name 表示 Bean 名称的 {@link String}。
     * @return 表示存在该名称的 Bean 的工厂，则为表示该 Bean 工厂的 {@link Optional}{@code <}{@link BeanFactory}{@code
     * >}，否则为 {@link Optional#empty()}。
     */
    Optional<BeanFactory> factory(String name);

    /**
     * 获取指定类型的 Bean 的工厂。
     *
     * @param type 表示 Bean 名称的 {@link Type}。
     * @return 表示存在该类型的 Bean 的工厂，则为表示该 Bean 工厂的 {@link Optional}{@code <}{@link BeanFactory}{@code
     * >}，否则为 {@link Optional#empty()}。
     * @throws AmbiguousBeanException 存在多个该类型 Bean 的工厂。
     */
    Optional<BeanFactory> factory(Type type);

    /**
     * 获取当前容器中指定类型的 Bean 的所有工厂。
     *
     * @param type 表示 Bean 的类型的 {@link Type}。
     * @return 表示该类型的 Bean 的所有工厂的列表的 {@link List}{@code <}{@link BeanFactory}{@code >}。
     */
    List<BeanFactory> factories(Type type);

    /**
     * 获取当前容器中的所有 Bean 工厂。
     *
     * @return 表示所有 Bean 工厂的列表的 {@link List}{@code <}{@link BeanFactory}{@code >}。
     */
    List<BeanFactory> factories();

    /**
     * 以当前容器作为起点，在整体容器系统中查找指定名称的 Bean 的工厂。
     *
     * @param name 表示 Bean 的名称的 {@link String}。
     * @return 表示存在该名称的 Bean 的工厂，则为表示该 Bean 工厂的 {@link Optional}{@code <}{@link BeanFactory}{@code
     * >}，否则为 {@link Optional#empty()}。
     */
    Optional<BeanFactory> lookup(String name);

    /**
     * 以当前容器作为起点，在整体容器系统中查找指定类型的 Bean 的工厂。
     *
     * @param type 表示 Bean 名称的 {@link Type}。
     * @return 表示存在该类型的 Bean 的工厂，则为表示该 Bean 工厂的 {@link Optional}{@code <}{@link BeanFactory}{@code
     * >}，否则为 {@link Optional#empty()}。
     * @throws AmbiguousBeanException 存在多个该类型 Bean 的工厂。
     */
    Optional<BeanFactory> lookup(Type type);

    /**
     * 获取整个容器树中指定类型的 Bean 的所有工厂。
     *
     * @param type 表示 Bean 的类型的 {@link Type}。
     * @return 表示该类型的 Bean 的所有工厂的列表的 {@link List}{@code <}{@link BeanFactory}{@code >}。
     */
    List<BeanFactory> all(Type type);

    /**
     * 获取整个容器树中满足指定条件的所有 Bean 工厂。
     *
     * @return 表示满足条件的所有 Bean 工厂的列表的 {@link List}{@code <}{@link BeanFactory}{@code >}。
     */
    List<BeanFactory> all();

    /**
     * 启动容器。
     * <p>启动容器过程中，会将所有的单例非懒加载的 Bean 进行初始化。</p>
     */
    void start();

    /**
     * 停止容器。
     */
    void stop();

    /**
     * 为 {@link BeanContainer} 提供针对 Bean 的操作接口。
     *
     * @author 梁济时
     * @since 2022-08-02
     */
    interface Beans {
        /**
         * 在当前容器中获取指定类型的 Bean。
         *
         * @param beanClass 表示 Bean 的类型的 {@link Class}。
         * @param initialArguments 表示 Bean 的初始化参数的 {@link Object}{@code []}。
         * @param <T> 表示 Bean 的类型的 {@link T}。
         * @return 表示 Bean 的实例的 {@link Object}。
         * @throws BeanNotFoundException 该类型的 Bean 不存在。
         */
        <T> T get(Class<T> beanClass, Object... initialArguments);

        /**
         * 在当前容器中获取指定类型的 Bean。
         *
         * @param beanType 表示 Bean 的类型的 {@link Type}。
         * @param initialArguments 表示 Bean 的初始化参数的 {@link Object}{@code []}。
         * @param <T> 表示 Bean 的类型 {@link T}。
         * @return 表示 Bean 的实例的 {@link Object}。
         * @throws BeanNotFoundException 该类型的 Bean 不存在。
         */
        <T> T get(Type beanType, Object... initialArguments);

        /**
         * 在当前容器中获取指定名称的 Bean。
         *
         * @param beanName 表示 Bean 的名称的 {@link String}。
         * @param initialArguments 表示 Bean 的初始化参数的 {@link Object}{@code []}。
         * @param <T> 表示 Bean 的类型 {@link T}。
         * @return 表示 Bean 的实例的 {@link Object}。
         * @throws BeanNotFoundException 该名称的 Bean 不存在。
         */
        <T> T get(String beanName, Object... initialArguments);

        /**
         * 在全部相关容器中获取指定类型的 Bean。
         *
         * @param beanClass 表示 Bean 的类型的 {@link Class}。
         * @param initialArguments 表示 Bean 的初始化参数的 {@link Object}{@code []}。
         * @param <T> 表示 Bean 的类型 {@link T}。
         * @return 表示 Bean 的实例的 {@link Object}。
         * @throws BeanNotFoundException 该类型的 Bean 不存在。
         */
        <T> T lookup(Class<T> beanClass, Object... initialArguments);

        /**
         * 在全部相关容器中获取指定类型的 Bean。
         *
         * @param beanType 表示 Bean 的类型的 {@link Type}。
         * @param initialArguments 表示 Bean 的初始化参数的 {@link Object}{@code []}。
         * @param <T> 表示 Bean 的类型 {@link T}。
         * @return 表示 Bean 的实例的 {@link Object}。
         * @throws BeanNotFoundException 该类型的 Bean 不存在。
         */
        <T> T lookup(Type beanType, Object... initialArguments);

        /**
         * 在全部相关容器中获取指定名称的 Bean。
         *
         * @param beanName 表示 Bean 的名称的 {@link String}。
         * @param initialArguments 表示 Bean 的初始化参数的 {@link Object}{@code []}。
         * @param <T> 表示 Bean 的类型 {@link T}。
         * @return 表示 Bean 的实例的 {@link Object}。
         * @throws BeanNotFoundException 该名称的 Bean 不存在。
         */
        <T> T lookup(String beanName, Object... initialArguments);

        /**
         * 列出当前容器中所有指定类型的 Bean。
         *
         * @param beanClass 表示 Bean 的类型的 {@link Class}。
         * @param <T> 表示 Bean 的类型 {@link T}。
         * @return 表示该类型 Bean 的以名称为索引的映射的 {@link Map}{@code <}{@link String}{@code , }{@link
         * Object}{@code >}。
         */
        <T> Map<String, T> list(Class<T> beanClass);

        /**
         * 列出当前容器中所有指定类型的 Bean。
         *
         * @param beanType 表示 Bean 的类型的 {@link Type}。
         * @param <T> 表示 Bean 的类型 {@link T}。
         * @return 表示该类型 Bean 的以名称为索引的映射的 {@link Map}{@code <}{@link String}{@code , }{@link
         * Object}{@code >}。
         */
        <T> Map<String, T> list(Type beanType);

        /**
         * 列出全部相关容器中所有指定类型的 Bean。
         *
         * @param beanClass 表示 Bean 的类型的 {@link Class}。
         * @param <T> 表示 Bean 的类型 {@link T}。
         * @return 表示该类型 Bean 的以名称为索引的映射的 {@link Map}{@code <}{@link String}{@code , }{@link
         * Object}{@code >}。
         */
        <T> Map<String, T> all(Class<T> beanClass);

        /**
         * 列出全部相关容器中所有指定类型的 Bean。
         *
         * @param beanType 表示 Bean 的类型的 {@link Type}。
         * @param <T> 表示 Bean 的类型 {@link T}。
         * @return 表示该类型 Bean 的以名称为索引的映射的 {@link Map}{@code <}{@link String}{@code , }{@link
         * Object}{@code >}。
         */
        <T> Map<String, T> all(Type beanType);
    }

    /**
     * 获取 Bean 的操作方法。
     *
     * @return 表示 Bean 的操作方法的 {@link Beans}。
     */
    Beans beans();

    /**
     * 销毁指定的单例 Bean。
     * <p><b>临时方案，用户不应尝试使用该方法，后续会提供插件的加载/卸载方案以提供替代能力。</b></p>
     *
     * @param beanName 表示待销毁的单例 Bean 的名称的 {@link String}。
     */
    @Deprecated
    void destroySingleton(String beanName);

    /**
     * 从容器中移除指定名称的 Bean。
     * <p><b>临时方案，用户不应尝试使用该方法，后续会提供插件的加载/卸载方案以提供替代能力。</b></p>
     *
     * @param beanName 表示待移除的单例 Bean 的名称的 {@link String}。
     */
    @Deprecated
    void removeBean(String beanName);
}
