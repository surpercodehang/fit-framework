/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.data;

import modelengine.fitframework.validation.constraints.NotBlank;
import modelengine.fitframework.validation.constraints.Positive;

/**
 * 表示产品的数据类。
 *
 * @author 吕博文
 * @since 2024-08-02
 */
public class Product {
    @NotBlank(message = "产品名不能为空")
    private String name;

    @Positive(message = "产品价格必须为正")
    private Double price;

    @Positive(message = "产品数量必须为正")
    private Integer quantity;

    @NotBlank(message = "产品类别不能为空")
    private String category;

    /**
     * Product 默认构造函数。
     *
     */
    public Product() {}

    public Product(String name, Double price, Integer quantity, String category) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }
}
