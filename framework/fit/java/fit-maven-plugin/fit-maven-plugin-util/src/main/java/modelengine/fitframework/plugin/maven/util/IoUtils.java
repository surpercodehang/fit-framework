/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 为输入输出流提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 1.0
 */
public class IoUtils {
    /** 表示默认的字符集。 */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private IoUtils() {}

    /**
     * 从流中读取所有文本。
     *
     * @param in 表示包含文本信息的流的 {@link InputStream}。
     * @return 表示从流中读取到的文本信息的 {@link String}。
     * @throws IOException 读取过程发生IO异常。
     */
    public static String read(InputStream in) throws IOException {
        return read(in, null);
    }

    /**
     * 从流中读取所有文本。
     *
     * @param in 表示包含文本信息的流的 {@link InputStream}。
     * @param charset 表示文本的字符集的 {@link Charset}。
     * @return 表示从流中读取到的文本信息的 {@link String}。
     * @throws IOException 读取过程发生IO异常。
     */
    public static String read(InputStream in, Charset charset) throws IOException {
        if (in == null) {
            throw new IllegalStateException("InputStream is null.");
        } else {
            Charset actualCharset = ObjectUtils.nullIf(charset, DEFAULT_CHARSET);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, actualCharset))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    /**
     * 从输入流中读取指定长度的数据。
     * <p>当未能读取到指定长度的数据时会持续阻塞，并累积读入的数据。</p>
     *
     * @param in 表示输入流的 {@link InputStream}。
     * @return 表示从输入流中读取到的数据的 {@code byte[]}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IOException 读取过程发生输入输出异常。
     */
    public static byte[] readBytes(InputStream in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("The input stream to read cannot be null.");
        }
        int length = in.available();
        if (length < 0) {
            throw new IllegalArgumentException("The length of data to read cannot be negative.");
        }
        if (length == 0) {
            return new byte[0];
        }
        byte[] buffer = new byte[length];
        int read = 0;
        do {
            int current = in.read(buffer, read, length - read);
            if (current < 0) {
                throw new IOException(String.format(Locale.ROOT,
                        "Fail to read from input stream: no enough available bytes. "
                                + "[expectedLength=%d, actualLength=%d]", length, read));
            } else {
                read += current;
            }
        } while (read < length);
        return buffer;
    }
}
