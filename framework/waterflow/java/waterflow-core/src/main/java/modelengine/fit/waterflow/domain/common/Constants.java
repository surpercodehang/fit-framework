/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.common;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import modelengine.fit.waterflow.domain.enums.FlowNodeStatus;
import modelengine.fit.waterflow.domain.enums.FlowTraceStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 常数类
 *
 * @author 宋永坦
 * @since 1.0
 */
public final class Constants {
    /**
     * 定时任务获取
     */
    public static final String SCHEDULE = "schedule";

    /**
     * 定时任务间隔
     */
    public static final String SCHEDULER_INTERVAL = "schedulerInterval";

    /**
     * 延迟时间
     */
    public static final String DELAY_TIME = "delayTime";

    /**
     * 任务定义ID
     */
    public static final String TASK_DEFINITION_ID = "taskDefinitionId";

    /**
     * 默认每页大小
     */
    public static final String PAGE_SIZE = "100";

    /**
     * 系统自动补充的属性字段
     */
    public static final List<String> SYSTEM_FIELDS = unmodifiableList(
            Arrays.asList("id", "decomposed_from", "created_by", "created_date", "modified_by", "modified_date",
                    "source_app", "target_url", "owner", "state"));

    /**
     * 任务第三方id
     */
    public static final String REQUIREMENT_ID = "id";

    /**
     * 创建人
     */
    public static final String APPLIER = "applier";

    /**
     * 责任人
     */
    public static final String OWNER = "owner";

    /**
     * 宽表字段名前缀
     */
    public static final String FIELD_VALUE = "field_value_";

    /**
     * 系统级任务property字段数目
     */
    public static final Integer SYSTEM_PROPERTY_NUM = 30;

    /**
     * 任务property字段数目
     */
    public static final Integer PROPERTY_NUM = 100;

    /**
     * 任务状态
     */
    public static final String STATE = "state";

    /**
     * 任务作废
     */
    public static final String STATE_DELETED = "deleted";

    /**
     * 重试的最大次数
     */
    public static final Integer RETRY_TIMES = 10;

    /**
     * 重试的时间间隔
     */
    public static final Integer RETRY_INTERVAL = 1000;

    /**
     * 数据拉取类型
     */
    public static final String DATA_FETCH_TYPE = "dataFetchType";

    /**
     * 任务数据源定义唯一标识
     */
    public static final String TASK_SOURCE_ID = "taskSourceId";

    /**
     * 数据来源平台
     */
    public static final String SOURCE_APP = "sourceApp";

    /**
     * value change的url
     */
    public static final String VALUE_CHANGE_URL = "//fit-jane-dev.paas.huawei.com/props-value/";

    /**
     * 流程定义ID分隔符
     */
    public static final char STREAM_ID_SEPARATOR = '-';

    /**
     * 流程节点事件中条件属性的KEY值
     */
    public static final String CONDITION_RULE_PROPERTY_KEY = "conditionRule";

    /**
     * 任务定义status字段
     */
    public static final String STATUS = "status";

    /**
     * 任务定义优先级字段
     */
    public static final String PRIORITY = "priority";

    /**
     * 创建时间
     */
    public static final String CREATED_DATE = "created_date";

    /**
     * 任务定义title字段
     */
    public static final String TITLE = "title";

    /**
     * 任务定义created_by字段
     */
    public static final String CREATED_BY = "created_by";

    /**
     * 流程上下文 ID字段
     */
    public static final String FLOW_CONTEXT_ID_KEY = "flowContextId";

    /**
     * 流程trace ID字段
     */
    public static final String TRACE_ID_KEY = "traceId";

    /**
     * 流程节点 ID字段
     */
    public static final String NODE_ID_KEY = "nodeId";

    /**
     * 流程业务数据businessData字段
     */
    public static final String BUSINESS_DATA_KEY = "businessData";

    /**
     * 流程业务数据operator字段
     */
    public static final String OPERATOR_KEY = "operator";

    /**
     * 用于处理流程引擎发布事件的线程池
     */
    public static final String FLOWS_EVENT_HANDLER_EXECUTOR = "flowsEventHandlerExecutor";

    /**
     * flowContext 终止状态的互斥状态列表，即如果原始状态为terminate\error\archived，则不能更改为terminate
     */
    public static final List<String> CONTEXT_TERMINATE_EXCLUSIVE_STATUS_LIST = Collections.unmodifiableList(
            Arrays.asList(
                    FlowNodeStatus.TERMINATE.toString(), FlowNodeStatus.ARCHIVED.toString(),
                    FlowNodeStatus.ERROR.toString()));

    /**
     * flowContext 其他状态的互斥状态列表
     */
    public static final List<String> CONTEXT_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST = Collections.singletonList(
            FlowNodeStatus.TERMINATE.toString());

    /**
     * flowContext状态互斥map
     */
    public static final Map<String, List<String>> CONTEXT_EXCLUSIVE_STATUS_MAP = unmodifiableMap(
            new HashMap<String, List<String>>() {
                {
                    put(FlowNodeStatus.TERMINATE.toString(), CONTEXT_TERMINATE_EXCLUSIVE_STATUS_LIST);
                    put(FlowNodeStatus.ERROR.toString(), CONTEXT_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
                    put(FlowNodeStatus.PENDING.toString(), CONTEXT_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
                    put(FlowNodeStatus.READY.toString(), CONTEXT_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
                    put(FlowNodeStatus.PROCESSING.toString(), CONTEXT_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
                    put(FlowNodeStatus.ARCHIVED.toString(), CONTEXT_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
                }
            });

    /**
     * flowTrace 终止状态的互斥状态列表，即如果原始状态为terminate\error\archived，则不能更改为terminate
     */
    public static final List<String> TRACE_TERMINATE_EXCLUSIVE_STATUS_LIST = Collections.unmodifiableList(Arrays.asList(
            FlowTraceStatus.TERMINATE.toString(), FlowTraceStatus.ARCHIVED.toString(),
            FlowTraceStatus.ERROR.toString()));

    /**
     * flowTrace 其他状态的互斥状态列表
     */
    public static final List<String> TRACE_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST = Collections.singletonList(
            FlowTraceStatus.TERMINATE.toString());

    /**
     * flowTrace状态互斥map
     */
    public static final Map<String, List<String>> TRACE_EXCLUSIVE_STATUS_MAP = unmodifiableMap(
            new HashMap<String, List<String>>() {
                {
                    put(FlowTraceStatus.TERMINATE.toString(), TRACE_TERMINATE_EXCLUSIVE_STATUS_LIST);
                    put(FlowTraceStatus.ERROR.toString(), TRACE_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
                    put(FlowTraceStatus.RUNNING.toString(), TRACE_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
                    put(FlowTraceStatus.READY.toString(), TRACE_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
                    put(FlowTraceStatus.ARCHIVED.toString(), TRACE_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
                }
            });

    /**
     * 流程运行流程元数据信息
     */
    public static final String CONTEXT_DATA = "contextData";

    /**
     * 流程运行非落盘数据
     */
    public static final String PASS_DATA = "passData";

    /**
     * 未确认的索引编号
     */
    public static final Integer UNCONFIRMED_INDEX = 1000000;

    /**
     * 不需要保序的索引编号
     */
    public static final Integer NOT_PRESERVED_INDEX = -1;

    /**
     * 来自于flat map的标识
     */
    public static final String FROM_FLATMAP = "FROM_FLATMAP";
}
