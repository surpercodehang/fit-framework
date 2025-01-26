/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.external;

import modelengine.fit.http.server.HttpClassicServer;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.runtime.FitStarter;
import modelengine.fitframework.util.ThreadUtils;

/**
 * 用于测试的统一 FIT 运行时。
 *
 * @author 季聿阶
 * @since 2023-10-23
 */
public class TestFitRuntime {
    /**
     * 表示 FIT 统一测试运行时的单例。
     */
    public static final TestFitRuntime INSTANCE = new TestFitRuntime();

    private static BeanContainer container;

    private static BrokerClient brokerClient;

    private volatile boolean started = false;

    private TestFitRuntime() {
    }

    /**
     * 判断 FIT 运行时是否启动完毕。
     *
     * @return 如果启动完毕，返回 {@code true}，否则，返回 {@code false}。
     */
    public boolean isStarted() {
        return this.started;
    }

    /**
     * 启动 FIT 运行时。
     */
    public void start() {
        if (this.isStarted()) {
            return;
        }
        FitRuntime runtime = FitStarter.start(HttpTest.class, new String[] {"server.http.port=8080"});
        HttpClassicServer httpClassicServer = runtime.root()
                .container()
                .lookup(HttpClassicServer.class)
                .map(BeanFactory::<HttpClassicServer>get)
                .orElseThrow(() -> new IllegalStateException("Failed to start http server."));
        while (!httpClassicServer.isStarted()) {
            ThreadUtils.sleep(0);
        }
        container = runtime.root().container();
        brokerClient = container.lookup(BrokerClient.class)
                .map(BeanFactory::<BrokerClient>get)
                .orElseThrow(() -> new IllegalStateException("Failed to get broker client."));
        this.started = true;
    }

    /**
     * 获取 bean 容器。
     *
     * @return 表示 bean 容器的 {@link BeanContainer}。
     */
    public BeanContainer getContainer() {
        return container;
    }

    /**
     * 获取 FIT 调用的代理。
     *
     * @return 表示 FIT 调用代理的 {@link BrokerClient}。
     */
    public BrokerClient getBrokerClient() {
        return brokerClient;
    }
}
