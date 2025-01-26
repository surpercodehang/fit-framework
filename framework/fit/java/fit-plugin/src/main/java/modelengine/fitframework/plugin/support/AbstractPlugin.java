/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.ConfigChain;
import modelengine.fitframework.conf.ConfigDecryptor;
import modelengine.fitframework.conf.ConfigLoadingResult;
import modelengine.fitframework.conf.support.DefaultConfigChain;
import modelengine.fitframework.event.EventPublisher;
import modelengine.fitframework.globalization.StringResource;
import modelengine.fitframework.globalization.StringResources;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.support.DefaultBeanContainer;
import modelengine.fitframework.jvm.scan.PackageScanner;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginCollection;
import modelengine.fitframework.plugin.PluginStartedObserver;
import modelengine.fitframework.plugin.PluginStartingObserver;
import modelengine.fitframework.plugin.PluginStoppedObserver;
import modelengine.fitframework.plugin.PluginStoppingObserver;
import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.resource.ResourceResolver;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.support.AbstractDisposable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为插件提供基类。
 *
 * @author 梁济时
 * @since 2023-01-30
 */
public abstract class AbstractPlugin extends AbstractDisposable implements Plugin {
    /** 表示内置配置文件名的统一前缀的 {@link String}。 */
    public static final String BUILTIN_CONFIG_PREFIX = "application";

    private static final Logger LOG = Logger.get(AbstractPlugin.class);

    private static final String PLUGIN_CONFIG_NAME = "Plugin Config";
    private static final String BUILTIN_CONFIG_CHAIN_NAME = "Builtin Config Chain";
    private static final String IMPORTED_CONFIG_CHAIN_NAME = "Imported Config Chain";
    private static final String INHERITED_CONFIG_CHAIN_NAME = "Inherited Config Chain";
    private static final String PROFILE_BUILTIN_CONFIG_NAME = "Builtin Config (Profile)";
    private static final String DEFAULT_BUILTIN_CONFIG_NAME = "Builtin Config (Default)";
    private static final String PREFIXED_INHERITED_CONFIG_NAME = "Global Config (Prefixed)";

    private static final String BASE_PACKAGE_KEY = "fit.beans.packages";
    private static final String PLUGIN_CONFIG_PREFIX = "plugin" + Config.SEPARATOR_DOT;

    private static final String PLUGIN_BEAN_NAME = "plugin";
    private static final String CONTAINER_BEAN_NAME = "beanContainer";
    private static final String CONFIG_BEAN_NAME = "config";

    private final BeanContainer container;
    private final PluginCollection children;
    private final EventPublisher publisherOfEvents;

    private volatile Config config;
    private volatile StringResource sr;
    private volatile ResourceResolver resolverOfResources;
    private volatile ConfigChain importedConfigChain;

    private volatile boolean initialized;
    private volatile boolean started;
    private final Object monitor;

    public AbstractPlugin() {
        this.container = new DefaultBeanContainer(this);
        this.children = new DefaultPluginCollection(this);
        this.publisherOfEvents = new PluginEventPublisher(this);

        this.started = false;
        this.monitor = LockUtils.newSynchronizedLock();
    }

    @Override
    public final Config config() {
        return this.config;
    }

    @Override
    public final PluginCollection children() {
        return this.children;
    }

    @Override
    public final ResourceResolver resolverOfResources() {
        return this.resolverOfResources;
    }

    @Override
    public final EventPublisher publisherOfEvents() {
        return this.publisherOfEvents;
    }

    @Override
    public final BeanContainer container() {
        return this.container;
    }

    @Override
    public final StringResource sr() {
        return this.sr;
    }

    @Override
    public final boolean initialized() {
        return this.initialized;
    }

    @Override
    public final void initialize() {
        if (this.initialized) {
            return;
        }
        synchronized (this.monitor) {
            if (this.initialized) {
                return;
            }
            this.initialize0();
            this.initialized = true;
        }
    }

    private void initialize0() {
        LOG.debug("Initialize plugin: {}", this.metadata());
        this.registerJars();
        this.resolverOfResources = ResourceResolver.forClassLoader(this.pluginClassLoader());
        this.importedConfigChain = new DefaultConfigChain(IMPORTED_CONFIG_CHAIN_NAME);
        this.config = this.loadConfig();
        this.sr = StringResources.forPlugin(this);
        this.registerSystemBeans();
        this.scanBeans();
        this.postProcessConfig();
        this.onInitialized();
    }

