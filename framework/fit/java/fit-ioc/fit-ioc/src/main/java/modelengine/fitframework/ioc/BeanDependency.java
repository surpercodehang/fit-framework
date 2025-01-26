/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.support.DefaultBeanDependency;

import java.lang.reflect.Type;

/**
 * 为 Bean 的依赖提供信息。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-05-10
 */
public interface BeanDependency {
    /**
     * 获取依赖 Bean 的源 Bean 的元数据。
     *
     * @return 表示Bean的元数据的 {@link BeanMetadata}。
     */
    BeanMetadata source();

    /**
     * 获取所依赖的 Bean 的名称。
     *
     * @return 表示 Bean 名称的 {@link String}。
     */
    String name();

    /**
     * 获取所依赖的 Bean 的类型。
     *
     * @return 表示 Bean 类型的 {@link Type}。
     */
    Type type();

    /**
     * 获取一个值，该值指示依赖是否是必须的。
     *
     * @return 若是必须的，则为 {@code true}；否则为 {@code false}。
     */
    boolean required();

    /**
     * 获取所依赖的 Bean 所在位置的注解信息。
     *
     * @return 表示所依赖的 Bean 所在位置的注解信息的 {@link AnnotationMetadata}。
     */
    AnnotationMetadata annotations();

    /**
     * 在指定 Bean 容器中解析依赖。
     *
     * @return 表示依赖的解析结果的 {@link DependencyResolvingResult}。
     * @throws IllegalArgumentException {@code container} 为 {@code null}。
     */
    default DependencyResolvingResult resolve() {
        return this.source()
                .runtime()
                .resolverOfDependencies()
                .resolve(this.source(), this.name(), this.type(), this.annotations());
    }

    /**
     * 使用所依赖 Bean 的元数据、名称和类型，以及指示是否是必须的值创建 Bean 依赖的新实例。
     *
     * @param source 表示所依赖的 Bean 元数据的 {@link BeanMetadata}。
     * @param name 表示所依赖的 Bean 名称的 {@link String}。
     * @param type 表示所依赖的 Bean 类型的 {@link Type}。
     * @param required 若为 {@code true}，则依赖是必须的；否则是可选的。
     * @param annotations 表示所依赖的 Bean 所在位置的注解信息的 {@link AnnotationMetadata}。
     * @return 表示Bean依赖信息的 {@link BeanDependency}。
     */
    static BeanDependency create(BeanMetadata source, String name, Type type, boolean required,
            AnnotationMetadata annotations) {
        return new DefaultBeanDependency(source, name, type, required, annotations);
    }
}
