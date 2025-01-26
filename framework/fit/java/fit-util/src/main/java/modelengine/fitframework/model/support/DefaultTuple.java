/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.model.Tuple;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link Tuple} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-08-14
 */
public class DefaultTuple implements Tuple {
    private final List<Object> elements;

    /**
     * 通过一系列的元素来实例化元组。
     *
     * @param elements 表示一系列元素的 {@link List}{@code <}{@link Object}{@code >}。
     * @throws IllegalArgumentException 当 {@code elements} 为 {@code null} 时。
     */
    public DefaultTuple(List<Object> elements) {
        this.elements = new ArrayList<>(Validation.notNull(elements, "The elements cannot be null."));
    }

    @Override
    public int capacity() {
        return this.elements.size();
    }

    @Override
    public <T> Optional<T> get(int index) {
        if (index < 0 || index >= this.capacity()) {
            throw new IndexOutOfBoundsException(StringUtils.format(
                    "Index to get element of tuple is out of range. [index={0}, size={1}]",
                    index,
                    this.capacity()));
        }
        return Optional.ofNullable(ObjectUtils.cast(this.elements.get(index)));
    }
}
