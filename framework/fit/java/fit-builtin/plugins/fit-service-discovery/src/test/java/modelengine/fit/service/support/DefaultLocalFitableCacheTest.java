/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.broker.LocalExecutorFactory;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginMetadata;
import modelengine.fitframework.runtime.FitRuntime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * {@link DefaultLocalFitableCache} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-27
 */
@DisplayName("测试 DefaultLocalFitableCache 类")
class DefaultLocalFitableCacheTest {
    @Test
    @DisplayName("提供 RegistryListener 类 contains 方法时，返回 true")
    void givenDefaultLocalFitableCacheWhenListenerModePushThenNotThrow() {
        LocalExecutorFactory executorFactory = mock(LocalExecutorFactory.class);
        DefaultLocalFitableCache cache = new DefaultLocalFitableCache(executorFactory);
        Plugin plugin = mock(Plugin.class);
        PluginMetadata pluginMetadata = mock(PluginMetadata.class);
        when(plugin.metadata()).thenReturn(pluginMetadata);
        when(pluginMetadata.name()).thenReturn("pluginMetadata");
        LocalExecutor fitable = mock(LocalExecutor.class);
        when(fitable.id()).thenReturn(UniqueFitableId.create("genericableId", "1.0.0", "fitableId", "1.0.0"));
        List<LocalExecutor> fitables = Collections.singletonList(fitable);
        when(executorFactory.get(plugin, false)).thenReturn(fitables);
        cache.onPluginStarted(plugin);
        FitRuntime runtime = mock(FitRuntime.class);
        List<Plugin> plugins = Collections.singletonList(plugin);
        when(runtime.plugins()).thenReturn(plugins);
        cache.onRuntimeStarted(runtime);
        UniqueFitableId uniqueFitableId = UniqueFitableId.create("genericableId", "fitableId");
        boolean isContains = cache.contains(uniqueFitableId);
        assertThat(isContains).isTrue();
    }
}