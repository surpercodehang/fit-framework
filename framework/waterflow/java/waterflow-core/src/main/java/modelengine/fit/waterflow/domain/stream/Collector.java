/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.stream;

/**
 * 新数据发射器
 *
 * @param <T> 新数据类型
 * @since 1.0
 */
public interface Collector<T> {
    /**
     * 发射一条新数据
     *
     * @param data 待发射的数据
     */
    void collect(T data);
}
