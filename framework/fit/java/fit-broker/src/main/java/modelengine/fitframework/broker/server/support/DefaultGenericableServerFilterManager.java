/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.server.support;

import modelengine.fitframework.broker.server.GenericableServerFilter;
import modelengine.fitframework.broker.server.GenericableServerFilterManager;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginStartedObserver;
import modelengine.fitframework.plugin.PluginStoppingObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link GenericableServerFilterManager} 的默认实现。
 *
 * @author 李金绪
 * @since 2024-08-27
 */
public class DefaultGenericableServerFilterManager
        implements GenericableServerFilterManager, PluginStartedObserver, PluginStoppingObserver {
    List<GenericableServerFilter> filters = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void register(GenericableServerFilter filter) {
        if (filter != null) {
            this.filters.add(filter);
            Collections.sort(this.filters, GenericableServerFilter.PriorityComparator.INSTANCE);
        }
    }

    @Override
    public void unregister(GenericableServerFilter filter) {
        if (filter != null) {
            this.filters.remove(filter);
        }
    }

    @Override
    public List<GenericableServerFilter> get() {
        return this.filters;
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        this.getFilters(plugin).forEach(this::register);
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        this.getFilters(plugin).forEach(this::unregister);
    }

    private List<GenericableServerFilter> getFilters(Plugin plugin) {
        return plugin.container()
                .factories(GenericableServerFilter.class)
                .stream()
                .map(BeanFactory::<GenericableServerFilter>get)
                .collect(Collectors.toList());
    }
}
