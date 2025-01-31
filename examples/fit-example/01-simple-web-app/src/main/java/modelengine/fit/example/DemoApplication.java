/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.example;

import modelengine.fitframework.runtime.FitStarter;

/**
 * 启动类。
 *
 * @author 季聿阶
 * @since 2025-01-31
 */
public class DemoApplication {
    public static void main(String[] args) {
        FitStarter.start(DemoApplication.class, args);
    }
}