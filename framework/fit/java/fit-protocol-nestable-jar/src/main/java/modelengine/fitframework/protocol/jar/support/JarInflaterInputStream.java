/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * 为 {@link InflaterInputStream} 提供用于 JAR 的实现。
 *
 * @author 梁济时
 * @since 2022-09-29
 */
public final class JarInflaterInputStream extends InflaterInputStream {
    private boolean extraBytesWritten;

    public JarInflaterInputStream(InputStream in, long size) {
        super(in, new Inflater(true), sizeOfInflaterBuffer(size));
    }

    @Override
    protected void fill() throws IOException {
        try {
            super.fill();
        } catch (EOFException e) {
            this.handleEofException(e);
        }
    }

    private void handleEofException(EOFException cause) throws EOFException {
        if (this.extraBytesWritten) {
            throw cause;
        }
        this.len = 1;
        this.buf[0] = 0;
        this.extraBytesWritten = true;
        this.inf.setInput(this.buf, 0, this.len);
    }

    /**
     * 获取指定字节数的 JAR 条目数据所使用的缓存的大小。
     *
     * @param dataSize 表示 JAR 条目中包含数据的字节数的 {@code long}。
     * @return 表示 Inflater 所使用的缓存的大小的 {@code int}。
     */
    private static int sizeOfInflaterBuffer(long dataSize) {
        long size = dataSize + 2;
        if (size > 65536) {
            return 8192;
        } else if (size < 0) {
            return 4096;
        } else {
            return (int) size;
        }
    }
}
