/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.proxy;

import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolvers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * 表示 AOP 代理的工厂帮助类。
 *
 * @author 季聿阶
 * @since 2024-10-28
 */
public class AopProxyFactories {
    private final List<AopProxyFactory> orderedFactories;

    public AopProxyFactories() {
        this.orderedFactories = all();
    }

    /**
     * 获取所有排序好的的 {@link AopProxyFactory}。
     *
     * @return 表示获取到的所有排序好的的 {@link List}{@code <}{@link AopProxyFactory}{@code >}。
     */
    public List<AopProxyFactory> getAll() {
        return this.orderedFactories;
    }

    private static List<AopProxyFactory> all() {
        ServiceLoader<AopProxyFactory> loader =
                ServiceLoader.load(AopProxyFactory.class, AopProxyFactory.class.getClassLoader());
        List<AopProxyFactory> factories = new ArrayList<>();
        loader.forEach(factories::add);
        factories.sort((f1, f2) -> {
            AnnotationMetadataResolver resolver = AnnotationMetadataResolvers.create();
            AnnotationMetadata annotations = resolver.resolve(f1.getClass());
            int order1 =
                    Optional.ofNullable(annotations.getAnnotation(Order.class)).map(Order::value).orElse(Order.MEDIUM);
            annotations = resolver.resolve(f2.getClass());
            int order2 =
                    Optional.ofNullable(annotations.getAnnotation(Order.class)).map(Order::value).orElse(Order.MEDIUM);
            return Integer.compare(order1, order2);
        });
        return factories;
    }
}
