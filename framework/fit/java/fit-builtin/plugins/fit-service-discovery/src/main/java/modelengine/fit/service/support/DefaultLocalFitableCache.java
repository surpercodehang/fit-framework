/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.service.LocalFitableCache;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.broker.LocalExecutorFactory;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginStartedObserver;
import modelengine.fitframework.plugin.PluginStoppingObserver;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.runtime.FitRuntimeStartedObserver;
import modelengine.fitframework.util.LockUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link LocalFitableCache} 的默认实现。
 *
 * @author 季聿阶
 * @since 2021-11-22
 */
@Component
public class DefaultLocalFitableCache
        implements LocalFitableCache, PluginStartedObserver, PluginStoppingObserver, FitRuntimeStartedObserver {
    private static final Logger log = Logger.get(DefaultLocalFitableCache.class);

    private final LocalExecutorFactory localExecutorFactory;

    private final Set<UniqueFitableId> localFitables = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Object lock = LockUtils.newSynchronizedLock();

    public DefaultLocalFitableCache(LocalExecutorFactory localExecutorFactory) {
        this.localExecutorFactory = notNull(localExecutorFactory, "The proxy manager cannot be null.");
    }

    @Override
    public boolean contains(UniqueFitableId id) {
        notNull(id, "The unique fitable id cannot be null.");
        synchronized (this.lock) {
            return this.localFitables.contains(id);
        }
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        log.debug("Service discovery plugin observe plugin started. [plugin={}]", plugin.metadata().name());
        this.addPluginFitables(plugin);
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        log.debug("Service discovery plugin observe plugin stopping. [plugin={}]", plugin.metadata().name());
        this.removePluginFitables(plugin);
    }

    @Override
    public void onRuntimeStarted(FitRuntime runtime) {
        log.debug("Service discovery plugin observe fit runtime started.");
        runtime.plugins().forEach(this::addPluginFitables);
        synchronized (this.lock) {
            log.debug("Service discovery plugin added local fitables to local cache. [totalSize={}]",
                    this.localFitables.size());
        }
    }

    private void addPluginFitables(Plugin plugin) {
        List<LocalExecutor> executors = this.localExecutorFactory.get(plugin, false);
        synchronized (this.lock) {
            executors.forEach(executor -> this.addLocalFitable(executor.id().genericableId(),
                    executor.id().fitableId()));
        }
        log.debug("Service discovery plugin added fitables to local cache. [size={}]", executors.size());
    }

    private void addLocalFitable(String genericableId, String fitableId) {
        notBlank(genericableId, "The genericable id cannot be blank.");
        notBlank(fitableId, "The fitable id cannot be blank.");
        this.localFitables.add(UniqueFitableId.create(genericableId, fitableId));
    }

    private void removePluginFitables(Plugin plugin) {
        List<LocalExecutor> executors = this.localExecutorFactory.get(plugin, false);
        synchronized (this.lock) {
            executors.forEach(executor -> this.removeLocalFitable(executor.id().genericableId(),
                    executor.id().fitableId()));
        }
        log.debug("Service discovery plugin removed fitables from local cache. [size={}]", executors.size());
    }

    private void removeLocalFitable(String genericableId, String fitableId) {
        notBlank(genericableId, "The genericable id cannot be blank.");
        notBlank(fitableId, "The fitable id cannot be blank.");
        this.localFitables.remove(UniqueFitableId.create(genericableId, fitableId));
    }
}
