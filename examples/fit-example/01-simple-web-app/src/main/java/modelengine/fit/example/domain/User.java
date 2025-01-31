/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.example.domain;

/**
 * 表示用户资源类。
 *
 * @author 季聿阶
 * @since 2025-01-31
 */
public class User {
    private final String name;
    private final String age;
    private final int id;

    public User(String name, String age, int id) {
        this.name = name;
        this.age = age;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getAge() {
        return this.age;
    }

    public int getId() {
        return id;
    }
}
