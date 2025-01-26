/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

/**
 * {@link PluginKey} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-23
 */
@DisplayName("测试 PluginKey 类以及相关类")
class PluginKeyTest {
    @Test
    @DisplayName("提供 PluginKey 类时，返回正常信息")
    void givenUniqueFitableIdWhenCreateThenReturnFitableInfo() {
        String group = "1";
        String name = "2";
        String version = "3";
        PluginKey pluginKey = mock(PluginKey.class);
        when(pluginKey.group()).thenReturn(group);
        when(pluginKey.name()).thenReturn(name);
        when(pluginKey.version()).thenReturn(version);
        String actual = PluginKey.identify(pluginKey);
        String expected = "1:2:3";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("提供 DuplicatePluginException 类时，返回正常信息")
    void givenDuplicatePluginExceptionShouldReturnMessage() {
        String message = "123";
        DuplicatePluginException exception = new DuplicatePluginException(message);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("提供 PluginCategory 类时，返回正常信息")
    void givenPluginCategoryShouldReturnMessage() {
        PluginCategory system = PluginCategory.SYSTEM;
        assertThat(system.getId()).isEqualTo(1);
        assertThat(system.getCode()).isEqualTo("system");
        PluginCategory user = PluginCategory.USER;
        assertThat(user.getId()).isEqualTo(2);
        assertThat(user.getCode()).isEqualTo(user.toString());
    }

    @Test
    @DisplayName("提供 PluginCollection 类 empty 时，返回 false")
    void givenPluginCollectionShouldReturnFalse() {
        PluginCollection collection = mock(PluginCollection.class);
        when(collection.size()).thenReturn(1);
        assertThat(collection.empty()).isFalse();
    }

    @Test
    @DisplayName("提供 PluginMetadata 类时，返回正常信息")
    void givenPluginMetadataShouldReturnPluginMetadata() {
        Comparator<PluginMetadata> comparator = PluginMetadata.startupComparator();
        PluginMetadata o1 = mock(PluginMetadata.class);
        when(o1.category()).thenReturn(PluginCategory.SYSTEM);
        when(o1.level()).thenReturn(1);
        when(o1.name()).thenReturn("1");
        PluginMetadata o2 = mock(PluginMetadata.class);
        when(o2.category()).thenReturn(PluginCategory.SYSTEM);
        when(o2.level()).thenReturn(2);
        when(o2.name()).thenReturn("2");
        assertThat(comparator.compare(o1, o2)).isLessThan(0);
    }
}