/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;

/**
 * 为 {@link BeanContainerBuilder} 提供解析程序的配置。
 *
 * @author 梁济时
 * @since 2022-07-04
 */
public interface BeanContainerBuilderResolverConfigurator {
    /**
     * 设置Bean解析程序。
     *
     * @param beanResolver 表示Bean解析程序的 {@link BeanResolver}。
     * @return 表示当前Bean容器构建程序的 {@link BeanContainerBuilder}。
     */
    BeanContainerBuilder bean(BeanResolver beanResolver);

    /**
     * 设置依赖解析程序。
     *
     * @param dependencyResolver 表示依赖解析程序的 {@link DependencyResolver}。
     * @return 表示当前Bean容器构建程序的 {@link BeanContainerBuilder}。
     */
    BeanContainerBuilder dependency(DependencyResolver dependencyResolver);

    /**
     * 设置注解元数据解析程序。
     *
     * @param annotationMetadataResolver 表示注解元数据解析程序的 {@link AnnotationMetadataResolver}。
     * @return 表示当前Bean容器构建程序的 {@link BeanContainerBuilder}。
     */
    BeanContainerBuilder annotation(AnnotationMetadataResolver annotationMetadataResolver);
}
