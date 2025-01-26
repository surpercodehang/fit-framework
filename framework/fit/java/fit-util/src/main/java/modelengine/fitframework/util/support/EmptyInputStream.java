/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import modelengine.fitframework.inspection.Nonnull;

import java.io.IOException;
import java.io.InputStream;

/**
 * 为 {@link InputStream} 提供空的实现。
 *
 * @author 梁济时
 * @since 2022-11-28
 */
public final class EmptyInputStream extends InputStream {
    /**
     * 表示当前类型的唯一实例。
     */
    public static final EmptyInputStream INSTANCE = new EmptyInputStream();

    /**
     * 隐藏默认构造方法，避免单例类型被外部实例化。
     */
    private EmptyInputStream() {
    }

    @Override
    public int read() {
        return -1;
    }

    @Override
    public int read(@Nonnull byte[] buffer) {
        return 0;
    }

    @Override
    public int read(@Nonnull byte[] buffer, int offset, int length) {
        return 0;
    }

    @Override
    public long skip(long count) {
        return 0;
    }

    @Override
    public int available() {
        return 0;
    }

    @Override
    public void close() {
    }

    @Override
    public synchronized void mark(int readLimit) {
    }

    @Override
    public synchronized void reset() throws IOException {
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
