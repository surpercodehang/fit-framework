/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.test.person;

import modelengine.fit.serialization.test.enums.Gender;

/**
 * 为单元测试提供人的信息定义。
 *
 * @author 易文渊
 * @since 2024-09-27
 */
public class PersonGender {
    private String name;
    private Gender gender;

    /**
     * 获取姓名。
     *
     * @return 表示姓名的 {@link String}。
     */
    public String name() {
        return this.name;
    }

    /**
     * 设置名字。
     *
     * @param name 表示姓名的 {@link String}。
     */
    public void name(String name) {
        this.name = name;
    }

    /**
     * 获取性别。
     *
     * @return 表示性别的 {@link Gender}。
     */
    public Gender gender() {
        return this.gender;
    }

    /**
     * 设置性别。
     *
     * @param gender 表示性别的 {@link Gender}。
     */
    public void gender(Gender gender) {
        this.gender = gender;
    }
}