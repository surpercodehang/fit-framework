/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Set;

/**
 * {@link ConfigLoaders} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-15
 */
@DisplayName("测试 ConfigLoaders 类")
class ConfigLoadersTest {
    @Nested
    @DisplayName("测试类：SuccessResult")
    class TestSuccessResult {
        @Test
        @DisplayName("提供 ConfigLoadingResult 类 loaded 方法时，返回 true")
        void givenConfigLoadingResultWhenLoadedThenReturnTrue() {
            Config config = mock(Config.class);
            ConfigLoadingResult loaders = ConfigLoaders.success(config);
            assertThat(loaders.loaded()).isTrue();
        }

        @Test
        @DisplayName("提供 ConfigLoadingResult 类 config 方法时，")
        void givenConfigLoadingResultShouldReturnConfig() {
            Config config = mock(Config.class);
            ConfigLoadingResult loaders = ConfigLoaders.success(config);
            assertThat(loaders.config()).isEqualTo(config);
        }

        @Nested
        @DisplayName("测试方法：equals")
        class TestEquals {
            @SuppressWarnings("EqualsWithItself")
            @Test
            @DisplayName("提供 ConfigLoadingResult 类 equals 方法与本身比较时时，返回 true")
            void givenSelfShouldReturnTrue() {
                Config config = mock(Config.class);
                ConfigLoadingResult loaders = ConfigLoaders.success(config);
                assertThat(loaders.equals(loaders)).isTrue();
            }

            @Test
            @DisplayName("提供 ConfigLoadingResult 类 equals 方法与相同配置的新对象比较时，返回 true")
            void givenSameTypeShouldReturnTrue() {
                Config config = mock(Config.class);
                ConfigLoadingResult loaders = ConfigLoaders.success(config);
                ConfigLoadingResult otherLoaders = ConfigLoaders.success(config);
                assertThat(loaders.equals(otherLoaders)).isTrue();
            }

            @SuppressWarnings("EqualsBetweenInconvertibleTypes")
            @Test
            @DisplayName("提供 ConfigLoadingResult 类 equals 方法与其他类型对象比较时，返回 false")
            void givenOtherTypeShouldReturnFalse() {
                Config config = mock(Config.class);
                ConfigLoadingResult loaders = ConfigLoaders.success(config);
                assertThat(Objects.equals(loaders, String.class)).isFalse();
            }
        }

        @Test
        @DisplayName("提供 ConfigLoadingResult 类 hasCode 方法与相同配置的新对象比较时，返回正常结果")
        void givenConfigLoadingResultShouldReturnHasCode() {
            Config config = mock(Config.class);
            ConfigLoadingResult loaders = ConfigLoaders.success(config);
            ConfigLoadingResult otherLoaders = ConfigLoaders.success(config);
            assertThat(loaders.hashCode()).isEqualTo(otherLoaders.hashCode());
        }

        @Test
        @DisplayName("提供 ConfigLoadingResult 类 toString 方法与相同值的新对象比较时，返回正常结果")
        void givenConfigLoadingResultShouldReturnStringValue() {
            Config config = mock(Config.class);
            ConfigLoadingResult loaders = ConfigLoaders.success(config);
            ConfigLoadingResult otherLoaders = ConfigLoaders.success(config);
            assertThat(loaders.toString()).isEqualTo(otherLoaders.toString());
        }
    }

    @Nested
    @DisplayName("测试类：FailureResult")
    class TestFailureResult {
        @Test
        @DisplayName("提供 ConfigLoadingResult 类 loaded 方法时，返回 false")
        void givenConfigLoadingResultWhenLoadedThenReturnTrue() {
            ConfigLoadingResult loaders = ConfigLoaders.failure();
            assertThat(loaders.loaded()).isFalse();
        }

        @Test
        @DisplayName("提供 ConfigLoadingResult 类 config 方法时，")
        void givenConfigLoadingResultShouldReturnConfig() {
            ConfigLoadingResult loaders = ConfigLoaders.failure();
            assertThat(loaders.config()).isNull();
        }

