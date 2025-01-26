/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client;

import modelengine.fit.client.support.DefaultRequestContext;
import modelengine.fitframework.broker.CommunicationType;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 表示请求上下文。
 *
 * @author 季聿阶
 * @since 2022-09-19
 */
public interface RequestContext {
    /**
     * 获取请求的超时时间。
     *
     * @return 表示请求超时时间的 {@code long}。
     */
    long timeout();

    /**
     * 获取请求超时时间的单位。
     *
     * @return 表示请求超时时间单位的 {@link TimeUnit}。
     */
    TimeUnit timeoutUnit();

    /**
     * 获取请求的通信方式。
     *
     * @return 表示请求通信方式的 {@link CommunicationType}。
     */
    CommunicationType communicationType();

    /**
     * 获取请求超时时间的字符串表述。
     *
     * @return 表示请求超时时间的字符串表述的 {@link String}。
     */
    String timeoutValue();

    /**
     * 获取请求的扩展信息集合。
     *
     * @return 表示请求的扩展信息集合的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    Map<String, String> extensions();

    /**
     * 创建一个请求上下文。
     *
     * @param timeout 表示请求超时时间的 {@code long}。
     * @param timeoutUnit 表示请求超时时间单位的 {@link TimeUnit}。
     * @param communicationType 表示请求通信方式的 {@link CommunicationType}。
     * @param extensions 表示请求的扩展信息集合的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     * @return 表示创建的请求上下文的 {@link RequestContext}。
     */
    static RequestContext create(long timeout, TimeUnit timeoutUnit, CommunicationType communicationType,
            Map<String, String> extensions) {
        return new DefaultRequestContext(timeout, timeoutUnit, communicationType, extensions);
    }
}
