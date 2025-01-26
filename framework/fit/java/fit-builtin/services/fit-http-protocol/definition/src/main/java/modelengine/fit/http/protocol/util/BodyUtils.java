/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.util;

import modelengine.fit.http.protocol.MessageHeaders;
import modelengine.fit.http.protocol.ReadableMessageBody;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.ThreadUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于处理 Http 消息体的工具类。
 *
 * @author 季聿阶
 * @since 2022-12-07
 */
public class BodyUtils {
    private static final int BUFFER_SIZE = 512;

    /**
     * 从指定的消息体中根据指定的消息头读取消息内容。
     *
     * @param body 表示指定的消息体的 {@link ReadableMessageBody}。
     * @param headers 表示指定的消息头的 {@link MessageHeaders}。
     * @return 表示读取到的 Http 消息体内容的 {@code byte[]}。
     * @throws IOException 当发生 I/O 异常时。
     */
    public static byte[] readBody(ReadableMessageBody body, MessageHeaders headers) throws IOException {
        if (HeaderUtils.isChunked(headers)) {
            return readBodyByChunked(body::read);
        } else {
            return readBodyByLength(body::read, HeaderUtils.contentLength(headers));
        }
    }

    /**
     * 从指定的消息体的输入流中根据指定的消息头读取消息内容。
     *
     * @param in 表示指定的消息体的输入流的 {@link InputStream}。
     * @param headers 表示指定的消息头的 {@link MessageHeaders}。
     * @return 表示读取到的 Http 消息体内容的 {@code byte[]}。
     * @throws IOException 当发生 I/O 异常时。
     */
    public static byte[] readBody(InputStream in, MessageHeaders headers) throws IOException {
        if (HeaderUtils.isChunked(headers)) {
            return readBodyByChunked(in::read);
        } else {
            return readBodyByLength(in::read, HeaderUtils.contentLength(headers));
        }
    }

    private static byte[] readBodyByChunked(ReadBytesMethod readMethod) throws IOException {
        int read = 0;
        List<byte[]> bytesList = new ArrayList<>();
        int currentRead;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((currentRead = readMethod.read(buffer)) >= 0) {
            if (currentRead > 0) {
                read += currentRead;
                byte[] readBytes = new byte[currentRead];
                System.arraycopy(buffer, 0, readBytes, 0, currentRead);
                bytesList.add(readBytes);
                continue;
            }
            ThreadUtils.sleep(0L);
            if (Thread.currentThread().isInterrupted()) {
                throw new IllegalStateException(StringUtils.format(
                        "Failed to read enough message body by Chunked: read is interrupted. [read={0}]",
                        read));
            }
        }
        byte[] allReadBytes = new byte[read];
        int copyIndex = 0;
        for (byte[] readBytes : bytesList) {
            System.arraycopy(readBytes, 0, allReadBytes, copyIndex, readBytes.length);
            copyIndex += readBytes.length;
        }
        return allReadBytes;
    }

    private static byte[] readBodyByLength(ReadBytesByLengthMethod readMethod, int length) throws IOException {
        if (length <= 0) {
            return new byte[0];
        }
        byte[] bytes = new byte[length];
        int read = 0;
        while (read < length) {
            int curRead = readMethod.read(bytes, read, length - read);
            if (curRead < 0) {
                break;
            }
            read += curRead;
            if (read < length) {
                ThreadUtils.sleep(0L);
                if (Thread.currentThread().isInterrupted()) {
                    throw new IllegalStateException(StringUtils.format(
                            "Failed to read enough message body by Content-Length: read is interrupted. [read={0}]",
                            read));
                }
            }
        }
        if (read < length) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to read enough message body by Content-Length. [read={0}]",
                    read));
        }
        return bytes;
    }

    /**
     * 表示直接读取整个字节数组的接口。
     */
    private interface ReadBytesMethod {
        /**
         * 读取最多指定字节数组长度个字节，放到指定字节数组中。
         *
         * @param bytes 表示指定的字节数组的 {@code byte[]}。
         * @return 表示读取到的字节数，当不存在任何数据时，返回 {@code -1}。
         * @throws IOException 当方式 I/O 异常时。
         */
        int read(byte[] bytes) throws IOException;
    }

    /**
     * 表示通过偏移量和长度读取整个字节数组的接口。
     */
    private interface ReadBytesByLengthMethod {
        /**
         * 读取最多指定长度个字节，放到指定字节数组的从指定偏移量开始的位置处。
         *
         * @param bytes 表示指定的字节数组的 {@code byte[]}。
         * @param off 表示指定字节数组中的偏移量的 {@code int}。
         * @param len 表示最大读取的字节数的 {@code int}。
         * @return 表示读取到的字节数，当不存在任何数据时，返回 {@code -1}。
         * @throws IOException 当方式 I/O 异常时。
         */
        int read(byte[] bytes, int off, int len) throws IOException;
    }
}
