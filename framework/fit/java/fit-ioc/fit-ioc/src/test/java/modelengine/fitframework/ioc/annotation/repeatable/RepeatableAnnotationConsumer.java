/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.repeatable;

/**
 * 为可重复注解提供消费方。
 *
 * @author 梁济时
 * @since 2022-05-31
 */
@Entry
@Value("RepeatableAnnotationConsumer")
@A1(a1 = "A1")
@A2(a2 = "A2")
public class RepeatableAnnotationConsumer {}
