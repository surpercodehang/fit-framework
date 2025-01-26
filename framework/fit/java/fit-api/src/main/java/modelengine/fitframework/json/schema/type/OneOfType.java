/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.json.schema.type;

import static modelengine.fitframework.inspection.Validation.notEmpty;
import static modelengine.fitframework.inspection.Validation.notNull;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表示 JsonSchema 中 OneOf 的类型。
 *
 * @author 季聿阶
 * @since 2024-10-22
 */
public class OneOfType implements Type {
    private final List<Type> types;

    public OneOfType(Type... types) {
        notNull(types, "The types cannot be null.");
        this.types = Collections.unmodifiableList(Arrays.asList(types));
    }

    public OneOfType(List<Type> types) {
        notEmpty(types, "The types cannot be empty.");
        this.types = Collections.unmodifiableList(types);
    }

    public List<Type> types() {
        return this.types;
    }

    @Override
    public String getTypeName() {
        return this.types.stream().map(Type::getTypeName).collect(Collectors.joining(", ", "OneOf{", "}"));
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || this.getClass() != another.getClass()) {
            return false;
        }
        OneOfType oneOfType = (OneOfType) another;
        return Objects.equals(this.types, oneOfType.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.types);
    }

    @Override
    public String toString() {
        return this.getTypeName();
    }
}
