/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.errors.ScriptExecutionException;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.ActivationContext;
import modelengine.fit.ohscript.script.interpreter.Interpreter;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.util.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 非终结节点
 *
 * @since 1.0
 */
public abstract class NonTerminalNode extends SyntaxNode {
    private static final Map<String, Interpreter> interpreterMap = new ConcurrentHashMap<>();

    /**
     * 非终结符类型
     */
    protected NonTerminal nodeType;

    /**
     * comments is important to give LLM context
     * LLM will make decision based on the comments attached any statement
     */
    private CommentsNode comments = null;

    private Location location = null;

    /**
     * 构造函数
     *
     * @param nodeType 非终结符类型
     */
    protected NonTerminalNode(NonTerminal nodeType) {
        this.nodeType = nodeType;
        if (nodeType != null && nodeType.ownScope()) {
            this.scope = Tool.newId();
        }
    }

    /**
     * 判断是否为开始节点
     *
     * @return 是否为开始节点
     */
    public boolean isStart() {
        return nodeType.isStart();
    }

    @Override
    public NonTerminal nodeType() {
        return this.nodeType;
    }

    @Override
    public String lexeme() {
        String lexeme = "";
        for (SyntaxNode child : this.children()) {
            lexeme += " " + child.lexeme().trim() + " ";
        }
        lexeme = lexeme.replace(" .", ".")
                .replace(" {", "{")
                .replace("} ", "}")
                .replace(" (", "(")
                .replace(") ", ")")
                .replace(" [", "[")
                .replace("] ", "]");
        final int len = 40;
        return lexeme.length() > len ? lexeme.substring(0, len) + "..." : lexeme;
    }

    @Override
    public Location location() {
        if (location == null) {
            int sl = 10000;
            int el = 0;
            int sp = 0;
            int ep = 0;
            for (SyntaxNode child : this.children()) {
                Location loc = child.location();
                if (loc == null) {
                    continue;
                }
                if (loc.startLine() < sl) {
                    sl = loc.startLine();
                    sp = loc.startPosition();
                }
                if (loc.endLine() > el) {
                    el = loc.endLine();
                    ep = loc.endPosition();
                }
            }
            location = new Location(sl, el, sp, ep);
        }
        return location;
    }

    /**
     * returnable to make sure when return statement occurs, the non-terminal is the return boundary of the return
     * statement
     */
    @Override
    public void optimizeAlpha() {
        List<SyntaxNode> nodes = new ArrayList<>();
        this.polishNode(this, nodes);
        this.refreshChildren(nodes);
    }

    /**
     * 对节点进行优化处理
     *
     * @param parent 父节点
     * @param nodes 节点列表
     */
    protected void polishNode(NonTerminalNode parent, List<SyntaxNode> nodes) {
        for (int i = 0; i < parent.childCount(); i++) {
            SyntaxNode n = parent.child(i);
            if (n instanceof NonTerminalNode && ((NonTerminalNode) n).isNodeIgnored()) {
                continue;
            }
            if (n.nodeType() == this.nodeType() || n.nodeType()
                    == NonTerminal.IGNORED) { // if there is recursive, extract all recursive node children back to the
                // root node
                this.polishNode((NonTerminalNode) n, nodes);
            } else {
                nodes.add(n);
            }
        }
    }

    /**
     * 判断节点是否被忽略
     *
     * @return 是否被忽略
     */
    protected boolean isNodeIgnored() {
        return this.childCount() == 0;
    }

    @Override
    public void optimizeBeta() {
        if (this.parent() == null) {
            return;
        }
        if (this.childCount() == 1) {
            SyntaxNode child = this.child(0);
            this.parent().replaceChild(this, child);
        }
    }

    @Override
    public void semanticCheck() {
        this.children().forEach(node -> node.semanticCheck());
    }

    /**
     * 删除指定索引的子节点
     *
     * @param index 索引
     * @return 被删除的子节点
     */
    public SyntaxNode removeAt(int index) {
        SyntaxNode node = this.nodes.remove(index);
        node.setParent(null);
        return node;
    }

    @Override
    public ReturnValue interpret(ASTEnv env, ActivationContext context) throws OhPanic {
        try {
            Interpreter interpreter = getInterpreter();
            return interpreter.interpret(this, env, context);
        } catch (OhPanic ex) {
            if (ex.node() == null) {
                throw new OhPanic(ex.getMessage(), this, this.location().startLine(), ex.code());
            } else {
                throw ex;
            }
        } catch (ScriptExecutionException ex) {
            if (ex.node() == null) {
                throw new ScriptExecutionException(ex, this, this.location().startLine());
            } else {
                throw ex;
            }
        } catch (Exception ex) {
            throw new ScriptExecutionException(ex, this, this.location().startLine());
        }
    }

    @Override
    public void assignValue(ReturnValue value, ASTEnv env, ActivationContext current) throws OhPanic {
        Interpreter interpreter = getInterpreter();
        interpreter.assignValue(this, value, env, current);
    }

    /**
     * 获取解释器实例
     *
     * @return 解释器实例
     */
    private Interpreter getInterpreter() {
        return this.getInterpreter(this.nodeType.name());
    }

    /**
     * 通过名称获取解释器，增加了缓存
     *
     * @param name 解释器名称
     * @return 解释器
     */
    private Interpreter getInterpreter(String name) {
        Interpreter interpreter = interpreterMap.get(name);
        if (interpreter == null) {
            Interpreter result;
            try {
                result = Interpreter.valueOf(name);
            } catch (IllegalArgumentException e) {
                result = Interpreter.GENERAL;
            }
            interpreter = result;
            interpreterMap.put(name, interpreter);
        }
        return interpreter;
    }

    /**
     * 设置注释节点
     *
     * @param commentsNode 注释节点
     */
    public void setComments(CommentsNode commentsNode) {
        this.comments = commentsNode;
    }

    /**
     * 获取注释节点
     *
     * @return 注释节点
     */
    public CommentsNode comments() {
        return this.comments;
    }
}
