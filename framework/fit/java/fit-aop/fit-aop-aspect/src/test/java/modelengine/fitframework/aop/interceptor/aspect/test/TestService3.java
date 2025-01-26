/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.test;

/**
 * 测试服务类。
 *
 * @author 季聿阶
 * @since 2022-05-14
 */
public class TestService3 extends TestService1 {
    @Override
    public String m1() {
        return "m1";
    }

    public String selfMethod() {
        return "selfMethod";
    }
}
