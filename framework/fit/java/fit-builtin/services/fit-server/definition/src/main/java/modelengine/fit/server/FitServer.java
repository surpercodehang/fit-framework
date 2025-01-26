/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.server;

import modelengine.fitframework.broker.Endpoint;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 表示 FIT 的服务器。
 *
 * @author 季聿阶
 * @since 2022-09-16
 */
public interface FitServer {
    /**
     * 启动服务器。
     *
     * @throws StartServerException 当启动过程中发生异常时。
     */
    void start() throws StartServerException;

    /**
     * 停止服务器。
     */
    void stop();

    /**
     * 获取服务器启动后的服务端点列表。
     *
     * @return 表示服务器启动后的服务端点列表的 {@link List}{@code <}{@link Endpoint}{@code >}。
     */
    List<Endpoint> endpoints();

    /**
     * 获取服务器启动后的扩展信息集合。
     *
     * @return 表示服务器启动后的扩展信息集合的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    default Map<String, String> extensions() {
        return Collections.emptyMap();
    }
}
