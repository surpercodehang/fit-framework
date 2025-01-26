/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.codereader;

/**
 * 代码源
 *
 * @since 1.0
 */
public interface CodeReader {
    /**
     * 读取一行代码
     *
     * @return 返回读取的一行代码
     */
    String readLine();

    /**
     * 关闭代码源
     */
    void close();
}
