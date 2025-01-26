/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http;

/**
 * 表示安全问题导致的异常。
 *
 * @author 何天放
 * @since 2024-07-29
 */
public class FitSecurityException extends Exception {
    /**
     * 使用异常信息初始化 {@link FitSecurityException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public FitSecurityException(String message) {
        super(message);
    }
}
