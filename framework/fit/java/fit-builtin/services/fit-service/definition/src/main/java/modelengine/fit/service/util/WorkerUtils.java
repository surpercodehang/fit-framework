/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.util;

import modelengine.fit.server.FitServer;
import modelengine.fit.service.entity.Address;
import modelengine.fit.service.entity.Endpoint;
import modelengine.fit.service.entity.Worker;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示进程的工具类。
 *
 * @author 季聿阶
 * @since 2023-09-14
 */
public class WorkerUtils {
    /**
     * 获取本地进程信息。
     *
     * @param container 表示某一个插件的容器的 {@link BeanContainer}。
     * <p>该参数为不确定的插件容器，因此不可以依赖容器内搜索能力，只可以使用全局搜索能力。</p>
     * @param workerConfig 表示进程配置信息的 {@link WorkerConfig}。
     * @return 表示本地进程信息的 {@link Worker}。
     */
    public static Worker getLocalWorker(BeanContainer container, WorkerConfig workerConfig) {
        Worker worker = new Worker();
        worker.setId(workerConfig.id());
        worker.setEnvironment(workerConfig.environment());
        worker.setAddresses(Collections.singletonList(getAddress(container, workerConfig)));
        worker.setExtensions(getExtensions(container));
        return worker;
    }

    private static Address getAddress(BeanContainer container, WorkerConfig workerConfig) {
        Address address = new Address();
        address.setHost(workerConfig.domain());
        address.setEndpoints(getFitServers(container).stream()
                .map(FitServer::endpoints)
                .flatMap(List::stream)
                .map(endpoint -> {
                    Endpoint another = new Endpoint();
                    another.setPort(endpoint.port());
                    another.setProtocol(endpoint.protocolCode());
                    return another;
                })
                .collect(Collectors.toList()));
        return address;
    }

    private static Map<String, String> getExtensions(BeanContainer container) {
        return getFitServers(container).stream().map(FitServer::extensions).reduce(new HashMap<>(), (m1, m2) -> {
            m1.putAll(m2);
            return m1;
        });
    }

    private static List<FitServer> getFitServers(BeanContainer container) {
        return container.all(FitServer.class).stream().map(BeanFactory::<FitServer>get).collect(Collectors.toList());
    }
}
