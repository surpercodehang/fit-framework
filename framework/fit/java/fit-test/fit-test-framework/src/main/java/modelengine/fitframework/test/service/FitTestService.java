/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.service;

import java.lang.reflect.Method;

/**
 * 测试框架的管理接口。
 *
 * @author 邬涨财
 * @author 易文渊
 * @since 2023-02-07
 */
public interface FitTestService {
    /**
     * 预处理器，在执行当前测试类所有的测试用例前调用。
     */
    void beforeAll();

    /**
     * 在执行底层测试框架的生命周期回调方法（beforeEach方法）之前，进行预处理。
     */
    void beforeEach();

    /**
     * 在执行底层测试框架的生命周期回调方法（afterEach方法）之后，进行预处理。
     */
    void afterEach();

    /**
     * 设置当前阶段测试信息。
     *
     * @param clazz 表示测试类的 {@link Class}。
     * @param testInstance 表示测试类实例的 {@link Object}。
     * @param method 表示测试方法的 {@link Method}。
     */
    void setTestInfo(Class<?> clazz, Object testInstance, Method method);

    /**
     * 对测试类实例进行再处理。
     */
    void prepareTestInstance();

    /**
     * 测试实例执行结束后的处理。
     */
    void afterAll();

    /**
     * 根据测试类类型创建测试框架的管理类。
     *
     * @param clazz 表示测试类类型的 {@link Class}{@code <?>}。
     * @return 表示创建的测试框架的管理类的 {@link FitTestService}。
     */
    static FitTestService create(Class<?> clazz) {
        return new DefaultFitTestService(clazz);
    }
}