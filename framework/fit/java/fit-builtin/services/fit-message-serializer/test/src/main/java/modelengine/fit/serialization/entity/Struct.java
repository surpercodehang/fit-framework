/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.entity;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 测试结构体。
 *
 * @author 季聿阶
 * @since 2022-09-10
 */
public class Struct {
    private String f1;
    private int f2;
    private Boolean f3;
    private List<Long> f4;
    private Map<String, Double> f5;

    public String getF1() {
        return this.f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public int getF2() {
        return this.f2;
    }

    public void setF2(int f2) {
        this.f2 = f2;
    }

    public Boolean getF3() {
        return this.f3;
    }

    public void setF3(Boolean f3) {
        this.f3 = f3;
    }

    public List<Long> getF4() {
        return this.f4;
    }

    public void setF4(List<Long> f4) {
        this.f4 = f4;
    }

    public Map<String, Double> getF5() {
        return this.f5;
    }

    public void setF5(Map<String, Double> f5) {
        this.f5 = f5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Struct struct = (Struct) o;
        return this.f2 == struct.f2 && Objects.equals(this.f1, struct.f1) && Objects.equals(this.f3, struct.f3)
                && Objects.equals(this.f4, struct.f4) && Objects.equals(this.f5, struct.f5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.f1, this.f2, this.f3, this.f4, this.f5);
    }
}
