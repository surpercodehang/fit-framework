/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 为 {@link HttpClassicClientFactory} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-02-16
 */
@DisplayName("测试 HttpClassicClientFactory 接口")
public class HttpClassicClientFactoryTest {
    @Test
    @DisplayName("给定空参，构建对象为默认对象")
    void givenEmptyParameterThenReturnDefaultObject() {
        HttpClassicClientFactory.Config build = HttpClassicClientFactory.Config.builder().build();
        assertThat(build.connectionRequestTimeout()).isEqualTo(-1);
        assertThat(build.connectTimeout()).isEqualTo(-1);
        assertThat(build.custom()).isEmpty();
        assertThat(build.socketTimeout()).isEqualTo(-1);
    }

    @Test
    @DisplayName("给定一个有效参数，根据传参构建对象成功")
    void givenValidParameterThenBuildObjectSuccessfully() {
        HttpClassicClientFactory.Config config = HttpClassicClientFactory.Config.builder(null)
                .socketTimeout(1)
                .connectTimeout(2)
                .connectionRequestTimeout(3)
                .build();
        HttpClassicClientFactory.Config build = HttpClassicClientFactory.Config.builder(config).build();
        assertThat(build.connectionRequestTimeout()).isEqualTo(3);
        assertThat(build.connectTimeout()).isEqualTo(2);
        assertThat(build.socketTimeout()).isEqualTo(1);
    }
}
