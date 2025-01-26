/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

/**
 * 表示 Http 报文的起始行。
 *
 * @author 季聿阶
 * @since 2022-07-11
 */
public interface StartLine {
    /**
     * 获取 Http 协议的版本。
     *
     * @return 表示 Http 协议版本的 {@link HttpVersion}。
     */
    HttpVersion httpVersion();
}
