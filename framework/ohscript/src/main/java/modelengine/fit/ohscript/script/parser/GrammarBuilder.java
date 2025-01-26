/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser;

import modelengine.fit.ohscript.script.errors.GrammarSyntaxException;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.util.Tool;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 语法构建器
 *
 * @since 1.0
 */
public class GrammarBuilder {
    private final Map<String, Grammar> grammars = new HashMap<>();

    private static boolean isFirstChanged(Production production, boolean changed) {
        boolean isChanged = changed;
        boolean broken = false;
        int oldSize = production.first().size();
        for (Symbol symbol : production.symbols()) {
            broken = checkBroken(production, symbol, broken);
        }
        // if all symbol in production can produce epsilon, then add epsilon to grammar.first
        if (!broken) {
            production.first().add(Terminal.EPSILON);
        }
        if (production.first().size() > oldSize) {
            isChanged = true;
        }
        return isChanged;
    }

    private static boolean checkBroken(Production production, Symbol symbol, boolean broken) {
        boolean isBroken = broken;
        // symbol is a terminal symbol, then FIRST(this) is {symbol}.
        if (symbol.isTerminal()) {
            if (!isBroken) {
                production.first().add(ObjectUtils.cast(symbol.symbol()));
                isBroken = true;
            }
        } else {
            // if the first symbol is non-terminal, then this.first == the non-terminal.first
            Grammar grammar = production.grammar().grammars().get(symbol.name());
            if (grammar == null) {
                Tool.grammarError("symbol: " + symbol.name() + " is not found in all possible grammars");
            }
            grammar.trace();
            // if there is epsilon in this symbol FIRST, continue to compute next symbol, the next symbol
            // FIRST will be added to the production as well
            List<Terminal> grammarFirst = grammar.first();
            boolean hasEpsilon = grammarFirst.remove(Terminal.EPSILON);
            if (!isBroken) {
                production.first().addAll(grammarFirst);
            }
            // if this is epsilon, go on add the first to production;
            if (!isBroken && !hasEpsilon) {
                isBroken = true; // there is no epsilon in this symbol, do not need to traverse next symbol
            }
        }
        return isBroken;
    }

    /**
     * 添加一个产生式规则
     *
     * @param production 规则的表达式
     * @return 对应该production的语法
     */
    public Grammar appendProductions(String production) {
        String[] leftRight = production.split("->");
        if (leftRight.length != 2) {
            throw new IllegalArgumentException("production grammar left and right should be linked by a ->");
        }
        Grammar grammar = createGrammar(leftRight[0]);
        createProductions(leftRight[1], grammar);
        return grammar;
    }

    /**
     * 构建语法
     *
     * @return 构建完成的语法
     * @throws GrammarSyntaxException 如果语法构建过程中存在错误，则抛出该异常
     */
    public Grammars build() {
        Grammars gs = new Grammars(grammars);
        this.verify(gs);
        return gs;
    }

    private void verify(Grammars grammars) throws GrammarSyntaxException {
        // verify start
        verifyStart(grammars);
        // verify other grammars
        this.computeFirsts();
        this.computeFollows();
        // print not traced grammars
        printGrammarWarning(grammars);
    }

    /**
     * 去掉头部所有的terminal，留下尾部第一个terminal
     * trim(abCdef) = Cd;
     *
     * @param origin input symbol list
     * @return trimmed symbol list
     */
    private List<Symbol> trimSProductionSymbols(List<Symbol> origin) {
        List<Symbol> symbols = new ArrayList<>();
        boolean onHead = false;
        List<Symbol> buffer = new ArrayList<>();
        for (Symbol symbol : origin) {
            if (symbol.isTerminal()) {
                if (onHead) {
                    buffer.add(symbol);
                }
            } else {
                if (!onHead) {
                    onHead = true;
                } else {
                    symbols.addAll(buffer);
                    buffer.clear();
                }
                symbols.add(symbol);
            }
        }
        if (buffer.size() > 0) {
            symbols.add(buffer.get(0));
        }
        return symbols;
    }

