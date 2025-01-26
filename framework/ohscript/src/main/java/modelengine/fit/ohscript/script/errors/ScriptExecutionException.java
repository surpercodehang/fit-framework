/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.errors;

import modelengine.fit.ohscript.script.parser.nodes.NonTerminalNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;

/**
 * 表示脚本执行过程中发生的异常。
 * <p>该异常需要向外透出，由 Java 执行端捕获。</p>
 *
 * @author 季聿阶
 * @since 2023-12-15
 */
public class ScriptExecutionException extends RuntimeException {
    private final SyntaxNode node;

    /**
     * 使用异常信息来初始化 {@link ScriptExecutionException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public ScriptExecutionException(String message) {
        super(message);
        this.node = null;
    }

    /**
     * 使用异常信息和异常原因来初始化 {@link ScriptExecutionException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public ScriptExecutionException(String message, Throwable cause) {
        super(message, cause);
        node = null;
    }

    /**
     * 使用异常原因、非终端节点和起始行来初始化 {@link ScriptExecutionException} 类的新实例。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     * @param node 表示非终端节点的 {@link NonTerminalNode}。
     * @param startLine 表示起始行的 {@link int}。
     */
    public ScriptExecutionException(Throwable cause, NonTerminalNode node, int startLine) {
        super(cause.getMessage() + System.lineSeparator() + " at " + node.name() + System.lineSeparator() + " at line "
                + startLine, cause);
        this.node = node;
    }

    /**
     * 使用异常信息和异常原因来初始化 {@link ScriptExecutionException} 类的新实例。
     *
     * @param e 表示异常原因的 {@link OhPanic}。
     */
    public ScriptExecutionException(OhPanic e) {
        super(e.getMessage(), e);
        this.node = e.node();
    }

    /**
     * 使用异常信息和异常原因来初始化 {@link ScriptExecutionException} 类的新实例。
     *
     * @param e 表示异常原因的 {@link Exception}。
     */
    public ScriptExecutionException(Exception e) {
        super(e.getMessage(), e);
        this.node = null;
    }

    /**
     * 获取引发异常的非终端节点。
     *
     * @return 表示非终端节点的 {@link SyntaxNode}。
     */
    public SyntaxNode node() {
        return this.node;
    }
}
