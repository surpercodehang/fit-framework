/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.test;

/**
 * 测试服务1的接口。
 *
 * @author 季聿阶
 * @since 2022-05-25
 */
public interface TestService1Interface extends AnotherTestService1Interface {
    /**
     * 测试方法1。
     *
     * @return 表示测试返回值的 {@link String}。
     */
    String m1();
}
