/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime;

import modelengine.fitframework.ioc.BeanResolver;
import modelengine.fitframework.ioc.DependencyResolver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;

/**
 * 定义 {@link FitRuntime} 中所使用的所有解析程序。
 *
 * @author 梁济时
 * @since 2022-12-05
 */
public interface FitRuntimeResolvers {
    /**
     * 获取 Bean 解析程序。
     *
     * @return 表示 Bean 解析程序的 {@link BeanResolver}。
     */
    BeanResolver bean();

    /**
     * 获取依赖解析程序。
     *
     * @return 表示依赖解析程序的 {@link DependencyResolver}。
     */
    DependencyResolver dependency();

    /**
     * 获取注解解析程序。
     *
     * @return 表示注解解析程序的 {@link AnnotationMetadataResolver}。
     */
    AnnotationMetadataResolver annotation();
}
