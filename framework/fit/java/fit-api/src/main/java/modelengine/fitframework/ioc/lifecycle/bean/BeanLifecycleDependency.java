/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean;

/**
 * 表示Bean生命周期的依赖，实现了该接口的Bean不会被 {@link BeanLifecycleInterceptor} 拦截。
 *
 * @author 梁济时
 * @since 2022-05-20
 */
public interface BeanLifecycleDependency {}
