/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.adapter.north.junit5;

import modelengine.fitframework.test.service.FitTestService;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Method;

/**
 * Junit5 的自定义扩展类。
 *
 * @author 邬涨财
 * @author 易文渊
 * @since 2023-02-01
 */
public class FitExtension implements BeforeAllCallback, TestInstancePostProcessor, AfterAllCallback, BeforeEachCallback,
        AfterEachCallback {
    private FitTestService service;

    @Override
    public void beforeAll(ExtensionContext context) {
        this.service = FitTestService.create(context.getRequiredTestClass());
        this.setInfo(context);
        this.service.beforeAll();
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        this.setInfo(context, testInstance);
        this.service.prepareTestInstance();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        this.setInfo(context);
        this.service.afterAll();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        this.setInfo(context);
        this.service.beforeEach();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        this.setInfo(context);
        this.service.afterEach();
    }

    private void setInfo(ExtensionContext context) {
        this.setInfo(context, null);
    }

    private void setInfo(ExtensionContext context, Object testInstance) {
        Class<?> testClass = context.getRequiredTestClass();
        Object realInstance = testInstance == null ? context.getTestInstance().orElse(null) : testInstance;
        Method testMethod = context.getTestMethod().orElse(null);
        this.service.setTestInfo(testClass, realInstance, testMethod);
    }
}
