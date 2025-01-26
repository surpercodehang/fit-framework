/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.globalization;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

/**
 * 表示 {@link ResourceBundleStringResource} 的单元测试。
 *
 * @author 梁济时
 * @since 2022-11-21
 */
@DisplayName("测试 ResourceBundleStringResource")
class ResourceBundleStringResourceTest {
    @Nested
    @DisplayName("测试参数是数组的情况")
    class TestArray {
        @Test
        @DisplayName("使用指定的语言环境中定义的资源格式化")
        void shouldReturnFormattedMessage() {
            ResourceBundleStringResource bundle =
                    new ResourceBundleStringResource(this.getClass().getClassLoader(), "i18n/message", "UTF-8");
            String messageEn = bundle.getMessage(new Locale("en"), "HELLO", "world");
            assertThat(messageEn).isEqualTo("Hello, world!");
            String messageZh = bundle.getMessage(new Locale("zh"), "HELLO", "world");
            assertThat(messageZh).isEqualTo("你好，world！");
        }
    }

    @Nested
    @DisplayName("测试参数是映射的情况")
    class TestMap {
        @Test
        @DisplayName("使用指定的语言环境中定义的资源格式化")
        void shouldReturnFormattedMessage() {
            ResourceBundleStringResource bundle =
                    new ResourceBundleStringResource(this.getClass().getClassLoader(), "i18n/message", "UTF-8");
            Map<String, Object> map = MapBuilder.<String, Object>get().put("0", "world").build();
            String messageEn = bundle.getMessage(new Locale("en"), "HELLO", map);
            assertThat(messageEn).isEqualTo("Hello, world!");
            String messageZh = bundle.getMessage(new Locale("zh"), "HELLO", map);
            assertThat(messageZh).isEqualTo("你好，world！");
        }
    }
}
