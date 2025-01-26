/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import modelengine.fitframework.annotation.Stereotype;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.support.DefaultBeanDefinition;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * 为 Bean 提供定义。
 *
 * @author 梁济时
 * @since 2022-05-16
 */
public interface BeanDefinition {
    /**
     * 获取 Bean 的名称。
     *
     * @return 表示 Bean 名称的集合的 {@link String}。
     */
    String name();

    /**
     * 获取 Bean 的类型。
     *
     * @return 表示 Bean 类型的 {@link Type}。
     */
    @Nonnull
    Type type();

    /**
     * 获取 Bean 的别名。
     *
     * @return 表示 Bean 的别名的集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> aliases();

    /**
     * 获取 Bean 上应用的模式。
     *
     * @return 表示 Bean 的模式的 {@link String}。
     */
    String stereotype();

    /**
     * 获取 Bean 上定义的注解的元数据。
     *
     * @return 表示注解元数据的 {@link AnnotationMetadata}。
     */
    AnnotationMetadata annotations();

    /**
     * 获取一个值，该值指示 Bean 是否是单例的。
     *
     * @return 若 Bean 是单例的，则为 {@code true}；否则为 {@code false}。
     */
    default boolean singleton() {
        return !StringUtils.equalsIgnoreCase(this.stereotype(), Stereotype.PROTOTYPE);
    }

    /**
     * 获取一个值，该值指示 Bean 是否是首选的。
     * <p>当有多个实现时，将优先使用首选的 Bean。</p>
     *
     * @return 若 Bean 是首选的，则为 {@code true}；否则为 {@code false}。
     */
    boolean preferred();

    /**
     * 获取一个值，该值指示 Bean 是否在使用时才被加载。
     *
     * @return 若在使用时才被加载，则为 {@code true}，否则为 {@code false}。
     */
    boolean lazy();

    /**
     * 获取 Bean 的依赖。
     *
     * @return 表示所依赖的 Bean 的名称的集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> dependencies();

    /**
     * 获取 Bean 的可用范围。
     *
     * @return 表示 Bean 的可用范围的 {@link BeanApplicableScope}。
     */
    BeanApplicableScope applicable();

    /**
     * 获取 Bean 的属性值。
     *
     * @return 表示 Bean 的属性值的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> properties();

    /**
     * 返回一个构建程序，用以构建 {@link BeanDefinition} 的新实例。
     *
     * @return 表示用以构建Bean定义的构建程序的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultBeanDefinition.Builder(null);
    }

    /**
     * 返回一个构建程序，使用当前 Bean 定义作为初始值。
     *
     * @return 表示用以构建 Bean 定义的构建程序的 {@link Builder}。
     */
    default Builder copy() {
        return new DefaultBeanDefinition.Builder(this);
    }

    /**
     * 为 {@link BeanDefinition} 提供构建程序。
     *
     * @author 梁济时
     * @since 2022-05-16
     */
    interface Builder {
        /**
         * 设置 Bean 的名称。
         *
         * @param name 表示 Bean 的名称的 {@link String}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 设置 Bean 的类型。
         *
         * @param type 表示 Bean 的类型的 {@link Type}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder type(Type type);

        /**
         * 设置 Bean 的别名。
         *
         * @param aliases 表示别名的集合的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder aliases(Set<String> aliases);

        /**
         * 设置 Bean 上应用的模式。
         *
         * @param stereotype 表示 Bean 上应用的模式的 {@link String}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder stereotype(String stereotype);

        /**
         * 设置 Bean 上定义的注解的元数据。
         *
         * @param annotations 表示 Bean 定义的注解元数据的 {@link AnnotationMetadata}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder annotations(AnnotationMetadata annotations);

        /**
         * 设置一个值，该值指示 Bean 是否是首选的。
         *
         * @param preferred 若为 {@code true}，则 Bean 是首选的；否则不是首选的。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder preferred(boolean preferred);

        /**
         * 设置一个值，该值指示 Bean 是否在使用时才被加载。
         *
         * @param lazy 若为 {@code true}，则在被使用时才被加载，否则在预加载时即被加载。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder lazy(boolean lazy);

        /**
         * 设置 Bean 的依赖。
         *
         * @param dependencies 表示所依赖的 Bean 的名称的集合的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder dependencies(Set<String> dependencies);

        /**
         * 设置 Bean 的可用范围。
         *
         * @param applicable 表示 Bean 的可用范围的 {@link BeanApplicableScope}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder applicable(BeanApplicableScope applicable);

        /**
         * 添加一个配置。
         *
         * @param key 表示配置的键的 {@link String}。
         * @param value 表示配置的值的 {@link Object}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder set(String key, Object value);

        /**
         * 构建 Bean 定义的新实例。
         *
         * @return 表示新构建的 Bean 实例的 {@link BeanDefinition}。
         */
        BeanDefinition build();
    }
}
