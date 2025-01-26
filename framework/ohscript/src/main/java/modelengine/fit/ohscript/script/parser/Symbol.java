/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.NodeType;

/**
 * 符号，终结符或者非终结符
 * Symbol is used as production element type
 * the symbol could be terminal or non-terminal
 * name is basically the terminal or non-terminal name, except NonTerminal.
 * IGNORE could have different name with format:  ***'
 *
 * @author 张群辉
 * @since  2023-05-01
 */
public class Symbol<T extends NodeType> {
    /**
     * 符号
     *
     * @param symbol 符号
     */
    protected final T symbol;

    private final String name;

    /**
     * 构造函数
     *
     * @param symbol 符号
     * @param name 名称
     */
    public Symbol(T symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

    /**
     * 是否是终结类型
     *
     * @return 是否是Terminal
     */
    public boolean isTerminal() {
        return symbol() instanceof Terminal;
    }

    /**
     * 符号
     *
     * @return 符号
     */
    public T symbol() {
        return symbol;
    }

    /**
     * 名称
     *
     * @return 名称
     */
    public String name() {
        return this.name;
    }
}
