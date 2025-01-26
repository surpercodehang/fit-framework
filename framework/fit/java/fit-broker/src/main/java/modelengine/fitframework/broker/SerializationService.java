/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import modelengine.fit.serialization.MessageSerializer;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * 为序列化程序提供管理。
 *
 * @author 梁济时
 * @since 2020-11-12
 */
public interface SerializationService {
    /**
     * 获取指定消息格式的序列化程序。
     *
     * @param format 表示消息格式的 {@code int}。
     * @return 表示指定消息格式的序列化程序的 {@link Optional}{@code <}{@link MessageSerializer}{@code >}。
     */
    Optional<MessageSerializer> get(int format);

    /**
     * 返回一个为指定格式的消息所使用的序列化程序。
     *
     * @param format 表示序列化程序的 {@code int}。
     * @return 消息序列化程序的 {@link MessageSerializer}。
     * @throws IllegalStateException 当没有指定的序列化方式的序列化器时。
     */
    default MessageSerializer require(int format) {
        return this.get(format)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "MessageSerializer required but not found. [format={0}]",
                        format)));
    }

    /**
     * 获取对应的泛服务所支持的序列化方式。
     *
     * @param genericableMethod 表示泛服务的方法的 {@link Method}。
     * @return 表示指定泛服务所支持的序列化方式的 {@link List}{@code <}{@link Integer}{@code >}。
     */
    List<Integer> getSupportedFormats(Method genericableMethod);
}
