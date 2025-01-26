/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation;

import modelengine.fitframework.ioc.annotation.support.DefaultAnnotationMetadataResolver;

/**
 * 为 {@link AnnotationMetadataResolver} 提供工具方法。
 *
 * @author 季聿阶
 * @since 2023-03-25
 */
public final class AnnotationMetadataResolvers {
    private static final AnnotationMetadataResolver INSTANCE = new DefaultAnnotationMetadataResolver();

    private AnnotationMetadataResolvers() {}

    /**
     * 获取一个注解解析器的实例。
     *
     * @return 表示注解解析器的实例的 {@link AnnotationMetadataResolver}。
     */
    public static AnnotationMetadataResolver create() {
        return INSTANCE;
    }
}
