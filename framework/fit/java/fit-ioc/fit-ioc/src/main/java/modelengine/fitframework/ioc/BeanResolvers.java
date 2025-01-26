/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import modelengine.fitframework.ioc.support.BeanResolverComposite;
import modelengine.fitframework.ioc.support.DefaultBeanResolver;
import modelengine.fitframework.ioc.support.PartialJsr250BeanResolver;

import java.lang.reflect.Type;
import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * 为 {@link BeanResolver} 提供工具方法。
 *
 * @author 梁济时
 * @since 2022-08-16
 */
public final class BeanResolvers {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private BeanResolvers() {}

    /**
     * 使用工厂所创建 Bean 的类型和创建方法创建 {@link BeanResolver.Factory} 的默认实现。
     *
     * @param type 表示 Bean 的实际类型的 {@link Type}。
     * @param mapper 表示通过原始Bean创建目标Bean的方法的 {@link Function}。
     * @return 表示创建出来的工厂的 {@link BeanResolver.Factory}。
     * @throws IllegalArgumentException 当 {@code type} 或 {@code mapper} 为 {@code null} 时。
     */
    public static BeanResolver.Factory factory(Type type, Function<Object, Object> mapper) {
        return new DefaultBeanResolver.Factory(type, mapper);
    }

    /**
     * 从指定的类加载程序中加载 Bean 解析程序的实例。
     *
     * @param classLoader 表示指定类加载器的 {@link ClassLoader}。
     * @return 表示加载到的 Bean 解析程序的 {@link BeanResolver}。
     */
    public static BeanResolver load(ClassLoader classLoader) {
        BeanResolverComposite composite = new BeanResolverComposite();
        composite.addAll(ServiceLoader.load(BeanResolver.class, classLoader));
        composite.add(new PartialJsr250BeanResolver());
        composite.add(new DefaultBeanResolver());
        if (composite.size() > 1) {
            return composite;
        } else {
            return composite.get(0);
        }
    }
}
