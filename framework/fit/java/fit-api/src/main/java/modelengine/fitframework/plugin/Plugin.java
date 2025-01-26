/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.event.EventPublisher;
import modelengine.fitframework.globalization.StringResource;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.jvm.scan.PackageScanner;
import modelengine.fitframework.jvm.scan.PackageScanner.Callback;
import modelengine.fitframework.resource.ResourceResolver;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.Disposable;

/**
 * 为应用程序提供插件定义。
 *
 * @author 梁济时
 * @since 2022-05-18
 */
public interface Plugin extends Disposable {
    /**
     * 获取插件的元数据。
     *
     * @return 表示插件元数据的 {@link PluginMetadata}。
     */
    PluginMetadata metadata();

    /**
     * 获取插件的配置。
     *
     * @return 表示插件配置的 {@link Config}。
     */
    Config config();

    /**
     * 获取插件隔离所使用的类加载器。
     *
     * @return 表示类加载器的 {@link ClassLoader}。
     */
    ClassLoader pluginClassLoader();

    /**
     * 创建一个携带指定回调方法的包扫描器，该包扫描器仅扫描当前插件的所有 Bean 对象。
     *
     * @param callback 表示指定回调方法的 {@link Callback}。
     * @return 表示包扫描器的 {@link PackageScanner}。
     */
    PackageScanner scanner(Callback callback);

    /**
     * 获取插件所属的运行时环境。
     *
     * @return 表示运行时环境的 {@link FitRuntime}。
     */
    FitRuntime runtime();

    /**
     * 获取父插件。
     *
     * @return 表示父插件的 {@link Plugin}。
     */
    Plugin parent();

    /**
     * 获取子插件的集合。
     *
     * @return 表示子插件的集合的 {@link PluginCollection}。
     */
    PluginCollection children();

    /**
     * 获取一个资源解析程序，用以解析插件中的资源。
     *
     * @return 表示用以解析插件中的资源的解析程序的 {@link ResourceResolver}。
     */
    ResourceResolver resolverOfResources();

    /**
     * 获取插件内部事件的发布程序。
     *
     * @return 表示插件内部事件的发布程序的 {@link EventPublisher}。
     */
    EventPublisher publisherOfEvents();

    /**
     * 获取插件所使用的容器。
     *
     * @return 表示插件所使用的容器的 {@link BeanContainer}。
     */
    BeanContainer container();

    /**
     * 获取插件中管理的所有字符串资源。
     *
     * @return 表示字符串资源的 {@link StringResource}。
     */
    StringResource sr();

    /**
     * 获取一个值，该值指示插件是否已经被初始化。
     *
     * @return 若已经被初始化，则为 {@code true}，否则为 {@code false}。
     */
    boolean initialized();

    /**
     * 初始化插件。
     */
    void initialize();

    /**
     * 获取当前插件是否已经被启动的标志。
     *
     * @return 若插件已经被启动，则返回 {@code true}，否则返回 {@code false}。
     */
    boolean started();

    /**
     * 启动插件。
     */
    void start();

    /**
     * 获取当前插件是否已经被停止的标志。
     *
     * @return 若插件已经被停止，则返回 {@code true}，否则返回 {@code false}。
     */
    boolean stopped();

    /**
     * 停止插件。
     */
    void stop();
}
