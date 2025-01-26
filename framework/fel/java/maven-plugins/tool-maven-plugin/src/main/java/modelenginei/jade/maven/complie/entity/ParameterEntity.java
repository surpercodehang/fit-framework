/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelenginei.jade.maven.complie.entity;

/**
 * 表示参数的定义。
 *
 * @author 杭潇
 * @since 2024-06-06
 */
public class ParameterEntity {
    private String defaultValue;
    private String description;
    private boolean isRequired;
    private String name;
    private String type;

    /**
     * 表示获取参数的描述信息。
     *
     * @return 参数的描述信息的 {@link String}。
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 表示设置参数的描述信息。
     *
     * @param description 表示待设置的参数的描述信息的 {@link String}。
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 表示获取参数的默认值。
     *
     * @return 参数默认值的 {@link String}。
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * 表示设置参数的默认值。
     *
     * @param defaultValue 参数默认值的 {@link String}。
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * 表示参数是否必须的，如果不设置则是非必须值。
     *
     * @return 该参数是否是必须的 {@link Boolean}。
     */
    public boolean isRequired() {
        return this.isRequired;
    }

    /**
     * 表示设置参数是否是必须的值。
     *
     * @param isRequired 参数是否是必须的 {@link Boolean}。
     */
    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    /**
     * 表示获取参数的名字。
     *
     * @return 参数的名字的 {@link String}。
     */
    public String getName() {
        return this.name;
    }

    /**
     * 表示设置参数名。
     *
     * @param name 表示待设置的参数名的 {@link String}。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 表示获取参数的类型。
     *
     * @return 参数的类型的 {@link String}。
     */
    public String getType() {
        return this.type;
    }

    /**
     * 表示设置参数类型。
     *
     * @param type 表示待设置的参数类型的 {@link String}。
     */
    public void setType(String type) {
        this.type = type;
    }
}
