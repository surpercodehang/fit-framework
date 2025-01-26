/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.data;

import modelengine.fitframework.validation.Validated;

import java.util.List;
import java.util.Map;

/**
 * 表示公司的校验器。
 *
 * @author 李金绪
 * @since 2024-09-06
 */
public class NestedValidate {
    /**
     * 嵌套校验的测试方法一。
     *
     * @param obj 需要被校验的对象的 {@link Company}。
     */
    public void test1(@Validated Company obj) {}

    /**
     * 嵌套校验的测试方法二。
     *
     * @param obj 需要被校验的对象的 {@link List}{@code <}{@link Car}{@code >}。
     */
    public void test2(@Validated List<Car> obj) {}

    /**
     * 嵌套校验的测试方法三。
     *
     * @param obj 需要被校验的对象的 {@link List}{@code <}{@link List}{@code <}{@link Car}{@code >>}。
     */
    public void test3(@Validated List<List<Car>> obj) {}

    /**
     * 嵌套校验的测试方法四。
     *
     * @param obj 需要被校验的对象的 {@link Map}{@code <}{@link String}{@code , }{@link Car}{@code >}。
     */
    public void test4(@Validated Map<String, Car> obj) {}

    /**
     * 嵌套校验的测试方法五。
     *
     * @param obj 需要被校验的对象的 {@link Map}{@code <}{@link Car}{@code , }{@link Product}{@code >}。
     */
    public void test5(@Validated Map<Car, Product> obj) {}

    /**
     * 嵌套校验的测试方法六。
     *
     * @param obj 需要被校验的对象的
     * {@link Map}{@code <}{@link Car}{@code , }{@link Map}{@code <}{@link Car}{@code , }{@link Product}{@code >>}。
     */
    public void test6(@Validated Map<Car, Map<Car, Product>> obj) {}

    /**
     * 嵌套校验的测试方法七。
     *
     * @param obj 需要被校验的对象的 {@link List}{@code <}{@link Map}{@code <}{@link Car}{@code , }{@link Product}{@code >>}。
     */
    public void test7(@Validated List<Map<Car, Product>> obj) {}

    /**
     * 嵌套校验的测试方法八。
     *
     * @param obj 需要被校验的对象的 {@link Map}{@code <}{@link Car}{@code , }{@link List}{@code <}{@link Product}{@code >>}。
     */
    public void test8(@Validated Map<Car, List<Product>> obj) {}

    /**
     * 嵌套校验的测试方法九。
     *
     * @param obj 需要被校验的对象的 {@link List}{@code <}{@link Company}{@code >}。
     */
    public void test9(@Validated List<Company> obj) {}
}
