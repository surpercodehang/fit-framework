/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.data;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 公司 pojo。
 *
 * @author 易文渊
 * @since 2024-09-27
 */
public class Company {
    @Valid
    @NotNull(message = "雇员列表不能为空")
    List<@Valid @NotNull Employee> employees;

    /**
     * 构建函数。
     *
     * @param employees 表示雇员列表的 {@link List}{@code <}{@link Employee}{@code >}。
     */
    public Company(List<Employee> employees) {
        this.employees = employees;
    }
}