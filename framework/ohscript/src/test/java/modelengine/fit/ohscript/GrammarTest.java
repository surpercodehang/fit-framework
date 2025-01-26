/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.ohscript.script.lexer.Lexer;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.Grammar;
import modelengine.fit.ohscript.script.parser.GrammarBuilder;
import modelengine.fit.ohscript.script.parser.Grammars;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.parser.ParserBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 语法测试
 *
 * @since 1.0
 */
class GrammarTest {
    private Grammars grammars;

    @BeforeEach
    void setup() {
        ParserBuilder parserBuilder = new ParserBuilder(new GrammarBuilder(), new Lexer());
        this.grammars = parserBuilder.grammars();
    }

    @Test
    void test_grammar_builder_verify_grammars() {
        assertTrue(this.grammars.isVerified());
    }

    @Test
    void test_start_first_and_follow() {
        Grammar start = grammars.start();
        assertEquals(NonTerminal.SCRIPT, start.type());
        List<Terminal> first = start.first();
        assertTrue(first.contains(Terminal.IMPORT));
        assertTrue(first.contains(Terminal.LET));
        assertTrue(first.contains(Terminal.LET));
        assertTrue(first.contains(Terminal.EPSILON));
        List<Terminal> follow = new ArrayList<>(start.follow());
        assertEquals(Terminal.END, follow.get(0));
        assertEquals(1, follow.size());
    }

    @Test
    void test_import_statement_first_and_follow() {
        Grammar importStatement = grammars.getStandard(NonTerminal.IMPORT_DECLARES);
        List<Terminal> first = importStatement.first();
        assertTrue(first.contains(Terminal.IMPORT));
        List<Terminal> follow = new ArrayList<>(importStatement.follow());
        assertTrue(follow.contains(Terminal.LET));
        assertTrue(follow.contains(Terminal.FUNC));
        assertTrue(first.contains(Terminal.EPSILON));
        assertTrue(follow.contains(Terminal.END));
    }

    @Test
    void test_statement_list_first_and_follow() {
        Grammar statements = grammars.getStandard(NonTerminal.STATEMENTS);
        List<Terminal> first = statements.first();
        assertTrue(first.contains(Terminal.EPSILON));
        assertTrue(first.contains(Terminal.LET));
        assertTrue(first.contains(Terminal.FUNC));
        assertTrue(first.contains(Terminal.IF));
        assertTrue(first.contains(Terminal.DO));
        assertTrue(first.contains(Terminal.WHILE));
        assertTrue(first.contains(Terminal.VAR));
        assertTrue(first.contains(Terminal.ID));
        assertTrue(first.contains(Terminal.LEFT_BRACE));
        List<Terminal> follow = new ArrayList(statements.follow());
        assertTrue(follow.contains(Terminal.END));
    }

    @Test
    void test_statement_first_and_follow() {
        Grammar statement = grammars.getStandard(NonTerminal.STATEMENT);
        List<Terminal> first = statement.first();
        assertTrue(first.contains(Terminal.LET));
        assertTrue(first.contains(Terminal.FUNC));
        assertTrue(first.contains(Terminal.IF));
        assertTrue(first.contains(Terminal.DO));
        assertTrue(first.contains(Terminal.WHILE));
        assertTrue(first.contains(Terminal.VAR));
        assertTrue(first.contains(Terminal.ID));
        assertTrue(first.contains(Terminal.LEFT_BRACE));
        List<Terminal> follow = new ArrayList(statement.follow());
        assertTrue(follow.contains(Terminal.LET));
        assertTrue(follow.contains(Terminal.VAR));
        assertTrue(follow.contains(Terminal.FUNC));
        assertTrue(follow.contains(Terminal.IF));
        assertTrue(follow.contains(Terminal.DO));
        assertTrue(follow.contains(Terminal.WHILE));
        assertTrue(follow.contains(Terminal.END));
        assertTrue(follow.contains(Terminal.RIGHT_BRACE));
        assertTrue(follow.contains(Terminal.LEFT_BRACE));
        assertTrue(follow.contains(Terminal.ID));
        assertTrue(follow.contains(Terminal.RETURN));
    }

    @Test
    void test_entity_first_and_follow() {
        Grammar entity = grammars.getStandard(NonTerminal.ENTITY_DECLARE);
        List<Terminal> first = entity.first();
        assertEquals(2, first.size());
        assertTrue(first.contains(Terminal.ENTITY));
        assertTrue(first.contains(Terminal.ENTITY_BODY_BEGIN));
    }
}
