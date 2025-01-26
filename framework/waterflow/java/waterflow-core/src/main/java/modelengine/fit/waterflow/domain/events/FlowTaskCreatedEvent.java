/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.events;

import modelengine.fitframework.event.Event;

import java.util.List;

/**
 * flow任务创建了事件
 *
 * @author 杨祥宇
 * @since 1.0
 */
public class FlowTaskCreatedEvent implements Event {
    private final List<String> flowContextId;

    private final String streamId;

    private final String nodeId;

    private final Object publisher;

    public FlowTaskCreatedEvent(List<String> flowContextId, String streamId, String nodeId, Object publisher) {
        this.flowContextId = flowContextId;
        this.streamId = streamId;
        this.nodeId = nodeId;
        this.publisher = publisher;
    }

    @Override
    public Object publisher() {
        return this.publisher;
    }

    public List<String> getFlowContextId() {
        return flowContextId;
    }

    public String getStreamId() {
        return streamId;
    }

    public String getNodeId() {
        return nodeId;
    }
}
