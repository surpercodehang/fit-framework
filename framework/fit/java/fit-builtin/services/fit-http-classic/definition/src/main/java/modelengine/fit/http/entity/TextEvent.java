/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity;

import modelengine.fit.http.entity.support.DefaultTextEvent;
import modelengine.fitframework.serialization.ObjectSerializer;

import java.time.Duration;

/**
 * 表示文本事件消息实体。
 *
 * @author 易文渊
 * @since 2024-07-16
 */
public interface TextEvent {
    /**
     * 文本事件唯一标识前缀。
     */
    String EVENT_ID = "id";

    /**
     * 文本事件名称前缀。
     */
    String EVENT_NAME = "event";

    /**
     * 文本事件重试时间前缀。
     */
    String EVENT_RETRY = "retry";

    /**
     * 文本事件数据前缀。
     */
    String EVENT_DATA = "data";

    /**
     * 文本事件换行符。
     */
    String LF = "\n";

    /**
     * 文本事件行内分隔符。
     */
    String COLON = ":";

    /**
     * 获取文本事件的唯一标识。
     *
     * @return 表示文本事件的唯一标识的 {@link String}。
     */
    String id();

    /**
     * 获取文本事件。
     *
     * @return 表示文本事件的 {@link String}。
     */
    String event();

    /**
     * 获取需要接收文本事件的超时时间。
     *
     * @return 表示需要接收文本事件的超时时间的 {@link Duration}。
     */
    Duration retry();

    /**
     * 获取文本事件的注释。
     *
     * @return 表示文本事件的注释的 {@link String}。
     */
    String comment();

    /**
     * 获取文本事件的数据。
     *
     * @return 表示文本事件的数据的 {@link Object}。
     */
    Object data();

    /**
     * 获取当前对象序列化后的文本。
     *
     * @param objectSerializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @return 表示序列化后的文本的 {@link String}。
     */
    String serialize(ObjectSerializer objectSerializer);

    /**
     * 创建 {@link TextEvent} 的构建器。
     *
     * @return 表示 {@link TextEvent} 构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultTextEvent.Builder();
    }

    /**
     * 创建 {@link TextEvent} 的构建器。
     *
     * @param data 表示事件数据的 {@link Object}。
     * @return 表示 {@link TextEvent} 构建器的 {@link Builder}。
     */
    static Builder custom(Object data) {
        return new DefaultTextEvent.Builder(data);
    }

    /**
     * 表示 {@link TextEvent} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置事件编号。
         *
         * @param id 表示事件编号的 {@link String}.
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder id(String id);

        /**
         * 向当前构建器中设置事件名称。
         *
         * @param event 表示事件名称的 {@link String}.
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder event(String event);

        /**
         * 向当前构建器中设置客户端重试时间。
         *
         * @param retry 表示客户端重试时间的 {@link Duration}.
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder retry(Duration retry);

        /**
         * 向当前构建器中设置事件注释。
         *
         * @param comment 表示事件注释的 {@link String}.
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder comment(String comment);

        /**
         * 向当前构建器中设置事件内容。
         *
         * @param data 表示事件内容的 {@link Object}.
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder data(Object data);

        /**
         * 构建对象。
         *
         * @return 表示构建出来对象的 {@link TextEvent}。
         */
        TextEvent build();
    }
}