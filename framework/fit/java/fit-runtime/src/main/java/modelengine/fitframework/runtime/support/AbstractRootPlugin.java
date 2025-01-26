/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.aop.AopInterceptor;
import modelengine.fitframework.aop.proxy.AopProxyFactories;
import modelengine.fitframework.broker.DynamicRouter;
import modelengine.fitframework.broker.FitExceptionCreator;
import modelengine.fitframework.broker.FitableFactory;
import modelengine.fitframework.broker.LoadBalancer;
import modelengine.fitframework.broker.SerializationService;
import modelengine.fitframework.broker.TargetLocator;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.RouterFactory;
import modelengine.fitframework.broker.client.support.DefaultBrokerClient;
import modelengine.fitframework.broker.client.support.DefaultInvokerFactory;
import modelengine.fitframework.broker.client.support.DefaultRouterFactory;
import modelengine.fitframework.broker.serialization.DefaultSerializationService;
import modelengine.fitframework.broker.server.Dispatcher;
import modelengine.fitframework.broker.server.GenericableServerFilterManager;
import modelengine.fitframework.broker.server.support.DefaultDispatcher;
import modelengine.fitframework.broker.server.support.DefaultGenericableServerFilterManager;
import modelengine.fitframework.broker.support.DefaultDynamicRouter;
import modelengine.fitframework.broker.support.DefaultFitExceptionCreator;
import modelengine.fitframework.broker.support.DefaultFitableFactory;
import modelengine.fitframework.broker.support.DefaultGenericableFactory;
import modelengine.fitframework.broker.support.DefaultLoadBalancer;
import modelengine.fitframework.broker.support.DefaultTargetLocator;
import modelengine.fitframework.conf.runtime.ApplicationConfig;
import modelengine.fitframework.conf.runtime.DefaultApplication;
import modelengine.fitframework.conf.runtime.DefaultMatata;
import modelengine.fitframework.conf.runtime.DefaultWorker;
import modelengine.fitframework.conf.runtime.MatataConfig;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginCategory;
import modelengine.fitframework.plugin.PluginComparators;
import modelengine.fitframework.plugin.RootPlugin;
import modelengine.fitframework.plugin.support.AbstractPlugin;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 为根插件提供基类。
 *
 * @author 梁济时
 * @since 2023-02-07
 */
public abstract class AbstractRootPlugin extends AbstractPlugin implements RootPlugin {
    private static final Logger log = Logger.get(AbstractRootPlugin.class);

    private static final String FRAMEWORK_CONFIG_PREFIX = "fitframework";
    private static final String FIT_RUNTIME_BEAN_NAME = "fitRuntime";
    private static final String AOP_PROXY_FACTORIES_BEAN_NAME = "aopProxyFactories";
    private static final String AOP_INTERCEPTOR_BEAN_NAME = "aopInterceptor";
    private static final String SERIALIZATION_SERVICE_BEAN_NAME = "serializationService";
    private static final String DYNAMIC_ROUTER_BEAN_NAME = "dynamicRouter";
    private static final String GENERICABLE_FACTORY_BEAN_NAME = "genericableFactory";
    private static final String TARGET_LOCATOR_BEAN_NAME = "targetLocator";
    private static final String LOAD_BALANCER_BEAN_BANE = "loadBalancer";
    private static final String FIT_EXCEPTION_CREATOR_BEAN_NAME = "fitExceptionCreator";
    private static final String FITABLE_FACTORY_BEAN_NAME = "fitableFactory";
    private static final String INVOKER_FACTORY_BEAN_NAME = "invokerFactory";
    private static final String LOCAL_GENERICABLE_REPOSITORY_NAME = "localGenericableRepository";
    private static final String ROUTER_FACTORY_BEAN_NAME = "routerFactory";
    private static final String BROKER_CLIENT_BEAN_NAME = "brokerClient";
    private static final String DISPATCHER_BEAN_NAME = "dispatcher";
    private static final String MATATA_CONFIG_BEAN_NAME = "matataConfig";
    private static final String WORKER_CONFIG_BEAN_NAME = "workerConfig";
    private static final String APPLICATION_CONFIG = "applicationConfig";
    private static final String GENERICABLE_FILTER_MANAGER_NAME = "genericableServerFilterManager";
    private static final int LEVEL_MAX = 7;
    private static final int LEVEL_MIN = 1;

    private MatataConfig matata;
    private WorkerConfig worker;

    @Override
    public final Plugin parent() {
        return null;
    }

    @Override
    protected String getBuiltinConfigPrefix() {
        return FRAMEWORK_CONFIG_PREFIX;
    }

    @Override
    protected void registerSystemBeans() {
        this.container().registry().register(this.runtime(), FIT_RUNTIME_BEAN_NAME);
        this.registerConfigBeans();
        this.registerAopBeans();
        this.registerBrokerBeans();
        super.registerSystemBeans();
    }

    private void registerConfigBeans() {
        ApplicationConfig application = this.config().get("application", DefaultApplication.class);
        this.container().registry().register(application, APPLICATION_CONFIG);
        log.debug("Config 'application.*' is {}.", application);
        this.matata = this.config().get("matata", DefaultMatata.class);
        this.container().registry().register(this.matata, MATATA_CONFIG_BEAN_NAME);
        log.debug("Config 'matata.*' is {}.", this.matata);
        this.worker = this.config().get("worker", DefaultWorker.class);
        this.container().registry().register(this.worker, WORKER_CONFIG_BEAN_NAME);
        log.debug("Config 'worker.*' is {}.", this.worker);
    }

