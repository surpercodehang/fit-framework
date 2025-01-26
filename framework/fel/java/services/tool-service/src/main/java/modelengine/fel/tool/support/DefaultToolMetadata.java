/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.tool.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fel.tool.Tool;
import modelengine.fel.tool.ToolSchema;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表示基于摘要信息构建的工具元数据。
 *
 * @author 王攀博
 * @since 2024-04-18
 */
public class DefaultToolMetadata implements Tool.Metadata {
    private static final Map<String, Type> JSON_SCHEMA_TYPE_TO_JAVA_TYPE = MapBuilder.<String, Type>get()
            .put("string", String.class)
            .put("integer", BigInteger.class)
            .put("number", BigDecimal.class)
            .put("boolean", Boolean.class)
            .put("object", Map.class)
            .put("array", List.class)
            .build();

    private final List<String> parameterOrder;
    private final List<Type> parameterType;
    private final Set<String> requiredParameters;
    private final Map<String, Object> parametersDefaultValues;
    private final Map<String, Object> returnSchema;
    private final String returnConverter;

    /**
     * 通过工具的格式规范初始化 {@link DefaultToolMetadata} 的新实例。
     *
     * @param schema 表示工具格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public DefaultToolMetadata(Map<String, Object> schema) {
        Map<String, Object> parametersSchema =
                notNull(cast(schema.get(ToolSchema.PARAMETERS)), "The parameters schema cannot be null.");
        Map<String, Object> properties = cast(parametersSchema.get(ToolSchema.PARAMETERS_PROPERTIES));
        // 合并额外参数
        if (schema.get(ToolSchema.EXTRA_PARAMETERS) != null) {
            Map<String, Object> extraParameters = cast(schema.get(ToolSchema.EXTRA_PARAMETERS));
            properties = MapUtils.merge(properties, cast(extraParameters.get(ToolSchema.PARAMETERS_PROPERTIES)));
        }
        this.parameterOrder =
                notNull(cast(schema.get(ToolSchema.PARAMETERS_ORDER)), "The order schema cannot be null.");
        this.parameterType = extractParameterType(properties);
        this.requiredParameters = this.extractRequiredParameters(parametersSchema);
        this.parametersDefaultValues = this.extractParamDefaultValue(properties);
        this.returnSchema = getIfNull(cast(schema.get(ToolSchema.RETURN_SCHEMA)), Collections::emptyMap);
        this.returnConverter = nullIf(cast(this.returnSchema.get(ToolSchema.RETURN_CONVERTER)), StringUtils.EMPTY);
    }

    private static Type convertJsonSchemaTypeToJavaType(String schemaType) {
        Type javaType = JSON_SCHEMA_TYPE_TO_JAVA_TYPE.get(schemaType);
        return notNull(javaType,
                () -> new IllegalStateException(StringUtils.format("Unsupported json schema type. [type={0}]",
                        schemaType)));
    }

    @Override
    public List<Type> parameterTypes() {
        return this.parameterType;
    }

    @Override
    public List<String> parameterOrder() {
        return this.parameterOrder;
    }

    @Override
    public Object parameterDefaultValue(String name) {
        return parametersDefaultValues.get(name);
    }

    @Override
    public Set<String> requiredParameters() {
        return this.requiredParameters;
    }

    @Override
    public Map<String, Object> returnType() {
        return this.returnSchema;
    }

    @Override
    public String returnConverter() {
        return this.returnConverter;
    }

    private List<Type> extractParameterType(Map<String, Object> properties) {
        List<String> parameterNames = this.parameterOrder();
        List<Type> types = new ArrayList<>();
        for (String parameterName : parameterNames) {
            Map<String, Object> property = cast(properties.get(parameterName));
            notNull(property, "No property. [name={0}]", parameterName);
            String propertyType = cast(property.get(ToolSchema.PROPERTIES_TYPE));
            types.add(convertJsonSchemaTypeToJavaType(propertyType));
        }
        return types;
    }

    private Set<String> extractRequiredParameters(Map<String, Object> parametersSchema) {
        List<String> required = cast(parametersSchema.get(ToolSchema.PARAMETERS_REQUIRED));
        if (required == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(required);
    }

    private Map<String, Object> extractParamDefaultValue(Map<String, Object> properties) {
        Map<String, Object> defaultParamValue = new HashMap<>();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            Map<String, Object> property = cast(entry.getValue());
            Object value = property.get(ToolSchema.PARAMETER_DEFAULT_VALUE);
            if (value != null) {
                defaultParamValue.put(entry.getKey(), value);
            }
        }
        return defaultParamValue;
    }
}
