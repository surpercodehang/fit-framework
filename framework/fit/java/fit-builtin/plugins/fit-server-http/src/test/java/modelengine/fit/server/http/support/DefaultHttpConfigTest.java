/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.server.http.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.conf.runtime.ServerConfig;
import modelengine.fitframework.conf.runtime.support.DefaultSecure;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * 表示 {@link DefaultHttpConfig} 的单元测试。
 *
 * @author 季聿阶
 * @since 2023-09-10
 */
@DisplayName("测试 DefaultHttpConfig")
public class DefaultHttpConfigTest {
    @Test
    @DisplayName("当配置被正确设置时，可以获取正确的配置值")
    void shouldReturnConfig() {
        DefaultHttpConfig config = new DefaultHttpConfig();
        config.setEnabled(true);
        config.setPort(8080);
        config.setToRegisterPort(8081);
        DefaultSecure secure = new DefaultSecure();
        config.setSecure(secure);
        config.setLargeBodySize(4096);
        secure.setEnabled(true);
        secure.setPort(8443);
        secure.setToRegisterPort(8444);
        secure.setKeyStoreFile("/test");
        secure.setTrustStorePassword("123456");
        secure.setKeyStorePassword("123456");
        assertThat(config).returns(true, DefaultHttpConfig::isProtocolEnabled)
                .returns(Optional.of(8080), DefaultHttpConfig::port)
                .returns(Optional.of(8081), DefaultHttpConfig::toRegisterPort)
                .returns(4096L, DefaultHttpConfig::largeBodySize);
        Assertions.assertThat(config.secure()).isNotEmpty()
                .get()
                .returns(true, ServerConfig.Secure::isProtocolEnabled)
                .returns(Optional.of(8443), ServerConfig.Secure::port)
                .returns(Optional.of(8444), ServerConfig.Secure::toRegisterPort)
                .returns(Optional.of("/test"), ServerConfig.Secure::keyStoreFile)
                .returns(Optional.of("123456"), ServerConfig.Secure::trustStorePassword)
                .returns(Optional.of("123456"), ServerConfig.Secure::keyStorePassword);
    }
}
