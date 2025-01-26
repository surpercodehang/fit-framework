/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.util;

import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.IoUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 计算和获取字符串的哈希值的工具类。
 *
 * @author 李鑫
 * @since 2021-11-22
 */
public class DigestUtils {
    private static final Logger log = Logger.get(DigestUtils.class);

    /**
     * 获取指定字符串列表的哈希值。
     *
     * @param values 表示待计算哈希值的字符串列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param algorithm 表示哈希算法名字的 {@link String}。
     * @return 表示指定字符串列表的哈希值的 {@link String}。
     */
    public static String hashCode(List<String> values, String algorithm) {
        try {
            MessageDigest instance = MessageDigest.getInstance(algorithm);
            values.forEach(str -> instance.update(str.getBytes(StandardCharsets.UTF_8)));
            return IoUtils.toHexString(instance.digest());
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to get hash code.", e);
            throw new IllegalStateException("Failed to get hash code.", e);
        }
    }
}
