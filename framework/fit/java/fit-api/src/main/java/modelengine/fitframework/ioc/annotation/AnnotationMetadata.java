/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation;

import modelengine.fitframework.ioc.annotation.support.EmptyAnnotationMetadata;

import java.lang.annotation.Annotation;

/**
 * 为注解提供元数据定义。
 *
 * @author 梁济时
 * @since 2022/05/03
 */
public interface AnnotationMetadata {
    /**
     * 检查是否定义了指定注解。
     *
     * @param type 表示待检查的注解的类型的 {@link Class}{@code <? extends }{@link Annotation}{@code >}。
     * @return 若定义了指定注解，则为 {@code true}，否则为 {@code false}。
     */
    boolean isAnnotationPresent(Class<? extends Annotation> type);

    /**
     * 检查是否没有定义指定注解。
     *
     * @param type 表示待检查的注解的类型的 {@link Class}{@code <? extends }{@link Annotation}{@code >}。
     * @return 若定义了指定注解，则为 {@code false}，否则为 {@code true}。
     */
    default boolean isAnnotationNotPresent(Class<? extends Annotation> type) {
        return !this.isAnnotationPresent(type);
    }

    /**
     * 获取定义的所有注解。
     *
     * @return 表示所定义的注解的 {@link Annotation}{@code []}。
     */
    Annotation[] getAnnotations();

    /**
     * 获取指定的指定类型的注解。
     *
     * @param type 表示注解类型的 {@link Class}。
     * @param <T> 表示注解的实际类型。
     * @return 若定义了该注解，则为表示注解的 {@link Annotation}；否则为 {@code null}。
     */
    <T extends Annotation> T getAnnotation(Class<T> type);

    /**
     * 获取指定类型的注解。
     *
     * @param type 表示注解类型的 {@link Class}。
     * @param <T> 表示注解的实际类型。
     * @return 若定义了该注解，则为表示注解的 {@link Annotation}{@code []}。
     */
    <T extends Annotation> T[] getAnnotationsByType(Class<T> type);

    /**
     * 获取空的注解元数据。
     *
     * @return 表示空的注解元数据的 {@link AnnotationMetadata}。
     */
    static AnnotationMetadata empty() {
        return EmptyAnnotationMetadata.INSTANCE;
    }
}
