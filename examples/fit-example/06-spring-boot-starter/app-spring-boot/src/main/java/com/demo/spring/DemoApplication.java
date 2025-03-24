/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package com.demo.spring;

import modelengine.fitframework.starter.spring.annotation.EnableFitProxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 表示样例启动程序。
 *
 * @author 季聿阶
 * @since 2025-02-12
 */
@SpringBootApplication
@EnableFitProxy(basePackages = {"modelengine.fit.example", "modelengine.fit.another"})
public class DemoApplication {
    /**
     * 表示启动主函数。
     *
     * @param args 表示启动参数的 {@link String}{@code []}。
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
