/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.spi;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 流程实例异常 Genericable。
 *
 * @author 李哲峰
 * @since 1.0
 */
public interface WaterflowExceptionNotify {
    /**
     * ON_EXCEPTION_GENERICABLE
     */
    String ON_EXCEPTION_GENERICABLE = "1b5ffv4ib16iui8ddizapuejgqtsjj59";

    /**
     * 异常回调实现
     *
     * @param nodeId 异常发生的节点Id
     * @param contexts 流程上下文
     * @param errorMessage 异常错误信息
     */
    @Genericable(id = ON_EXCEPTION_GENERICABLE)
    void onException(String nodeId, List<Map<String, Object>> contexts, String errorMessage);
}
