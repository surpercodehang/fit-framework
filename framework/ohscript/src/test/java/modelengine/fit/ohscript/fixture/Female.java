/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.fixture;

import java.util.List;

/**
 * Female
 * 测试数据类
 *
 * @since 1.0
 */
public class Female implements Human {
    private Integer age = 200;

    private String name = "Elsa";

    private Human friend = null;

    @Override
    public Integer getAge() {
        return this.age;
    }

    @Override
    public String getName(String firstName) {
        return name + " " + firstName;
    }

    @Override
    public List<Human> makeFriends(List<Human> friends) {
        return null;
    }

    @Override
    public Human getFriend() {
        return this.friend;
    }
}
