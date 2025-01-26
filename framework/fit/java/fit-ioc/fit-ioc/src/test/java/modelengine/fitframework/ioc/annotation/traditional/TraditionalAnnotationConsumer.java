/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.traditional;

/**
 * 为传统注解提供消费方。
 *
 * @author 梁济时
 * @since 2022-05-31
 */
@FirstLevel(TraditionalAnnotationConsumer.VALUE)
public class TraditionalAnnotationConsumer {
    /**
     * 表示注解的值。
     */
    public static final String VALUE = "Traditional";
}
