/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

/**
 * 为IoC容器提供工厂Bean的定义。
 *
 * <p>将通过接口中泛型参数所提供的类型进行匹配。</p>
 *
 * @param <T> 表示Bean的类型。
 * @author 季聿阶
 * @since 2022-05-31
 */
public interface BeanSupplier<T> {
    /**
     * 获取Bean实例。
     *
     * @return 表示Bean实例的 {@link Object}。
     */
    T get();
}