        @Test
        @DisplayName("提供 ConfigLoadingResult 类 toString 方法与相同值的新对象比较时，返回正常结果")
        void givenConfigLoadingResultShouldReturnStringValue() {
            ConfigLoadingResult loaders = ConfigLoaders.failure();
            assertThat(loaders.toString()).isEqualTo("Failure");
        }
    }

    @Nested
    @DisplayName("测试类：Empty")
    class TestEmpty {
        @Test
        @DisplayName("提供 ConfigLoaders 类 extensions 方法时，返回空")
        void givenConfigLoadersShouldReturnEmpty() {
            ConfigLoader loaders = ConfigLoaders.empty();
            Set<String> extensions = loaders.extensions();
            assertThat(extensions).isEmpty();
        }

        @Test
        @DisplayName("提供 ConfigLoaders 类 load 方法时，返回 FailureResult 信息")
        void givenConfigLoadersShouldReturnFailureResult() {
            ConfigLoader loaders = ConfigLoaders.empty();
            ConfigLoadingResult load = loaders.load(null, null);
            assertThat(load.config()).isNull();
        }
    }

    @Nested
    @DisplayName("测试类：DefaultChain")
    class TestDefaultChain {
        @Test
        @DisplayName("提供 DefaultChain 类 extensions 方法时，返回空")
        void givenDefaultChainShouldReturnEmpty() {
            ConfigLoaderChain chain = ConfigLoaders.createChain();
            assertThat(chain.extensions()).isEmpty();
        }

        @Test
        @DisplayName("提供 DefaultChain 类 load 方法加载空配置时，返回 FailureResult 信息")
        void givenDefaultChainWhenLoadNullThenReturnFailureResult() {
            ConfigLoaderChain chain = ConfigLoaders.createChain();
            chain.addLoader(null);
            ConfigLoadingResult load = chain.load(null, null);
            assertThat(load.config()).isNull();
        }

        @Test
        @DisplayName("提供 DefaultChain 类 load 方法加载正常配置时，返回 SuccessResult 信息")
        void givenDefaultChainWhenLoadNormalThenReturnSuccessResult() {
            ConfigLoaderChain chain = ConfigLoaders.createChain();
            ConfigLoader loader = mock(ConfigLoader.class);
            chain.addLoader(loader);
            Config config = mock(Config.class);
            when(loader.load(null, null)).thenReturn(ConfigLoaders.success(config));
            ConfigLoadingResult load = chain.load(null, null);
            assertThat(load.loaded()).isTrue();
        }

        @Test
        @DisplayName("提供 DefaultChain 类删除配置时，返回空")
        void givenDefaultChainWhenRemoveConfigThenReturnEmpty() {
            ConfigLoaderChain chain = ConfigLoaders.createChain();
            ConfigLoader loader = mock(ConfigLoader.class);
            chain.addLoader(loader);
            ConfigLoader configLoader = chain.loaderAt(0);
            chain.removeLoader(null);
            chain.removeLoader(configLoader);
            assertThat(chain.numberOfLoaders()).isEqualTo(0);
        }

        @Test
        @DisplayName("提供 DefaultChain 类 subscribe 方法时，返回 true")
        void givenDefaultChainWhenSubscribeThenReturnTrue() {
            ConfigLoaderChain chain = ConfigLoaders.createChain();
            ConfigLoaderChainListenerTest listener = new ConfigLoaderChainListenerTest();
            chain.subscribe(listener);
            ConfigLoader loader = mock(ConfigLoader.class);
            chain.addLoader(loader);
            assertThat(listener.isFlag()).isTrue();
            chain.unsubscribe(listener);
            chain.removeLoader(loader);
            assertThat(listener.isFlag()).isTrue();
        }
    }

    private static class ConfigLoaderChainListenerTest implements ConfigLoaderChainListener {
        private boolean isFlag = false;

        @Override
        public void onConfigLoaderAdded(ConfigLoaderChain chain, ConfigLoader loader) {
            this.isFlag = true;
        }

        @Override
        public void onConfigLoaderRemoved(ConfigLoaderChain chain, ConfigLoader loader) {
            this.isFlag = false;
        }

        public boolean isFlag() {
            return this.isFlag;
        }
    }
}