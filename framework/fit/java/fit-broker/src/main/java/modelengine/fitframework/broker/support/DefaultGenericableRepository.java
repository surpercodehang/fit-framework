/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.broker.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Degradation;
import modelengine.fitframework.broker.ConfigurableFitable;
import modelengine.fitframework.broker.ConfigurableGenericable;
import modelengine.fitframework.broker.FitableFactory;
import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.GenericableFactory;
import modelengine.fitframework.broker.GenericableRepository;
import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.broker.UniqueGenericableId;
import modelengine.fitframework.broker.event.LocalExecutorRegisteredObserver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolvers;
import modelengine.fitframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示 {@link GenericableRepository} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-03-08
 */
public class DefaultGenericableRepository implements GenericableRepository, LocalExecutorRegisteredObserver {
    private final String name;
    private final Map<UniqueGenericableId, ConfigurableGenericable> genericables = new ConcurrentHashMap<>();
    private final GenericableFactory genericableFactory;
    private final FitableFactory fitableFactory;

    /**
     * 使用指定的名称、泛服务工厂和实现工厂初始化 {@link DefaultGenericableRepository} 的新实例。
     *
     * @param name 表示名称的 {@link String}。
     * @param genericableFactory 表示泛服务工厂的 {@link GenericableFactory}。
     * @param fitableFactory 表示实现工厂的 {@link FitableFactory}。
     * @throws IllegalArgumentException 当 {@code name}、{@code genericableFactory} 或 {@code fitableFactory} 为
     * {@code null}，或 {@code name} 为空白字符串时。
     */
    public DefaultGenericableRepository(String name, GenericableFactory genericableFactory,
            FitableFactory fitableFactory) {
        this.name = notBlank(name, "The genericable repository name cannot be blank.");
        this.genericableFactory = notNull(genericableFactory, "The genericable factory cannot be null.");
        this.fitableFactory = notNull(fitableFactory, "The fitable factory cannot be null.");
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Optional<Genericable> get(String id, String version) {
        notBlank(id, "The genericable id cannot be blank.");
        notBlank(version, "The genericable version cannot be blank.");
        UniqueGenericableId uniqueGenericableId = UniqueGenericableId.create(id, version);
        return Optional.ofNullable(this.genericables.get(uniqueGenericableId));
    }

    @Override
    public Map<UniqueGenericableId, Genericable> getAll() {
        return Collections.unmodifiableMap(this.genericables);
    }

    @Override
    public void onLocalExecutorRegistered(UniqueFitableId id, LocalExecutor executor) {
        notNull(id, "The unique fitable id cannot be null.");
        notNull(executor, "The local fitable executor cannot be null.");
        UniqueGenericableId uniqueGenericableId =
                UniqueGenericableId.create(id.genericableId(), id.genericableVersion());
        ConfigurableGenericable genericable =
                this.genericables.computeIfAbsent(uniqueGenericableId, key -> this.createGenericable(executor, key));
        ConfigurableFitable fitable =
                this.fitableFactory.create(id.fitableId(), id.fitableVersion()).aliases(executor.aliases());
        fitable.genericable(genericable);
        AnnotationMetadata annotations = AnnotationMetadataResolvers.create().resolve(executor.method());
        if (annotations.isAnnotationPresent(Degradation.class)) {
            fitable.degradationFitableId(annotations.getAnnotation(Degradation.class).to());
        }
        if (executor.isMicro() && executor.metadata().preferred()) {
            genericable.route(fitable.id());
        }
        genericable.appendFitable(fitable);
    }

    private ConfigurableGenericable createGenericable(LocalExecutor executor, UniqueGenericableId id) {
        Method genericableMethod = ReflectionUtils.getInterfaceMethod(executor.method()).orElse(executor.method());
        return this.genericableFactory.create(id)
                .name(ReflectionUtils.toLongString(genericableMethod))
                .method(genericableMethod);
    }
}
