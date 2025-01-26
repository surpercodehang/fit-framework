/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.support;

import static modelengine.fit.service.server.RegistryServer.MAX_WORKER_NUM;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.service.WorkerCache;
import modelengine.fitframework.annotation.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示 {@link WorkerCache} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-07-19
 */
@Component
public class DefaultWorkerCache implements WorkerCache {
    private final Map<String, Instant> expires = new ConcurrentHashMap<>();

    @Override
    public void refreshWorker(String workerId, Instant expireTime) {
        notBlank(workerId, "The worker id cannot be blank.");
        notNull(expireTime, "The expire time cannot be null.");
        Instant existExpireTime = this.expires.get(workerId);
        if (existExpireTime == null || expireTime.isAfter(existExpireTime)) {
            if (!this.expires.containsKey(workerId) && this.expires.size() >= MAX_WORKER_NUM) {
                throw new IllegalStateException("Too many workers.");
            }
            this.expires.put(workerId, expireTime);
        }
        this.expire();
    }

    @Override
    public boolean isExpired(String workerId) {
        notBlank(workerId, "The worker id cannot be blank.");
        Instant existExpireTime = this.expires.get(workerId);
        return existExpireTime == null || Instant.now().isAfter(existExpireTime);
    }

    private void expire() {
        List<String> keys = new ArrayList<>(this.expires.keySet());
        for (String key : keys) {
            Instant expireTime = this.expires.get(key);
            if (expireTime == null || Instant.now().isAfter(expireTime)) {
                this.expires.remove(key);
            }
        }
    }
}
