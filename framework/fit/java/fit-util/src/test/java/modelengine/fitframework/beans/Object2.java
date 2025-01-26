/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans;

/**
 * 表示测试对象 2。
 *
 * @author 季聿阶
 * @since 2023-02-07
 */
public class Object2 {
    private String f1;
    private int f2;

    /**
     * 获取第一个属性。
     *
     * @return 表示第一个属性的 {@link String}。
     */
    public String getF1() {
        return this.f1;
    }

    /**
     * 设置第一个属性。
     *
     * @param f1 表示待设置的第一个属性的 {@link String}。
     */
    public void setF1(String f1) {
        this.f1 = f1;
    }

    /**
     * 获取第二个属性。
     *
     * @return 表示第二个属性的 {@link String}。
     */
    public int getF2() {
        return this.f2;
    }

    /**
     * 设置第二个属性。
     *
     * @param f2 表示待设置的第二个属性的 {@link String}。
     */
    public void setF2(int f2) {
        this.f2 = f2;
    }
}
