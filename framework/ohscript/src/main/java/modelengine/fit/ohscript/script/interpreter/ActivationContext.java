/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.interpreter;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.ArgumentEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.IdentifierEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.SymbolEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.UnknownSymbolEntry;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fit.ohscript.util.Tool;
import modelengine.fitframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 一次函数调用或语句块的执行状态
 *
 * @since 1.0
 */
public class ActivationContext {
    private static final String MEMBER_FLAG = ".";

    private final long id;

    private final long scope;

    private final Map<String, ReturnValue> symbolTable = new HashMap<>();

    private final ActivationContext inClosure;

    private final ASTEnv env;

    /**
     * 构造函数
     *
     * @param scope 当前上下文的作用域
     * @param inClosure 外部上下文
     * @param env 当前上下文的环境
     */
    public ActivationContext(long scope, ActivationContext inClosure, ASTEnv env) {
        this.id = Tool.newId();
        this.scope = scope;
        this.inClosure = inClosure;
        this.env = env;
    }

    /**
     * 获取当前上下文的ID
     *
     * @return 当前上下文的ID
     */
    public long id() {
        return this.id;
    }

    /**
     * 获取当前上下文的作用域
     *
     * @return 当前上下文的作用域
     */
    public long scope() {
        return this.scope;
    }

    /**
     * 将符号节点和对应的值放入符号表
     *
     * @param node 符号节点
     * @param value 符号值
     * @throws OhPanic 抛出OhPanic异常
     */
    public synchronized void put(TerminalNode node, ReturnValue value) throws OhPanic {
        String name = node.lexeme();
        ReturnValue current = this.get(node);
        if (current == null || current == ReturnValue.UNKNOWN) {
            ActivationContext context = this;
            while (context.scope() != node.symbolEntry().scope() && context.inClosure != null) {
                context = context.inClosure;
            }
            context.symbolTable.put(name, value);
        } else {
            current.update(value);
        }
    }

    /**
     * 将"this"值放入符号表
     *
     * @param value "this"值
     */
    public void putThis(ReturnValue value) {
        this.symbolTable.put(Constants.THIS, value);
    }

    /**
     * 将所有符号和对应的值放入符号表
     *
     * @param all 符号和对应的值的映射
     */
    public void putAll(Map<String, ReturnValue> all) {
        this.symbolTable.putAll(all);
    }

    private long getScope(SymbolEntry entry) {
        if (entry instanceof IdentifierEntry) {
            return entry.scope();
        }
        return (entry instanceof UnknownSymbolEntry || entry instanceof ArgumentEntry)
                ? entry.node().scope()
                : entry.node().parentScope();
    }

    /**
     * 根据符号节点获取符号表中的值
     *
     * @param node 符号节点
     * @return 符号值
     */
    public ReturnValue get(TerminalNode node) {
        String name = node.lexeme();
        // "this" is very different
        if (StringUtils.equals(name, Constants.THIS)) {
            return this.get(Constants.THIS);
        }
        // find the symbol entry of terminal node, which has been defined in symbolize phase
        SymbolEntry entry = node.symbolEntry();
        long entryScope = entry.scope();
        ReturnValue value = this.get(name, entryScope);
        if (value == null) {
            value = this.get(MEMBER_FLAG + name, entryScope);
        }
        return value;
    }

    /**
     * 根据名称和作用域获取符号表中的值
     *
     * @param name 符号名称
     * @param scope 符号作用域
     * @return 符号值
     */
    public ReturnValue get(String name, long scope) {
        if (this.scope() != scope) {
            if (this.inClosure == null) {
                return null;
            } else {
                return this.inClosure.get(name, scope);
            }
        } else {
            return this.symbolTable.get(name);
        }
    }

    /**
     * 根据名称获取符号表中的值
     *
     * @param name 符号名称
     * @return 符号值
     */
    public ReturnValue get(String name) {
        ReturnValue value = this.symbolTable.get(name);
        if (value != null) {
            return value;
        }
        if (this.inClosure != null) {
            return this.inClosure.get(name);
        } else {
            return null;
        }
    }

    /**
     * 获取符号表中的"this"值
     *
     * @return "this"值
     */
    public ReturnValue getThis() {
        return this.get(Constants.THIS);
    }

    /**
     * 从符号表中移除"this"值
     */
    public void removeThis() {
        this.symbolTable.remove(Constants.THIS);
    }

    /**
     * 返回当前上下文的符号表
     *
     * @return 当前上下文的符号表
     */
    public Map<String, ReturnValue> all() {
        return new HashMap<>(this.symbolTable);
    }

    /**
     * 返回当前上下文的环境
     *
     * @return 当前上下文的环境
     */
    public ASTEnv env() {
        return this.env;
    }

    /**
     * 返回根上下文
     *
     * @return 根上下文
     */
    public ActivationContext root() {
        ActivationContext context = this;
        while (context.inClosure != null && context.inClosure.env() != null) {
            context = context.inClosure;
        }
        return context;
    }
}
