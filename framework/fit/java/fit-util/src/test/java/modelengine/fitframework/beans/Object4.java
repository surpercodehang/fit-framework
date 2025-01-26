/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans;

/**
 * 表示测试对象 4。
 *
 * @author 季聿阶
 * @since 2024-02-25
 */
public class Object4<T, U> {
    private T d1;
    private U d2;
    private String d3;

    /**
     * 获取第一个泛型属性。
     *
     * @return 表示第一个泛型属性的 {@link T}。
     */
    public T getD1() {
        return this.d1;
    }

    /**
     * 设置第一个泛型属性。
     *
     * @param d1 表示待设置的第一个泛型属性的 {@link T}。
     */
    public void setD1(T d1) {
        this.d1 = d1;
    }

    /**
     * 获取第二个泛型属性。
     *
     * @return 表示第二个泛型属性的 {@link U}。
     */
    public U getD2() {
        return this.d2;
    }

    /**
     * 设置第二个泛型属性。
     *
     * @param d2 表示待设置的第二个泛型属性的 {@link U}。
     */
    public void setD2(U d2) {
        this.d2 = d2;
    }

    /**
     * 获取第三个属性。
     *
     * @return 表示第三个属性的 {@link String}。
     */
    public String getD3() {
        return this.d3;
    }

    /**
     * 设置第三个属性。
     *
     * @param d3 表示待设置的第三个属性的 {@link String}。
     */
    public void setD3(String d3) {
        this.d3 = d3;
    }
}
