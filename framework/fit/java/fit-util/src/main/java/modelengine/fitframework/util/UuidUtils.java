/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import modelengine.fitframework.inspection.Validation;

import java.util.UUID;

/**
 * 为唯一通用识别码（Universally Unique Identifier）提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class UuidUtils {
    /** 表示 UUID 不同部分之间的分隔符。 */
    public static final char SEPARATOR = '-';

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private UuidUtils() {}

    /**
     * 检查一个字符是否可用作 UUID。
     * <p><b>此处检查的字符不是用以分隔符的字符，即字符只能为 "0-9"、"a-f" 或 "A-F" 之一。</b></p>
     *
     * @param ch 表示待检查的字符的 {@code char}。
     * @return 若可用作 UUID，则为 {@code true}；否则为 {@code false}。
     */
    public static boolean isUuidCharacter(char ch) {
        return CharacterUtils.between(ch, '0', '9', true, true) || CharacterUtils.between(ch, 'a', 'f', true, true)
                || CharacterUtils.between(ch, 'A', 'F', true, true);
    }

    /**
     * 检查一个字符的序列是否都可用作 UUID。
     *
     * @param chars 表示待检查的字符序列的 {@link CharSequence}。
     * @param from 表示待检查的片段在字符序列中的开始索引的 {@code int}。
     * @param to 表示待检查的片段在字符序列中的结束索引的 {@code int}。（该索引不在检查范围内）
     * @return 若都可用作 UUID，则为 {@code true}；否则为 {@code false}。
     * @throws IllegalArgumentException 当 {@code from} 或 {@code to} 不在有效的值域内时。
     * @see #isUuidCharacter(char)
     */
    public static boolean isUuidCharacterSequence(CharSequence chars, int from, int to) {
        Validation.greaterThanOrEquals(from,
                0,
                "The start index of char sequence cannot be negative. [from={0}]",
                from);
        Validation.between(to,
                from,
                chars.length(),
                "The end index of char sequence is out of range. [from={0}, to={1}, charsLength={2}]",
                from,
                to,
                chars.length());
        for (int i = from; i < to; i++) {
            if (!isUuidCharacter(chars.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查一个字符串是否包含有效的 UUID 信息。
     *
     * @param uuid 表示待检查的字符串的 {@link String}。
     * @return 若包含有效的 UUID 信息，则为 {@code true}；否则为 {@code false}。
     */
    public static boolean isUuidString(String uuid) {
        return isUuidString(uuid, false);
    }

    /**
     * 检查一个字符串是否包含有效的 UUID 信息。
     *
     * @param uuid 表示待检查的字符串的 {@link String}。
     * @param ignoreSeparator 表示忽略分隔符的标识，如果为 {@code true}，表示忽略 UUID 的分隔符，否则不忽略。
     * @return 若包含有效的 UUID 信息，则为 {@code true}；否则为 {@code false}。
     */
    public static boolean isUuidString(String uuid, boolean ignoreSeparator) {
        return ignoreSeparator ? isUuid32(uuid) : isUuid36(uuid);
    }

    /**
     * 生成一个随机的 UUID。
     *
     * @return 表示新生成的随机的 UUID 的 {@link String}。
     * @see UUID#randomUUID()
     */
    public static String randomUuidString() {
        return UUID.randomUUID().toString();
    }

    private static boolean isUuid32(String uuid) {
        if (StringUtils.isBlank(uuid) || uuid.length() != 32) {
            return false;
        }
        return isUuidCharacterSequence(uuid, 0, 32);
    }

    private static boolean isUuid36(String uuid) {
        if (StringUtils.isBlank(uuid) || uuid.length() != 36) {
            return false;
        }
        int[] lengths = {8, 4, 4, 4, 12};
        boolean ret = isUuidCharacterSequence(uuid, 0, lengths[0]);
        int index = lengths[0];
        for (int i = 1; ret && i < lengths.length; i++) {
            int start = index + 1;
            int end = start + lengths[i];
            ret = uuid.charAt(index) == SEPARATOR && isUuidCharacterSequence(uuid, start, end);
            index = end;
        }
        return ret;
    }
}
