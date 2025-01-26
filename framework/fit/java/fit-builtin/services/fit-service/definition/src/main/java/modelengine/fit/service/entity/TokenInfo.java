/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.entity;

/**
 * 用于表示组合的令牌实体类。
 *
 * @author 李金绪
 * @since 2024-07-16
 */
public class TokenInfo {
    private String token;
    private String status;
    private int timeout;
    private String type;

    /**
     * 用于实例化令牌类。
     *
     * @param token 表示访问令牌的 {@link String}。
     * @param status 表示令牌状态的 {@link String}。
     * @param timeout 表示令牌超时时间的 {@code int}。
     * @param type 表示令牌类型的 {@link String}。
     */
    public TokenInfo(String token, String status, int timeout, String type) {
        this.token = token;
        this.status = status;
        this.timeout = timeout;
        this.type = type;
    }

    /**
     * 无参构造函数。
     */
    public TokenInfo() {}

    /**
     * 获取令牌。
     *
     * @return 返回访问令牌的 {@link String}。
     */
    public String getToken() {
        return this.token;
    }

    /**
     * 设置访问令牌。
     *
     * @param token 表示访问令牌的 {@link String}。
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 获取令牌状态。
     *
     * @return 返回令牌状态的 {@link String}。
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * 设置令牌状态。
     *
     * @param status 表示令牌状态的 {@link String}。
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取令牌类型。
     *
     * @return 返回令牌类型的 {@link String}。
     */
    public String getType() {
        return this.type;
    }

    /**
     * 设置令牌类型。
     *
     * @param type 表示令牌类型的 {@link String}。
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取令牌超时时间。
     *
     * @return 返回令牌超时时间的 {@code int}。
     */
    public int getTimeout() {
        return this.timeout;
    }

    /**
     * 设置令牌超时时间。
     *
     * @param timeout 表示令牌超时时间的 {@code int}。
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
