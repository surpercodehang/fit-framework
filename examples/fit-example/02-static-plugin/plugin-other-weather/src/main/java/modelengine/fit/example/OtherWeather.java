/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.example;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

/**
 * 表示 {@link Weather} 的另一个实现。
 *
 * @author 季聿阶
 * @since 2025-01-31
 */
@Component
public class OtherWeather implements Weather {
    @Override
    @Fitable(id = "other")
    public String get() {
        return "Other weather plugin is working.";
    }
}
