/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.tool.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fel.tool.Tool;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link Tool} 的 FIT 调用实现。
 *
 * @author 王攀博
 * @since 2024-04-24
 */
public class FitTool extends AbstractTool {
    /**
     * 表示 FIT 调用工具的类型。
     */
    public static final String TYPE = "FIT";

    private final BrokerClient brokerClient;

    /**
     * 通过 FIT 调用代理、Json 序列化器和工具元数据信息来初始化 {@link FitTool} 的新实例。
     *
     * @param brokerClient 表示 FIT 调用代理的的 {@link BrokerClient}。
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @param info 表示工具的基本信息的 {@link Info}。
     * @param metadata 表示工具元数据信息的 {@link Metadata}。
     * @throws IllegalArgumentException 当 {@code brokerClient}、{@code serializer}、{@code itemInfo} 或
     * {@code metadata} 为 {@code null} 时。
     */
    public FitTool(BrokerClient brokerClient, ObjectSerializer serializer, Info info, Metadata metadata) {
        super(serializer, info, metadata);
        this.brokerClient = notNull(brokerClient, "The broker client cannot be null.");
    }

    @Override
    public Object execute(Object... args) {
        Map<String, Object> runnables = cast(this.info().runnables().get(TYPE));
        if (MapUtils.isEmpty(runnables)) {
            throw new IllegalStateException("No runnables info. [type=FIT]");
        }
        String genericableId = notBlank(cast(runnables.get("genericableId")), "No genericable id in runnables info.");
        Router router = this.brokerClient.getRouter(genericableId);
        String fitableId = cast(runnables.get("fitableId"));
        Router.Filter filter = null;
        if (StringUtils.isNotBlank(fitableId)) {
            filter = new FitableIdFilter(fitableId);
        }
        return router.route(filter).communicationType(CommunicationType.ASYNC).invoke(args);
    }
}
