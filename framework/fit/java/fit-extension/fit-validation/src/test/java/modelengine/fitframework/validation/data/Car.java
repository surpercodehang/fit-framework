/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.data;

import modelengine.fitframework.validation.constraints.NotBlank;
import modelengine.fitframework.validation.constraints.NotEmpty;
import modelengine.fitframework.validation.constraints.Range;
import modelengine.fitframework.validation.group.NormalCarGroup;
import modelengine.fitframework.validation.group.OldCarGroup;

/**
 * 表示汽车的数据类。
 *
 * @author 李金绪
 * @since 2024-09-04
 */
public class Car {
    @Range(min = 0, max = 6, message = "座位数量范围只能在0和6！")
    private int seats;

    @Range(min = 0, max = 2, message = "发动机数量范围只能在0和2！")
    private int engines;

    @NotBlank(message = "品牌不能为空！")
    private String brand;

    @NotEmpty(message = "型号不能为空！")
    private String model;

    @Range(min = 2000, max = 2030, message = "生产年份在2000-2030之内", groups = {NormalCarGroup.class})
    private int normalManufactureYear;

    @Range(min = 1900, max = 1999, message = "生产年份在1900-1999之内", groups = {OldCarGroup.class})
    private int oldManufactureYear;

    /**
     * 表示创建一个 {@link Car} 的新实例。
     */
    public Car() {}

    /**
     * 表示创建一个 {@link Car} 的新实例。
     *
     * @param seats 表示座位数量的 {@code int}。
     * @param engines 表示发动机数量的 {@code int}。
     * @param brand 表示品牌的 {@link String}。
     * @param model 表示型号的 {@link String}。
     * @param normalManufactureYear 表示新型汽车的生产年份的 {@code int}。
     * @param oldManufactureYear 表示老旧汽车的生产年份的 {@code int}。
     */
    public Car(int seats, int engines, String brand, String model, int normalManufactureYear, int oldManufactureYear) {
        this.seats = seats;
        this.engines = engines;
        this.brand = brand;
        this.model = model;
        this.normalManufactureYear = normalManufactureYear;
        this.oldManufactureYear = oldManufactureYear;
    }
}
