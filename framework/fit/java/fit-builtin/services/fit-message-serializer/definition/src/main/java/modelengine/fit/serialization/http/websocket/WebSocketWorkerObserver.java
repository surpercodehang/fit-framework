/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.http.websocket;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.websocket.Session;
import modelengine.fit.serialization.MessageSerializer;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.flowable.Subscription;
import modelengine.fitframework.flowable.util.worker.Worker;
import modelengine.fitframework.flowable.util.worker.WorkerObserver;
import modelengine.fitframework.serialization.TagLengthValues;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;

/**
 * 表示 {@link Worker} 用于处理流式调用的实现。
 *
 * @author 何天放
 * @since 2024-04-30
 */
public class WebSocketWorkerObserver implements WorkerObserver<Object> {
    private final Session session;
    private final MessageSerializer messageSerializer;
    private final Type type;
    private final int index;
    private final BiConsumer<Session, Integer> tryCloseFunction;

    public WebSocketWorkerObserver(Session session, MessageSerializer messageSerializer, Type type, int index,
            BiConsumer<Session, Integer> tryCloseFunction) {
        this.session = notNull(session, "The session cannot be null.");
        this.messageSerializer = notNull(messageSerializer, "The message serializer cannot be null.");
        this.type = notNull(type, "The type cannot be null.");
        this.index = index;
        this.tryCloseFunction = notNull(tryCloseFunction, "The try close function cannot be null.");
    }

    @Override
    public void onWorkerSubscribed(Subscription subscription) {

    }

    @Override
    public void onWorkerConsumed(Object data, long id) {
        byte[] content = this.messageSerializer.serializeResponse(type, data);
        TagLengthValues tlvs = TagLengthValues.create();
        WebSocketUtils.setIndex(tlvs, this.index);
        WebSocketUtils.setType(tlvs, StreamMessageType.CONSUME.code());
        WebSocketUtils.setContent(tlvs, content);
        this.session.send(tlvs.serialize());
    }

    @Override
    public void onWorkerFailed(Exception cause) {
        TagLengthValues failMessageContent = TagLengthValues.create();
        if (cause instanceof FitException) {
            FailMessageContentUtils.setCode(failMessageContent, ((FitException) cause).getCode());
            FailMessageContentUtils.setMessage(failMessageContent, cause.getMessage());
            FailMessageContentUtils.setExceptionProperties(failMessageContent, ((FitException) cause).getProperties());
        } else {
            FailMessageContentUtils.setCode(failMessageContent, -1);
            FailMessageContentUtils.setMessage(failMessageContent, cause.getMessage());
        }
        TagLengthValues tlvs = TagLengthValues.create();
        WebSocketUtils.setIndex(tlvs, this.index);
        WebSocketUtils.setType(tlvs, StreamMessageType.FAIL.code());
        WebSocketUtils.setContent(tlvs, failMessageContent.serialize());
        this.session.send(tlvs.serialize());
        this.tryCloseFunction.accept(this.session, this.index);
    }

    @Override
    public void onWorkerCompleted() {
        TagLengthValues tlvs = TagLengthValues.create();
        WebSocketUtils.setIndex(tlvs, this.index);
        WebSocketUtils.setType(tlvs, StreamMessageType.COMPLETE.code());
        this.session.send(tlvs.serialize());
        this.tryCloseFunction.accept(this.session, this.index);
    }
}
