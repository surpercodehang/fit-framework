/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.example;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

/**
 * 表示 {@link Weather} 的默认实现。
 *
 * @author 季聿阶
 * @since 2025-01-31
 */
@Component
public class DefaultWeather implements Weather {
    @Override
    @Fitable(id = "default-weather")
    public String get() {
        return "Default weather plugin is working.";
    }
}
