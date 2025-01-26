/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.exception;

/**
 * 表示Fit插件异常的类
 *
 * @author 张浩亮
 * @since 2021/5/24
 */
public class FitMavenPluginException extends RuntimeException {
    public FitMavenPluginException(String message) {
        super(message);
    }

    public FitMavenPluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
