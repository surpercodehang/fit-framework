/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.data;

import modelengine.fitframework.validation.Validated;
import modelengine.fitframework.validation.constraints.Range;

import java.util.List;

/**
 * 表示测试嵌套校验的数据类。
 *
 * @author 李金绪
 * @since 2024-09-06
 */
public class Company {
    @Range(min = 0, max = 1, message = "经理只能有0-1个！")
    private int manager;

    @Range(min = 0, max = 100, message = "工人只能有0-100个！")
    private int worker;

    @Validated
    private Product product;

    @Validated
    private List<Car> cars;

    /**
     * 表示创建一个 {@link Company} 的新实例。
     */
    public Company() {}

    /**
     * 表示创建一个 {@link Company} 的新实例。
     *
     * @param manager 表示经理数量的 {@code int}。
     * @param worker 表示工人数量的 {@code int}。
     * @param product 表示产品的 {@link Product}。
     * @param cars 表示车辆的 {@link List}{@code <}{@link Car}{@code >}。
     */
    public Company(int manager, int worker, Product product, List<Car> cars) {
        this.manager = manager;
        this.worker = worker;
        this.product = product;
        this.cars = cars;
    }
}
