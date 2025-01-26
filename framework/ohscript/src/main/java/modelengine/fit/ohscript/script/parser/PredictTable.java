/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.util.Tool;

import java.util.HashMap;
import java.util.Map;

/**
 * 预测表
 *
 * @since 1.0
 */
public class PredictTable {
    private final Map<Terminal, Map<Grammar, Production>> table = new HashMap<>();

    /**
     * 构建预测表
     *
     * @param grammars 语法表
     */
    public PredictTable(Grammars grammars) {
        grammars.grammars().forEach(grammar -> grammar.productions().forEach(production -> {
            if (production.isEmpty()) {
                grammar.follow().forEach(symbol -> addMapping(grammar, production, symbol));
            }
            production.first().forEach(symbol -> addMapping(grammar, production, symbol));
        }));
    }

    private void addMapping(Grammar grammar, Production production, Terminal symbol) {
        Map<Grammar, Production> firsts = table.computeIfAbsent(symbol, k -> new HashMap<>());
        firsts.put(grammar, production);
    }

    /**
     * 匹配产生式
     *
     * @param token 符号
     * @param grammar 语法
     * @param line 行数，用于指导报错
     * @return 匹配到的产生式
     */
    public Production match(Token token, Grammar grammar, String line) {
        Terminal terminal = token.tokenType();
        Map<Grammar, Production> productions = table.get(terminal);
        String lineSeparator = System.lineSeparator();
        if (productions == null) {
            Tool.grammarError(
                    "`" + terminal.text() + "` is not found in predict table" + lineSeparator + line);
        }
        Production production = productions.get(grammar);
        if (production == null) {
            StringBuilder sb = new StringBuilder();
            for (Production p : productions.values()) {
                sb.append(p.display() + lineSeparator);
            }
            Tool.grammarError("token: " + terminal.name() + ":" + token.lexeme()
                    + " doesn't match any ohScript grammar definition at line: " + token.line() + ", position from "
                    + token.start() + " to " + token.end() + lineSeparator + line
                    + lineSeparator + " the possible productions should be matched would be:" + sb);
        }
        return production;
    }
}
