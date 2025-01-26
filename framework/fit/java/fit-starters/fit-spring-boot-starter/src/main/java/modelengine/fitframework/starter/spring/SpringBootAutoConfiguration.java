/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.starter.spring;

import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.runtime.FitStarter;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 表示 Spring-Boot Starter 中的自动配置类。
 *
 * @author 季聿阶
 * @since 2024-03-25
 */
@Configuration
@EnableConfigurationProperties(PluginProperties.class)
@ComponentScan(basePackages = "modelengine.fitframework.starter.spring")
public class SpringBootAutoConfiguration {
    private final ApplicationArguments args;

    /**
     * 通过应用的启动参数来初始化 {@link SpringBootAutoConfiguration} 的新实例。
     *
     * @param args 表示应用的启动参数的 {@link ApplicationArguments}。
     */
    public SpringBootAutoConfiguration(ApplicationArguments args) {
        this.args = args;
    }

    /**
     * 将 FIT 运行时注册到 Spring 容器中。
     *
     * @return 表示 FIT 运行时的 {@link FitRuntime}。
     */
    @Bean
    public FitRuntime fitRuntime() {
        return FitStarter.start(SpringBootAutoConfiguration.class, this.args.getSourceArgs());
    }

    /**
     * 将 FIT 调用代理客户端注册到 Spring 容器中。
     *
     * @param fitRuntime 表示依赖的 FIT 运行时的 {@link FitRuntime}。
     * @return 表示 FIT 调用代理客户端的 {@link BrokerClient}。
     */
    @Bean
    public BrokerClient brokerClient(FitRuntime fitRuntime) {
        return fitRuntime.root()
                .container()
                .factory(BrokerClient.class)
                .map(BeanFactory::<BrokerClient>get)
                .orElseThrow(() -> new IllegalStateException("No broker client in FIT runtime."));
    }
}
