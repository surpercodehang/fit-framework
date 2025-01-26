/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.errors;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;

/**
 * 严重的不恢复错误
 *
 * @since 1.0
 */
public class OhPanic extends Exception {
    private final SyntaxNode node;

    private final Integer code;

    /**
     * 创建一个新的OhPanic错误
     *
     * @param message 错误信息
     * @param node 出错的语法节点
     * @param line 出错的行号
     * @param code 错误代码
     */
    public OhPanic(String message, SyntaxNode node, Integer line, Integer code) {
        super(message + System.lineSeparator() + "at " + node.lexeme() + " --- at line " + line);
        this.node = node;
        this.code = code;
    }

    /**
     * 创建一个新的OhPanic错误
     *
     * @param message 错误信息
     * @param code 错误代码
     */
    public OhPanic(String message, Integer code) {
        super(message);
        this.node = null;
        this.code = code == null ? 1 : code;
    }

    /**
     * 获取出错的语法节点
     *
     * @return 出错的语法节点
     */
    public SyntaxNode node() {
        return this.node;
    }

    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    public Integer code() {
        return code;
    }
}
