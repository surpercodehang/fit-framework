/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.data.repository;

import modelengine.fit.data.repository.exception.CapacityOverflowException;

/**
 * 缓存数据校验服务。
 *
 * @author 邬涨财
 * @since 2024-01-24
 */
public interface ValidationService {
    /**
     * 判断内存是否可以分配。
     *
     * @param toAllocateMemory 表示需要分配的内存值的 {@code long}。
     * @throws CapacityOverflowException 如果分配该内存值后，内存容量会溢出，则会抛出该异常。
     */
    void validateCapacity(long toAllocateMemory) throws CapacityOverflowException;
}
