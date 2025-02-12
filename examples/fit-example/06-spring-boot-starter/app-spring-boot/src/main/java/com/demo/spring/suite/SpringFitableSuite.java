/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package com.demo.spring.suite;

import modelengine.fit.example.SpringWeather;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.FitableSuite;

import org.springframework.stereotype.Component;

/**
 * 表示 Spring Boot 底座上的 Fitables 实现。
 *
 * @author 季聿阶
 * @since 2025-02-13
 */
@Component
@FitableSuite
public class SpringFitableSuite implements SpringWeather {
    @Override
    @Fitable(id = "default")
    public String get() {
        return "Spring weather service is working.";
    }
}
