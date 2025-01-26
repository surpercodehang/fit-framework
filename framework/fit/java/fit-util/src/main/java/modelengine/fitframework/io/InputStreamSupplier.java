/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 为 {@link InputStream} 提供供应程序。
 *
 * @author 梁济时
 * @since 2023-01-12
 */
@FunctionalInterface
public interface InputStreamSupplier {
    /**
     * 获取输入流实例。
     *
     * @return 表示输入流实例的 {@link InputStream}。
     * @throws IOException 打开输入流过程发生输入输出异常。
     */
    InputStream get() throws IOException;
}
