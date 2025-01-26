/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import modelengine.fitframework.util.Disposable;
import modelengine.fitframework.util.DisposedCallback;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 为 {@link Disposable} 提供基类。
 *
 * @author 梁济时
 * @since 2021-02-25
 */
public abstract class AbstractDisposable implements Disposable {
    private final AtomicBoolean disposed;
    private final AtomicReference<DisposedCallback> disposedCallback;

    /**
     * 初始化 {@link AbstractDisposable} 类的新实例。
     */
    public AbstractDisposable() {
        this.disposed = new AtomicBoolean(false);
        this.disposedCallback = new AtomicReference<>();
    }

    @Override
    public final void dispose() {
        if (this.disposed.compareAndSet(false, true)) {
            this.dispose0();
            this.onDisposed();
        }
    }

    /**
     * 释放对象占用的资源。
     */
    protected void dispose0() {}

    @Override
    public boolean disposed() {
        return this.disposed.get();
    }

    @Override
    public void subscribe(DisposedCallback callback) {
        this.disposedCallback.getAndUpdate(current -> DisposedCallbackGroup.combine(current, callback));
    }

    @Override
    public void unsubscribe(DisposedCallback callback) {
        this.disposedCallback.getAndUpdate(current -> DisposedCallbackGroup.remove(current, callback));
    }

    /**
     * 触发 {@link DisposedCallback} 回调。
     */
    protected void onDisposed() {
        DisposedCallback callback = this.disposedCallback.get();
        if (callback != null) {
            callback.onDisposed(this);
        }
    }
}
