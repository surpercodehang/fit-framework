/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation;

import java.lang.reflect.AnnotatedElement;

/**
 * 为注解的元数据提供解析程序。
 *
 * @author 梁济时
 * @since 2022-05-04
 */
@FunctionalInterface
public interface AnnotationMetadataResolver {
    /**
     * 为指定的类型解析注解元数据。
     *
     * @param element 表示待解析的可注解元素的 {@link AnnotatedElement}。
     * @return 表示从对象类型解析到的注解元数据的 {@link AnnotationMetadata}。
     * @throws IllegalArgumentException {@code objectClass} 为 {@code null}。
     */
    AnnotationMetadata resolve(AnnotatedElement element);
}
