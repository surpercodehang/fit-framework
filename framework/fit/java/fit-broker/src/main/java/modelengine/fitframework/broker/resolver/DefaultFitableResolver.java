/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.resolver;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.broker.LocalExecutorRepository;
import modelengine.fitframework.broker.LocalExecutorResolver;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.broker.support.LocalFitableExecutor;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.util.GenericableUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * 表示 {@link LocalExecutorResolver} 的默认解析器。
 * <p>该解析器解析没有 {@link modelengine.fitframework.annotation.Fitable} 注解的本地方法。</p>
 *
 * @author 季聿阶
 * @since 2022-07-04
 */
public class DefaultFitableResolver implements LocalExecutorResolver {
    private final BeanContainer container;
    private final LocalExecutorRepository.Registry registry;

    public DefaultFitableResolver(BeanContainer container, LocalExecutorRepository.Registry registry) {
        this.container = notNull(container, "Container of a local proxy resolver cannot be null.");
        this.registry = notNull(registry, "The registry to register local proxy cannot be null.");
    }

    @Override
    public boolean resolve(BeanMetadata metadata, Method method) {
        notNull(metadata, "Metadata of bean to resolve local proxy cannot be null.");
        notNull(method, "Method to resolve local proxy cannot be null.");
        if (this.shouldSkip(method)) {
            return false;
        }
        Method interfaceMethod = ReflectionUtils.getInterfaceMethod(method).orElse(null);
        if (interfaceMethod == null) {
            return false;
        }
        AnnotationMetadata annotations = this.container.runtime().resolverOfAnnotations().resolve(interfaceMethod);
        if (annotations.isAnnotationNotPresent(Genericable.class)) {
            return false;
        }
        Genericable annotation = annotations.getAnnotation(Genericable.class);
        String genericableId = getGenericableId(interfaceMethod, annotation);
        String fitableId = metadata.name();
        UniqueFitableId uniqueFitableId = UniqueFitableId.create(genericableId, fitableId);
        LocalExecutor executor =
                new LocalFitableExecutor(uniqueFitableId, true, metadata, this.beanSupplier(metadata.type()), method);
        this.registry.register(uniqueFitableId, executor);
        return true;
    }

    private static String getGenericableId(Method method, Genericable annotation) {
        return StringUtils.isNotBlank(annotation.id())
                ? annotation.id()
                : GenericableUtils.getGenericableId(method.getDeclaringClass(),
                        method.getName(),
                        method.getParameterTypes());
    }

    private boolean shouldSkip(Method method) {
        if (Modifier.isPrivate(method.getModifiers())) {
            // 私有方法无法被外部调用，因此不需要生成代理。
            return true;
        }
        if (method.getName().startsWith("access$")) {
            // 存在内部类时，会自动生成 access$ 前缀的方法，需要跳过。
            return true;
        }
        return Modifier.isStatic(method.getModifiers());
    }

    private Supplier<Object> beanSupplier(Type type) {
        return () -> this.container.beans().get(type);
    }
}