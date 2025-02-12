/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package com.demo.spring.controller;

import modelengine.fit.example.PluginWeather;
import modelengine.fitframework.broker.client.BrokerClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器。
 *
 * @author 季聿阶
 * @since 2025-02-13
 */
@RestController
public class DemoController {
    private final BrokerClient client;

    public DemoController(BrokerClient client) {
        this.client = client;
    }

    /**
     * 用于测试从 Spring 调用 FIT 插件的链路。
     *
     * @return 表示从 FIT 插件中获取的天气结果的 {@link String}。
     */
    @GetMapping(path = "/weather/plugin")
    public String getPluginWeather() {
        return this.client.getRouter(PluginWeather.class, "PluginWeather").route().invoke("fit");
    }

    /**
     * 用于测试从 FIT 插件调用 Spring 应用的链路。
     *
     * @return 表示从 Spring 应用中获取的天气结果的 {@link String}。
     */
    @GetMapping(path = "/weather/spring")
    public String getSpringWeather() {
        return this.client.getRouter(PluginWeather.class, "PluginWeather").route().invoke("spring");
    }
}
