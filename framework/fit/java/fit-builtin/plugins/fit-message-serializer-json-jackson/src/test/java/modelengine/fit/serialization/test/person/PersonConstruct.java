/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.test.person;

import java.beans.ConstructorProperties;

/**
 * 为全参构造反序列化提供测试支持。
 *
 * @author 易文渊
 * @since 2024-10-10
 */
public class PersonConstruct {
    private final String name;
    private final int age;

    /**
     * 全参构造函数，用于创建对象。
     *
     * @param name 表示人名的 {@link String}。
     * @param age 表示年龄的 {@code int}。
     */
    @ConstructorProperties({"name", "age"})
    public PersonConstruct(String name, int age) {
        this.name = name;
        this.age = age;
    }
}