/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link DefaultNettyServerConfig} 的单元测试。
 *
 * @author 季聿阶
 * @since 2023-09-07
 */
@DisplayName("测试 DefaultNettyServerConfig")
public class DefaultNettyServerConfigTest {
    @Test
    @DisplayName("当配置被正确设置时，可以获取正确的配置值")
    void shouldReturnConfig() {
        DefaultNettyServerConfig config = new DefaultNettyServerConfig();
        config.setMaxThreadNum(2);
        config.setDisplayError(true);
        assertThat(config).returns(2, DefaultNettyServerConfig::getMaxThreadNum)
                .returns(true, DefaultNettyServerConfig::isDisplayError);
    }
}