    /**
     * 注册 JAR。包括公共 JAR 和私有 JAR。
     */
    protected void registerJars() {}

    private ConfigChain loadConfig() {
        ConfigChain pluginChain = new DefaultConfigChain(PLUGIN_CONFIG_NAME);
        this.addInheritedConfigChain(pluginChain);
        this.addImportedConfigChain(pluginChain);
        this.addBuiltinConfigChain(pluginChain);
        return pluginChain;
    }

    private void addInheritedConfigChain(ConfigChain pluginChain) {
        ConfigChain chain = new DefaultConfigChain(INHERITED_CONFIG_CHAIN_NAME);
        pluginChain.addConfig(chain);
        chain.addConfig(new PrefixedConfig(PREFIXED_INHERITED_CONFIG_NAME,
                this.runtime().config(),
                PLUGIN_CONFIG_PREFIX + this.metadata().name()));
        List<String> hierarchicalNames = this.metadata().hierarchicalNames();
        LinkedList<String> linkedNames = new LinkedList<>(hierarchicalNames);
        List<String> prefixNames = new ArrayList<>();
        while (!linkedNames.isEmpty()) {
            prefixNames.add(String.join(Config.SEPARATOR_DOT, linkedNames));
            linkedNames.removeLast();
        }
        for (String prefixName : prefixNames) {
            chain.addConfig(new PrefixedConfig(PREFIXED_INHERITED_CONFIG_NAME,
                    this.runtime().config(),
                    PLUGIN_CONFIG_PREFIX + prefixName));
        }
        chain.addConfig(this.runtime().config());
    }

    private void addImportedConfigChain(ConfigChain pluginChain) {
        pluginChain.addConfig(this.importedConfigChain);
    }

    private void addBuiltinConfigChain(ConfigChain pluginChain) {
        ConfigChain chain = new DefaultConfigChain(BUILTIN_CONFIG_CHAIN_NAME);
        pluginChain.addConfigs(chain);
        if (StringUtils.isNotBlank(this.runtime().profile())) {
            String prefix = this.getBuiltinConfigPrefix() + Config.SEPARATOR_HYPHEN + this.runtime().profile();
            this.runtime()
                    .loadEmbeddedConfig(this.resolverOfResources, PROFILE_BUILTIN_CONFIG_NAME, prefix)
                    .ifPresent(chain::addConfig);
        }
        this.runtime()
                .loadEmbeddedConfig(this.resolverOfResources,
                        DEFAULT_BUILTIN_CONFIG_NAME,
                        this.getBuiltinConfigPrefix())
                .ifPresent(chain::addConfig);
    }

    /**
     * 获取内置配置文件的前缀。
     *
     * @return 表示内置配置文件前缀的 {@link String}。
     */
    protected String getBuiltinConfigPrefix() {
        return BUILTIN_CONFIG_PREFIX;
    }

    /**
     * 注册系统提供的 Bean。
     */
    protected void registerSystemBeans() {
        this.container().registry().register(this, PLUGIN_BEAN_NAME);
        this.container().registry().register(this.container(), CONTAINER_BEAN_NAME);
        this.container().registry().register(this.config(), CONFIG_BEAN_NAME);
    }

    /**
     * 扫描 Bean。
     */
    protected void scanBeans() {
        this.container().registry().subscribe(this::onBeanRegistered);
        List<String> configs = this.config().list(BASE_PACKAGE_KEY, String.class);
        if (CollectionUtils.isEmpty(configs)) {
            return;
        }
        Set<String> basePackages = configs.stream()
                .map(basePackage -> StringUtils.split(basePackage, ','))
                .flatMap(Stream::of)
                .map(StringUtils::trim)
                .collect(Collectors.toCollection(HashSet::new));
        Class<?> entryClass = this.runtime().entry();
        PackageScanner scanner =
                this.scanner((packageScanner, clazz) -> this.onClassDetected(packageScanner, clazz, entryClass));
        if (entryClass != null) {
            List<BeanMetadata> beans = this.container().registry().register(entryClass);
            Set<String> entryBasePackages = beans.stream()
                    .map(bean -> this.runtime().resolverOfBeans().packages(bean))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            if (entryBasePackages.isEmpty()) {
                // 在没有任何扫描包路径的设置时，才采用入口类的包路径作为扫描包路径。
                notNull(entryClass.getPackage(),
                        "The default package of entry class is not supported. [entryClass={0}]",
                        entryClass.getName());
                entryBasePackages = Collections.singleton(entryClass.getPackage().getName());
            }
            basePackages.addAll(entryBasePackages);
        }
        scanner.scan(basePackages);
    }

