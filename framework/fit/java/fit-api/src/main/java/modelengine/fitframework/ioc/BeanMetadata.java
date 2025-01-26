/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import modelengine.fitframework.annotation.Stereotype;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * 为 Bean 提供元数据。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public interface BeanMetadata {
    /**
     * 获取 Bean 所属的容器。
     *
     * @return 表示Bean所属容器的 {@link BeanContainer}。
     */
    @Nonnull
    BeanContainer container();

    /**
     * 获取 FIT 运行时。
     *
     * @return 表示 FIT 运行时的 {@link FitRuntime}。
     */
    @Nonnull
    default FitRuntime runtime() {
        return this.container().runtime();
    }

    /**
     * 获取 Bean 的名称。
     *
     * @return 表示 Bean 的名称的 {@link String}。
     */
    String name();

    /**
     * 获取 Bean 的别名。
     *
     * @return 表示 Bean 的别名的集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> aliases();

    /**
     * 获取 Bean 的类型。
     *
     * @return 表示 Bean 的类型的 {@link Type}。
     */
    Type type();

    /**
     * 获取 Bean 所应用的模式。
     *
     * @return 表示 Bean 上应用模式的 {@link String}。
     */
    String stereotype();

    /**
     * 获取 Bean 的配置。
     *
     * @return 表示 Bean 的配置的 {@link Config}。
     */
    Config config();

    /**
     * 获取一个值，该值指示指定的 Bean 是否是单例的。
     *
     * @return 若是单例的，则为 {@code true}；否则为 {@code false}。
     */
    default boolean singleton() {
        return !StringUtils.equalsIgnoreCase(this.stereotype(), Stereotype.PROTOTYPE);
    }

    /**
     * 获取一个值，该值指示当前 Bean 是否是首选的。
     *
     * @return 若是首选的，则为 {@code true}；否则为 {@code false}。
     */
    boolean preferred();

    /**
     * 获取一个值，该值指示 Bean 是否在被使用时才被加载。
     *
     * @return 若在被使用时才被加载，则为 {@code true}，否则为 {@code false}。
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
     * 获取 Bean 定义的注解的元数据。
     *
     * @return 表示注解元数据的 {@link AnnotationMetadata}。
     */
    AnnotationMetadata annotations();
}
