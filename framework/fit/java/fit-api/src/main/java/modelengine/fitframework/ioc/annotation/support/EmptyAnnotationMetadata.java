/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.ioc.annotation.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

/**
 * 为 {@link AnnotationMetadata} 提供空实现。
 *
 * @author 梁济时
 * @since 2022-08-16
 */
public class EmptyAnnotationMetadata implements AnnotationMetadata {
    /**
     * 获取 {@link EmptyAnnotationMetadata} 的唯一实例。
     */
    public static final EmptyAnnotationMetadata INSTANCE = new EmptyAnnotationMetadata();

    /**
     * 隐藏默认构造方法，避免单例类被外部实例化。
     */
    private EmptyAnnotationMetadata() {}

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> type) {
        return false;
    }

    @Override
    public Annotation[] getAnnotations() {
        return new Annotation[0];
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return null;
    }

    @Override
    public <T extends Annotation> T[] getAnnotationsByType(Class<T> type) {
        return cast(Array.newInstance(type, 0));
    }
}
