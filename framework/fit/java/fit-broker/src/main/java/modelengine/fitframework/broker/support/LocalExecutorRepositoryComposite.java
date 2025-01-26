/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.broker.LocalExecutorRepository;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.broker.UniqueGenericableId;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.IntStream;

/**
 * 组合的服务实现的本地执行器仓库。
 *
 * @author 梁济时
 * @since 2020-09-25
 */
public class LocalExecutorRepositoryComposite implements LocalExecutorRepository {
    private static final Logger log = Logger.get(LocalExecutorRepositoryComposite.class);

    private final LocalExecutorRepository rootRepository;
    private final List<LocalExecutorRepository> pluginRepositories;
    private final ReadWriteLock lock;

    public LocalExecutorRepositoryComposite(LocalExecutorRepository rootRepository) {
        this.rootRepository = notNull(rootRepository, "The root repository of local executors cannot be null.");
        this.pluginRepositories = new ArrayList<>();
        this.lock = LockUtils.newReentrantReadWriteLock();
    }

    /**
     * 安装一个插件的仓库。
     *
     * @param repository 表示待安装的插件仓库的 {@link LocalExecutorRepository}。
     */
    public void install(LocalExecutorRepository repository) {
        notNull(repository, "The local executor repository to install cannot be null.");
        LockUtils.synchronize(this.lock.writeLock(), () -> {
            this.getChild(repository.name()).ifPresent(this::uninstall);
            log.debug("Install local executor repository. [name={}]", repository.name());
            this.pluginRepositories.add(repository);
        });
    }

    /**
     * 卸载一个插件的仓库。
     *
     * @param repository 表示待卸载的插件仓库的 {@link LocalExecutorRepository}。
     */
    public void uninstall(LocalExecutorRepository repository) {
        notNull(repository, "The local executor repository to uninstall cannot be null.");
        LockUtils.synchronize(this.lock.writeLock(), () -> {
            OptionalInt first = IntStream.range(0, this.pluginRepositories.size())
                    .filter(i -> Objects.equals(this.pluginRepositories.get(i).name(), repository.name()))
                    .findFirst();
            if (first.isPresent()) {
                log.debug("Uninstall local executor repository. [name={}]", repository.name());
                this.pluginRepositories.remove(first.getAsInt());
            } else {
                log.debug("The local executor repository to uninstall not exists. [name={}]", repository.name());
            }
        });
    }

    /**
     * 获取根仓库。
     * <p>仅限同包访问，外界应该优先访问 {@link LocalExecutorRepository} 的方法。</p>
     *
     * @return 表示根仓库的 {@link LocalExecutorRepository}。
     */
    LocalExecutorRepository getRootRepository() {
        return this.rootRepository;
    }

    /**
     * 获取所有插件的仓库列表。
     *
     * @return 表示所有插件的仓库列表的 {@link List}{@code <}{@link LocalExecutorRepository}{@code >}。
     */
    public List<LocalExecutorRepository> getChildren() {
        return LockUtils.synchronize(this.lock.readLock(), () -> Collections.unmodifiableList(this.pluginRepositories));
    }

    /**
     * 获取指定名字的插件的仓库。
     *
     * @param name 表示指定插件名字的 {@link String}。
     * @return 表示指定名字的插件的仓库的 {@link Optional}{@code <}{@link LocalExecutorRepository}{@code >}。
     */
    public Optional<LocalExecutorRepository> getChild(String name) {
        return LockUtils.synchronize(this.lock.readLock(),
                () -> this.pluginRepositories.stream()
                        .filter(repository -> StringUtils.equals(repository.name(), name))
                        .findAny());
    }

    @Override
    public Registry registry() {
        return this.rootRepository.registry();
    }

    @Override
    public String name() {
        return this.rootRepository.name();
    }

    @Override
    public Set<LocalExecutor> executors() {
        Set<LocalExecutor> executors = new HashSet<>(this.rootRepository.executors());
        this.getChildren().forEach(repository -> executors.addAll(repository.executors()));
        return executors;
    }

    @Override
    public Set<LocalExecutor> executors(UniqueGenericableId id) {
        Set<LocalExecutor> fitableProxies = new HashSet<>(this.rootRepository.executors(id));
        this.getChildren().forEach(repository -> fitableProxies.addAll(repository.executors(id)));
        return fitableProxies;
    }

    @Override
    public Optional<LocalExecutor> executor(UniqueFitableId id) {
        Optional<LocalExecutor> executor = this.rootRepository.executor(id);
        if (executor.isPresent()) {
            return executor;
        }
        return this.getChildren()
                .stream()
                .map(repository -> repository.executor(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