    /**
     * 计算所有产生式的FIRST集
     * 对于每一个产生式，计算它的FIRST集，如果有一个产生式的FIRST集发生变化，那么继续计算，直到所有的产生式的FIRST集都不再变化
     */
    private void computeFirsts() {
        // get all productions
        List<Production> productions = new ArrayList<>();
        grammars.values().forEach(g -> productions.addAll(g.productions()));
        // traverse productions multi times until there is no change for all grammar.follow
        boolean changed;
        do {
            changed = false;
            for (Production production : productions) {
                changed = isFirstChanged(production, changed);
            }
        } while (changed);
    }

    private void computeFollows() {
        // get all productions
        List<Production> productions = new ArrayList<>();
        grammars.values().forEach(g -> productions.addAll(g.productions()));
        // traverse productions multi times until there is no change for all grammar.follow
        boolean changed;
        do {
            changed = false;
            for (Production production : productions) {
                changed = isFollowChanged(production, changed);
            }
        } while (changed);
    }

    private boolean isFollowChanged(Production production, boolean changed1) {
        boolean isChanged = changed1;
        Grammar grammar = production.grammar();
        // trim production symbols
        List<Symbol> symbols = this.trimSProductionSymbols(production.symbols());
        Set<Terminal> first = new HashSet<>(); // next first is my follow
        boolean containsEmpty = true;
        for (int i = symbols.size() - 1; i >= 0; i--) {
            Symbol symbol = symbols.get(i);
            // from right to left, if there are terminal from right
            if (symbol.isTerminal()) {
                first.clear();
                first.add(ObjectUtils.cast(symbol.symbol));
                containsEmpty = false;
                continue;
            }
            Grammar g = grammars.get(symbol.name()); // a non-terminal grammar in this production
            int oldSize = g.follow().size();
            // if the rest produce empty, add parent follow to my follow
            if (containsEmpty) {
                g.follow().addAll(grammar.follow());
            }
            // next first add to my follow
            g.follow().addAll(first);
            if (!g.first().contains(Terminal.EPSILON)) { // if I don't have epsilon, the previous grammar will only
                // have my first, or it will have my follow
                first.clear();
                containsEmpty = false;
            }
            first.addAll(g.first());
            if (g.follow().size() > oldSize) {
                isChanged = true;
            }
        }
        return isChanged;
    }

    private void printGrammarWarning(Grammars grammars) {
        grammars.grammars()
                .stream()
                .filter(grammar -> !grammar.traced())
                .forEach(g -> Tool.warn("Warning: grammar " + g.name() + " is not traced"));
    }

    private void verifyStart(Grammars grammars) {
        Grammar start = grammars.get("SCRIPT");
        if (start == null) {
            Tool.grammarError("there must be one start grammar with name: SCRIPT");
        }
        if (!Objects.equals(start.type(), NonTerminal.SCRIPT)) {
            Tool.grammarError("script grammar must be NonTerminal.SCRIPT");
        }
        if (grammars.grammars().stream().filter(g -> Objects.equals(g.type(), NonTerminal.SCRIPT)).count() > 1) {
            Tool.grammarError("there must be only one start grammar with name: SCRIPT");
        }
        start.trace();
    }

    private Grammar createGrammar(String grammarName) {
        NonTerminal nonTerminal = NonTerminal.valueFrom(grammarName);
        Grammar grammar = grammars.get(grammarName);
        if (grammar == null) {
            grammar = new Grammar(nonTerminal, grammarName);
            grammars.put(grammarName, grammar);
        }
        return grammar;
    }

    private void createProductions(String expression, Grammar grammar) {
        String[] ps = expression.split("\\s*\\|\\s*");
        for (String p : ps) {
            new Production(grammar, p);
        }
    }
}
