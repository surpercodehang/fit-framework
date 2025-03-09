/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.util.support;

import modelengine.fitframework.inspection.Validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code .properties} 文件中的键的部分用 {@code .} 号分隔后的子键。
 *
 * @author 季聿阶
 * @since 2021-01-17
 */
public class PropertyKey {
    /**
     * 表示 {@code .properties} 文件中的键用 {@code .} 号拆分后的部分的格式。
     * <p>其有两种格式：</p>
     * <ul>
     *     <li>非数组格式，例如："foo"，"bar"。</li>
     *     <li>数组格式，例如："foo[0]"，"bar[1]"。</li>
     * </ul>
     */
    private static final Pattern PATTERN = Pattern.compile("^([\\w-]+)(\\[([1-9]\\d*|0)])?$");

    private final String actualKey;
    private final boolean isArray;
    private final int arrayIndex;

    /**
     * 将一个 {@code .properties} 文件的一行内容的键用 {@code .} 号拆分后的部分转换为 {@link PropertyKey}。
     * <p>{@code key} 的格式必须满足 {@link #PATTERN} 的格式。</p>
     *
     * @param key 表示一个 {@code .properties} 文件的一行内容的键用 {@code .} 号拆分后的部分的 {@link String}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 或者空白字符串时，或当 {@code key} 不满足
     * {@link #PATTERN} 的格式要求时。
     */
    public PropertyKey(String key) {
        Validation.notBlank(key, "Property key cannot be blank.");
        Matcher matcher = PATTERN.matcher(key);
        Validation.isTrue(matcher.matches(), "Illegal property key pattern. [key={0}]", key);
        this.actualKey = matcher.group(1);
        String index = matcher.group(3);
        if (index != null) {
            this.isArray = true;
            this.arrayIndex = Integer.parseInt(index);
        } else {
            this.isArray = false;
            this.arrayIndex = -1;
        }
    }

    /**
     * 获取实际的属性键。
     *
     * @return 表示实际属性键的 {@link String}。
     */
    public String getActualKey() {
        return this.actualKey;
    }

    /**
     * 判断当前属性键是否为数组格式。
     *
     * @return 表示是否为数组格式的 {@code boolean}。
     */
    public boolean isArray() {
        return this.isArray;
    }

    /**
     * 获取数组索引。
     *
     * @return 表示数组索引的 {@code int}，当不是数组格式时返回 -1。
     */
    public int getArrayIndex() {
        return this.arrayIndex;
    }
}
