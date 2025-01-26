/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.test;

import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.aop.annotation.Before;
import modelengine.fitframework.aop.annotation.Pointcut;

/**
 * 用于测试 execution 表达式的测试切面定义。
 *
 * @author 季聿阶
 * @since 2022-05-15
 */
@Aspect
public class TestExecutionAspect {
    @Before("execution(String modelengine.fitframework.aop.interceptor.aspect.test.TestService1.m1())")
    private void before1() {}

    @Pointcut(pointcut = "execution(String modelengine.fitframework.aop.interceptor.aspect.test.TestService1.*(..))")
    private void pointcut1() {}

    @Pointcut(pointcut = "this(service1) && args(name)", argNames = "service1,name")
    private void pointcut2(TestService1 service1, String name) {}
}
