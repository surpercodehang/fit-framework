/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.server.http.websocket.support;

import modelengine.fit.serialization.MessageSerializer;
import modelengine.fit.server.http.websocket.ConfigurableWebSocketServerContext;
import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.flowable.util.worker.Worker;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.Version;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 表示 {@link ConfigurableWebSocketServerContext} 的默认实现。
 *
 * @author 何天放
 * @since 2024-04-30
 */
public class DefaultWebSocketServerContext implements ConfigurableWebSocketServerContext {
    private final String genericableId;
    private final String fitableId;
    private final Map<Integer, Type> publisherArgumentElementTypes = new HashMap<>();
    private final Map<Integer, Emitter<?>> emitters = new HashMap<>();
    private final Map<Integer, Boolean> publisherFinishedTags = new HashMap<>();
    private final AtomicBoolean finished = new AtomicBoolean(false);
    private Worker<?> worker;
    private Version genericableVersion;
    private TagLengthValues extensions;
    private int format;
    private MessageSerializer messageSerializer;

    public DefaultWebSocketServerContext(String genericableId, String fitableId) {
        this.genericableId = genericableId;
        this.fitableId = fitableId;
    }

    @Override
    public String genericableId() {
        return this.genericableId;
    }

    @Override
    public Version genericableVersion() {
        return this.genericableVersion;
    }

    @Override
    public String fitableId() {
        return this.fitableId;
    }

    @Override
    public TagLengthValues extensions() {
        return this.extensions;
    }

    @Override
    public int format() {
        return this.format;
    }

    @Override
    public Map<Integer, Type> publisherArgumentElementTypes() {
        return this.publisherArgumentElementTypes;
    }

    @Override
    public Map<Integer, Emitter<?>> emitters() {
        return this.emitters;
    }

    @Override
    public Map<Integer, Boolean> publisherFinishedTags() {
        return this.publisherFinishedTags;
    }

    @Override
    public AtomicBoolean finished() {
        return this.finished;
    }

    @Override
    public Worker<?> worker() {
        return this.worker;
    }

    @Override
    public MessageSerializer messageSerializer() {
        return this.messageSerializer;
    }

    @Override
    public void genericableVersion(Version version) {
        this.genericableVersion = version;
    }

    @Override
    public void extensions(TagLengthValues extensions) {
        this.extensions = extensions;
    }

    @Override
    public void format(int format) {
        this.format = format;
    }

    @Override
    public void worker(Worker<?> worker) {
        this.worker = worker;
    }

    @Override
    public void messageSerializer(MessageSerializer messageSerializer) {
        this.messageSerializer = messageSerializer;
    }
}
