/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity.support;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.TextEvent;
import modelengine.fit.http.entity.TextEventStreamEntity;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 表示 {@link TextEventStreamEntity} 的默认实现。
 *
 * @author 易文渊
 * @since 2024-07-15
 */
public class DefaultTextEventStreamEntity extends AbstractEntity implements TextEventStreamEntity {
    private final Choir<TextEvent> stream;

    /**
     * 创建文本事件流消息体数据的默认实现对象。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param stream 表示数据流的 {@link Choir}{@code <}{@link Object}{@code >}。
     */
    public DefaultTextEventStreamEntity(HttpMessage httpMessage, Choir<?> stream) {
        super(httpMessage);
        this.stream = stream == null
                ? Choir.empty()
                : stream.map(data -> data instanceof TextEvent
                        ? ObjectUtils.cast(data)
                        : TextEvent.custom(data).build());
    }

    @Nonnull
    @Override
    public MimeType resolvedMimeType() {
        return MimeType.TEXT_EVENT_STREAM;
    }

    @Override
    public Choir<TextEvent> stream() {
        return this.stream;
    }
}