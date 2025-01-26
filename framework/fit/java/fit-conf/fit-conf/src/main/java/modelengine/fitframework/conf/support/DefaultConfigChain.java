/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.support;

import static modelengine.fitframework.inspection.Validation.between;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.ConfigChain;
import modelengine.fitframework.conf.ConfigChainListener;
import modelengine.fitframework.conf.ConfigDecryptor;
import modelengine.fitframework.conf.ConfigValueSupplier;
import modelengine.fitframework.conf.ModifiableConfig;
import modelengine.fitframework.conf.ModifiableConfigListener;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link Config} 提供组合模式的实现。
 *
 * @author 梁济时
 * @since 2022-05-18
 */
public class DefaultConfigChain extends AbstractConfig implements ConfigChain {
    private final List<Config> configs;
    private final List<ConfigChainListener> listeners;
    private final ConfigListener configListener;

    private volatile List<Config> currentConfigs;
    private volatile List<ConfigChainListener> currentListeners;

    /**
     * 使用配置的名称初始化 {@link DefaultConfigChain} 类的新实例。
     *
     * @param name 表示配置的名称的 {@link String}。
     */
    public DefaultConfigChain(String name) {
        super(name);
        this.configs = new ArrayList<>();
        this.listeners = new LinkedList<>();
        this.configListener = this.new ConfigListener();
    }

    private List<Config> configs() {
        List<Config> actual;
        if ((actual = this.currentConfigs) == null) {
            synchronized (this.configs) {
                if ((actual = this.currentConfigs) == null) {
                    this.currentConfigs = new ArrayList<>(this.configs);
                    actual = this.currentConfigs;
                }
            }
        }
        return actual;
    }

    private List<ConfigChainListener> listeners() {
        List<ConfigChainListener> actual;
        if ((actual = this.currentListeners) == null) {
            synchronized (this.listeners) {
                if ((actual = this.currentListeners) == null) {
                    this.currentListeners = new ArrayList<>(this.listeners);
                    actual = this.currentListeners;
                }
            }
        }
        return actual;
    }

