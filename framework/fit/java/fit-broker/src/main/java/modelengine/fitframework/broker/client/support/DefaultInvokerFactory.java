/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.FitableFactory;
import modelengine.fitframework.broker.GenericableFactory;
import modelengine.fitframework.broker.GenericableRepository;
import modelengine.fitframework.broker.InvocationContext;
import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.broker.LocalExecutorFactory;
import modelengine.fitframework.broker.LocalExecutorRepository;
import modelengine.fitframework.broker.LocalExecutorResolver;
import modelengine.fitframework.broker.LocalGenericableRepository;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.InvokerFactory;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.support.DefaultGenericableRepository;
import modelengine.fitframework.broker.support.DefaultLocalExecutorRepository;
import modelengine.fitframework.broker.support.DefaultLocalGenericableRepository;
import modelengine.fitframework.broker.support.LocalExecutorRepositoryComposite;
import modelengine.fitframework.broker.support.PriorityGenericableRepository;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.lifecycle.container.BeanContainerInitializedObserver;
import modelengine.fitframework.ioc.lifecycle.container.BeanContainerStoppedObserver;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 为 {@link InvokerFactory} 提供默认实现。
 *
 * @author 季聿阶
 * @since 2021-10-26
 */
public class DefaultInvokerFactory implements InvokerFactory, LocalExecutorFactory, LocalExecutorResolver.RootContainer,
        BeanContainerInitializedObserver, BeanContainerStoppedObserver {
    private static final Logger log = Logger.get(DefaultInvokerFactory.class);

    private final GenericableFactory genericableFactory;
    private final FitableFactory fitableFactory;
    private final Config config;

    private final BeanContainer rootContainer;
    private final LocalExecutorRepository rootLocalExecutorRepository;
    private final LocalExecutorRepositoryComposite localExecutorRepository;
    private final DefaultLocalGenericableRepository localGenericableRepository;
    private final LazyLoader<GenericableRepository> genericableRepositoryLoader;

    private final LazyLoader<String> appNameLoader = new LazyLoader<>(this::getApplicationName);
    private final WorkerConfig worker;

    /**
     * 通过系统的 Bean 容器、泛服务工厂、泛服务实现工厂和系统配置来创建一个新的 {@link DefaultInvokerFactory} 的实例。
     * <p>构造函数中主要会做两件事：
     * <ol>
     *     <li>初始化本地的 {@link LocalExecutorRepository} 和 {@link GenericableRepository}。</li>
     *     <li>获取调用的相关配置。</li>
     * </ol>
     * </p>
     * <p>在初始化本地仓库中：
     * <ol>
     *     <li>构建系统的 {@link LocalExecutorRepository} 的根仓库，并设置一个插件的 {@link LocalExecutorRepositoryComposite}
     *     仓库，允许后续插件安装及卸载过程中可以在插件仓库中进行。</li>
     *     <li>构建系统的 {@link GenericableRepository} 的根仓库，并设置一个插件的 {@link DefaultLocalGenericableRepository}
     *     仓库，允许后续插件安装及卸载过程中可以在插件仓库中进行。</li>
     *     <li>将两个根仓库进行关联，确保本地 {@link LocalExecutor} 注册之后，可以将其配置导入到 {@link GenericableRepository}
     *     中。</li>
     * </ol>
     * </p>
     *
     * @param container 表示系统的 Bean 容器的 {@link BeanContainer}。
     * @param genericableFactory 表示泛服务工厂的 {@link GenericableFactory}。
     * @param fitableFactory 表示泛服务实现工厂的 {@link FitableFactory}。
     * @param config 表示系统配置的 {@link Config}。
     * @param worker 表示进程配置的 {@link WorkerConfig}。
     */
    public DefaultInvokerFactory(BeanContainer container, GenericableFactory genericableFactory,
            FitableFactory fitableFactory, Config config, WorkerConfig worker) {
        this.rootContainer = container;
        this.genericableFactory = notNull(genericableFactory, "The genericable factory cannot be null.");
        this.fitableFactory = notNull(fitableFactory, "The fitable factory cannot be null.");
        this.config = notNull(config, "The config cannot be null.");
        this.worker = notNull(worker, "The worker config cannot be null.");

        DefaultLocalExecutorRepository rootExecutorRepository = new DefaultLocalExecutorRepository("root");
        this.rootLocalExecutorRepository = rootExecutorRepository;
        this.localExecutorRepository = new LocalExecutorRepositoryComposite(rootExecutorRepository);
        DefaultGenericableRepository rootGenericableRepository =
                new DefaultGenericableRepository("root", this.genericableFactory, this.fitableFactory);
        this.localGenericableRepository = new DefaultLocalGenericableRepository(this.genericableFactory,
                this.fitableFactory,
                rootGenericableRepository);
        this.genericableRepositoryLoader = new LazyLoader<>(() -> {
            List<GenericableRepository> repositories = container.all(GenericableRepository.class)
                    .stream()
                    .map(BeanFactory::<GenericableRepository>get)
                    .collect(Collectors.toList());
            return new PriorityGenericableRepository(repositories);
        });
        rootExecutorRepository.observeLocalExecutorRegistered(rootGenericableRepository);
    }

    private String getApplicationName() {
        String applicationName = notBlank(this.config.get("application.name", String.class),
                "No application name. [config='application.name']");
        log.debug("Config 'application.name' is {}.", applicationName);
        return applicationName;
    }

    /**
     * 获取本地的泛服务仓库。
     *
     * @return 表示本地的泛服务仓库的 {@link LocalGenericableRepository}。
     */
    public LocalGenericableRepository localGenericableRepository() {
        return this.localGenericableRepository;
    }

    @Override
    public Invoker create(String genericableId, boolean isMicro, Method genericableMethod, Router.Filter filter) {
        InvocationContext.Builder builder = InvocationContext.custom()
                .genericableId(genericableId)
                .isMicro(isMicro)
                .genericableMethod(genericableMethod)
                .localWorkerId(this.worker.id())
                .appName(this.appNameLoader.get())
                .isGeneric(false)
                .retry(0)
                .timeout(3000)
                .timeoutUnit(TimeUnit.MILLISECONDS)
                .environmentPrioritySequence(this.worker.environmentSequence())
                .routingFilter(filter);
        return new DefaultInvoker(this.localGenericableRepository,
                this.genericableRepositoryLoader.get(),
                genericableId,
                builder);
    }

    @Override
    public void onBeanContainerInitialized(BeanContainer container) {
        String pluginName = container.plugin().metadata().name();
        DefaultLocalExecutorRepository pluginLocalExecutorRepository = new DefaultLocalExecutorRepository(pluginName);
        this.localExecutorRepository.install(pluginLocalExecutorRepository);
        DefaultGenericableRepository pluginGenericableRepository =
                new DefaultGenericableRepository(pluginName, this.genericableFactory, this.fitableFactory);
        this.localGenericableRepository.install(pluginGenericableRepository);
        pluginLocalExecutorRepository.observeLocalExecutorRegistered(pluginGenericableRepository);
        container.factories()
                .forEach(factory -> this.resolveLocalExecutors(factory.metadata(),
                        container,
                        pluginLocalExecutorRepository.registry()));
    }

    @Override
    public void onBeanContainerStopped(BeanContainer container) {
        String pluginName = container.plugin().metadata().name();
        this.localGenericableRepository.getChild(pluginName).ifPresent(this.localGenericableRepository::uninstall);
        this.localExecutorRepository.getChild(pluginName).ifPresent(this.localExecutorRepository::uninstall);
    }

    @Override
    public void resolveAll(BeanMetadata metadata) {
        notNull(metadata, "The bean metadata cannot be null.");
        this.resolveLocalExecutors(metadata, this.rootContainer, this.rootLocalExecutorRepository.registry());
    }

    private void resolveLocalExecutors(BeanMetadata metadata, BeanContainer container,
            LocalExecutorRepository.Registry registry) {
        LocalExecutorResolver resolver = LocalExecutorResolver.factory().create(container, registry);
        this.resolveLocalExecutors(resolver, metadata);
    }

    private void resolveLocalExecutors(LocalExecutorResolver resolver, BeanMetadata metadata) {
        Class<?> objectClass = TypeUtils.toClass(metadata.type());
        while (objectClass != null && !Objects.equals(objectClass, Object.class)) {
            Method[] methods = ReflectionUtils.getDeclaredMethods(objectClass);
            for (Method method : methods) {
                if (method.isSynthetic()) {
                    continue;
                }
                resolver.resolve(metadata, method);
            }
            objectClass = objectClass.getSuperclass();
        }
    }

    @Override
    public Optional<LocalExecutor> get(UniqueFitableId id) {
        return this.localExecutorRepository.executor(id);
    }

    @Override
    public List<LocalExecutor> get(Plugin plugin, boolean isMicro) {
        LocalExecutorRepository proxyRepository = this.localExecutorRepository.getChild(plugin.metadata().name())
                .orElseThrow(() -> new IllegalStateException(StringUtils.format("No plugin. [pluginName={0}]",
                        plugin.metadata().name())));
        return proxyRepository.executors()
                .stream()
                .filter(localExecutor -> localExecutor.isMicro() == isMicro)
                .collect(Collectors.toList());
    }
}
