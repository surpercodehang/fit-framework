/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.runtime;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.util.EnvironmentUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;

/**
 * 表示 {@link WorkerConfig} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-07-07
 */
public class DefaultWorker implements WorkerConfig {
    private static final String WORKER_ID = "worker.id";
    private static final String WORKER_INSTANCE_ID = "worker.instance-id";
    private static final String WORKER_HOST = "worker.host";
    private static final String WORKER_DOMAIN = "worker.domain";
    private static final String WORKER_ENVIRONMENT = "worker.environment";
    private static final String WORKER_ENVIRONMENT_SEQUENCE = "worker.environment-sequence";

    private String id;
    private String instanceId;
    private String host;
    private String domain;
    private String environment;
    private String environmentSequence;

    /**
     * 设置进程唯一标识的配置。
     *
     * @param id 表示待设置的进程唯一标识的配置的 {@link String}。
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 设置进程实例唯一标识的配置。
     *
     * @param instanceId 表示待设置的进程实例唯一标识的配置的 {@link String}。
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * 设置进程所在主机的配置。
     *
     * @param host 表示待设置的进程所在主机的配置的 {@link String}。
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 设置进程域名的配置。
     *
     * @param domain 表示待设置的进程域名的配置的 {@link String}。
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * 设置环境的配置。
     *
     * @param environment 表示待设置的环境配置的 {@link String}。
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * 设置环境调用链的配置。
     *
     * @param environmentSequence 表示待设置的环境调用链配置的 {@link String}。
     */
    public void setEnvironmentSequence(String environmentSequence) {
        this.environmentSequence = environmentSequence;
    }

    @Override
    public String id() {
        return notBlank(this.id, "Config '" + WORKER_ID + "' cannot be blank.");
    }

    @Override
    public String instanceId() {
        return notBlank(this.instanceId, "Config '" + WORKER_INSTANCE_ID + "' cannot be blank.");
    }

    @Override
    public String host() {
        return notBlank(this.host, "Config '" + WORKER_HOST + "' cannot be blank.");
    }

    @Override
    public String domain() {
        if (StringUtils.isBlank(this.domain)) {
            return notBlank(this.host, "Config '" + WORKER_DOMAIN + "' cannot be blank.");
        }
        return this.domain;
    }

    @Override
    public String environment() {
        return notBlank(this.environment, "The '" + WORKER_ENVIRONMENT + "' cannot be blank.");
    }

    @Override
    public String rawEnvironmentSequence() {
        return notBlank(this.environmentSequence, "The '" + WORKER_ENVIRONMENT_SEQUENCE + "' cannot be blank.");
    }

    @Override
    public List<String> environmentSequence() {
        return EnvironmentUtils.buildEnvironmentSequence(this.rawEnvironmentSequence(), this.environment());
    }

    @Override
    public String toString() {
        String content = StringUtils.format("/{\"id\": \"{0}\", \"instance-id\": \"{1}\", \"host\": \"{2}\", "
                        + "\"domain\": \"{3}\", \"environment\": \"{4}\", \"environment-sequence\": \"{5}\"/}",
                this.id,
                this.instanceId,
                this.host,
                this.domain,
                this.environment,
                this.environmentSequence);
        return StringUtils.format("/{\"worker\": {0}/}", content);
    }
}
