/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service;

import modelengine.fit.http.annotation.DocumentIgnored;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.service.server.RegistryServer;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * 用于注册中心的检查。
 *
 * @author 季聿阶
 * @since 2023-06-30
 */
@Component
@RequestMapping("/fit")
public class CheckController {
    private final RegistryServer server;

    public CheckController(RegistryServer server) {
        this.server = server;
    }

    /**
     * 获取所有服务的信息。
     *
     * @return 表示所有服务信息的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    @DocumentIgnored
    @GetMapping("/check")
    public Map<String, Object> getAllServices() {
        return MapBuilder.<String, Object>get()
                .put("workers", this.server.getWorkers())
                .put("applications", this.server.getApplications())
                .put("workerApplications", this.server.getWorkerApplications())
                .put("applicationMetas", this.server.getApplicationMetas())
                .build();
    }
}
