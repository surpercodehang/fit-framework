/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.support;

import static modelengine.fitframework.inspection.Validation.greaterThanOrEquals;
import static modelengine.fitframework.inspection.Validation.lessThanOrEquals;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.protocol.ReadableMessageBody;
import modelengine.fitframework.util.StringUtils;

import java.io.InputStream;

/**
 * {@link ReadableMessageBody} 的抽象类。
 *
 * @author 季聿阶
 * @since 2022-09-03
 */
public abstract class AbstractReadableMessageBody extends InputStream implements ReadableMessageBody {
    /**
     * 校验读取字节数组的参数。
     *
     * @param bytes 表示读取数据后存放的数组的 {@code byte[]}。
     * @param off 表示存放数据的偏移量的 {@code int}。
     * @param len 表示读取数据的最大数量的 {@code int}。
     * @throws IllegalArgumentException 当 {@code bytes} 为 {@code null} 时。
     * @throws IndexOutOfBoundsException 当 {@code off} 或 {@code len} 为负数时，或 {@code off + len}
     * 超过了 {@code bytes} 的长度时。
     */
    protected void validate(byte[] bytes, int off, int len) {
        notNull(bytes, "The bytes to read cannot be null.");
        greaterThanOrEquals(off,
                0,
                () -> new IndexOutOfBoundsException(StringUtils.format("The off in read cannot be negative. [off={0}]",
                        off)));
        greaterThanOrEquals(len,
                0,
                () -> new IndexOutOfBoundsException(StringUtils.format("The len in read cannot be negative. [len={0}]",
                        len)));
        lessThanOrEquals(off + len, bytes.length, () -> {
            String message = StringUtils.format("The (off + len) in read cannot be greater than bytes.length. "
                    + "[off={0}, len={1}, bytesLength={2}]", off, len, bytes.length);
            return new IndexOutOfBoundsException(message);
        });
    }
}
