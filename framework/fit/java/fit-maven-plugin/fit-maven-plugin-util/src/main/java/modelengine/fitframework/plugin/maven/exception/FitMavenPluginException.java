/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.plugin.maven.exception;

/**
 * 表示Fit插件异常的类
 *
 * @author 张浩亮
 * @since 2021/5/24
 */
public class FitMavenPluginException extends RuntimeException {
    /**
     * 使用指定的消息初始化 {@link FitMavenPluginException} 的新实例。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public FitMavenPluginException(String message) {
        super(message);
    }

    /**
     * 使用指定的消息和原因初始化 {@link FitMavenPluginException} 的新实例。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public FitMavenPluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
