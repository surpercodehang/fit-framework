/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelenginei.jade.maven.complie.entity;

import modelengine.fel.tool.ToolSchema;
import modelengine.fel.tool.annotation.ToolMethod;
import modelengine.fitframework.util.MapBuilder;
import modelenginei.jade.maven.complie.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示 {@link ToolMethod} 注解下的方法实体。
 *
 * @author 杭潇
 * @author 易文渊
 * @since 2024-06-13
 */
public class ToolEntity {
    private String genericableId;
    private String fitableId;
    private String namespace;
    private String name;
    private String description;
    private List<ParameterEntity> parameterEntities = new ArrayList<>();
    private List<String> extraParameters;
    private String returnDescription;
    private String returnType;
    private String returnConvertor;
    private Map<String, List<String>> extensions;

    /**
     * 设置 Genericable Id 值。
     *
     * @param genericableId 待设置的 Genericable Id 值。
     */
    public void setGenericableId(String genericableId) {
        this.genericableId = genericableId;
    }

    /**
     * 设置 Fitable Id 值。
     *
     * @param fitableId 待设置的 Fitable Id 值。
     */
    public void setFitableId(String fitableId) {
        this.fitableId = fitableId;
    }

    /**
     * 设置方法名。
     *
     * @param name 表示给定的方法名的 {@link String}。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置命名空间。
     *
     * @param namespace 表示给定命名空间的 {@link String}。
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * 设置方法描述。
     *
     * @param description 表示给定的方法描述的 {@link String}。
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 设置方法的参数信息列表。
     *
     * @param parameterEntities 表示给定的参数信息列表的 {@link List}{@code <}{@link ParameterEntity}{@code >}。
     */
    public void setParameterEntities(List<ParameterEntity> parameterEntities) {
        this.parameterEntities = parameterEntities;
    }

    /**
     * 设置返回值的描述。
     *
     * @param returnDescription 表示给定的返回值的描述的 {@link String}。
     */
    public void setReturnDescription(String returnDescription) {
        this.returnDescription = returnDescription;
    }

    /**
     * 设置的返回值类型。
     *
     * @param returnType 表示给定的返回值类型的 {@link String}。
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * 设置方法额外参数的集合。
     *
     * @param extraParameters 表示方法额外参数集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public void setExtraParameters(List<String> extraParameters) {
        this.extraParameters = extraParameters;
    }

    /**
     * 设置输出转换器的名称。
     *
     * @param returnConvertor 表示输出转换器的名称的 {@link String}。
     */
    public void setReturnConvertor(String returnConvertor) {
        this.returnConvertor = returnConvertor;
    }

    /**
     * 设置扩展属性。
     *
     * @param extensions 表示扩展属性的
     * {@link Map}{@code <}{@link String}{@code ,}{@link List}{@code <}{@link String}{@code >>}。
     */
    public void setExtensions(Map<String, List<String>> extensions) {
        this.extensions = extensions;
    }

    /**
     * 获取工具信息的标准化格式。
     *
     * @return 表示工具标准化信息的 {@link Map}{@link String}{@code , }{@link Object}{@code >}。
     */
    public Map<String, Object> normalize() {
        MapBuilder<String, Object> mapBuilder = new MapBuilder<>();
        return mapBuilder.put(ToolSchema.NAME_SPACE, this.namespace)
                .put(ToolSchema.SCHEMA, normalizeSchema())
                .put(ToolSchema.RUNNABLE, normalizeRunnable())
                .put(ToolSchema.EXTENSIONS, this.extensions)
                .build();
    }

    private Map<String, Object> normalizeSchema() {
        List<String> orderParameters =
                this.parameterEntities.stream().map(ParameterEntity::getName).collect(Collectors.toList());
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put(ToolSchema.NAME, this.name);
        schema.put(ToolSchema.DESCRIPTION, this.description);
        if (this.extraParameters.isEmpty()) {
            schema.put(ToolSchema.PARAMETERS, this.normalizeParameters(orderParameters));
        } else {
            List<String> fullParameters = new ArrayList<>(orderParameters);
            fullParameters.removeAll(this.extraParameters);
            schema.put(ToolSchema.PARAMETERS, this.normalizeParameters(fullParameters));
            schema.put(ToolSchema.EXTRA_PARAMETERS, this.normalizeParameters(this.extraParameters));
        }
        schema.put(ToolSchema.PARAMETERS_ORDER, orderParameters);
        Map<String, Object> returnDetails = new HashMap<>(JsonUtils.convertToMap(this.returnType));
        returnDetails.put(ToolSchema.RETURN_CONVERTER, this.returnConvertor);
        if (this.returnDescription != null) {
            returnDetails.put(ToolSchema.DESCRIPTION, this.returnDescription);
        }
        schema.put(ToolSchema.RETURN_SCHEMA, returnDetails);
        return schema;
    }

    private Map<String, Object> normalizeParameters(List<String> includes) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(ToolSchema.PROPERTIES_TYPE, "object");
        Map<String, Object> variableInfo = new LinkedHashMap<>();
        Set<String> requiredKey = new LinkedHashSet<>();
        for (ParameterEntity parameterEntity : this.parameterEntities) {
            if (!includes.contains(parameterEntity.getName())) {
                continue;
            }
            Map<String, Object> paramDetails = new LinkedHashMap<>(JsonUtils.convertToMap(parameterEntity.getType()));
            if (parameterEntity.getDescription() != null) {
                paramDetails.put(ToolSchema.DESCRIPTION, parameterEntity.getDescription());
            }
            if (!parameterEntity.getDefaultValue().isEmpty()) {
                paramDetails.put(ToolSchema.PARAMETER_DEFAULT_VALUE, parameterEntity.getDefaultValue());
            }
            if (parameterEntity.isRequired()) {
                requiredKey.add(parameterEntity.getName());
            }
            variableInfo.put(parameterEntity.getName(), paramDetails);
        }
        map.put(ToolSchema.PARAMETERS_PROPERTIES, variableInfo);
        map.put(ToolSchema.PARAMETERS_REQUIRED, requiredKey);
        return map;
    }

    private Map<String, Object> normalizeRunnable() {
        Map<String, Object> fitInfo = MapBuilder.<String, Object>get()
                .put("genericableId", this.genericableId)
                .put("fitableId", this.fitableId)
                .build();
        return MapBuilder.<String, Object>get().put("FIT", fitInfo).build();
    }
}