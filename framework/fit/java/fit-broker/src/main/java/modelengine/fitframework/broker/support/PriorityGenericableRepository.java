/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.GenericableRepository;
import modelengine.fitframework.broker.UniqueGenericableId;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolvers;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 表示带优先级的 {@link Genericable} 仓库。
 *
 * @author 季聿阶
 * @since 2023-03-25
 */
public class PriorityGenericableRepository implements GenericableRepository {
    private final List<GenericableRepository> repositories;

    public PriorityGenericableRepository(List<GenericableRepository> repositories) {
        this.repositories =
                repositories.stream().sorted(Comparator.comparingInt(this::getOrder)).collect(Collectors.toList());
    }

    private int getOrder(GenericableRepository repository) {
        AnnotationMetadataResolver resolver = AnnotationMetadataResolvers.create();
        AnnotationMetadata annotations = resolver.resolve(repository.getClass());
        return Optional.ofNullable(annotations.getAnnotation(Order.class)).map(Order::value).orElse(Order.MEDIUM);
    }

    @Override
    public String name() {
        return "priority";
    }

    @Override
    public Optional<Genericable> get(String id, String version) {
        for (GenericableRepository repository : this.repositories) {
            Optional<Genericable> genericable = repository.get(id, version);
            if (genericable.isPresent()) {
                return genericable;
            }
        }
        return Optional.empty();
    }

    @Override
    public Map<UniqueGenericableId, Genericable> getAll() {
        Map<UniqueGenericableId, Genericable> genericables = new TreeMap<>();
        for (GenericableRepository repository : this.repositories) {
            genericables.putAll(repository.getAll());
        }
        return genericables;
    }
}