    /**
     * 后置处理配置。
     */
    protected void postProcessConfig() {
        this.container.factories(ConfigDecryptor.class)
                .stream()
                .map(BeanFactory::<ConfigDecryptor>get)
                .forEach(decryptor -> this.config.decrypt(decryptor));
    }

    /**
     * 当插件被初始化完成后被调用的方法。
     */
    protected void onInitialized() {}

    @Override
    public final boolean started() {
        return this.started;
    }

    @Override
    public final void start() {
        this.initialize();
        if (this.started()) {
            return;
        }
        synchronized (this.monitor) {
            if (this.started()) {
                return;
            }
            this.start0();
            this.started = true;
        }
    }

    private void start0() {
        LOG.debug("Start plugin: {}", this.metadata());
        this.onStarting();
        this.container().start();
        this.onStarted();
    }

    /**
     * 当插件被启动前调用的方法。
     */
    protected void onStarting() {
        PluginStartingObserver.notify(this);
    }

    /**
     * 当插件被启动后调用的方法。
     */
    protected void onStarted() {
        PluginStartedObserver.notify(this);
    }

    /**
     * 在 Bean 扫描过程中，但扫描到了类型时回调的方法。
     *
     * @param scanner 表示发现类型的扫描程序的 {@link PackageScanner}。
     * @param clazz 表示发现的类型的 {@link Class}。
     * @param entryClass 表示入口类的类型的 {@link Class}{@code <?>}。
     */
    private void onClassDetected(PackageScanner scanner, Class<?> clazz, Class<?> entryClass) {
        if (Objects.equals(clazz, entryClass)) {
            return;
        }
        List<BeanMetadata> beans = this.container().registry().register(clazz);
        for (BeanMetadata bean : beans) {
            Set<String> basePackages = this.runtime().resolverOfBeans().packages(bean);
            scanner.scan(basePackages);
        }
    }

    /**
     * 当有 Bean 被注册到容器中时回调的方法。
     *
     * @param metadata 表示新注册的 Bean 的元数据的 {@link BeanMetadata}。
     */
    private void onBeanRegistered(BeanMetadata metadata) {
        Set<String> patterns = this.runtime().resolverOfBeans().configurations(metadata);
        for (String pattern : patterns) {
            Resource[] resources;
            try {
                resources = this.resolverOfResources().resolve(pattern);
            } catch (IOException ex) {
                throw new IllegalStateException(StringUtils.format(
                        "Failed to lookup configuration resources. [pattern={0}]",
                        pattern), ex);
            }
            if (resources.length < 1) {
                throw new IllegalStateException(StringUtils.format("No configuration resource found. [pattern={0}]",
                        pattern));
            } else if (resources.length > 1) {
                throw new IllegalStateException(StringUtils.format(
                        "More than 1 configuration resource found. [pattern={0}]",
                        pattern));
            } else {
                ConfigLoadingResult result = this.runtime().loaderOfConfigs().load(resources[0]);
                if (result.loaded()) {
                    this.importedConfigChain.addConfig(result.config());
                } else {
                    throw new IllegalStateException(StringUtils.format(
                            "Failed to load configuration from resource. [resource={0}]",
                            resources[0]));
                }
            }
        }
    }

    @Override
    public final boolean stopped() {
        return !this.started();
    }

    @Override
    public final void stop() {
        if (this.stopped()) {
            return;
        }
        synchronized (this.monitor) {
            if (this.stopped()) {
                return;
            }
            this.stop0();
            this.dispose();
            this.started = false;
        }
    }

    private void stop0() {
        LOG.debug("Stop plugin: {}", this.metadata());
        this.onStopping();
        this.container().stop();
        this.onStopped();
    }

    /**
     * 当插件被停止前调用的方法。
     */
    protected void onStopping() {
        PluginStoppingObserver.notify(this);
    }

    /**
     * 当插件被停止后调用的方法。
     */
    protected void onStopped() {
        PluginStoppedObserver.notify(this);
    }

    @Override
    public PackageScanner scanner(PackageScanner.Callback callback) {
        return new PluginClassLoaderScanner(this.pluginClassLoader(), callback);
    }

    @Override
    public String toString() {
        return this.metadata().toString();
    }
}
