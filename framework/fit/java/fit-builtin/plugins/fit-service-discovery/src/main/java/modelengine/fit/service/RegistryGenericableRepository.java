/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.broker.ConfigurableFitable;
import modelengine.fitframework.broker.ConfigurableGenericable;
import modelengine.fitframework.broker.FitableFactory;
import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.GenericableFactory;
import modelengine.fitframework.broker.GenericableRepository;
import modelengine.fitframework.broker.UniqueGenericableId;
import modelengine.fitframework.conf.runtime.MatataConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 表示注册中心相关的 {@link Genericable} 仓库。
 * <p>该仓库中仅包含注册中心查询地址的几个服务，因为这几个服务是所有服务的根服务。</p>
 *
 * @author 邬涨财
 * @author 季聿阶
 * @since 2023-04-17
 */
@Order(Order.HIGHEST)
@Component
public class RegistryGenericableRepository implements GenericableRepository {
    private final GenericableFactory genericableFactory;
    private final FitableFactory fitableFactory;

    private final Map<UniqueGenericableId, ConfigurableGenericable> registryGenericables = new HashMap<>();

    /**
     * 构建注册中心相关的服务仓对象。
     *
     * @param genericableFactory 表示服务工厂的 {@link GenericableFactory}。
     * @param fitableFactory 表示服务实现工厂的 {@link FitableFactory}。
     * @param matata 表示 matata 配置的 {@link MatataConfig}。
     */
    public RegistryGenericableRepository(GenericableFactory genericableFactory, FitableFactory fitableFactory,
            MatataConfig matata) {
        this.genericableFactory = notNull(genericableFactory, "The genericable factory cannot be null.");
        this.fitableFactory = notNull(fitableFactory, "The fitable factory cannot be null.");
        this.initializeRegistryGenericables(matata.registry().availableServices());
    }

    private void initializeRegistryGenericables(List<MatataConfig.Registry.AvailableService> services) {
        services.forEach(availableService -> {
            ConfigurableGenericable genericable = this.genericableFactory.create(availableService.genericableId(),
                    availableService.genericableVersion());
            ConfigurableFitable fitable =
                    this.fitableFactory.create(availableService.fitableId(), availableService.fitableVersion());
            fitable.genericable(genericable);
            genericable.fitables(Collections.singletonList(fitable));
            this.registryGenericables.put(availableService.toUniqueId().toUniqueGenericableId(), genericable);
        });
    }

    @Override
    public String name() {
        return "registry";
    }

    @Override
    public Optional<Genericable> get(String id, String version) {
        UniqueGenericableId uniqueId = UniqueGenericableId.create(id, version);
        return Optional.ofNullable(this.registryGenericables.get(uniqueId));
    }

    @Override
    public Map<UniqueGenericableId, Genericable> getAll() {
        return Collections.unmodifiableMap(this.registryGenericables);
    }
}
