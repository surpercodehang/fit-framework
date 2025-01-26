/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import java.io.IOException;
import java.io.InputStream;

/**
 * 为 {@link InputStream} 提供支持设置长度的装饰程序。
 *
 * @author 梁济时
 * @since 2023-02-21
 */
final class LimitedInputStream extends InputStream {
    private final InputStream inner;
    private final long length;
    private long position;

    LimitedInputStream(InputStream inner, long length) {
        this.inner = inner;
        this.length = length;
        this.position = 0L;
    }

    @Override
    public void close() throws IOException {
        this.inner.close();
    }

    @Override
    public int read() throws IOException {
        if (this.position < this.length) {
            int value = this.inner.read();
            if (value > -1) {
                this.position++;
            }
            return value;
        } else {
            return -1;
        }
    }

    @Override
    public int read(byte[] bytes, int offset, int length) throws IOException {
        int actualLength = length;
        if (actualLength > 0) {
            if (this.position >= this.length) {
                return -1;
            } else {
                actualLength = (int) Math.min(actualLength, this.length - position);
            }
        }
        int result = this.inner.read(bytes, offset, actualLength);
        if (result > 0) {
            this.position += result;
        }
        return result;
    }

    @Override
    public int available() {
        long result = this.length - this.position;
        if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return (int) result;
        }
    }

    @Override
    public long skip(long bytes) throws IOException {
        long actualBytes = bytes;
        if (actualBytes > 0) {
            actualBytes = Math.min(this.length - this.position, bytes);
        }
        return this.inner.skip(actualBytes);
    }
}
