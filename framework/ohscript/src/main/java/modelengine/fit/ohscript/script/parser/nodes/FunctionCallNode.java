/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 方法调用节点
 *
 * @since 1.0
 */
public class FunctionCallNode extends CallNode {
    private SyntaxNode functionName = null;

    public FunctionCallNode() {
        super(NonTerminal.FUNC_CALL);
    }

    /**
     * 创建一个模拟的函数调用节点
     *
     * @param name 函数名称
     * @param args 函数参数列表
     * @return 模拟的函数调用节点
     */
    public static FunctionCallNode mock(SyntaxNode name, Set<SyntaxNode> args) {
        FunctionCallNode call = new FunctionCallNode();
        args.forEach(arg -> call.addChild(arg));
        call.addChild(name);
        call.functionName = name;
        return call;
    }

    /**
     * 获取函数名称
     *
     * @return 函数名称
     */
    public SyntaxNode functionName() {
        return this.child(this.childCount() - 1);
    }

    @Override
    public void optimizeGama() {
        if (functionName != null) {
            return;
        }
        super.optimizeGama();
        if (functionName != null) {
            return;
        }

        List<SyntaxNode> children = this.children().stream().filter(c -> {
            if (c instanceof TerminalNode) {
                TerminalNode node = (TerminalNode) c;
                return node.nodeType() != Terminal.LEFT_PAREN && node.nodeType() != Terminal.RIGHT_PAREN
                        && node.nodeType() != Terminal.COMMA;
            }
            return true;
        }).collect(Collectors.toList());
        if (this.functionName == null) {
            this.functionName = children.remove(0);
        }

        if (children.size() == 0) {
            children.add(TerminalNode.unit());
        }
        this.nodes.clear();
        for (SyntaxNode child : children) {
            this.addChild(child);
            child.optimizeGama();
        }
        this.addChild(this.functionName);
    }

    /**
     * 获取函数调用的参数列表
     *
     * @return 函数调用的参数列表
     */
    public List<SyntaxNode> args() {
        List<SyntaxNode> children = this.children();
        children.remove(children.size() - 1);
        return children;
    }

    @Override
    public String lexeme() {
        String args = "";
        for (SyntaxNode arg : this.args()) {
            args += arg.lexeme() + ",";
        }
        if (args.length() > 0) {
            args = args.substring(0, args.length() - 1);
        }
        return (this.functionName == null ? "func" : this.functionName.lexeme()) + "(" + args + ")";
    }
}
