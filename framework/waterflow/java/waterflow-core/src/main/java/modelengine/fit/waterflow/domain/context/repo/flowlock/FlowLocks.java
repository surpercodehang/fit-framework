/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.context.repo.flowlock;

import modelengine.fit.waterflow.domain.common.Constants;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 流程实例的锁接口
 *
 * @author 高诗意
 * @since 1.0
 */
public interface FlowLocks {
    /**
     * 本地锁全局静态对象
     */
    Map<String, Lock> locks = new ConcurrentHashMap<>();

    /**
     * 节点分布式锁key前缀
     */
    String NODE_LOCK_KEY_PREFIX = "water-flow-node";

    /**
     * 获取本地锁
     *
     * @param key 获取本地锁的key值，一般是流程版本的streamID
     * @return {@link Lock} 锁对象
     */
    default Lock getLocalLock(String key) {
        return Optional.ofNullable(locks.putIfAbsent(key, new ReentrantLock())).orElseGet(() -> locks.get(key));
    }

    /**
     * 获取分布式锁
     *
     * @param key 分布式锁的key值
     * @return {@link Lock} 锁对象
     */
    Lock getDistributeLock(String key);

    /**
     * 删除本地锁
     * TODO xiangyu 删除流程定义的时候需要删除该定义的本地锁资源
     *
     * @param key 删除本地锁的key值，一般是流程版本的streamID
     */
    default void removeLocalLock(String key) {
        locks.remove(key);
    }

    /**
     * 获取节点分布式锁key值
     * 获取分布式锁的key值，一般是prefix-streamID-nodeID-type
     * 比如key值为：water-flow-node-streamId-nodeID-type
     *
     * @param streamId 版本ID
     * @param nodeId 事件边ID
     * @param processType 该key用于何种处理
     * @return 分布式锁key值
     */
    default String lockKey(String streamId, String nodeId, String processType) {
        return StringUtils.join(Constants.STREAM_ID_SEPARATOR, NODE_LOCK_KEY_PREFIX, streamId, nodeId, processType);
    }
}
