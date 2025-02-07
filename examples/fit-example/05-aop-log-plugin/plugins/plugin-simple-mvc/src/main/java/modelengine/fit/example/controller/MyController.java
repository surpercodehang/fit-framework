/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.example.controller;

import modelengine.fit.example.service.MyService;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fitframework.annotation.Component;

/**
 * HTTP 控制器。
 */
@Component
public class MyController {
    private final MyService myService;

    public MyController(MyService myService) {
        this.myService = myService;
    }

    @GetMapping(path = "/hello")
    public void hello() {
        this.myService.doSomething();
    }
}
