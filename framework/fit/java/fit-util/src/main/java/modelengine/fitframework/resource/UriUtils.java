/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 编解码 URI 的工具类。
 * <p>该工具类参考了 RFC 3986。/p>
 *
 * @author 季聿阶
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc3986">RFC 3986</a>
 * @since 2022-12-22
 */
public class UriUtils {
    /**
     * 对查询参数进行编码。
     *
     * @param query 表示待编码的查询参数的 {@link String}。
     * @param charset 表示查询参数的字符集的 {@link Charset}。
     * @return 表示编码后的查询参数的 {@link String}。
     */
    public static String encodeQuery(String query, Charset charset) {
        return encode(query, charset, UriComponentType.QUERY);
    }

    /**
     * 对 URI 的结尾部分进行编码。
     *
     * @param fragment 表示待编码的 URI 的结尾部分的 {@link String}。
     * @param charset 表示 URI 的结尾部分的字符集的 {@link Charset}。
     * @return 表示编码后的 URI 的结尾部分的 {@link String}。
     */
    public static String encodeFragment(String fragment, Charset charset) {
        return encode(fragment, charset, UriComponentType.FRAGMENT);
    }

    private static String encode(String value, Charset charset, UriComponentType type) {
        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY;
        }
        notNull(charset, "The charset cannot be null.");
        byte[] bytes = value.getBytes(charset);
        int len = calculateEncodedLength(bytes, type);
        if (len == bytes.length) {
            return value;
        }
        return encode(bytes, charset, len, type);
    }

    private static int calculateEncodedLength(byte[] bytes, UriComponentType type) {
        int len = bytes.length;
        for (byte aByte : bytes) {
            if (!type.isAllowed(aByte)) {
                len += 2;
            }
        }
        return len;
    }

    private static String encode(byte[] bytes, Charset charset, int len, UriComponentType type) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream(len)) {
            for (byte aByte : bytes) {
                if (type.isAllowed(aByte)) {
                    output.write(aByte);
                } else {
                    output.write('%');
                    char hex1 = Character.toUpperCase(Character.forDigit((aByte >> 4) & 0xF, 16));
                    output.write(hex1);
                    char hex2 = Character.toUpperCase(Character.forDigit(aByte & 0xF, 16));
                    output.write(hex2);
                }
            }
            return new String(output.toByteArray(), charset);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
