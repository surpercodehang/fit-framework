/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.tree.support;

import modelengine.fitframework.ioc.annotation.tree.AnnotationTreeNode;
import modelengine.fitframework.ioc.annotation.tree.AnnotationTreeNodeProperty;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.convert.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 为 {@link AnnotationTreeNodeProperty} 提供值转换的装饰程序。
 *
 * @author 梁济时
 * @since 2023-01-28
 */
final class ConvertedAnnotationTreeNodeProperty implements AnnotationTreeNodeProperty {
    private final AnnotationTreeNodeProperty origin;
    private final Converter converter;

    ConvertedAnnotationTreeNodeProperty(AnnotationTreeNodeProperty origin, Converter converter) {
        this.origin = origin;
        this.converter = converter;
    }

    @Override
    public AnnotationTreeNode node() {
        return this.origin.node();
    }

    @Override
    public String name() {
        return this.origin.name();
    }

    @Override
    public Object defaultValue() {
        return this.converter.convert(this.origin.defaultValue());
    }

    @Override
    public Object value() {
        return this.converter.convert(this.origin.value());
    }

    @Override
    public List<AnnotationTreeNodeProperty> sources() {
        return this.origin.sources();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            ConvertedAnnotationTreeNodeProperty another = (ConvertedAnnotationTreeNodeProperty) obj;
            return Objects.equals(this.origin, another.origin) && Objects.equals(this.converter, another.converter);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.origin, this.converter});
    }

    @Override
    public String toString() {
        return StringUtils.format("[origin={0}, converter={1}]",
                this.origin, this.converter.getClass().getName());
    }
}
