/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.common;

/**
 * 异常类型枚举类
 *
 * @author 陈镕希
 * @since 1.0
 */
public enum ErrorCodes {
    // /** ------------ Generic Exception. From 10000000 to 10000999 --------------------- */
    /**
     * 枚举类转换异常
     */
    ENUM_CONVERT_FAILED(10000001, "Cannot convert enum {0} by name: {1}."),
    /**
     * 入参不合法
     */
    INPUT_PARAM_IS_INVALID(10000003, "Input param is invalid, invalid param is {0}."),
    /**
     * 分页查询时Offset范围不正确。
     */
    PAGINATION_OFFSET_INVALID(10000008, "The range of offset is incorrect."),
    /**
     * 分页查询时Limit范围不正确。
     */
    PAGINATION_LIMIT_INVALID(10000009, "The range of limit is incorrect."),

    /**
     * 类型转换失败。
     */
    TYPE_CONVERT_FAILED(10000011, "Cannot convert type."),

    /** ------------ waterflow Exception 10007000-10007999 --------------------- */
    /**
     * flow节点任务数达到最大值
     */
    FLOW_NODE_MAX_TASK(100070024, "Flow node id {0} tasks over the limit."),

    /**
     * 流程节点转换不支持操作
     */
    FLOW_NODE_CREATE_ERROR(10007000, "Processor can not be null during create flowable node."),
    /**
     * 流程节点不支持执行操作
     */
    FLOW_NODE_OPERATOR_NOT_SUPPORT(10007001, "Flow node with id: {0}, type: {1}, for operator [{2}] not supported."),
    /**
     * 流程没有开始节点
     */
    FLOW_HAS_NO_START_NODE(10007002, "Flow definition with id: {0} has no start node."),
    /**
     * 流程执行错误，没有手动执行任务
     */
    FLOW_ENGINE_INVALID_MANUAL_TASK(10007004, "Flow engine executor error for invalid manual task."),
    /**
     * 流程定义解析失败
     */
    FLOW_ENGINE_PARSER_NOT_SUPPORT(10007010, "Flow engine parser not support {0} operator."),
    FLOW_EXECUTE_FITABLE_TASK_FAILED(10007012,
            "execute jober failed, jober name: {0}, jober type: {1}, fitables: {2}, errors: {3}"),
    /**
     * 流程引擎数据库不支持该操作
     */
    FLOW_ENGINE_DATABASE_NOT_SUPPORT(100070014, "Operation :{0} is not supported."),
    /**
     * 通过eventMetaId查询to节点失败
     */
    FLOW_FIND_TO_NODE_BY_EVENT_FAILED(100070016, "Find to node by event metaId :{0} failed."),
    /**
     * 流程回调函数执行fitables失败
     */
    FLOW_EXECUTE_CALLBACK_FITABLES_FAILED(100070023,
            "Failed to execute callback, callback name: {0}, callback type: {1}, fitables: {2}, errors: {3}"),

    /**
     * 流程引擎OhScript语法错误
     */
    FLOW_ENGINE_OHSCRIPT_GRAMMAR_ERROR(100070024, "OhScript grammar error. Source Code: {0}"),

    /**
     * 流程引擎条件规则变量未找到
     */
    FLOW_ENGINE_CONDITION_RULE_PARSE_ERROR(100070025, "Condition rule parse error. Condition Rule: {0}"),

    /**
     * 流程执行过程出现异常
     */
    FLOW_ENGINE_EXECUTOR_ERROR(10007500, "Error code: 10007500, Flow engine executor errors "
            + "stream id: {0}, node id: {1}, name: {2}, exception: {3}, error message: {4}."),
    ;

    private final Integer errorCode;

    private final String message;

    ErrorCodes(Integer errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "err " + this.errorCode + ": " + this.message;
    }
}
