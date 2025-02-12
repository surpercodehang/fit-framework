/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.example;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

import java.util.Objects;

/**
 * 表示 {@link PluginWeather} 的默认实现。
 *
 * @author 季聿阶
 * @since 2025-02-13
 */
@Component
public class FitWeather implements PluginWeather {
    private final SpringWeather springWeather;

    public FitWeather(SpringWeather springWeather) {
        this.springWeather = springWeather;
    }

    @Override
    @Fitable(id = "default")
    public String get(String location) {
        if (Objects.equals(location, "fit")) {
            return "FIT weather plugin is working.";
        }
        return this.springWeather.get();
    }
}
