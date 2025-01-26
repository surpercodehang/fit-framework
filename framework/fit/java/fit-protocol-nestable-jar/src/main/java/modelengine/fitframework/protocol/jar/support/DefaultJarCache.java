/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarCache;
import modelengine.fitframework.protocol.jar.JarFormatException;
import modelengine.fitframework.protocol.jar.JarLocation;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.Permission;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 为 {@link JarCache} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-02-21
 */
public final class DefaultJarCache implements JarCache {
    /**
     * 表示当前类型的唯一实例。
     */
    public static final DefaultJarCache INSTANCE = new DefaultJarCache();

    private final Map<JarLocation, WeakReference<CachedJar>> cache;

    private DefaultJarCache() {
        this.cache = new WeakHashMap<>();
    }

    @Override
    public Jar get(JarLocation location) throws IOException {
        return this.getOrLoad(location);
    }

    private CachedJar getOrLoad(JarLocation location) throws IOException {
        CachedJar jar;
        WeakReference<CachedJar> ref;
        boolean created = false;
        if ((ref = this.cache.get(location)) == null || (jar = ref.get()) == null) {
            synchronized (this.cache) {
                if ((ref = this.cache.get(location)) == null || (jar = ref.get()) == null) {
                    jar = new CachedJar(location);
                    ref = new WeakReference<>(jar);
                    this.cache.put(location, ref);
                    created = true;
                }
            }
        }
        jar.initialize();
        if (created) {
            synchronized (this.cache) {
                // 使用归档件中持有的位置信息实例作为键，确保在归档件实例存在时，键具有强引用，避免被释放。
                this.cache.remove(jar.location());
                this.cache.put(jar.location(), new WeakReference<>(jar));
            }
        }
        return jar;
    }

    private Jar load(JarLocation location) throws IOException {
        if (location.nests().isEmpty()) {
            return DataBlockJar.load(location.file());
        }
        List<String> nests = location.nests();
        int last = nests.size() - 1;
        JarLocation parentLocation = JarLocation.custom().file(location.file()).nests(nests.subList(0, last)).build();
        CachedJar parent = this.getOrLoad(parentLocation);
        Jar.Entry entry = parent.jar.entries().get(nests.get(last));
        if (entry == null) {
            throw new JarFormatException(String.format(Locale.ROOT, "JAR not found. [location=%s]", location));
        }
        return entry.asJar();
    }

    private final class CachedJar implements Jar {
        private final JarLocation location;
        private volatile Jar jar;
        private volatile Jar.EntryCollection entries;
        private final Object monitor;

        private CachedJar(JarLocation location) {
            this.location = location;
            this.jar = null;
            this.monitor = new byte[0];
        }

        private void initialize() throws IOException {
            if (this.jar != null) {
                return;
            }
            synchronized (this.monitor) {
                if (this.jar == null) {
                    this.jar = DefaultJarCache.this.load(this.location);
                    this.entries = new CachedJarEntryCollection(this, this.jar.entries());
                }
            }
        }

        @Override
        public JarLocation location() {
            return this.jar.location();
        }

        @Override
        public Permission permission() {
            return this.jar.permission();
        }

        @Override
        public EntryCollection entries() {
            return this.entries;
        }

        @Override
        public String comment() {
            return this.jar.comment();
        }

        @Override
        public String toString() {
            return this.location.toString();
        }
    }

    private final class CachedJarEntry extends EmptyJarEntryDecorator {
        private final Jar jar;

        private CachedJarEntry(Jar jar, Jar.Entry entry) {
            super(entry);
            this.jar = jar;
        }

        @Override
        public Jar jar() {
            return this.jar;
        }

        @Override
        public Jar asJar() throws IOException {
            return DefaultJarCache.this.get(this.location().asJar());
        }
    }

    private final class CachedJarEntryCollection extends EmptyJarEntryCollectionDecorator {
        private final Jar cachedJar;

        private CachedJarEntryCollection(Jar cachedJar, Jar.EntryCollection entries) {
            super(entries);
            this.cachedJar = cachedJar;
        }

        @Override
        protected Jar.Entry decorate(Jar.Entry entry) {
            return new CachedJarEntry(this.cachedJar, entry);
        }
    }
}
