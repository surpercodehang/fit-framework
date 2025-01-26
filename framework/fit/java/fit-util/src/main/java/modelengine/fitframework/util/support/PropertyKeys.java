/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code .properties} 文件中的键的部分。
 *
 * @author 季聿阶
 * @since 2022-01-31
 */
public class PropertyKeys {
    private static final char KEY_SEPARATOR = '.';

    private final List<PropertyKey> keys;

    /**
     * 获取键经过切分后的所有部分。
     *
     * @param key 表示键的 {@link String}。
     */
    public PropertyKeys(String key) {
        this.keys = StringUtils.splitToList(key, KEY_SEPARATOR)
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(PropertyKey::new)
                .collect(Collectors.toList());
        Validation.isTrue(CollectionUtils.isNotEmpty(this.keys),
                "Property key must have 1 sub-key at least. [key={0}]",
                key);
    }

    /**
     * 获取键经过切分后的指定部分。
     *
     * @param index 表示切分后的下标的 {@code int}。
     * @return 表示键经过切分年后的指定部分的 {@link PropertyKey}。
     */
    public PropertyKey get(int index) {
        Validation.between(index,
                0,
                this.keys.size() - 1,
                "Property key index out of range. [index={0}, keysSize={1}]",
                index,
                this.keys.size());
        return this.keys.get(index);
    }

    /**
     * 判断指定位置是否为切分后的最后一部分。
     *
     * @param index 表示指定下标的 {@code int}。
     * @return 如果是最后一部分，返回 {@code true}，否则，返回 {@code false}。
     */
    public boolean isLast(int index) {
        return index == this.keys.size() - 1;
    }
}
