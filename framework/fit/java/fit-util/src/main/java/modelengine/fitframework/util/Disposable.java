/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

/**
 * 为可销毁的对象提供定义。
 *
 * @author 梁济时
 * @since 2021-02-25
 */
public interface Disposable extends AutoCloseable {
    /**
     * 销毁当前对象。
     */
    void dispose();

    /**
     * 获取一个值，该值指示对象是否已经被销毁。
     *
     * @return 若对象已经被销毁，则为 {@code true}；否则为 {@code false}。
     */
    boolean disposed();

    /**
     * 订阅对象被销毁事件。
     *
     * @param callback 表示当对象被销毁时执行的回调方法的 {@link DisposedCallback}。
     */
    void subscribe(DisposedCallback callback);

    /**
     * 取消订阅对象被销毁事件。
     *
     * @param callback 表示对象被销毁时执行的回调方法的 {@link DisposedCallback}。
     */
    void unsubscribe(DisposedCallback callback);

    @Override
    default void close() {
        this.dispose();
    }

    /**
     * 销毁指定对象。
     *
     * @param disposableObject 表示待销毁的对象的 {@link Object}。
     */
    static void safeDispose(Disposable disposableObject) {
        if (disposableObject == null) {
            return;
        }
        try {
            disposableObject.dispose();
        } catch (Exception ignored) {
            // Ignore any exceptions.
        }
    }
}
