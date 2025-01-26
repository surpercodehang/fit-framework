/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans;

import modelengine.fitframework.beans.support.ReflectionFactoryInstantiator;

/**
 * 任意对象生成器。
 *
 * @param <T> 表示待实例化对象的类型的 {@link T}。
 * @author 季聿阶
 * @since 2022-05-10
 */
public interface ObjectInstantiator<T> {
    /**
     * 实例化一个对象。
     *
     * @return 表示待实例化的对象的 {@link T}。
     */
    T newInstance();

    /**
     * 创建一个标准的对象实例化器。
     *
     * @param type 表示待实例化的对象的类型的 {@link class}{@code <}{@link T}{@code >}。
     * @param <T> 表示对象类型的 {@link T}。
     * @return 表示指定类型的标准对象实例化器的 {@link ObjectInstantiator}{@code <}{@link T}{@code >}。
     * @throws IllegalArgumentException 当 {@code type} 为 {@code null} 时。
     */
    static <T> ObjectInstantiator<T> standard(Class<T> type) {
        return new ReflectionFactoryInstantiator<>(type);
    }
}
