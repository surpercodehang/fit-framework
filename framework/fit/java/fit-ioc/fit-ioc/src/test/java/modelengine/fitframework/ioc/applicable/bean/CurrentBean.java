/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.applicable.bean;

import modelengine.fitframework.annotation.ApplicableScope;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.ioc.BeanApplicableScope;

/**
 * 定义应用范围为 {@link BeanApplicableScope#CURRENT} 的 Bean。
 *
 * @author 梁济时
 * @since 2022-08-30
 */
@Component
@ApplicableScope(BeanApplicableScope.CURRENT)
public class CurrentBean {}
