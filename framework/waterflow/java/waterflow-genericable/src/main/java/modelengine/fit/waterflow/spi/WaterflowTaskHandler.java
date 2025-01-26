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
 * 流程服务的Genericable
 *
 * @author 晏钰坤
 * @since 1.0
 */
public interface WaterflowTaskHandler {
    /**
     * 处理流程中的任务调用
     *
     * @param flowData 流程执行上下文数据
     * @return 任务执行返回结果
     */
    @Genericable(id = "b735c87f5e7e408d852d8440d0b2ecdf")
    List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData);
}
