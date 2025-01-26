/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.server.http.websocket;

import modelengine.fit.serialization.MessageSerializer;
import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.flowable.Publisher;
import modelengine.fitframework.flowable.util.worker.Worker;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.Version;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 表示处理流式调用请求时的上下文。
 *
 * @author 何天放
 * @since 2024-04-30
 */
public interface WebSocketServerContext {
    /**
     * 获取调用的泛服务的唯一标识。
     *
     * @return 表示调用的泛服务的唯一标识的 {@link String}。
     */
    String genericableId();

    /**
     * 获取调用的泛服务的版本。
     *
     * @return 表示调用的泛服务的版本的 {@link Version}。
     */
    Version genericableVersion();

    /**
     * 获取调用的泛服务实现的唯一标识。
     *
     * @return 表示调用的泛服务实现的唯一标识的 {@link String}。
     */
    String fitableId();

    /**
     * 获取泛服务调用中的扩展字段。
     *
     * @return 表示调用的泛服务中扩展字段的 {@link TagLengthValues}。
     */
    TagLengthValues extensions();

    /**
     * 获取泛服务调用的序列化方式的编码。
     *
     * @return 表示泛服务调用的序列化方式的编码 {@code int}。
     */
    int format();

    /**
     * 获取所调用的泛服务各个类型为 {@link Publisher} 的参数的元素类型。
     * <p>Map 中的键为该参数在参数列表中的索引。</p>
     *
     * @return 表示各个类型为 {@link Publisher} 的参数的元素类型的
     * {@link Map}{@code <}{@link String}{@code ,}{@link Type}{@code >}。
     */
    Map<Integer, Type> publisherArgumentElementTypes();

    /**
     * 获取所调用的泛服务各个类型为 {@link Publisher} 的参数对应的发送者。
     * <p>Map 中的键为参数在参数列表中的索引。</p>
     *
     * @return 表示各个类型为 {@link Publisher} 的参数所对应发送者的
     * {@link Map}{@code <}{@link String}{@code ,}{@link Type}{@code >}。
     */
    Map<Integer, Emitter<?>> emitters();

    /**
     * 获取当前调用中各个 {@link Publisher} 是否结束的信息。
     *
     * @return 表示各个 {@link Publisher} 是否结束的 {@link AtomicInteger}。
     */
    Map<Integer, Boolean> publisherFinishedTags();

    /**
     * 获取当前调用是否终结的标记。
     *
     * @return 表示是否中介标记的 {@link AtomicBoolean}。
     */
    AtomicBoolean finished();

    /**
     * 获取当前调用中用于辅助返回值 {@link Publisher} 的消费响应式流中元素的工具。
     * <p>仅在当前调用的返回值类型为 {@link Publisher} 时具有意义。</p>
     *
     * @return 表示辅助响应式流进行消费的 {@link Worker}。
     */
    Worker<?> worker();

    /**
     * 获取消息序列化器。
     *
     * @return 表示消息序列化器的 {@link MessageSerializer}。
     */
    MessageSerializer messageSerializer();
}
