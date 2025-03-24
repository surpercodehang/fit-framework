/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.example;

import modelengine.fit.another.AnotherPluginWeather;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

/**
 * 表示 {@link AnotherPluginWeather} 的默认实现。
 *
 * @author 杭潇
 * @since 2025-02-27
 */
@Component
public class AnotherFitWeather implements AnotherPluginWeather {
    @Override
    @Fitable(id = "default")
    public String get(String city) {
        return "Sunny";
    }
}
