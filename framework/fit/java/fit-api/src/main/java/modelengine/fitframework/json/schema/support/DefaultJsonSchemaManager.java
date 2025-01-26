/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.json.schema.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.json.schema.JsonSchema;
import modelengine.fitframework.json.schema.JsonSchemaManager;
import modelengine.fitframework.json.schema.util.SchemaTypeUtils;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表示 {@link JsonSchemaManager} 的默认实现。
 *
 * @author 季聿阶
 * @since 2024-03-31
 */
public class DefaultJsonSchemaManager implements JsonSchemaManager {
    private static final String DEFAULT_REFERENCED_PREFIX = "#/schemas/";

    @Override
    public JsonSchema createSchema(Type type) {
        notNull(type, "The type to create json schema cannot be null.");
        Map<Type, JsonSchema> schemas = this.createSchemas(Collections.singleton(type));
        Validation.equals(schemas.size(), 1, "No json schema created. [type={0}]", type.getTypeName());
        return schemas.values().iterator().next();
    }

    @Override
    public Map<Type, JsonSchema> createSchemas(Set<Type> types) {
        return this.createSchemas(types, DEFAULT_REFERENCED_PREFIX);
    }

    @Override
    public Map<Type, JsonSchema> createSchemas(Set<Type> types, String referencedPrefix) {
        if (CollectionUtils.isEmpty(types)) {
            return Collections.emptyMap();
        }
        Map<Type, JsonSchema> jsonSchemas = new HashMap<>();
        List<ArraySchema> arraySchemas = new ArrayList<>();
        Map<Type, ObjectSchema> referencedSchemas = new HashMap<>();
        for (Type type : types) {
            JsonSchema jsonSchema = this.prepareInitialSchema(type);
            jsonSchemas.put(jsonSchema.type(), jsonSchema);
            if (jsonSchema instanceof ArraySchema) {
                arraySchemas.add((ArraySchema) jsonSchema);
            }
            if (jsonSchema instanceof ObjectSchema) {
                referencedSchemas.put(jsonSchema.type(), (ObjectSchema) jsonSchema);
            }
        }
        String actualReferencedPrefix = nullIf(referencedPrefix, DEFAULT_REFERENCED_PREFIX);
        for (ArraySchema schema : arraySchemas) {
            schema.setItems(actualReferencedPrefix, referencedSchemas);
        }
        for (ObjectSchema schema : referencedSchemas.values()) {
            schema.setProperties(actualReferencedPrefix, referencedSchemas);
        }
        return jsonSchemas;
    }

    @Override
    public JsonSchema createSchema(Method method) {
        notNull(method, "The method to create json schema cannot be null.");
        ObjectSchema parameterSchema = new ObjectSchema(Map.class);
        for (Parameter parameter : method.getParameters()) {
            String name = StringUtils.EMPTY;
            String description = StringUtils.EMPTY;
            String defaultValue = StringUtils.EMPTY;
            boolean required = false;
            Property property = parameter.getDeclaredAnnotation(Property.class);
            if (property != null) {
                name = property.name();
                description = property.description();
                defaultValue = property.defaultValue();
                required = property.required();
            }
            if (StringUtils.isBlank(name)) {
                name = parameter.getName();
            }
            JsonSchema schema = this.createSchema(parameter.getParameterizedType());
            parameterSchema.addSchema(new DecoratedSchema(name, description, defaultValue, schema), required);
        }
        return parameterSchema;
    }

    private JsonSchema prepareInitialSchema(Type type) {
        if (SchemaTypeUtils.isArrayType(type)) {
            return new ArraySchema(type);
        }
        if (SchemaTypeUtils.isObjectType(type)) {
            return new ObjectSchema(type);
        }
        return JsonSchema.createPrimitive(type);
    }
}
