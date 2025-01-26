/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import modelengine.fitframework.broker.Tags;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 表示可修改的 {@link Tags}。
 *
 * @author 季聿阶
 * @since 2023-03-27
 */
public class ConfigurableTags implements Tags {
    private final Set<String> tags = new HashSet<>();

    @Override
    public Set<String> all() {
        return Collections.unmodifiableSet(this.tags);
    }

    @Override
    public boolean contains(String tag) {
        return this.tags.contains(tag);
    }

    /**
     * 设置标签集合。
     *
     * @param tags 表示待设置的标签集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public void set(Set<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }

    /**
     * 添加一个标签。
     *
     * @param tag 表示待添加的标签的 {@link String}。
     */
    public void append(String tag) {
        this.tags.add(tag);
    }

    /**
     * 删除一个标签。
     *
     * @param tag 表示待删除的标签的 {@link String}。
     */
    public void remove(String tag) {
        this.tags.remove(tag);
    }

    /**
     * 清除所有的标签。
     */
    public void clear() {
        this.tags.clear();
    }
}
