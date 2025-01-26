/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.ConfigDecryptor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

/**
 * 为 {@link PrefixedConfig} 提供单元测试。
 *
 * @author 季聿阶
 * @since 2023-10-10
 */
@DisplayName("测试 PrefixedConfig")
public class PrefixedConfigTest {
    private Config decorated;
    private ConfigDecryptor decryptor;
    private PrefixedConfig prefixedConfig;

    @BeforeEach
    void setup() {
        this.decorated = new MockConfig();
        this.decryptor = mock(ConfigDecryptor.class);
        when(this.decryptor.decrypt(anyString(), anyString())).thenAnswer(invocation -> {
            String originalValue = invocation.getArgument(1);
            return Optional.of(originalValue + "Decrypted");
        });
        this.prefixedConfig = new PrefixedConfig("test", this.decorated, "prefix");
    }

    @AfterEach
    void teardown() {
        this.decorated = null;
        this.decryptor = null;
        this.prefixedConfig = null;
    }

    @Test
    @DisplayName("前缀配置中的所有键，都是装饰配置中去掉前缀的键")
    void shouldReturnRemovedPrefixedKeys() {
        Set<String> actual = this.prefixedConfig.keys();
        assertThat(actual).hasSize(2).contains("k1", "k2");
    }

    @Test
    @DisplayName("前缀配置中标准化键的值，是带上前缀后键的值")
    void shouldReturnCanonicalKeyValueWithPrefix() {
        Object actual = this.prefixedConfig.getWithCanonicalKey("k1");
        assertThat(actual).isEqualTo("v1");
    }

    @Test
    @DisplayName("前缀配置中的值解密之后，是带上前缀键后的值解密后的结果")
    void shouldReturnDecryptedValue() {
        this.prefixedConfig.decrypt(this.decryptor);
        Object actual = this.prefixedConfig.getWithCanonicalKey("k1");
        assertThat(actual).isEqualTo("v1Decrypted");
    }
}
