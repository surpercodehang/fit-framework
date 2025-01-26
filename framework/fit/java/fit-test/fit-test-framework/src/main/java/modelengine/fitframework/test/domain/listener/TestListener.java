/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.listener;

import modelengine.fitframework.test.domain.TestContext;
import modelengine.fitframework.test.domain.resolver.TestContextConfiguration;

import java.util.Optional;

/**
 * 测试框架的监听类，用于监听单测类整个生命周期。
 *
 * @author 邬涨财
 * @author 易文渊
 * @since 2023-01-17
 */
public interface TestListener {
    /**
     * 预处理器，在 runtime 运行前，获取监听器配置。
     *
     * @param clazz 表示测试类型的 {@link Class}。
     * @return 表示监听器的 {@link Optional}{@code <}{@link TestContextConfiguration}{@code >}。
     */
    default Optional<TestContextConfiguration> config(Class<?> clazz) {
        return Optional.empty();
    }

    /**
     * 预处理器，在执行当前测试类所有的测试用例前调用。
     *
     * @param context 表示测试上下文类的 {@link TestContext}。
     */
    default void beforeTestClass(TestContext context) {}

    /**
     * 对测试类实例进行再处理，如注入依赖项。该方法会在测试类实例化后立即调用。
     *
     * @param context 表示测试上下文类的 {@link TestContext}。
     */
    default void prepareTestInstance(TestContext context) {}

    /**
     * 在执行底层测试框架的生命周期回调方法（beforeEach方法）之前，进行预处理。
     *
     * @param context 表示测试上下文类的 {@link TestContext}。
     */
    default void beforeTestMethod(TestContext context) {}

    /**
     * 在执行测试方法之前，进行预处理。该方法在生命周期回调方法（beforeEach方法）之后。
     *
     * @param context 表示测试上下文类的 {@link TestContext}。
     */
    default void beforeTestExecution(TestContext context) {}

    /**
     * 在执行测试方法之后，进行预处理。该方法在生命周期回调方法（afterEach方法）之前。
     *
     * @param context 表示测试上下文类的 {@link TestContext}。
     */
    default void afterTestExecution(TestContext context) {}

    /**
     * 在执行底层测试框架的生命周期回调方法（afterEach方法）之后，进行预处理。
     *
     * @param context 表示测试上下文类的 {@link TestContext}。
     */
    default void afterTestMethod(TestContext context) {}

    /**
     * 在执行当前测试类所有的测试用例后调用。
     *
     * @param context 表示测试上下文类的 {@link TestContext}。
     */
    default void afterTestClass(TestContext context) {}
}
