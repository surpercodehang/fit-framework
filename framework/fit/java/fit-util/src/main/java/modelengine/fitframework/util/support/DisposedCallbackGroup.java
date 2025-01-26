/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import modelengine.fitframework.util.Disposable;
import modelengine.fitframework.util.DisposedCallback;

/**
 * 为 {@link DisposedCallback} 提供组合。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2021-02-25
 */
class DisposedCallbackGroup implements DisposedCallback {
    private final DisposedCallback callback1;
    private final DisposedCallback callback2;

    /**
     * 使用待组合的两个回调方法初始化 {@link DisposedCallbackGroup} 类的新实例。
     *
     * @param callback1 表示待合并的第一个回调方法的 {@link DisposedCallback}。
     * @param callback2 表示待合并的第二个回调方法的 {@link DisposedCallback}。
     */
    private DisposedCallbackGroup(DisposedCallback callback1, DisposedCallback callback2) {
        this.callback1 = callback1;
        this.callback2 = callback2;
    }

    @Override
    public void onDisposed(Disposable disposable) {
        this.callback1.onDisposed(disposable);
        this.callback2.onDisposed(disposable);
    }

    /**
     * 将指定回调方法 {@code another} 合并至当前回调方法 {@code current} 中。
     *
     * @param current 表示当前的回调方法的 {@link DisposedCallback}。
     * @param another 表示待合并到当前回调方法的另一个回调方法的 {@link DisposedCallback}。
     * @return 表示合并后的回调方法的 {@link DisposedCallback}。
     */
    static DisposedCallback combine(DisposedCallback current, DisposedCallback another) {
        if (current == null) {
            return another;
        } else if (another == null) {
            return current;
        } else {
            return new DisposedCallbackGroup(current, another);
        }
    }

    /**
     * 将指定回调方法 {@code another} 从当前回调方法 {@code current} 中移除。
     *
     * @param current 表示当前的回调方法的 {@link DisposedCallback}。
     * @param another 表示待从当前回调方法中移除的另一个回调方法的 {@link DisposedCallback}。
     * @return 表示移除后的回调方法的 {@link DisposedCallback}。
     */
    static DisposedCallback remove(DisposedCallback current, DisposedCallback another) {
        if (current == another) {
            return null;
        }
        if (!(current instanceof DisposedCallbackGroup)) {
            return current;
        }
        DisposedCallbackGroup group = (DisposedCallbackGroup) current;
        DisposedCallback removedCallback1 = remove(group.callback1, another);
        if (removedCallback1 != group.callback1) {
            return combine(removedCallback1, group.callback2);
        }
        DisposedCallback removedCallback2 = remove(group.callback2, another);
        if (removedCallback2 == group.callback2) {
            return group;
        } else {
            return combine(group.callback1, removedCallback2);
        }
    }
}
