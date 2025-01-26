/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.test.person;

import com.fasterxml.jackson.annotation.JsonProperty;

import modelengine.fitframework.annotation.Property;

/**
 * 为单元测试提供人的信息定义。
 *
 * @author 易文渊
 * @since 2024-08-06
 */
public class PersonAlias {
    @Property(name = "first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @Property(name = "person_name")
    private PersonName name;

    /**
     * 获取名字。
     *
     * @return 表示名字的 {@link String}。
     */
    public String firstName() {
        return this.firstName;
    }

    /**
     * 设置名字。
     *
     * @param firstName 表示名字的 {@link String}。
     */
    public void firstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * 获取姓氏。
     *
     * @return 表示姓氏的 {@link String}。
     */
    public String lastName() {
        return this.lastName;
    }

    /**
     * 设置姓氏。
     *
     * @param lastName 表示姓氏的 {@link String}。
     */
    public void lastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * 设置名字。
     *
     * @return 表示名字的 {@link PersonName}。
     */
    public PersonName getName() {
        return this.name;
    }

    /**
     * 设置名字。
     *
     * @param name 表示待设置名字的 {@link PersonName}。
     */
    public void setName(PersonName name) {
        this.name = name;
    }
}