    @Override
    public Set<String> keys() {
        return this.configs().stream().map(Config::keys).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    @Override
    protected Object getWithCanonicalKey(String key) {
        Object result = null;
        for (Config config : this.configs()) {
            Object value = ConfigValueSupplier.get(config, key);
            result = merge(result, value);
        }
        return result;
    }

    @Override
    public void decrypt(@Nonnull ConfigDecryptor decryptor) {
        this.configs().forEach(config -> config.decrypt(decryptor));
    }

    @Override
    public int addConfig(Config config) {
        if (config == null) {
            return -1;
        }
        int index;
        synchronized (this.configs) {
            index = this.configs.size();
            this.configs.add(config);
            this.currentConfigs = null;
        }
        this.configListener.subscribe(config);
        this.notifyConfigAdded(config);
        return index;
    }

    @Override
    public void insertConfig(int index, Config config) {
        synchronized (this.configs) {
            between(index,
                    0,
                    this.configs.size(),
                    "The index to insert config is out of bounds. [index={0}, maximum={1}]",
                    index,
                    this.configs.size());
            notNull(config, "The config to insert cannot be null.");
            this.configs.add(index, config);
            this.currentConfigs = null;
        }
        this.configListener.subscribe(config);
        this.notifyConfigAdded(config);
    }

    @Override
    public void addConfigs(Config... configs) {
        if (configs == null) {
            return;
        }
        List<Config> actual = Stream.of(configs).filter(Objects::nonNull).collect(Collectors.toList());
        if (actual.isEmpty()) {
            return;
        }
        synchronized (this.configs) {
            this.configs.addAll(actual);
            this.currentConfigs = null;
        }
        for (Config config : actual) {
            this.configListener.subscribe(config);
            this.notifyConfigAdded(config);
        }
    }

    @Override
    public void removeConfig(Config config) {
        if (config == null) {
            return;
        }
        synchronized (this.configs) {
            if (!this.configs.remove(config)) {
                return;
            }
            this.currentConfigs = null;
        }
        this.configListener.unsubscribe(config);
        this.notifyConfigRemoved(config);
    }

    @Override
    public void clear() {
        List<Config> clearingConfigs;
        synchronized (this.configs) {
            clearingConfigs = new ArrayList<>(this.configs);
            this.configs.clear();
            this.currentConfigs = null;
        }
        for (Config config : clearingConfigs) {
            this.configListener.unsubscribe(config);
            this.notifyConfigRemoved(config);
        }
    }

    @Override
    public int numberOfConfigs() {
        return this.configs().size();
    }

    @Override
    public Config configAt(int index) {
        return this.configs().get(index);
    }

    @Override
    public void subscribe(ConfigChainListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (this.listeners) {
            this.listeners.add(listener);
            this.currentListeners = null;
        }
    }

    @Override
    public void unsubscribe(ConfigChainListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (this.listeners) {
            this.listeners.remove(listener);
            this.currentListeners = null;
        }
    }

    private class ConfigListener implements ModifiableConfigListener {
        @Override
        public void onValueChanged(ModifiableConfig config, String key) {
            DefaultConfigChain.this.notifyConfigChanged(config, key);
        }

        private void subscribe(Config config) {
            if (config instanceof ModifiableConfig) {
                ((ModifiableConfig) config).subscribe(this);
            }
        }

        private void unsubscribe(Config config) {
            if (config instanceof ModifiableConfig) {
                ((ModifiableConfig) config).unsubscribe(this);
            }
        }
    }

    /**
     * 通知有配置被添加到链中。
     *
     * @param config 表示已添加的配置的 {@link Config}。
     */
    protected void notifyConfigAdded(Config config) {
        List<ConfigChainListener> configChainListeners = this.listeners();
        for (ConfigChainListener listener : configChainListeners) {
            listener.onConfigAdded(this, config);
        }
    }

    /**
     * 通知有配置被从链中移除。
     *
     * @param config 表示已移除的配置的 {@link Config}。
     */
    protected void notifyConfigRemoved(Config config) {
        List<ConfigChainListener> configChainListeners = this.listeners();
        for (ConfigChainListener listener : configChainListeners) {
            listener.onConfigRemoved(this, config);
        }
    }

    /**
     * 通知有配置的值发生变化。
     *
     * @param config 表示值发生变化的配置的 {@link Config}。
     * @param key 表示发生变化的配置值的键的 {@link String}。
     */
    protected void notifyConfigChanged(ModifiableConfig config, String key) {
        List<ConfigChainListener> configChainListeners = this.listeners();
        for (ConfigChainListener listener : configChainListeners) {
            listener.onConfigChanged(this, config, key);
        }
    }

    private static Object merge(Object value1, Object value2) {
        if (value1 == null) {
            return value2;
        } else if (value2 == null) {
            return value1;
        } else {
            Map<Object, Object> map1 = ObjectUtils.cast(ObjectUtils.as(value1, Map.class));
            Map<Object, Object> map2 = ObjectUtils.cast(ObjectUtils.as(value2, Map.class));
            if (map1 != null && map2 != null) {
                return mergeMaps(map1, map2);
            } else if (map1 != null || map2 != null) {
                throw new IllegalStateException(StringUtils.format(
                        "Cannot merge an object value and a non-object value. [value1={0}, value2={1}]",
                        value1,
                        value2));
            } else {
                return mergeValues(value1, value2);
            }
        }
    }

    private static Map<Object, Object> mergeMaps(Map<Object, Object> map1, Map<Object, Object> map2) {
        Set<Object> keys = new HashSet<>();
        keys.addAll(map1.keySet());
        keys.addAll(map2.keySet());
        Map<Object, Object> merged = new HashMap<>(keys.size());
        for (Object key : keys) {
            merged.put(key, merge(map1.get(key), map2.get(key)));
        }
        return merged;
    }

    private static Object mergeValues(Object value1, Object value2) {
        List<Object> list1 = Optional.ofNullable(ObjectUtils.<List<Object>>cast(ObjectUtils.as(value1, List.class)))
                .orElseGet(() -> Collections.singletonList(value1));
        List<Object> list2 = Optional.ofNullable(ObjectUtils.<List<Object>>cast(ObjectUtils.as(value2, List.class)))
                .orElseGet(() -> Collections.singletonList(value2));
        List<Object> results = new ArrayList<>(list1.size() + list2.size());
        results.addAll(list1);
        results.addAll(list2);
        return results;
    }
}
