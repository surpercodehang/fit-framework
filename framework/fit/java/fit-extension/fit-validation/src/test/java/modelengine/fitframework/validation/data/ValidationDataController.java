/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.data;

import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.validation.Validated;
import modelengine.fitframework.validation.group.NormalCarGroup;

/**
 * 表示评估注解验证数据接口集。
 *
 * @author 吕博文
 * @since 2024-08-15
 */
@Component
@RequestMapping(path = "/validation", group = "评估注解验证数据接口")
public class ValidationDataController {
    /**
     * Car 类默认分组注解验证。
     *
     * @param car 表示注解验证类 {@link Car}。
     */
    @PostMapping(path = "/car/default", description = "验证 Car 类默认分组注解")
    public void validateCarDefaultGroup(@RequestBody @Validated Car car) {}

    /**
     * Car 类特定分组注解验证。
     *
     * @param car 表示注解验证类 {@link Car}。
     */
    @PostMapping(path = "/car/carGroup", description = "验证 Car 类特定分组注解")
    public void validateCarCarGroup(@RequestBody @Validated(NormalCarGroup.class) Car car) {}

    /**
     * Product 类默认分组注解验证。
     *
     * @param product 表示注解验证类 {@link Product}。
     */
    @PostMapping(path = "/product/default", description = "验证 Product 类默认分组注解")
    public void validateProductDefaultGroup(@RequestBody @Validated Product product) {}
}