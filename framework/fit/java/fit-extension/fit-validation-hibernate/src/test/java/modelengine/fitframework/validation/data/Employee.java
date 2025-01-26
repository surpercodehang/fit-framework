/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 雇员 pojo。
 *
 * @author 易文渊
 * @since 2024-09-27
 */
public class Employee {
    @NotBlank
    private String name;

    @Min(value = 18, message = "年龄必须大于等于18")
    private int age;

    /**
     * 构造函数。
     *
     * @param name 表示用户名字的 {@link String}。
     * @param age 表示用户年龄的 {@code int}。
     */
    public Employee(String name, int age) {
        this.name = name;
        this.age = age;
    }
}