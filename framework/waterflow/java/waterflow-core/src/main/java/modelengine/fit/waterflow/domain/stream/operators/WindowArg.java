/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.stream.operators;

import java.time.Duration;

/**
 * window的参数，用于判定window是否完成
 */
public class WindowArg {
    private final boolean isSessionComplete;

    private final long dataLength;

    private final long countToNow;

    private final Duration timeToNow;

    public WindowArg(boolean isSessionComplete, long dataLength, long countToNow, Duration timeToNow) {
        this.isSessionComplete = isSessionComplete;
        this.dataLength = dataLength;
        this.countToNow = countToNow;
        this.timeToNow = timeToNow;
    }

    /**
     * session是否完成
     *
     * @return
     */
    public boolean isSessionComplete() {
        return this.isSessionComplete;
    }

    /**
     * 数据长度
     *
     * @return long型的长度值
     */
    public long dataLength() {
        return this.dataLength;
    }

    /**
     * 截止到当前的计数个数
     *
     * @return 个数
     */
    public long countToNow() {
        return this.countToNow;
    }

    /**
     * 距当前时间的间隔
     *
     * @return 间隔
     */
    public Duration timeToNow() {
        return this.timeToNow;
    }
}
