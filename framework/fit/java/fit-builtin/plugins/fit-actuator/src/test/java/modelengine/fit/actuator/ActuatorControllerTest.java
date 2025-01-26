/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.actuator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.actuator.entity.FitableVo;
import modelengine.fit.actuator.entity.PluginVo;
import modelengine.fitframework.broker.Aliases;
import modelengine.fitframework.broker.Fitable;
import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.Tags;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginCategory;
import modelengine.fitframework.plugin.PluginMetadata;
import modelengine.fitframework.runtime.FitRuntime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link ActuatorController} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-07-05
 */
@DisplayName("测试 ActuatorController")
public class ActuatorControllerTest {
    private ActuatorController actuatorController;
    private FitRuntime fitRuntime;
    private BrokerClient brokerClient;

    @BeforeEach
    void setup() {
        this.fitRuntime = mock(FitRuntime.class);
        this.brokerClient = mock(BrokerClient.class);
        this.actuatorController = new ActuatorController(this.fitRuntime, this.brokerClient);
    }

    @AfterEach
    void teardown() {
        this.actuatorController = null;
        this.fitRuntime = null;
        this.brokerClient = null;
    }

    @Test
    @DisplayName("返回正确的插件列表")
    void shouldReturnPlugins() {
        Plugin p1 = mock(Plugin.class);
        PluginMetadata m1 = mock(PluginMetadata.class);
        when(p1.metadata()).thenReturn(m1);
        when(m1.group()).thenReturn("g1");
        when(m1.name()).thenReturn("n1");
        when(m1.version()).thenReturn("1.0.1");
        when(m1.category()).thenReturn(PluginCategory.USER);
        when(m1.level()).thenReturn(1);
        Plugin p2 = mock(Plugin.class);
        PluginMetadata m2 = mock(PluginMetadata.class);
        when(p2.metadata()).thenReturn(m2);
        when(m2.group()).thenReturn("g2");
        when(m2.name()).thenReturn("n2");
        when(m2.version()).thenReturn("1.0.2");
        when(m2.category()).thenReturn(PluginCategory.SYSTEM);
        when(m2.level()).thenReturn(2);
        Mockito.when(this.fitRuntime.plugins()).thenReturn(Arrays.asList(p1, p2));

        List<PluginVo> plugins = this.actuatorController.getPlugins();
        assertThat(plugins).isNotEmpty().hasSize(2);
        assertThat(plugins.get(1)).returns("g1", PluginVo::getGroup)
                .returns("n1", PluginVo::getName)
                .returns("1.0.1", PluginVo::getVersion)
                .returns("user", PluginVo::getCategory)
                .returns(1, PluginVo::getLevel);
        assertThat(plugins.get(0)).returns("g2", PluginVo::getGroup)
                .returns("n2", PluginVo::getName)
                .returns("1.0.2", PluginVo::getVersion)
                .returns("system", PluginVo::getCategory)
                .returns(2, PluginVo::getLevel);
    }

    @Test
    @DisplayName("返回正确的服务实现列表")
    void shouldReturnFitables() {
        Genericable genericable = mock(Genericable.class);
        when(this.brokerClient.getGenericable("g1")).thenReturn(genericable);

        Fitable f1 = mock(Fitable.class);
        when(genericable.fitables()).thenReturn(Collections.singletonList(f1));
        when(f1.id()).thenReturn("f1");
        when(f1.version()).thenReturn("1.0.0");
        Aliases aliases = mock(Aliases.class);
        when(aliases.all()).thenReturn(Collections.singleton("a1"));
        when(f1.aliases()).thenReturn(aliases);
        Tags tags = mock(Tags.class);
        when(tags.all()).thenReturn(Collections.singleton("t1"));
        when(f1.tags()).thenReturn(tags);
        when(f1.degradationFitableId()).thenReturn("d1");

        List<FitableVo> fitables = this.actuatorController.getFitables("g1");
        assertThat(fitables).hasSize(1)
                .element(0)
                .returns("f1", FitableVo::getId)
                .returns("1.0.0", FitableVo::getVersion)
                .returns(Collections.singleton("a1"), FitableVo::getAliases)
                .returns(Collections.singleton("t1"), FitableVo::getTags)
                .returns("d1", FitableVo::getDegradation);
    }
}
