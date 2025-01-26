/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.annotation;

import modelengine.fitframework.annotation.Fit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于测试类字段的模拟。
 * <p>被模拟的字段可以完全自定义行为。</p>
 * <p>注意：该注解通过 {@link Fit} 注解来进行测试字段的注入，注入前会生成一个对应类型的 Bean。</p>
 *
 * @author 邬涨财
 * @since 2023-01-29
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Fit
public @interface Mock {}
