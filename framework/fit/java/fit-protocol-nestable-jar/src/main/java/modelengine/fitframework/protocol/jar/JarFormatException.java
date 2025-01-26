/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import java.io.IOException;

/**
 * 当 JAR 的格式不正确时引发的异常。
 *
 * @author 梁济时
 * @since 2022-09-16
 */
public class JarFormatException extends IOException {
    /**
     * 使用异常信息初始化 {@link JarFormatException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public JarFormatException(String message) {
        super(message);
    }
}
