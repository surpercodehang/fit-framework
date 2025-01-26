/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.ConfigLoader;
import modelengine.fitframework.event.EventPublisher;
import modelengine.fitframework.ioc.BeanResolver;
import modelengine.fitframework.ioc.DependencyResolver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.RootPlugin;
import modelengine.fitframework.plugin.SharedJarRegistry;
import modelengine.fitframework.resource.ResourceResolver;
import modelengine.fitframework.util.Disposable;

import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * 为FIT应用程序提供运行时环境。
 *
 * @author 梁济时
 * @since 2022-05-18
 */
public interface FitRuntime extends Disposable {
    /**
     * 获取运行环境的入口类。
     *
     * @return 表示入口类的 {@link Class}。
     */
    Class<?> entry();

    /**
     * 获取命令行参数。
     *
     * @return 表示命令行参数的 {@link String}{@code []}。
     */
    String[] argumentsFromCommandLine();

    /**
     * 获取运行环境的位置。
     *
     * @return 表示运行环境位置的 {@link URL}。
     */
    URL location();

    /**
     * 获取运行时的版本。
     *
     * @return 表示运行时版本的 {@link String}。
     */
    String version();

    /**
     * 获取运行环境的启动配置。
     *
     * @return 表示启动配置的 {@link Config}。
     */
    Config config();

    /**
     * 获取根插件。
     *
     * @return 表示根插件的 {@link Plugin}。
     */
    RootPlugin root();

    /**
     * 获取运行环境中包含插件的集合。
     *
     * @return 表示插件集合的 {@link List}{@code <}{@link Plugin}{@code >}。
     */
    List<Plugin> plugins();

    /**
     * 获取指定名字的插件。
     *
     * @param name 表示插件名字的 {@link String}。
     * @return 表示指定名字的插件的 {@link Optional}{@code <}{@link Plugin}{@code >}。
     */
    Optional<Plugin> plugin(String name);

    /*
     * Components associated with a FIT runtime.
     */

    /**
     * 获取公共类的加载程序。
     *
     * @return 表示公共类的加载程序的 {@link ClassLoader}。
     */
    ClassLoader sharedClassLoader();

    /**
     * 为公共类的 JAR 提供注册入口。
     *
     * @return 表示公共类的 JAR 的注册入口的 {@link SharedJarRegistry}。
     */
    SharedJarRegistry registryOfSharedJars();

    /**
     * 获取配置的加载程序。
     *
     * @return 表示配置加载程序的 {@link ConfigLoader}。
     */
    ConfigLoader loaderOfConfigs();

    /**
     * 加载指定前缀的内置的配置。
     *
     * @param resourceResolver 表示用以解析运行时中资源的解析器的 {@link ResourceResolver}。
     * @param name 表示所加载的配置的名称的 {@link String}。
     * @param prefix 表示所加载的配置的文件名前缀的 {@link String}。
     * @return 表示加载到的指定前缀的内置的配置的 {@link Optional}{@code <}{@link Config}{@code >}。
     */
    Optional<Config> loadEmbeddedConfig(ResourceResolver resourceResolver, String name, String prefix);

    /**
     * 获取用以解析运行时中资源的解析程序。
     *
     * @return 表示资源解析程序的 {@link ResourceResolver}。
     */
    ResourceResolver resolverOfResources();

    /**
     * 获取 Bean 的解析程序。
     *
     * @return 表示 Bean 解析程序的 {@link BeanResolver}。
     */
    BeanResolver resolverOfBeans();

    /**
     * 获取依赖的解析程序。
     *
     * @return 表示依赖解析程序的 {@link DependencyResolver}。
     */
    DependencyResolver resolverOfDependencies();

    /**
     * 获取注解的解析程序。
     *
     * @return 表示注解解析程序的 {@link AnnotationMetadataResolver}。
     */
    AnnotationMetadataResolver resolverOfAnnotations();

    /**
     * 获取事件的发布程序。
     * <p>用以在运行环境的所有插件中发布事件。</p>
     *
     * @return 表示事件发布程序的 {@link EventPublisher}。
     */
    EventPublisher publisherOfEvents();

    /**
     * 获取当前应用程序的 Profile。
     *
     * @return 表示当前 Profile的 {@link String}。
     */
    String profile();

    /**
     * 获取一个值，该值指示运行时是否已经启动。
     *
     * @return 若已经启动，则为 {@code true}，否则为 {@code false}。
     */
    boolean started();

    /**
     * 启动运行时环境。
     */
    void start();
}
