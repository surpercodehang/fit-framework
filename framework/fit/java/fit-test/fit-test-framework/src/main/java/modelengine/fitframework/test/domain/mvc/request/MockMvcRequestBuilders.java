/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.mvc.request;

import modelengine.fit.http.protocol.HttpRequestMethod;

/**
 * 为模拟 MVC 客户端提供请求的构建集合。
 *
 * @author 王攀博
 * @since 2024-04-09
 */
public class MockMvcRequestBuilders {
    /**
     * 提供获取方法的请求参数的构建。
     *
     * @param url 表示请求的路径 {@link String}。
     * @return 表示请求的构建者 {@link MockRequestBuilder}。
     */
    public static MockRequestBuilder get(String url) {
        return new MockRequestBuilder(HttpRequestMethod.GET, url);
    }

    /**
     * 提供提交方法的请求参数的构建。
     *
     * @param url 表示请求的路径 {@link String}。
     * @return 表示请求的构建者 {@link MockRequestBuilder}。
     */
    public static MockRequestBuilder post(String url) {
        return new MockRequestBuilder(HttpRequestMethod.POST, url);
    }

    /**
     * 提供更新方法的请求参数的构建。
     *
     * @param url 表示请求的路径 {@link String}。
     * @return 表示请求的构建者 {@link MockRequestBuilder}。
     */
    public static MockRequestBuilder put(String url) {
        return new MockRequestBuilder(HttpRequestMethod.PUT, url);
    }

    /**
     * 提供修改方法的请求参数的构建。
     *
     * @param url 表示请求的路径 {@link String}。
     * @return 表示请求的构建者 {@link MockRequestBuilder}。
     */
    public static MockRequestBuilder patch(String url) {
        return new MockRequestBuilder(HttpRequestMethod.PATCH, url);
    }

    /**
     * 提供删除方法的请求参数的构建。
     *
     * @param url 表示请求的路径 {@link String}。
     * @return 表示请求的构建者 {@link MockRequestBuilder}。
     */
    public static MockRequestBuilder delete(String url) {
        return new MockRequestBuilder(HttpRequestMethod.DELETE, url);
    }
}
