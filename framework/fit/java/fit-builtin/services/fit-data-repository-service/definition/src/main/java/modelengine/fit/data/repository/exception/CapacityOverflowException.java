/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.data.repository.exception;

import modelengine.fitframework.exception.ErrorCode;
import modelengine.fitframework.exception.FitException;

/**
 * 表示容量溢出发生的异常。
 *
 * @author 邬涨财
 * @since 2024-01-24
 */
@ErrorCode(0x7FF00000)
public class CapacityOverflowException extends FitException {
    private static final String MAX_MEMORY_KEY = "maxMemory";
    private static final String USED_MEMORY_KEY = "usedMemory";
    private static final String FREE_MEMORY_KEY = "freeMemory";
    private static final String TO_ALLOCATE_MEMORY_KEY = "toAllocateMemory";

    /**
     * 通过异常信息来初始化 {@link CapacityOverflowException} 的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public CapacityOverflowException(String message) {
        super(message);
    }

    /**
     * 通过异常信息、最大内存值、已使用的内存值、空余内存值和待分配的内存值来初始化 {@link CapacityOverflowException} 的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param maxMemory 表示当前进程实际可运行的最大内存值的 {@code long}。
     * @param usedMemory 表示当前进程已使用的内存值的 {@code long}。
     * @param freeMemory 表示当前进程的空余内存值的 {@code long}。
     * @param toAllocateMemory 表示当前进程需要分配的内存值的 {@code long}。
     */
    public CapacityOverflowException(String message, long maxMemory, long usedMemory, long freeMemory,
            long toAllocateMemory) {
        super(message);
        this.setProperty(MAX_MEMORY_KEY, String.valueOf(maxMemory));
        this.setProperty(USED_MEMORY_KEY, String.valueOf(usedMemory));
        this.setProperty(FREE_MEMORY_KEY, String.valueOf(freeMemory));
        this.setProperty(TO_ALLOCATE_MEMORY_KEY, String.valueOf(toAllocateMemory));
    }

    /**
     * 获取最大内存值，如果信息缺失，返回 {@code -1}。
     * <p>单位为字节。</p>
     *
     * @return 表示最大内存值的 {@code long}。
     */
    public long maxMemory() {
        try {
            return Long.parseLong(this.getProperties().get(MAX_MEMORY_KEY));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 获取已使用的内存值，如果信息缺失，返回 {@code -1}。
     * <p>单位为字节。</p>
     *
     * @return 表示已使用的内存值的 {@code long}。
     */
    public long usedMemory() {
        try {
            return Long.parseLong(this.getProperties().get(USED_MEMORY_KEY));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 获取空余内存值，如果信息缺失，返回 {@code -1}。
     * <p>单位为字节。</p>
     *
     * @return 表示空余内存值的 {@code long}。
     */
    public long freeMemory() {
        try {
            return Long.parseLong(this.getProperties().get(FREE_MEMORY_KEY));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 获取需要分配的内存值，如果信息缺失，返回 {@code -1}。
     * <p>单位为字节。</p>
     *
     * @return 表示需要分配的内存值的 {@code long}。
     */
    public long toAllocateMemory() {
        try {
            return Long.parseLong(this.getProperties().get(TO_ALLOCATE_MEMORY_KEY));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
