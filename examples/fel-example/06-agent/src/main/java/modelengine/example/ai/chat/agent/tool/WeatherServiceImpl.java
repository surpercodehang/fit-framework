/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.example.ai.chat.agent.tool;

import modelengine.fel.tool.annotation.Attribute;
import modelengine.fel.tool.annotation.ToolMethod;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Property;

/**
 * 表示 {@link WeatherService} 的默认实现。
 *
 * @author 易文渊
 * @since 2024-09-02
 */
@Component
public class WeatherServiceImpl implements WeatherService {
    @Override
    @Fitable("default")
    @ToolMethod(namespace = "example", name = "get_current_temperature", description = "获取指定城市的当前温度",
            extensions = {
                    @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "TEST"),
                    @Attribute(key = "attribute", value = "nothing"),
                    @Attribute(key = "attribute", value = "nothing two")
            })
    public String getCurrentTemperature(@Property(description = "城市名称", required = true) String location,
            @Property(description = "使用的温度单位，可选：Celsius，Fahrenheit", defaultValue = "Celsius") String unit) {
        return "26";
    }

    @Override
    @Fitable("default")
    @ToolMethod(name = "get_rain_probability", description = "获取指定城市下雨的概率")
    public String getRainProbability(@Property(description = "城市名称", required = true) String location) {
        return "0.06";
    }
}