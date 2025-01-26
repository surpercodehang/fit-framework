/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.broker.LocalExecutorRepository;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.broker.UniqueGenericableId;
import modelengine.fitframework.broker.event.LocalExecutorRegisteredObserver;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.LockUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.Collectors;

/**
 * 为 {@link LocalExecutorRepository} 提供默认实现。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-09-24
 */
public class DefaultLocalExecutorRepository implements LocalExecutorRepository {
    private static final Logger log = Logger.get(DefaultLocalExecutorRepository.class);

    private final String name;

    /**
     * 表示本地执行器。
     * <p>其多层次键的含义依次为：
     * <ol>
     *     <li>服务唯一标识</li>
     *     <li>服务版本号</li>
     *     <li>服务实现唯一标识</li>
     *     <li>服务实现版本号</li>
     * </ol>
     * </p>
     */
    private final Map<String, Map<String, Map<String, Map<String, LocalExecutor>>>> localExecutors;
    private final ReadWriteLock lock;

    private final List<LocalExecutorRegisteredObserver> registeredObservers;

    public DefaultLocalExecutorRepository(String name) {
        this.name = notBlank(name, "The name of local fitable executor repository cannot be blank.");
        this.localExecutors = new HashMap<>();
        this.lock = LockUtils.newReentrantReadWriteLock();
        this.registeredObservers = new ArrayList<>();
    }

    /**
     * 观察本地执行器注册完毕的事件。
     *
     * @param observer 表示本地执行器注册完毕的观察者的 {@link LocalExecutorRegisteredObserver}。
     */
    public void observeLocalExecutorRegistered(LocalExecutorRegisteredObserver observer) {
        if (observer != null) {
            this.registeredObservers.add(observer);
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Set<LocalExecutor> executors() {
        return LockUtils.synchronize(this.lock.readLock(),
                () -> this.localExecutors.values()
                        .stream()
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()));
    }

    @Override
    public Set<LocalExecutor> executors(UniqueGenericableId id) {
        notNull(id, "The unique genericable id cannot be null.");
        return LockUtils.synchronize(this.lock.readLock(),
                () -> Optional.ofNullable(this.localExecutors.get(id.genericableId()))
                        .map(map -> map.get(id.genericableVersion()))
                        .orElseGet(HashMap::new)
                        .values()
                        .stream()
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()));
    }

    @Override
    public Optional<LocalExecutor> executor(UniqueFitableId id) {
        notNull(id, "The unique fitable id cannot be null.");
        return LockUtils.synchronize(this.lock.readLock(),
                () -> Optional.ofNullable(this.localExecutors.get(id.genericableId()))
                        .map(map -> map.get(id.genericableVersion()))
                        .map(map -> map.get(id.fitableId()))
                        .map(map -> map.get(id.fitableVersion())));
    }

    private void addLocalExecutor(UniqueFitableId id, LocalExecutor executor) {
        if (executor == null) {
            this.removeLocalExecutor(id);
        } else {
            log.debug("Add local executor of fitable. [id={}]", id);
            LockUtils.synchronize(this.lock.writeLock(), () -> {
                this.localExecutors.computeIfAbsent(id.genericableId(), genericableId -> new HashMap<>())
                        .computeIfAbsent(id.genericableVersion(), genericableVersion -> new HashMap<>())
                        .computeIfAbsent(id.fitableId(), fitableId -> new HashMap<>())
                        .put(id.fitableVersion(), executor);
                this.registeredObservers.forEach(observer -> observer.onLocalExecutorRegistered(id, executor));
            });
        }
    }

    private void removeLocalExecutor(UniqueFitableId id) {
        log.debug("Remove local executor of fitable. [id={}]", id);
        LockUtils.synchronize(this.lock.writeLock(), () -> {
            Map<String, Map<String, Map<String, LocalExecutor>>> firstMap = this.localExecutors.get(id.genericableId());
            if (firstMap == null) {
                return;
            }
            Map<String, Map<String, LocalExecutor>> secondMap = firstMap.get(id.genericableVersion());
            if (secondMap == null) {
                return;
            }
            Map<String, LocalExecutor> thirdMap = secondMap.get(id.fitableId());
            if (thirdMap == null) {
                return;
            }
            thirdMap.remove(id.fitableVersion());
            if (thirdMap.isEmpty()) {
                secondMap.remove(id.fitableId());
            }
            if (secondMap.isEmpty()) {
                firstMap.remove(id.genericableVersion());
            }
            if (firstMap.isEmpty()) {
                this.localExecutors.remove(id.genericableId());
            }
        });
    }

    @Override
    public LocalExecutorRepository.Registry registry() {
        return (id, executor) -> {
            notNull(id, "The unique fitable id of local fitable executor to register cannot be null.");
            this.addLocalExecutor(id, executor);
        };
    }
}
