/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.greaterThan;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 为安全提供工具方法。
 *
 * @author 季聿阶
 * @since 2023-07-26
 */
public class SecurityUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private SecurityUtils() {}

    /**
     * 计算指定文件的签名。
     *
     * @param file 表示待计算签名的文件的 {@link File}。
     * @param algorithm 表示计算签名使用的算法名称的 {@link String}。
     * @param bufferSize 表示在计算签名时使用的缓存大小的 {@code int}。
     * @return 表示文件的签名信息的 {@link String}。
     * @throws IllegalStateException 当算法不存在或读取文件失败时。
     */
    public static String signatureOf(File file, String algorithm, int bufferSize) {
        notNull(file, "The file to compute signature cannot be null.");
        notBlank(algorithm, "The algorithm to compute signature cannot be blank.");
        greaterThan(bufferSize, 0, "The buffer size to compute signature must be positive.");
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(StringUtils.format("Signature algorithm not found. [algorithm={0}]",
                    algorithm), e);
        }
        byte[] data;
        try (InputStream in = new FileInputStream(file)) {
            int total = (int) file.length();
            int read = 0;
            byte[] buffer = new byte[bufferSize];
            while (read < total) {
                int part = in.read(buffer, 0, buffer.length);
                digest.update(buffer, 0, part);
                read += part;
            }
            data = digest.digest();
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to read file to compute signature. [file={0}, error={1}]",
                    FileUtils.path(file),
                    e.getMessage()), e);
        }
        return hex(data);
    }

    private static String hex(byte[] data) {
        StringBuilder builder = new StringBuilder();
        for (byte aByte : data) {
            appendHex(builder, aByte);
        }
        return builder.toString();
    }

    private static void appendHex(StringBuilder builder, byte value) {
        int intValue = Byte.toUnsignedInt(value);
        appendSingleHex(builder, (intValue >> 4) & 0xf);
        appendSingleHex(builder, intValue & 0xf);
    }

    private static void appendSingleHex(StringBuilder builder, int value) {
        if (value < 10) {
            builder.append((char) (value + '0'));
        } else {
            builder.append((char) (value - 10 + 'a'));
        }
    }
}
