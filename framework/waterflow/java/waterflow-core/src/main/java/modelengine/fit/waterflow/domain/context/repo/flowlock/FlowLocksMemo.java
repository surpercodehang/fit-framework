/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.context.repo.flowlock;

import java.util.concurrent.locks.Lock;

/**
 * 流程锁，内存版本的实现
 *
 * @author 高诗意
 * @since 1.0
 */
public class FlowLocksMemo implements FlowLocks {
    /**
     * 获取分布式锁
     * 获取分布式锁的key值，一般是prefix-streamID-nodeID-suffixes
     * 比如key值为：flow-event-streamId-eventId-192.168.0.1; flow-node-streamId-eventId-192.168.0.1
     *
     * @param key 版本ID
     * @return {@link Lock} 锁对象
     */
    @Override
    public Lock getDistributeLock(String key) {
        return getLocalLock(key);
    }
}
