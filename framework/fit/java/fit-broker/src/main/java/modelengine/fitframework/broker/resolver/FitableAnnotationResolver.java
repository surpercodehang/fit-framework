/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.resolver;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.broker.LocalExecutorRepository;
import modelengine.fitframework.broker.LocalExecutorResolver;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.broker.support.LocalFitableExecutor;
import modelengine.fitframework.broker.util.AnnotationUtils;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * 为解析 {@link Fitable} 注解所定义的服务实现提供代理解析工具。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-09-24
 */
public class FitableAnnotationResolver implements LocalExecutorResolver {
    /**
     * 该模式仅支持数字、大小写字母以及 '-'、'_'、'*'、'.' 字符且长度在128以内。
     */
    private static final Pattern ID_PATTERN = Pattern.compile("^[\\w\\-\\.\\*]{1,128}+$");
    private static final Logger LOG = Logger.get(FitableAnnotationResolver.class);

    private final BeanContainer container;
    private final LocalExecutorRepository.Registry registry;

    public FitableAnnotationResolver(BeanContainer container, LocalExecutorRepository.Registry registry) {
        this.container = notNull(container, "Container of a local proxy resolver cannot be null.");
        this.registry = notNull(registry, "The registry to register local proxy cannot be null.");
    }

    @Override
    public boolean resolve(BeanMetadata metadata, Method method) {
        notNull(metadata, "Metadata of bean to resolve local proxy cannot be null.");
        notNull(method, "Method to resolve local proxy cannot be null.");
        AnnotationMetadata annotations = metadata.runtime().resolverOfAnnotations().resolve(method);
        Fitable annotation = annotations.getAnnotation(Fitable.class);
        if (annotation == null || StringUtils.isBlank(annotation.id())) {
            return false;
        }
        Optional<String> opGenericableId = this.resolveGenericableId(method, annotation);
        if (!opGenericableId.isPresent()) {
            return false;
        }
        String fitableId = annotation.id();
        if (!ID_PATTERN.matcher(opGenericableId.get()).matches() || !ID_PATTERN.matcher(fitableId).matches()) {
            LOG.error("Genericable id or fitable id does not meet the naming requirements. "
                    + "[genericableId={}, fitableId={}]", opGenericableId.get(), fitableId);
            throw new IllegalStateException("Genericable id or fitable id does not meet the naming requirements: "
                    + "only numbers, uppercase and lowercase letters, and '-', '_', '*', '.' are supported, "
                    + "and the length is less than 128.");
        }
        Supplier<Object> beanSupplier = () -> this.container.beans().get(metadata.type());
        UniqueFitableId uniqueFitableId = UniqueFitableId.create(opGenericableId.get(), fitableId);
        LocalExecutor executor = new LocalFitableExecutor(uniqueFitableId, false, metadata, beanSupplier, method);
        this.registry.register(uniqueFitableId, executor);
        return true;
    }

    private Optional<String> resolveGenericableId(Method method, Fitable annotation) {
        if (StringUtils.isNotBlank(annotation.genericable())) {
            return Optional.of(annotation.genericable());
        }
        return ReflectionUtils.getInterfaceMethod(method).flatMap(AnnotationUtils::getGenericableId);
    }
}