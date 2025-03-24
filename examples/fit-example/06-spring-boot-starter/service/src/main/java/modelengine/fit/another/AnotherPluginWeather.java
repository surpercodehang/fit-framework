/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.another;

import modelengine.fitframework.annotation.Genericable;

/**
 * 表示 FIT 插件需要实现的接口服务。
 *
 * @author 杭潇
 * @since 2025-02-27
 */
public interface AnotherPluginWeather {
    /**
     * 获取天气信息。
     *
     * @param city 表示城市的 {@link String}。
     * @return 表示天气信息的 {@link String}。
     */
    @Genericable(id = "TodayWeather")
    String get(String city);
}
