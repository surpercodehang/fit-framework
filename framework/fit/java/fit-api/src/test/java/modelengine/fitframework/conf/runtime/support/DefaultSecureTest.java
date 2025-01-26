/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.runtime.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * {@link DefaultSecure} 的测试类。
 *
 * @author 李金绪
 * @since 2024-08-22
 */
@DisplayName("测试 DefaultClientSecure")
public class DefaultSecureTest {
    @Test
    @DisplayName("测试正确创建实例")
    void shouldCreateInstance() {
        DefaultSecure secure = new DefaultSecure();
        secure.setSecureRandomEnabled(true);
        secure.setSecureProtocol("TLSv1.2");
        secure.setIsSslEnabled(true);
        secure.setSslCiphers(Arrays.asList("test"));
        assertThat(secure.secureRandomEnabled()).isTrue();
        assertThat(secure.secureProtocol().get()).isEqualTo("TLSv1.2");
        assertThat(secure.isSslEnabled()).isTrue();
        assertThat(secure.sslCiphers()).isEqualTo(Arrays.asList("test"));
    }
}