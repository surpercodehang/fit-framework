/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.example.service.impl;

import modelengine.fit.example.service.MyService;
import modelengine.fitframework.annotation.Component;

/**
 * 服务接口的实现。
 */
@Component
public class MyServiceImpl implements MyService {
    @Override
    public void doSomething() {
        System.out.println("do something");
    }
}