    private void registerAopBeans() {
        this.container().registry().register(new AopProxyFactories(), AOP_PROXY_FACTORIES_BEAN_NAME);
        this.container().registry().register(new AopInterceptor(this.container()), AOP_INTERCEPTOR_BEAN_NAME);
    }

    private void registerBrokerBeans() {
        SerializationService serializationService = new DefaultSerializationService(this.container());
        this.container().registry().register(serializationService, SERIALIZATION_SERVICE_BEAN_NAME);
        DynamicRouter dynamicRouter = new DefaultDynamicRouter();
        this.container().registry().register(dynamicRouter, DYNAMIC_ROUTER_BEAN_NAME);
        DefaultGenericableFactory genericableFactory = new DefaultGenericableFactory(dynamicRouter);
        this.container().registry().register(genericableFactory, GENERICABLE_FACTORY_BEAN_NAME);
        TargetLocator targetLocator =
                new DefaultTargetLocator(this.container(), this.worker, this.matata.registry().availableServices());
        this.container().registry().register(targetLocator, TARGET_LOCATOR_BEAN_NAME);
        LoadBalancer loadBalancer = new DefaultLoadBalancer(this.container(), serializationService, targetLocator);
        this.container().registry().register(loadBalancer, LOAD_BALANCER_BEAN_BANE);
        FitExceptionCreator exceptionCreator = new DefaultFitExceptionCreator(this.container());
        this.container().registry().register(exceptionCreator, FIT_EXCEPTION_CREATOR_BEAN_NAME);
        FitableFactory fitableFactory = new DefaultFitableFactory(this.container(), loadBalancer, targetLocator);
        this.container().registry().register(fitableFactory, FITABLE_FACTORY_BEAN_NAME);
        DefaultInvokerFactory invokerFactory = new DefaultInvokerFactory(this.container(),
                genericableFactory,
                fitableFactory,
                this.config(),
                this.worker);
        this.container().registry().register(invokerFactory, INVOKER_FACTORY_BEAN_NAME);
        this.container()
                .registry()
                .register(invokerFactory.localGenericableRepository(), LOCAL_GENERICABLE_REPOSITORY_NAME);
        RouterFactory routerFactory = new DefaultRouterFactory(invokerFactory);
        this.container().registry().register(routerFactory, ROUTER_FACTORY_BEAN_NAME);
        BrokerClient brokerClient = new DefaultBrokerClient(routerFactory);
        this.container().registry().register(brokerClient, BROKER_CLIENT_BEAN_NAME);
        GenericableServerFilterManager genericableServerFilterManager = new DefaultGenericableServerFilterManager();
        this.container().registry().register(genericableServerFilterManager, GENERICABLE_FILTER_MANAGER_NAME);
        Dispatcher dispatcher = new DefaultDispatcher(this.container(), this.worker, genericableServerFilterManager);
        this.container().registry().register(dispatcher, DISPATCHER_BEAN_NAME);
    }

    @Override
    protected void onInitialized() {
        this.loadPlugins();
        log.debug("Total {} plugins loaded.", this.children().size());
        super.onInitialized();
        this.obtainChildrenForStartup(PluginCategory.SYSTEM).forEach(Plugin::initialize);
    }

    @Override
    protected void onStarted() {
        this.obtainChildrenForStartup(PluginCategory.SYSTEM).forEach(Plugin::start);
        super.onStarted();
        List<Plugin> userPlugins = this.obtainChildrenForStartup(PluginCategory.USER);
        for (int level = LEVEL_MIN; level <= LEVEL_MAX; level++) {
            List<Plugin> levelPlugins = this.obtainPluginsByLevel(userPlugins, level);
            levelPlugins.forEach(Plugin::initialize);
            levelPlugins.forEach(Plugin::start);
        }
    }

    private List<Plugin> obtainPluginsByLevel(List<Plugin> plugins, int level) {
        return plugins.stream()
                .filter(userPlugin -> userPlugin.metadata().level() == level)
                .sorted(AbstractRootPlugin::sortPlugins)
                .collect(Collectors.toList());
    }

    /**
     * 加载插件。
     */
    protected abstract void loadPlugins();

    private List<Plugin> obtainChildrenForStartup(PluginCategory category) {
        return this.children()
                .stream()
                .filter(plugin -> Objects.equals(plugin.metadata().category(), category))
                .sorted(AbstractRootPlugin::sortPlugins)
                .collect(Collectors.toList());
    }

    private static int sortPlugins(Plugin plugin1, Plugin plugin2) {
        return PluginComparators.STARTUP.compare(plugin1, plugin2);
    }

    @Override
    public Plugin loadPlugin(URL plugin) {
        notNull(plugin, "The plugin file to load cannot be null.");
        return this.children().add(plugin);
    }

    @Override
    public Plugin unloadPlugin(URL plugin) {
        notNull(plugin, "The plugin file to unload cannot be null.");
        return this.children().remove(plugin);
    }
}
