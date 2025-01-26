/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginCollection;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.resource.UrlUtils;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.support.MappedIterator;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.regex.Matcher;
import java.util.stream.Stream;

/**
 * 为 {@link PluginCollection} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-12-05
 */
final class DefaultPluginCollection implements PluginCollection {
    private final Plugin plugin;
    private final List<String> keys;
    private final Map<String, Plugin> plugins;
    private final ReadWriteLock lock;

    /**
     * 使用所属的插件初始化 {@link DefaultPluginCollection} 类的新实例。
     *
     * @param plugin 表示所属插件的 {@link Plugin}。
     */
    DefaultPluginCollection(Plugin plugin) {
        this.plugin = plugin;
        this.keys = new LinkedList<>();
        this.plugins = new HashMap<>();
        this.lock = LockUtils.newReentrantReadWriteLock();
    }

    @Override
    public int size() {
        return LockUtils.synchronize(this.lock.readLock(), this.plugins::size);
    }

    @Override
    public Plugin add(URL location) {
        notNull(location, "The location of plugin to create cannot be null.");
        return LockUtils.synchronize(this.lock.writeLock(), () -> {
            String pluginKey = getPluginKey(location);
            if (this.plugins.containsKey(pluginKey)) {
                throw new IllegalStateException(StringUtils.format(
                        "A plugin with the same key already exists. [location={0}]",
                        location.toExternalForm()));
            }
            Plugin child = JarPluginResolver.INSTANCE.resolve(this.plugin, location);
            this.plugins.put(pluginKey, child);
            this.keys.add(pluginKey);
            return child;
        });
    }

    @Override
    public Plugin remove(URL location) {
        if (location == null) {
            return null;
        } else {
            return LockUtils.synchronize(this.lock.writeLock(), () -> {
                String pluginKey = getPluginKey(location);
                Plugin removedPlugin = this.plugins.remove(pluginKey);
                if (removedPlugin != null) {
                    this.keys.remove(pluginKey);
                }
                return removedPlugin;
            });
        }
    }

    @Override
    public Plugin get(int index) {
        return LockUtils.synchronize(this.lock.readLock(), () -> this.plugins.get(this.keys.get(index)));
    }

    @Override
    public Plugin get(URL location) {
        if (location == null) {
            return null;
        } else {
            return LockUtils.synchronize(this.lock.readLock(), () -> this.plugins.get(getPluginKey(location)));
        }
    }

    @Override
    public boolean contains(URL location) {
        if (location == null) {
            return false;
        } else {
            return LockUtils.synchronize(this.lock.readLock(), () -> this.plugins.containsKey(getPluginKey(location)));
        }
    }

    @Override
    public Stream<Plugin> stream() {
        return this.keys.stream().map(this.plugins::get);
    }

    @Nonnull
    @Override
    public Iterator<Plugin> iterator() {
        return new MappedIterator<>(this.keys.iterator(), this.plugins::get);
    }

    private static String getPluginKey(URL location) {
        return canonicalize(UrlUtils.extractInnerJarNameFromURL(location)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format("No JAR file. [location={0}]",
                        location))));
    }

    private static String canonicalize(String name) {
        Matcher matcher = JAR_NAME_PATTERN.matcher(name);
        if (matcher.matches()) {
            return matcher.group(1) + "-" + matcher.group(2) + Jar.FILE_EXTENSION;
        }
        throw new IllegalStateException(StringUtils.format("Failed to canonicalize plugin name. [name={0}]", name));
    }
}
