/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import modelengine.fitframework.broker.resolver.DefaultFitableResolver;
import modelengine.fitframework.broker.resolver.FitableAnnotationResolver;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表示本地执行器的解析器。
 *
 * @author 梁济时
 * @since 2020-09-03
 */
@FunctionalInterface
public interface LocalExecutorResolver {
    /**
     * 从一个 Bean 的方法中解析服务实现的本地代理并添加到缓存中。
     *
     * @param metadata 表示 Bean 的摘要信息的 {@link BeanMetadata}。
     * @param method 表示正在解析的 Bean 中的方法的 {@link Method}。
     * @return 若成功解析到代理，则为 {@code true}，否则为 {@code false}。
     * @throws IllegalArgumentException {@code factory}、{@code component}、{@code method} 或 {@code cache} 为
     * {@code null}。
     * @throws IllegalArgumentException 解析到的Genericable类型并非有效的Genericable定义。
     * @throws IllegalStateException Genericable类型中定义了多个Genericable方法。
     */
    boolean resolve(BeanMetadata metadata, Method method);

    /**
     * 表示根容器的本地执行器的解析器。
     */
    @FunctionalInterface
    interface RootContainer {
        /**
         * 解析指定的 Bean 的元数据，获得所有本地执行器，并添加到 FIT 跟容器中。
         *
         * @param metadata 表示指定 Bean 元数据的 {@link BeanMetadata}。
         */
        void resolveAll(BeanMetadata metadata);
    }

    /**
     * 获取一个工厂，用以创建本地代理解析工具的实例。
     *
     * @return 表示用以创建本地代理解析工具的工厂的 {@link Factory}。
     */
    static Factory factory() {
        // 该工厂组合模型中存在优先级，因此组合顺序是确定的。
        return Factory.combine(FitableAnnotationResolver::new, DefaultFitableResolver::new);
    }

    /**
     * 为 {@link LocalExecutorResolver} 提供工厂。
     *
     * @author 梁济时
     * @since 2020-09-24
     */
    @FunctionalInterface
    interface Factory {
        /**
         * 使用组件容器及用以存储代理的仓库初始化一个本地代理解析工具工厂的新实例。
         *
         * @param container 表示组件容器的 {@link BeanContainer}。
         * @param registry 表示本地代理仓库的 {@link LocalExecutorRepository.Registry}。
         * @return 表示新实例化的本地代理解析工具的 {@link LocalExecutorResolver}。
         */
        LocalExecutorResolver create(BeanContainer container, LocalExecutorRepository.Registry registry);

        /**
         * 将多个工厂合并成为一个工厂。
         *
         * @param factories 表示待合并的工厂的 {@link Factory}{@code []}。
         * @return 表示合并后的工厂的 {@link Factory}。
         */
        static Factory combine(Factory... factories) {
            List<Factory> actualFactories = Arrays.stream(ObjectUtils.nullIf(factories, new Factory[0]))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return (container, store) -> {
                LocalExecutorResolver[] resolvers = actualFactories.stream()
                        .map(factory -> factory.create(container, store))
                        .toArray(LocalExecutorResolver[]::new);
                return LocalExecutorResolver.combine(resolvers);
            };
        }
    }

    /**
     * 将多个解析成合并成为一个解析器。
     * <p>解析过程中将顺序使用各解析器进行解析，若某个解析器解析成功，解析过程即结束。</p>
     *
     * @param resolvers 表示待合并的解析器的 {@link LocalExecutorResolver}{@code []}。
     * @return 表示合并后的解析器的 {@link LocalExecutorResolver}。
     */
    static LocalExecutorResolver combine(LocalExecutorResolver... resolvers) {
        if (resolvers == null) {
            return (supplier, method) -> false;
        }
        List<LocalExecutorResolver> actualResolvers =
                Arrays.stream(resolvers).filter(Objects::nonNull).collect(Collectors.toList());
        return (brief, method) -> {
            for (LocalExecutorResolver resolver : actualResolvers) {
                if (resolver.resolve(brief, method)) {
                    return true;
                }
            }
            return false;
        };
    }
}
