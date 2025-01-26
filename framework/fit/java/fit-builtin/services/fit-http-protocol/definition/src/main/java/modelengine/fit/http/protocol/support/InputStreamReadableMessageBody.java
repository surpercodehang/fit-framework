/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * 表示 {@link modelengine.fit.http.protocol.ReadableMessageBody} 的从 {@link InputStream} 中读取消息体的实现。
 *
 * @author 季聿阶
 * @since 2022-07-14
 */
public class InputStreamReadableMessageBody extends AbstractReadableMessageBody {
    private final InputStream in;

    /**
     * 通过字节输入流来实例化 {@link InputStreamReadableMessageBody}。
     *
     * @param in 表示字节输入流的 {@link InputStream}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     */
    public InputStreamReadableMessageBody(InputStream in) {
        this.in = notNull(in, "The input stream cannot be null.");
    }

    @Override
    public int read() throws IOException {
        return this.in.read();
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        this.validate(bytes, off, len);
        return this.in.read(bytes, off, len);
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }
}
