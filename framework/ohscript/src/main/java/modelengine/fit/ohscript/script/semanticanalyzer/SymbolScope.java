/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.nodes.EntityDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityExtensionNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.ArgumentEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.ArrayEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.EntityEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.ExtensionEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.FunctionEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.IdentifierEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.MapEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.SymbolEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.UnknownSymbolEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts.GenericTypeExpr;
import modelengine.fitframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 符号范围
 *
 * @since 1.0
 */
public class SymbolScope implements Serializable {
    private static final long serialVersionUID = -6911172423493910959L;

    private final long id;

    private final Map<String, SymbolEntry> symbols = new HashMap<>();

    private long parentId;

    /**
     * 构造函数
     *
     * @param id 符号范围ID
     * @param parentId 父符号范围ID
     */
    public SymbolScope(long id, long parentId) {
        this.id = id;
        this.parentId = parentId;
    }

    /**
     * 添加标识符
     *
     * @param node 标识符节点
     * @param mutable 是否可变
     * @return 标识符入口
     */
    public IdentifierEntry addIdentifier(TerminalNode node, boolean mutable) {
        return this.addIdentifier(node, mutable, null);
    }

    /**
     * 添加标识符
     *
     * @param node 标识符节点
     * @param mutable 是否可变
     * @param host 宿主节点
     * @return 标识符入口
     */
    public IdentifierEntry addIdentifier(TerminalNode node, boolean mutable, SyntaxNode host) {
        IdentifierEntry entry = ObjectUtils.cast(this.symbols.get(node.lexeme()));
        if (entry != null) {
            node.panic(SyntaxError.ARGUMENT_ALREADY_DEFINED);
            return null;
        }
        entry = new IdentifierEntry(node, this.id(), mutable, host);
        this.symbols.put(node.lexeme(), entry);
        return entry;
    }

    /**
     * 获取符号范围ID
     *
     * @return 符号范围ID
     */
    public long id() {
        return this.id;
    }

    /**
     * 获取符号范围内的符号
     *
     * @param lexeme 符号名
     * @return 符号入口
     */
    public SymbolEntry getSymbol(String lexeme) {
        return symbols.get(lexeme);
    }

    /**
     * 获取父符号范围ID
     *
     * @return 父符号范围ID
     */
    public long getParent() {
        return this.parentId;
    }

    /**
     * 设置父符号范围ID
     *
     * @param parentId 父符号范围ID
     */
    public void setParent(long parentId) {
        this.parentId = parentId;
    }

    /**
     * 添加函数
     *
     * @param node 函数声明节点
     * @return 函数入口
     */
    public SymbolEntry addFunction(FunctionDeclareNode node) {
        SymbolEntry old = this.symbols.get(node.functionName().lexeme());
        if (old != null) {
            node.functionName().panic(SyntaxError.FUNCTION_ALREADY_DEFINED);
            return new UnknownSymbolEntry(node.functionName());
        }
        GenericTypeExpr argumentType = TypeExprFactory.createGeneric(node.argument());
        GenericTypeExpr returnType = TypeExprFactory.createGeneric(node);
        FunctionEntry entry = new FunctionEntry(node.functionName(), this.id(),
                TypeExprFactory.createFunction(node, argumentType, returnType));
        this.symbols.put(node.functionName().lexeme(), entry);
        return entry;
    }

    /**
     * 添加参数
     *
     * @param node 参数节点
     * @return 参数入口
     */
    public SymbolEntry addArgument(TerminalNode node) {
        if (node.nodeType() == Terminal.UNIT) {
            return ArgumentEntry.unit();
        }
        SymbolEntry entry = this.symbols.get(node.lexeme());
        if (entry != null) {
            node.panic(SyntaxError.ARGUMENT_ALREADY_DEFINED);
            return new UnknownSymbolEntry(node);
        }
        entry = new ArgumentEntry(node, this.id());
        this.symbols.put(node.lexeme(), entry);
        return entry;
    }

    /**
     * 添加实体
     *
     * @param node 实体声明节点
     * @return 实体入口
     */
    public SymbolEntry addEntity(EntityDeclareNode node) {
        EntityEntry entry = ObjectUtils.cast(this.symbols.get(node.declaredName().lexeme()));
        if (entry != null) {
            node.panic(SyntaxError.ENTITY_ALREADY_DEFINED);
            return new UnknownSymbolEntry(node.declaredName());
        }
        entry = new EntityEntry(node.declaredName(), this.id(), TypeExprFactory.createEntity(node, new HashMap<>()));
        this.symbols.put(node.declaredName().lexeme(), entry);
        return entry;
    }

    /**
     * 添加实体扩展
     *
     * @param node 实体扩展节点
     * @return 实体扩展入口
     */
    public SymbolEntry addEntityExtension(EntityExtensionNode node) {
        ExtensionEntry entry = ObjectUtils.cast(this.symbols.get(node.declaredName().lexeme()));
        if (entry != null) {
            node.panic(SyntaxError.EXTENSION_UPEXPECTED_ERROR);
            return new UnknownSymbolEntry(node.declaredName());
        }
        entry = new ExtensionEntry(node.declaredName(), this.id(),
                TypeExprFactory.createExtension(node, new HashMap<>()));
        this.symbols.put(node.declaredName().lexeme(), entry);
        return entry;
    }

    /**
     * 添加数组
     *
     * @param node 数组节点
     * @return 数组入口
     */
    public SymbolEntry addArray(SyntaxNode node) {
        ArrayEntry entry = new ArrayEntry(node.declaredName(), this.id());
        this.symbols.put(node.declaredName().lexeme(), entry);
        return entry;
    }

    /**
     * 添加映射
     *
     * @param node 映射节点
     * @return 映射入口
     */
    public SymbolEntry addMap(SyntaxNode node) {
        MapEntry entry = new MapEntry(node.declaredName(), this.id());
        this.symbols.put(node.declaredName().lexeme(), entry);
        return entry;
    }
}
