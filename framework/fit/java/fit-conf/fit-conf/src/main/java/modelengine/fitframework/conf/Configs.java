/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

import java.util.ServiceLoader;

/**
 * 为 {@link Config} 提供工具方法。
 *
 * @author 梁济时
 * @since 2022-08-16
 */
public final class Configs {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Configs() {}

    /**
     * 从指定类加载器中加载配置加载器。
     *
     * @param classLoader 表示指定类加载器的 {@link ClassLoader}。
     * @return 表示加载后的配置加载器的 {@link ConfigLoader}。
     */
    public static ConfigLoader load(ClassLoader classLoader) {
        Iterable<ConfigLoader> loaders = ServiceLoader.load(ConfigLoader.class, classLoader);
        ConfigLoaderChain chain = ConfigLoaderChain.createDefault();
        for (ConfigLoader loader : loaders) {
            chain.addLoader(loader);
        }
        if (chain.numberOfLoaders() > 1) {
            return chain;
        } else if (chain.numberOfLoaders() < 1) {
            return ConfigLoader.empty();
        } else {
            return chain.loaderAt(0);
        }
    }
}
