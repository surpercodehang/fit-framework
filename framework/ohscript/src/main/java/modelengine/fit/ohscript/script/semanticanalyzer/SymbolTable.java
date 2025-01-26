/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer;

import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.SymbolEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.UnknownTypeExpr;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fitframework.log.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 符号表
 *
 * @since 1.0
 */
public class SymbolTable implements Serializable {
    private static final long serialVersionUID = 5691933983785920L;

    private static final Logger log = Logger.get(SymbolTable.class);

    private final Map<Long, SymbolScope> scopes = new HashMap<>();

    /**
     * 添加一个新的符号作用域
     *
     * @param scopeId 新的符号作用域的ID
     * @param parentScopeId 新的符号作用域的父作用域ID
     * @return 新添加的符号作用域
     */
    public SymbolScope addScope(long scopeId, long parentScopeId) {
        return scopes.computeIfAbsent(scopeId, k -> new SymbolScope(k, parentScopeId));
    }

    /**
     * 根据scopeId清理scope
     *
     * @param scopeId 待清理的scopeId
     * @return 被清理的scope
     */
    public SymbolScope removeScope(long scopeId) {
        return scopes.remove(scopeId);
    }

    /**
     * 添加一个新的符号作用域
     *
     * @param scope 新的符号作用域
     * @return 新添加的符号作用域
     */
    public SymbolScope addScope(SymbolScope scope) {
        return scopes.computeIfAbsent(scope.id(), k -> scope);
    }

    /**
     * 获取指定ID的符号作用域
     *
     * @param scope 符号作用域ID
     * @return 符号作用域
     */
    public SymbolScope getScope(long scope) {
        return scopes.get(scope);
    }

    /**
     * 在指定的作用域中获取指定词法标识符的符号条目
     *
     * @param lexeme 词法标识符
     * @param scopeId 作用域ID
     * @return 符号条目
     */
    public SymbolEntry getSymbol(String lexeme, long scopeId) {
        // get the current scope
        SymbolScope scope = this.getScope(scopeId);
        if (scope == null) {
            return null;
        }
        SymbolEntry symbol = scope.getSymbol(lexeme);
        // it lexeme is an entity member, try to find if the symbol is in base scope
        if (symbol != null || !lexeme.startsWith(Constants.DOT)) {
            return tryLookUpInParent(lexeme, scopeId, symbol, scope);
        }
        SymbolEntry base = scope.getSymbol(Constants.DOT + Constants.BASE);
        if (base == null || base.typeExpr() instanceof UnknownTypeExpr) {
            return tryLookUpInParent(lexeme, scopeId, symbol, scope);
        }
        while (base != null) {
            scope = this.getScope(base.typeExpr().node().scope());
            symbol = scope.getSymbol(lexeme);
            if (symbol != null) {
                return symbol;
            } else {
                base = scope.getSymbol(Constants.DOT + Constants.BASE);
            }
        }
        return tryLookUpInParent(lexeme, scopeId, symbol, scope);
    }

    private SymbolEntry tryLookUpInParent(String lexeme, long scopeId, SymbolEntry symbol, SymbolScope scope) {
        // if symbol is not found, try to look up it in parent scope
        if (symbol != null || scopeId < 0) {
            return symbol;
        }
        return this.getSymbol(lexeme, scope.getParent());
    }
}
