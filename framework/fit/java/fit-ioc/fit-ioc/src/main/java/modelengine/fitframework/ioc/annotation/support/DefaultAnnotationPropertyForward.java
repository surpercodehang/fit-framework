/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.support;

import modelengine.fitframework.ioc.annotation.AnnotationProperty;
import modelengine.fitframework.ioc.annotation.AnnotationPropertyForward;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.convert.Converter;

/**
 * 为 {@link AnnotationPropertyForward} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-01-28
 */
public final class DefaultAnnotationPropertyForward implements AnnotationPropertyForward {
    private final AnnotationProperty target;
    private final Class<? extends Converter> converterClass;

    public DefaultAnnotationPropertyForward(AnnotationProperty target, Class<? extends Converter> converterClass) {
        this.target = target;
        this.converterClass = converterClass;
    }

    @Override
    public AnnotationProperty target() {
        return this.target;
    }

    @Override
    public Class<? extends Converter> converterClass() {
        return this.converterClass;
    }

    @Override
    public String toString() {
        return StringUtils.format("[target={0}, converter={1}]", this.target, this.converterClass.getName());
    }
}
