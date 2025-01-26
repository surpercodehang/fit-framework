/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.example.ai.chat.template;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.runtime.FitStarter;

/**
 * 启动程序。
 *
 * @author 易文渊
 * @since 2024-08-29
 */
@Component
public class DemoApplication {
    public static void main(String[] args) {
        FitStarter.start(DemoApplication.class, args);
    }
}