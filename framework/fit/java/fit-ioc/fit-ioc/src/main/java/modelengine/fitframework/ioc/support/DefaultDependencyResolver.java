/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanFactoryOrderComparator;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.DependencyResolver;
import modelengine.fitframework.ioc.DependencyResolvingResult;
import modelengine.fitframework.ioc.UnresolvableDependencyException;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 为 {@link DependencyResolver} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-05-10
 */
public class DefaultDependencyResolver implements DependencyResolver {
    @Override
    public DependencyResolvingResult resolve(@Nonnull BeanMetadata source, String name, @Nonnull Type type,
            @Nonnull AnnotationMetadata annotations) {
        try {
            if (StringUtils.isBlank(name)) {
                return this.dependency(source.container(), type);
            } else {
                return this.dependency(source.container(), name);
            }
        } catch (Exception e) {
            throw new UnresolvableDependencyException(StringUtils.format(
                    "Dependency unresolvable. [source={0}, dependency.name={1}, dependency.type={2}]",
                    source.name(),
                    name,
                    type), e);
        }
    }

    /**
     * 获取指定名称的 Bean。
     *
     * @param container 表示 Bean 所属的容器的 {@link BeanContainer}。
     * @param name 表示 Bean 的名称的 {@link String}。
     * @return 若存在该 Bean，则为表示 Bean 实例的 {@link Object}，否则为 {@code null}。
     */
    private DependencyResolvingResult dependency(BeanContainer container, String name) {
        return container.lookup(name)
                .map(factory -> DependencyResolvingResult.success(factory::get))
                .orElse(DependencyResolvingResult.failure());
    }

    /**
     * 获取指定类型的 Bean。
     *
     * @param container 表示 Bean 所属的容器的 {@link BeanContainer}。
     * @param type 表示 Bean 的类型的 {@link Type}。
     * @return 若存在该 Bean，则为表示 Bean 实例的 {@link Object}，否则为 {@code null}。
     */
    private DependencyResolvingResult dependency(BeanContainer container, Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterized = (ParameterizedType) type;
            if (parameterized.getRawType() == Map.class && parameterized.getActualTypeArguments()[0] == String.class) {
                return DependencyResolvingResult.success(() -> this.mapOfBeans(container,
                        parameterized.getActualTypeArguments()[1]));
            }
            if (parameterized.getRawType() == List.class) {
                return DependencyResolvingResult.success(() -> this.listOfBeans(container,
                        parameterized.getActualTypeArguments()[0]));
            }
        }
        return container.lookup(type)
                .map(factory -> DependencyResolvingResult.success(factory::get))
                .orElse(DependencyResolvingResult.failure());
    }

    private Map<String, Object> mapOfBeans(BeanContainer container, Type type) {
        Map<String, Object> beans = new LinkedHashMap<>();
        container.all(type)
                .stream()
                .sorted(BeanFactoryOrderComparator.INSTANCE)
                .forEach(factory -> beans.put(factory.metadata().name(), factory.get()));
        return beans;
    }

    private List<Object> listOfBeans(BeanContainer container, Type type) {
        return container.all(type)
                .stream()
                .sorted(BeanFactoryOrderComparator.INSTANCE)
                .map(BeanFactory::get)
                .collect(Collectors.toList());
    }
}
