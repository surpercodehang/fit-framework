/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import java.util.function.Supplier;

/**
 * 为依赖提供解析结果。
 *
 * @author 梁济时
 * @since 2022-06-27
 */
public interface DependencyResolvingResult {
    /**
     * 获取一个值，该值指示依赖是否被成功解析。
     *
     * @return 若被成功解析，则为 {@code true}；否则为 {@code false}。
     */
    boolean resolved();

    /**
     * 获取解析到的依赖。
     * <p>只有当 {@link DependencyResolvingResult#resolved()} 为 {@code true} 时有意义，但所依赖的对象依旧可能为 {@code null}。</p>
     *
     * @return 表示所依赖的对象的 {@link Object}。
     */
    Object get();

    /**
     * 获取表示失败的解析结果。
     *
     * @return 表示解析结果的 {@link DependencyResolvingResult}。
     */
    static DependencyResolvingResult failure() {
        return DependencyResolvingFailureResult.INSTANCE;
    }

    /**
     * 获取表示成功的解析结果。
     *
     * @param supplier 表示所依赖的对象的获取方法的 {@link Supplier}。
     * @return 表示解析结果的 {@link DependencyResolvingResult}。
     */
    static DependencyResolvingResult success(Supplier<Object> supplier) {
        return new DependencyResolvingSuccessResult(supplier);
    }
}
