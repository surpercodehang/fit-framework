/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.AttributeCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * {@link AttributeCollection} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-09-01
 */
public class DefaultAttributeCollection implements AttributeCollection {
    private final Map<String, Object> attributes = new HashMap<>();

    @Override
    public List<String> names() {
        return new ArrayList<>(this.attributes.keySet());
    }

    @Override
    public Optional<Object> get(String name) {
        notBlank(name, "The attribute name cannot be blank.");
        return Optional.ofNullable(this.attributes.get(name));
    }

    @Override
    public void set(String name, Object value) {
        notBlank(name, "The attribute name cannot be blank.");
        this.attributes.put(name, value);
    }

    @Override
    public void remove(String name) {
        notBlank(name, "The attribute name cannot be blank.");
        this.attributes.remove(name);
    }

    @Override
    public int size() {
        return this.attributes.size();
    }
}
