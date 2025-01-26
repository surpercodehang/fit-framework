/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity.support;

import static modelengine.fitframework.inspection.Validation.greaterThanOrEquals;
import static modelengine.fitframework.inspection.Validation.lessThanOrEquals;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.ReadableBinaryEntity;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 表示 {@link ReadableBinaryEntity} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-10-12
 */
public class DefaultReadableBinaryEntity extends AbstractEntity implements ReadableBinaryEntity {
    private final InputStream in;

    /**
     * 创建只读的二进制的消息体数据对象。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param in 表示获取的二进制数据来源的输入流的 {@link InputStream}。
     */
    public DefaultReadableBinaryEntity(HttpMessage httpMessage, InputStream in) {
        super(httpMessage);
        this.in = getIfNull(in, () -> new ByteArrayInputStream(new byte[0]));
    }

    @Override
    public int read() throws IOException {
        return this.in.read();
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        notNull(bytes, "The bytes to read cannot be null.");
        greaterThanOrEquals(off,
                0,
                () -> new IndexOutOfBoundsException(StringUtils.format("The off cannot be negative. [off={0}]", off)));
        greaterThanOrEquals(len,
                0,
                () -> new IndexOutOfBoundsException(StringUtils.format("The len cannot be negative. [len={0}]", len)));
        lessThanOrEquals(off + len,
                bytes.length,
                () -> new IndexOutOfBoundsException(StringUtils.format(
                        "The (off + len) cannot be greater than bytes.length. [off={0}, len={1}, bytes.length={2}]",
                        off,
                        len,
                        bytes.length)));
        return this.in.read(bytes, off, len);
    }

    @Override
    public InputStream getInputStream() {
        return this.in;
    }

    @Nonnull
    @Override
    public MimeType resolvedMimeType() {
        return MimeType.APPLICATION_OCTET_STREAM;
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.in.close();
    }
}
