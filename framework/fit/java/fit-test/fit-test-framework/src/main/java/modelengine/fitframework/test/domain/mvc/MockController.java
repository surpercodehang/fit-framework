/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.mvc;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fitframework.annotation.Component;

/**
 * 为测试提供模拟的控制器，用于检测服务是否启动成功。
 *
 * @author 季聿阶
 * @since 2024-09-23
 */
@Component(name = "$FIT$TestFramework$MockController")
public class MockController {
    /** 表示测试的路径。 */
    public static final String PATH = "/fit/test/framework/mock";

    /** 表示测试的正确返回值。 */
    public static final String OK = "OK";

    @GetMapping(path = PATH)
    public String test() {
        return OK;
    }
}
