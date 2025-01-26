/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.util.Tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 表示一个产生式规则，即一个非终结符可以被替换为的一系列符号
 * right side of a grammar: left non-terminal-> right production
 *
 * @author 张群辉
 * @since 2023-05-01
 */
public class Production implements Iterable<Symbol> {
    /**
     * 存储产生式规则的符号序列，包含终结符和非终结符
     */
    private final List<Symbol> symbols = new ArrayList<>();

    /**
     * 关联的 Grammar 对象，即这个产生式属于哪个语法规则
     */
    private final Grammar grammar;

    private final String display;

    /**
     * 在语法分析过程中，存储该产生式的 FIRST 集合。
     */
    private final Set<Terminal> first = new HashSet<>();

    /**
     * 构造一个产生式对象
     *
     * @param grammar 关联的语法规则
     * @param production 产生式字符串，即一系列的符号
     */
    public Production(Grammar grammar, String production) {
        this.display = grammar.name() + "->" + production;
        String[] words = this.formatProductionString(production).trim().split("\\s+");
        for (String word : words) {
            Terminal terminal = Terminal.valueFrom(word);
            if (terminal == null) {
                NonTerminal non = NonTerminal.valueFrom(word);
                if (non == null) {
                    throw new IllegalArgumentException("symbol: " + word + " is not defined in ohScript engine.");
                } else {
                    this.symbols.add(new Symbol<>(non, word));
                }
            } else {
                this.symbols.add(new Symbol<>(terminal, word));
            }
        }
        this.grammar = grammar;
        this.verifyLeftRecursion();
        grammar.productions().add(this);
    }

    private void verifyLeftRecursion() {
        Symbol firstSymbol = this.symbols().get(0); // check the first symbol
        if (!firstSymbol.isTerminal() && firstSymbol.name().equals(this.grammar.name())) {
            Tool.grammarError("there is left recursion grammar in given input");
        }
    }

    private String formatProductionString(String productionStr) {
        String handledProductionStr = productionStr.replace("`|`", Terminal.OR.name());
        handledProductionStr = handledProductionStr.replace("`||`", Terminal.OR_OR.name());
        for (Terminal value : Terminal.values()) {
            handledProductionStr = handledProductionStr.replace(value.text(), " " + value.name() + " ");
            handledProductionStr = handledProductionStr.replace(value.text(), " " + value.name() + " ");
        }
        return handledProductionStr;
    }

    /**
     * 产生式对应的语法，一个语法可以有多个产生式
     *
     * @return 语法
     */
    public Grammar grammar() {
        return this.grammar;
    }

    /**
     * 返回产生式的符号序列，包含终结符和非终结符
     *
     * @return 符号序列
     */
    public List<Symbol> symbols() {
        return new ArrayList<>(this.symbols);
    }

    /**
     * 判断产生式是否为空产生式
     *
     * @return 如果产生式是空产生式，返回true，否则返回false
     */
    public boolean isEmpty() {
        return this.symbols.size() == 1 && this.symbols.get(0).symbol() == Terminal.EPSILON;
    }

    /**
     * 计算此产生式的 FIRST 集合
     *
     * @return 终结符列表
     */
    public Set<Terminal> first() {
        return this.first;
    }

    @Override
    public Iterator<Symbol> iterator() {
        return this.symbols.iterator();
    }

    @Override
    public void forEach(Consumer<? super Symbol> action) {
        for (Symbol symbol : this.symbols) {
            action.accept(symbol);
        }
    }

    /**
     * 返回产生式的字符串表示形式，格式为：非终结符->产生式
     *
     * @return 产生式的字符串表示形式
     */
    public String display() {
        return this.display;
    }
}
