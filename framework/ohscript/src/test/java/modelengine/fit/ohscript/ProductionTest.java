/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.Grammar;
import modelengine.fit.ohscript.script.parser.GrammarBuilder;
import modelengine.fit.ohscript.script.parser.Grammars;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.parser.Symbol;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Production测试
 *
 * @since 1.0
 */
class ProductionTest {
    final GrammarBuilder grammarBuilder = new GrammarBuilder();

    @Test
    void test_normal_production_format() {
        grammarBuilder.appendProductions("SCRIPT->[];");
        Grammars grammars = grammarBuilder.build();
        List<Symbol> symbols = grammars.start().productions().get(0).symbols();
        assertEquals(Terminal.LEFT_BRACKET, symbols.get(0).symbol());
    }

    @Test
    void test_or_operator_production_format() {
        grammarBuilder.appendProductions("SCRIPT->ID OR ID OR_OR ID;");
        Grammars grammars = grammarBuilder.build();
        List<Symbol> symbols = grammars.start().productions().get(0).symbols();
        assertEquals(Terminal.OR, symbols.get(1).symbol());
        assertEquals(Terminal.OR_OR, symbols.get(3).symbol());
    }

    @Test
    void test_multi_production_input() {
        grammarBuilder.appendProductions("SCRIPT->ID OR ID OR_OR ID;| TRUE&&TRUE&TRUE");
        Grammars grammars = grammarBuilder.build();
        assertEquals(2, grammars.start().productions().size());
        List<Symbol> symbols = grammars.start().productions().get(1).symbols();
        assertEquals(Terminal.AND_AND, symbols.get(1).symbol());
        assertEquals(Terminal.AND, symbols.get(3).symbol());
    }

    @Test
    void test_multi_level_production_input() {
        grammarBuilder.appendProductions("SCRIPT->ID IDS'");
        grammarBuilder.appendProductions("IDS'->ε|.IDS'");
        Grammars grammars = grammarBuilder.build();
        Grammar start = grammars.start();
        assertEquals(NonTerminal.SCRIPT, start.type());
        List<Symbol> roots = start.productions().get(0).symbols();
        Grammar ids = grammars.get(roots.get(1).name());
        assertEquals(NonTerminal.IGNORED, ids.type());
    }
}
