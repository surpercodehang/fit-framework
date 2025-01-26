/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.util;

import modelengine.fit.serialization.Constants;
import modelengine.fit.serialization.MessageSerializer;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示消息序列化器的工具类。
 *
 * @author 季聿阶
 * @since 2023-09-21
 */
public class MessageSerializerUtils {
    /**
     * 通过容器，从全局获取支持指定格式的消息序列化器。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param format 表示指定格式的 {@code int}。
     * @return 表示指定格式的消息序列化器的 {@link Optional}{@code <}{@link MessageSerializer}{@code >}。
     */
    public static Optional<MessageSerializer> getMessageSerializer(BeanContainer container, int format) {
        return getMessageSerializers(container).stream()
                .filter(serializer -> serializer.getFormat() == format)
                .findFirst();
    }

    /**
     * 判断是否超过反序列化数据大小阈值。
     *
     * @param length 表示反序列化数据长度的 {@code long}。
     * @param config 表示指定配置的 {@link Config}。
     */
    public static void isSupportedLength(long length, Config config) {
        long largeDataSize = config.keys().contains(Constants.LARGE_DATA_SIZE) ? config.get(
                Constants.LARGE_DATA_SIZE, Long.class) : 0L;
        if (largeDataSize != 0L && length > largeDataSize) {
            throw new SerializationException(StringUtils.format("The deserialized data size exceeds the threshold. "
                    + "[largeDataSize={0}]", largeDataSize));
        }
    }

    private static List<MessageSerializer> getMessageSerializers(BeanContainer container) {
        return container.all(MessageSerializer.class)
                .stream()
                .map(BeanFactory::<MessageSerializer>get)
                .collect(Collectors.toList());
    }
}
