/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation;

import java.lang.annotation.Annotation;

/**
 * 为注解提供属性信息。
 *
 * @author 梁济时
 * @since 2022-05-03
 */
public interface AnnotationProperty {
    /**
     * 获取属性所属的注解类型。
     *
     * @return 表示注解类型的 {@link Class}。
     */
    Class<? extends Annotation> annotation();

    /**
     * 获取属性的名称。
     *
     * @return 表示属性名称的 {@link String}。
     */
    String name();
}
