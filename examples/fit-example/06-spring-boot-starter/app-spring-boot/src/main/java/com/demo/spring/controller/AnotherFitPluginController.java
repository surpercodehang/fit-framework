/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package com.demo.spring.controller;

import modelengine.fit.another.AnotherPluginWeather;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 插件测试控制器。
 *
 * @author 杭潇
 * @since 2025-02-27
 */
@RestController
public class AnotherFitPluginController {
    private final AnotherPluginWeather anotherPluginWeather;

    public AnotherFitPluginController(AnotherPluginWeather anotherPluginWeather) {
        this.anotherPluginWeather = anotherPluginWeather;
    }

    /**
     * 用于辅助验证多个包路径下的 bean 注册到 Spring。
     *
     * @return 表示从 FIT 插件中获取的城市天气结果的 {@link String}。
     */
    @GetMapping(path = "/weather/city")
    public String getHongKongWeather() {
        return this.anotherPluginWeather.get("HongKong");
    }
}
