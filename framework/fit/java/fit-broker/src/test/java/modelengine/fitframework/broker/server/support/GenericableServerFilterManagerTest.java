/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.server.support;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.broker.server.GenericableServerFilter;
import modelengine.fitframework.broker.server.GenericableServerFilterManager;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.plugin.Plugin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 表示 {@link GenericableServerFilterManager} 的测试。
 *
 * @author 李金绪
 * @since 2024-08-27
 */
@DisplayName("测试 GenericableServerFilterManager")
public class GenericableServerFilterManagerTest {
    private DefaultGenericableServerFilterManager manager;
    private GenericableServerFilter serverFilter1;
    private GenericableServerFilter serverFilter2;
    private Plugin plugin;

    @BeforeEach
    void setup() {
        this.manager = new DefaultGenericableServerFilterManager();
        this.serverFilter1 = mock(GenericableServerFilter.class);
        this.serverFilter2 = mock(GenericableServerFilter.class);
        when(this.serverFilter1.priority()).thenReturn(1);
        when(this.serverFilter2.priority()).thenReturn(2);
        this.plugin = mock(Plugin.class);
        BeanContainer container = mock(BeanContainer.class);
        when(this.plugin.container()).thenReturn(container);
        BeanFactory factory = mock(BeanFactory.class);
        when(container.factories(GenericableServerFilter.class)).thenReturn(Collections.singletonList(factory));
        GenericableServerFilter filter = mock(GenericableServerFilter.class);
        when(factory.get()).thenReturn(filter);
    }

    @Test
    @DisplayName("测试正确的注册过滤器")
    void shouldRegisterTrue() {
        this.manager.register(this.serverFilter2);
        this.manager.register(this.serverFilter1);
        Assertions.assertEquals(this.manager.get().get(0), this.serverFilter1);
    }

    @Test
    @DisplayName("测试正确的卸载过滤器")
    void shouldUnregisterTrue() {
        this.manager.register(this.serverFilter2);
        this.manager.register(this.serverFilter1);
        this.manager.unregister(this.serverFilter2);
        Assertions.assertEquals(this.manager.get().size(), 1);
    }

    @Test
    @DisplayName("插件启动时正确注册过滤器")
    void shouldRegisterWhenStart() {
        this.manager.onPluginStarted(this.plugin);
        Assertions.assertEquals(1, this.manager.get().size());
    }

    @Test
    @DisplayName("插件卸载时正确删除过滤器")
    void shouldRegisterWhenStop() {
        this.manager.onPluginStarted(this.plugin);
        this.manager.onPluginStopping(this.plugin);
        Assertions.assertEquals(0, this.manager.get().size());
    }
}
