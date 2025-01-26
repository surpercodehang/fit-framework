/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 为 {@link DependencyResolvingResult} 提供表示成功的结果。
 *
 * @author 梁济时
 * @since 2022-06-27
 */
public final class DependencyResolvingSuccessResult implements DependencyResolvingResult {
    private final Supplier<Object> supplier;

    /**
     * 使用解析到的依赖初始化 {@link DependencyResolvingSuccessResult} 类的新实例。
     *
     * @param supplier 表示解析到的依赖的获取方法的 {@link Object}。
     */
    public DependencyResolvingSuccessResult(Supplier<Object> supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean resolved() {
        return true;
    }

    @Override
    public Object get() {
        return this.supplier.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DependencyResolvingSuccessResult) {
            DependencyResolvingSuccessResult another = (DependencyResolvingSuccessResult) obj;
            return Objects.equals(another.get(), this.get());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {DependencyResolvingSuccessResult.class, this.get()});
    }

    @Override
    public String toString() {
        return StringUtils.format("dependency={0}", this.get());
    }
}
