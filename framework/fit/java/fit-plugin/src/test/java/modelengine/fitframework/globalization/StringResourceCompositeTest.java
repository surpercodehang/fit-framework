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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 为 {@link StringResourceComposite} 提供单元测试。
 *
 * @author 季聿阶
 * @since 2024-12-06
 */
@DisplayName("测试 StringResourceComposite")
public class StringResourceCompositeTest {
    @Nested
    @DisplayName("测试参数是数组的情况")
    class TestArray {
        @Test
        @DisplayName("使用指定的语言环境中定义的资源格式化")
        void shouldReturnFormattedMessage() {
            ResourceBundleStringResource bundle =
                    new ResourceBundleStringResource(this.getClass().getClassLoader(), "i18n/message", "UTF-8");
            StringResource sr = StringResourceComposite.combine(Arrays.asList(StringResources.empty(), bundle));
            String messageEn = sr.getMessage(new Locale("en"), "HELLO", "world");
            assertThat(messageEn).isEqualTo("Hello, world!");
            String messageZh = sr.getMessage(new Locale("zh"), "HELLO", "world");
            assertThat(messageZh).isEqualTo("你好，world！");
        }

        @Test
        @DisplayName("当没有参数且没有合适的消息时，使用默认的消息")
        void shouldReturnDefaultMessage() {
            StringResource sr = StringResourceComposite.combine(Collections.singletonList(StringResources.empty()));
            String messageEn = sr.getMessageWithDefault(new Locale("en"), "noKey", "world");
            assertThat(messageEn).isEqualTo("world");
            sr = StringResourceComposite.combine(Arrays.asList(StringResources.empty(), StringResources.empty()));
            messageEn = sr.getMessageWithDefault(new Locale("en"), "noKey", "world");
            assertThat(messageEn).isEqualTo("world");
        }

        @Test
        @DisplayName("当没有合适的消息时，使用默认的消息带上参数")
        void shouldReturnDefaultMessageWithArgs() {
            StringResource sr = StringResourceComposite.combine(Collections.singletonList(StringResources.empty()));
            String messageEn = sr.getMessageWithDefault(new Locale("en"), "noKey", "hello, {0}", "world");
            assertThat(messageEn).isEqualTo("hello, world");
            sr = StringResourceComposite.combine(Arrays.asList(StringResources.empty(), StringResources.empty()));
            messageEn = sr.getMessageWithDefault(new Locale("en"), "noKey", "hello, {0}", "world");
            assertThat(messageEn).isEqualTo("hello, world");
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
            StringResource sr = StringResourceComposite.combine(Arrays.asList(StringResources.empty(), bundle));
            Map<String, Object> map = MapBuilder.<String, Object>get().put("0", "world").build();
            String messageEn = sr.getMessage(new Locale("en"), "HELLO", map);
            assertThat(messageEn).isEqualTo("Hello, world!");
            String messageZh = sr.getMessage(new Locale("zh"), "HELLO", map);
            assertThat(messageZh).isEqualTo("你好，world！");
        }

        @Test
        @DisplayName("当没有参数且没有合适的消息时，使用默认的消息")
        void shouldReturnDefaultMessage() {
            StringResource sr = StringResourceComposite.combine(Collections.singletonList(StringResources.empty()));
            String messageEn = sr.getMessageWithDefault(new Locale("en"), "noKey", "world", new HashMap<>());
            assertThat(messageEn).isEqualTo("world");
            sr = StringResourceComposite.combine(Arrays.asList(StringResources.empty(), StringResources.empty()));
            messageEn = sr.getMessageWithDefault(new Locale("en"), "noKey", "world", new HashMap<>());
            assertThat(messageEn).isEqualTo("world");
        }

        @Test
        @DisplayName("当没有合适的消息时，使用默认的消息带上参数")
        void shouldReturnDefaultMessageWithArgs() {
            StringResource sr = StringResourceComposite.combine(Collections.singletonList(StringResources.empty()));
            Map<String, Object> map = MapBuilder.<String, Object>get().put("0", "world").build();
            String messageEn = sr.getMessageWithDefault(new Locale("en"), "noKey", "hello, {0}", map);
            assertThat(messageEn).isEqualTo("hello, world");
            sr = StringResourceComposite.combine(Arrays.asList(StringResources.empty(), StringResources.empty()));
            messageEn = sr.getMessageWithDefault(new Locale("en"), "noKey", "hello, {0}", map);
            assertThat(messageEn).isEqualTo("hello, world");
        }
    }
}
