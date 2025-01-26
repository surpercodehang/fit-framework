/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

/**
 * 当类文件格式不正确时引发的异常。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public class IllegalClassFormatException extends RuntimeException {
    /**
     * 使用异常信息初始化 {@link IllegalClassFormatException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public IllegalClassFormatException(String message) {
        super(message);
    }
}
