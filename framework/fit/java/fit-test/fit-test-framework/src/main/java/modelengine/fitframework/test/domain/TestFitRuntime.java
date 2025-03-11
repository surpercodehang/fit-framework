/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.test.domain;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.plugin.RootPlugin;
import modelengine.fitframework.runtime.shared.SharedUrlClassLoader;
import modelengine.fitframework.runtime.support.AbstractFitRuntime;
import modelengine.fitframework.test.domain.resolver.TestContextConfiguration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 为测试框架提供运行时环境。
 *
 * @author 邬涨财
 * @author 易文渊
 * @since 2023-01-17
 */
public class TestFitRuntime extends AbstractFitRuntime {
    private static final String USER_DIR_KEY = "user.dir";

    private final TestContextConfiguration configuration;

    /**
     * 使用指定的测试类、测试上下文配置和端口号初始化 {@link TestFitRuntime} 的新实例。
     *
     * @param clazz 表示测试类的 {@link Class}{@code <?>}。
     * @param configuration 表示测试上下文配置的 {@link TestContextConfiguration}。
     * @param port 表示端口号的 {@code int}。
     */
    public TestFitRuntime(Class<?> clazz, TestContextConfiguration configuration, int port) {
        super(clazz, new String[] {"server.http.port=" + port, "server.http.secure.enabled=false"});
        this.configuration = configuration;
    }

    @Override
    protected URL locateRuntime() {
        try {
            String userDir = notBlank(System.getProperty(USER_DIR_KEY), "User dir cannot be blank.");
            return new File(userDir).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Failed to get locate runtime when run test framework.");
        }
    }

    @Override
    protected SharedUrlClassLoader obtainSharedClassLoader() {
        return new SharedUrlClassLoader(new URL[0], TestFitRuntime.class.getClassLoader().getParent());
    }

    @Override
    protected RootPlugin createRootPlugin() {
        return new TestPlugin(this, this.configuration);
    }
}
