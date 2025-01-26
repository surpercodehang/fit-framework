/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import modelengine.fit.http.openapi3.swagger.entity.Schema;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 表示格式样例的抽象父类。
 *
 * @author 季聿阶
 * @since 2023-08-26
 */
public abstract class AbstractSchema implements Schema {
    private final String name;
    private final Type type;
    private final String description;
    private final List<String> examples;

    AbstractSchema(String name, Type type, String description, List<String> examples) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.examples = examples;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public List<String> examples() {
        return this.examples;
    }
}
