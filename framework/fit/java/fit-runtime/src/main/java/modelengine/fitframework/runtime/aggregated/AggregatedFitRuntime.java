/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime.aggregated;

import modelengine.fitframework.plugin.RootPlugin;
import modelengine.fitframework.protocol.jar.JarLocation;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.runtime.shared.SharedUrlClassLoader;
import modelengine.fitframework.runtime.support.AbstractFitRuntime;
import modelengine.fitframework.util.ClassUtils;
import modelengine.fitframework.util.FileUtils;

import java.net.URL;

/**
 * 为 FIT 运行时提供聚合启动场景的实现。
 * <p>聚合启动指的是所有的 Jar 包合并为一个 Jar in Jar 的单独 Jar 包进行启动。</p>
 *
 * @author 梁济时
 * @since 2023-01-31
 */
public final class AggregatedFitRuntime extends AbstractFitRuntime {
    /**
     * 使用入口类和命令行参数来初始化 {@link AggregatedFitRuntime} 类的新实例。
     *
     * @param entry 表示入口类的 {@link Class}{@code <?>}。
     * @param args 表示命令行参数的 {@link String}{@code []}。
     */
    public AggregatedFitRuntime(Class<?> entry, String[] args) {
        super(entry, args);
    }

    @Override
    protected URL locateRuntime() {
        URL domain = ClassUtils.locateOfProtectionDomain(AggregatedFitRuntime.class);
        JarLocation location = JarLocation.parse(domain);
        return FileUtils.urlOf(location.file());
    }

    @Override
    protected SharedUrlClassLoader obtainSharedClassLoader() {
        // FitRuntime 在 fit-api 包中，因此该类的类加载器可以作为共享的类加载器。
        return new SharedUrlClassLoader(new URL[0], FitRuntime.class.getClassLoader());
    }

    @Override
    protected RootPlugin createRootPlugin() {
        return new AggregatedRootPlugin(this);
    }
}
