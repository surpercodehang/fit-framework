/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.resource.Resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 为 {@link ConfigLoadingResult} 提供工具方法。
 *
 * @author 梁济时
 * @since 2022-12-16
 */
final class ConfigLoaders {
    /**
     * 生成一个表示成功的结果。
     *
     * @param config 表示加载得到的配置的实例的 {@link Config}。
     * @return 表示加载成功的结果的 {@link ConfigLoadingResult}。
     * @throws IllegalArgumentException {@code config} 为 {@code null}。
     */
    static ConfigLoadingResult success(Config config) {
        return new SuccessResult(config);
    }

    /**
     * 获取表示加载失败的结果。
     *
     * @return 表示加载失败的结果的 {@link ConfigLoadingResult}。
     */
    static ConfigLoadingResult failure() {
        return FailureResult.INSTANCE;
    }

    /**
     * 创建 {@link ConfigLoaderChain} 的默认实现。
     *
     * @return 表示新创建的配置加载程序链的默认实现的 {@link ConfigLoaderChain}。
     */
    static ConfigLoaderChain createChain() {
        return new DefaultChain();
    }

    /**
     * 获取配置加载程序的空实现的实例。
     *
     * @return 表示空实现的 {@link ConfigLoader}。
     */
    static ConfigLoader empty() {
        return Empty.INSTANCE;
    }

    /**
     * 为 {@link ConfigLoadingResult} 提供表示加载成功的实现。
     *
     * @author 梁济时
     * @since 2022-12-16
     */
    private static final class SuccessResult implements ConfigLoadingResult {
        private final Config config;

        /**
         * 使用被成功加载的配置初始化 {@link SuccessResult} 类的新实例。
         *
         * @param config 表示被成功加载的配置的 {@link Config}。
         * @throws IllegalArgumentException {@code config} 为 {@code null}。
         */
        private SuccessResult(Config config) {
            notNull(config, "The loaded config cannot be null.");
            this.config = config;
        }

        @Override
        public boolean loaded() {
            return true;
        }

        @Override
        public Config config() {
            return this.config;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj != null && obj.getClass() == this.getClass()) {
                SuccessResult another = (SuccessResult) obj;
                return Objects.equals(this.config, another.config);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {this.getClass(), this.config});
        }

        @Override
        public String toString() {
            return this.config.toString();
        }
    }

    /**
     * 为 {@link ConfigLoadingResult} 提供表示加载失败的实现。
     *
     * @author 梁济时
     * @since 2022-12-16
     */
    private static final class FailureResult implements ConfigLoadingResult {
        /**
         * 获取类型的唯一实例。
         */
        private static final FailureResult INSTANCE = new FailureResult();

        /**
         * 隐藏默认构造方法，避免单例类型被外部实例化。
         */
        private FailureResult() {}

        @Override
        public boolean loaded() {
            return false;
        }

        @Override
        public Config config() {
            return null;
        }

        @Override
        public String toString() {
            return "Failure";
        }
    }

    /**
     * 为 {@link ConfigLoaderChain} 提供默认实现。
     *
     * @author 梁济时
     * @since 2022-12-16
     */
    private static final class DefaultChain implements ConfigLoaderChain {
        private final List<ConfigLoader> loaders;
        private final List<ConfigLoaderChainListener> listeners;

        /**
         * 初始化 {@link DefaultChain} 类的新实例。
         */
        private DefaultChain() {
            this.loaders = new LinkedList<>();
            this.listeners = new LinkedList<>();
        }

        @Override
        public Set<String> extensions() {
            return this.loaders.stream()
                    .map(ConfigLoader::extensions)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        }

        @Override
        public ConfigLoadingResult load(Resource resource, String name) {
            for (ConfigLoader loader : this.loaders) {
                ConfigLoadingResult result = loader.load(resource, name);
                if (result.loaded()) {
                    return result;
                }
            }
            return ConfigLoadingResult.failure();
        }

        @Override
        public void addLoader(ConfigLoader loader) {
            if (loader == null) {
                return;
            }
            this.loaders.add(loader);
            this.notifyConfigLoaderAdded(loader);
        }

        @Override
        public void removeLoader(ConfigLoader loader) {
            if (loader == null) {
                return;
            }
            if (this.loaders.remove(loader)) {
                this.notifyConfigLoaderRemoved(loader);
            }
        }

        @Override
        public int numberOfLoaders() {
            return this.loaders.size();
        }

        @Override
        public ConfigLoader loaderAt(int index) {
            return this.loaders.get(index);
        }

        @Override
        public void subscribe(ConfigLoaderChainListener listener) {
            if (listener != null) {
                this.listeners.add(listener);
            }
        }

        @Override
        public void unsubscribe(ConfigLoaderChainListener listener) {
            if (listener != null) {
                this.listeners.remove(listener);
            }
        }

        private void notifyConfigLoaderAdded(ConfigLoader loader) {
            for (ConfigLoaderChainListener listener : this.listeners) {
                listener.onConfigLoaderAdded(this, loader);
            }
        }

        private void notifyConfigLoaderRemoved(ConfigLoader loader) {
            for (ConfigLoaderChainListener listener : this.listeners) {
                listener.onConfigLoaderRemoved(this, loader);
            }
        }
    }

    /**
     * 为 {@link ConfigLoader} 提供空实现。
     *
     * @author 梁济时
     * @since 2022-12-20
     */
    private static final class Empty implements ConfigLoader {
        /**
         * 获取类型的唯一实例。
         */
        private static final Empty INSTANCE = new Empty();

        /**
         * 隐藏默认构造方法，避免单例类型被外部实例化。
         */
        private Empty() {}

        @Override
        public Set<String> extensions() {
            return Collections.emptySet();
        }

        @Override
        public ConfigLoadingResult load(Resource resource, String name) {
            return ConfigLoadingResult.failure();
        }
    }
}
