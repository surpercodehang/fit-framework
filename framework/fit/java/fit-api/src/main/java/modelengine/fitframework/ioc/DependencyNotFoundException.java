/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

/**
 * 当Bean依赖不存在时引发的异常。
 *
 * @author 梁济时
 * @since 2022-05-09
 */
public class DependencyNotFoundException extends DependencyException {
    /**
     * 使用异常信息初始化 {@link DependencyNotFoundException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public DependencyNotFoundException(String message) {
        super(message);
    }

    /**
     * 使用引发异常的原因初始化 {@link DependencyNotFoundException} 类的新实例。
     *
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public DependencyNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link DependencyNotFoundException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public DependencyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
