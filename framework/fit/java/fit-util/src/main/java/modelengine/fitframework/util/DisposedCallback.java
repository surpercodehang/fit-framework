/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

/**
 * 当对象被销毁时被回调的方法。
 *
 * @author 梁济时
 * @since 2021-02-25
 */
@FunctionalInterface
public interface DisposedCallback {
    /**
     * 当对象被销毁时被通知。
     *
     * @param disposable 表示被销毁的对象的 {@link Disposable}。
     */
    void onDisposed(Disposable disposable);
}
