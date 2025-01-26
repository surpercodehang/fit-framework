/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean;

/**
 * 为值提供获取程序。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
@FunctionalInterface
public interface ValueSupplier {
    /**
     * 获取实际值。
     *
     * @return 表示实际值的 {@link Object}。
     */
    Object get();

    /**
     * 当指定的值可能是 {@link ValueSupplier} 的实例时，获取实际值。
     * <p>若 {@code value} 是一个 {@link ValueSupplier}，则使用 {@link ValueSupplier#get()} 方法获取值。</p>
     * <p>这个过程将递归下去，直到这个值不是一个 {@link ValueSupplier} 为止。</p>
     *
     * @param value 表示待获取实际值的原始值的 {@link Object}。
     * @return 表示实际值的 {@link Object}。
     */
    static Object real(Object value) {
        Object result = value;
        while (result instanceof ValueSupplier) {
            result = ((ValueSupplier) result).get();
        }
        return result;
    }
}
