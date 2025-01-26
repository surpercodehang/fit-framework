/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.serialization;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.serialization.MessageSerializer;
import modelengine.fitframework.broker.SerializationService;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 为 {@link SerializationService} 提供默认实现。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-11-12
 */
public class DefaultSerializationService implements SerializationService {
    private final BeanContainer container;
    private final Map<Method, List<Integer>> cachedSupportedFormatsMapping = new HashMap<>();

    public DefaultSerializationService(BeanContainer container) {
        this.container = notNull(container, "The bean container cannot be null.");
    }

    @Override
    public Optional<MessageSerializer> get(int format) {
        return this.getMessageSerializers().stream().filter(serializer -> serializer.getFormat() == format).findFirst();
    }

    @Override
    public List<Integer> getSupportedFormats(Method genericableMethod) {
        List<Integer> cachedSupportedFormats = this.cachedSupportedFormatsMapping.get(genericableMethod);
        if (CollectionUtils.isNotEmpty(cachedSupportedFormats)) {
            return cachedSupportedFormats;
        }
        List<Integer> supportedFormats = this.resolveSupportedSerialization(genericableMethod);
        if (CollectionUtils.isNotEmpty(supportedFormats)) {
            this.cachedSupportedFormatsMapping.put(genericableMethod, supportedFormats);
        }
        return supportedFormats;
    }

    private List<Integer> resolveSupportedSerialization(Method method) {
        return this.getMessageSerializers()
                .stream()
                .filter(serializer -> serializer.isSupported(method))
                .map(MessageSerializer::getFormat)
                .collect(Collectors.toList());
    }

    private List<MessageSerializer> getMessageSerializers() {
        return this.container.all(MessageSerializer.class)
                .stream()
                .map(BeanFactory::<MessageSerializer>get)
                .collect(Collectors.toList());
    }
}
