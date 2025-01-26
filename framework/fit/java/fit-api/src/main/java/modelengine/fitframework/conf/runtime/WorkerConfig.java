/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.runtime;

import java.util.List;

/**
 * 表示运行时 {@code 'worker.'} 前缀的配置项。
 *
 * @author 季聿阶
 * @since 2023-07-07
 */
public interface WorkerConfig {
    /**
     * 获取 {@code 'worker.id'} 的配置项。
     *
     * @return 表示 {@code 'worker.id'} 的配置项的 {@link String}。
     */
    String id();

    /**
     * 获取 {@code 'worker.instance-id'} 的配置项。
     *
     * @return 表示 {@code 'worker.instance-id'} 的配置项的 {@link String}。
     */
    String instanceId();

    /**
     * 获取 {@code 'worker.host'} 的配置项。
     *
     * @return 表示 {@code 'worker.host'} 的配置项的 {@link String}。
     */
    String host();

    /**
     * 获取 {@code 'worker.domain'} 的配置项。
     *
     * @return 表示 {@code 'worker.domain'} 的配置项的 {@link String}。
     */
    String domain();

    /**
     * 获取 {@code 'worker.environment'} 的配置项。
     *
     * @return 表示 {@code 'worker.environment'} 的配置项的 {@link String}。
     */
    String environment();

    /**
     * 获取 {@code 'worker.environment-sequence'} 的配置项。
     *
     * @return 表示 {@code 'worker.environment-sequence'} 的配置项的 {@link String}。
     */
    String rawEnvironmentSequence();

    /**
     * 获取 {@code 'worker.environment-sequence'} 的配置项解析后的内容。
     *
     * @return 表示 {@code 'worker.environment-sequence'} 的配置项解析后的环境链的 {@link List}{@code <}{@link
     * String}{@code >}。
     */
    List<String> environmentSequence();
}
