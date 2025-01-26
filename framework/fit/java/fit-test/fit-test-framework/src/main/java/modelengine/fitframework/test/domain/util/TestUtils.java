/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.util;

import modelengine.fitframework.test.domain.mvc.MockMvc;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 为模拟 {@link MockMvc} 提供常见的公用方法。
 *
 * @author 王攀博
 * @since 2024-04-09
 */
public class TestUtils {
    /**
     * 获取当前设备可用的端口。
     *
     * @return 返回当前设备可用的端口号 {@link int}。
     */
    public static int getLocalAvailablePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to get local available port.", e);
        }
    }
}
