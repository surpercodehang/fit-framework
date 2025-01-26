/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.plugin.RootPlugin;
import modelengine.fitframework.test.domain.listener.TestListener;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 测试上下文类。
 *
 * @author 邬涨财
 * @since 2023-01-17
 */
public class TestContext {
    private final Class<?> testClass;
    private final RootPlugin plugin;
    private Object testInstance;
    private Method testMethod;
    private final List<TestListener> listeners;

    /**
     * 通过目标测试类和插件来初始化 {@link TestContext}。
     *
     * @param testClass 表示待测试的目标测试类 {@link Class}{@code <?>}。
     * @param plugin 表示待测试的自定义的测试插件实例 {@link RootPlugin}。
     * @param listeners 表示监听器集合的 {@link List}{@code <}{@link TestListener}{@code >}。
     */
    public TestContext(Class<?> testClass, RootPlugin plugin, List<TestListener> listeners) {
        this.testClass = Validation.notNull(testClass, "The test class to create test context cannot be null.");
        this.plugin = Validation.notNull(plugin, "The root plugin to create test context cannot be null.");
        this.listeners = listeners;
    }

    /**
     * 获取观察者列表。
     *
     * @return 表示观察者列表的 {@link List}{@code <}{@link TestListener}{@code >}。
     */
    public List<TestListener> listeners() {
        return this.listeners;
    }

    /**
     * 获取单测类的类对象。
     *
     * @return 表示单测类的类对象的 {@link Class}{@code <?>}。
     */
    public Class<?> testClass() {
        return this.testClass;
    }

    /**
     * 获取插件对象。
     *
     * @return 表示插件对象的 {@link TestPlugin}。
     */
    public RootPlugin plugin() {
        return this.plugin;
    }

    /**
     * 获取单测类的实例对象。
     *
     * @return 表示单测类实例对象的 {@link Object}。
     */
    public Object testInstance() {
        return this.testInstance;
    }

    /**
     * 设置单测类的实例对象。
     *
     * @param testInstance 表示需要设置单测类的实例对象的 {@link Object}。
     */
    public void testInstance(Object testInstance) {
        this.testInstance = testInstance;
    }

    /**
     * 获取单测类的测试方法。
     *
     * @return 表示单测类测试方法的 {@link Method}。
     */
    public Method testMethod() {
        return this.testMethod;
    }

    /**
     * 获取单测类的测试方法。
     *
     * @param testMethod 表示单测类测试方法的 {@link Method}。
     */
    public void testMethod(Method testMethod) {
        this.testMethod = testMethod;
    }
}