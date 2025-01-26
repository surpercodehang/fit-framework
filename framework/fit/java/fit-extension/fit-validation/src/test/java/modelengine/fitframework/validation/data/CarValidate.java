/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.data;

import modelengine.fitframework.validation.Validated;
import modelengine.fitframework.validation.constraints.Range;
import modelengine.fitframework.validation.group.NormalCarGroup;

/**
 * 表示汽车的校验器。
 *
 * @author 李金绪
 * @since 2024-09-04
 */
@Validated
public class CarValidate {
    /**
     * Car 类的校验方法一。
     *
     * @param car 表示校验的 Car 对象 {@link Car}。
     */
    public void validate1(@Validated Car car) {}

    /**
     * Car 类的校验方法二。
     *
     * @param seats 表示输入的座位数量 {@link int}。
     * @param engines 表示输入的发动机数量 {@link int}。
     */
    public void validate2(@Range(min = 0, max = 6, message = "座位数量范围只能在0和6！") int seats, int engines) {}

    /**
     * Car 类的校验方法三。
     *
     * @param car 表示校验的 Car 对象 {@link Car}。
     */
    public void validate3(@Validated(NormalCarGroup.class) Car car) {}
}
