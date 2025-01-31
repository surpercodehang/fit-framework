/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.example.controller;

import modelengine.fit.example.domain.User;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fitframework.annotation.Component;

/**
 * 表示用户资源的控制器。
 *
 * @author 季聿阶
 * @since 2025-01-31
 */
@Component
public class UserController {
    private static int counter = 0;

    @GetMapping(path = "/user")
    public User getUser(@RequestParam("name") String name, @RequestParam("age") String age) {
        return new User(name, age, ++counter);
    }
}
