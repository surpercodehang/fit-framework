/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fit.ohscript.script.lexer.Lexer;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 文本解析测试
 *
 * @since 1.0
 */
class LexerTest {
    private final Lexer lexer = new Lexer();

    @Test
    void test_lexer_for_namespace_declare_and_import() {
        List<Token> tokens = lexer.scan(ProductionCases.IMPORT);
        Token token = tokens.get(0);
        Assertions.assertEquals(Terminal.IMPORT, token.tokenType());

        tokens = lexer.scan(ProductionCases.NAMESPACE);
        token = tokens.get(0);
        assertEquals(Terminal.NAME_SPACE, token.tokenType());
    }

    @Test
    void test_lexer_for_declare() {
        List<Token> tokens = lexer.scan(ProductionCases.VARIANT_DECLARATION);
        assertEquals(6, tokens.size());
        // 校验 let
        Token token = tokens.get(0);
        assertEquals(Terminal.LET, token.tokenType());
        assertEquals("let", token.lexeme());
        assertEquals(1, token.line());
        assertEquals(1, token.start());
        assertEquals(4, token.end());
        // 校验 var1
        token = tokens.get(1);
        assertEquals(Terminal.ID, token.tokenType());
        assertEquals("var1", token.lexeme());
        assertEquals(1, token.line());
        assertEquals(5, token.start());
        assertEquals(9, token.end());
        // 校验 =
        token = tokens.get(2);
        assertEquals(Terminal.EQUAL, token.tokenType());
        assertEquals("=", token.lexeme());
        assertEquals(1, token.line());
        assertEquals(10, token.start());
        assertEquals(11, token.end());
        // 校验 3.66
        token = tokens.get(3);
        assertEquals(Terminal.NUMBER, token.tokenType());
        assertEquals("-3.66", token.lexeme());
        assertEquals(1, token.line());
        assertEquals(11, token.start());
        assertEquals(17, token.end());
        // 校验 3.66
        token = tokens.get(4);
        assertEquals(Terminal.SEMICOLON, token.tokenType());
        assertEquals(";", token.lexeme());
        assertEquals(1, token.line());
        assertEquals(17, token.start());
        assertEquals(18, token.end());
        // 校验 end of line
        token = tokens.get(5);
        assertEquals(Terminal.EOL, token.tokenType());
    }

    @Test
    void test_lexer_for_expression() {
        // "let a = (b+5)*10/3-4;"
        List<Token> tokens = lexer.scan(ProductionCases.EXPRESSION);
        assertEquals(16, tokens.size());
        Token token = tokens.get(0);
        assertEquals(Terminal.LET, token.tokenType());
        token = tokens.get(1);
        assertEquals(Terminal.ID, token.tokenType());
        assertEquals("a", token.lexeme());
        token = tokens.get(2);
        assertEquals(Terminal.EQUAL, token.tokenType());
        token = tokens.get(3);
        assertEquals(Terminal.LEFT_PAREN, token.tokenType());
        token = tokens.get(5);
        assertEquals(Terminal.PLUS, token.tokenType());
        token = tokens.get(8);
        assertEquals(Terminal.STAR, token.tokenType());
        token = tokens.get(10);
        assertEquals(Terminal.SLASH, token.tokenType());
        token = tokens.get(12);
        assertEquals(Terminal.MINUS, token.tokenType());
    }

    @Test
    void test_lexer_for_if_statement_with_condition_greater_then() {
        // "if(a>=0 && b==0 || c<=0){\r\na=1;\r\n}else a=2;"
        List<Token> tokens = lexer.scan(ProductionCases.IF);
        int lineSeparatorLength = System.lineSeparator().length();
        assertEquals(26 + lineSeparatorLength * 2, tokens.size());
        Token token = tokens.get(0);
        assertEquals(Terminal.IF, token.tokenType());
        token = tokens.get(3);
        assertEquals(Terminal.GREATER_EQUAL, token.tokenType());
        token = tokens.get(5);
        assertEquals(Terminal.AND_AND, token.tokenType());
        token = tokens.get(7);
        assertEquals(Terminal.EQUAL_EQUAL, token.tokenType());
        token = tokens.get(9);
        assertEquals(Terminal.OR_OR, token.tokenType());
        token = tokens.get(11);
        assertEquals(Terminal.LESS_EQUAL, token.tokenType());
        token = tokens.get(15);
        assertEquals(Terminal.EOL, token.tokenType());
        token = tokens.get(15 + lineSeparatorLength);
        assertEquals(Terminal.ID, token.tokenType());
        assertEquals(2, token.line());
        token = tokens.get(15 + lineSeparatorLength + 1);
        assertEquals(Terminal.EQUAL, token.tokenType());
    }

    @Test
    void test_lexer_for_while_statement_with_condition_bang_equal() {
        // "while(while123!=100){a++;break;}"
        List<Token> tokens = lexer.scan(ProductionCases.WHILE);
        assertEquals(14, tokens.size());
        Token token = tokens.get(0);
        assertEquals(Terminal.WHILE, token.tokenType());
        token = tokens.get(3);
        assertEquals(Terminal.BANG_EQUAL, token.tokenType());
        token = tokens.get(8);
        assertEquals(Terminal.PLUS_PLUS, token.tokenType());
        token = tokens.get(10);
        assertEquals(Terminal.BREAK, token.tokenType());
    }

    @Test
    void test_lexer_for_for_statement_with_condition_less_equal() {
        // "for(var i=0; i<100; i++){a = b|c; d=e&f; continue;}"
        List<Token> tokens = lexer.scan(ProductionCases.FOR);
        Token token = tokens.get(0);
        assertEquals(Terminal.FOR, token.tokenType());
        token = tokens.get(18);
        assertEquals(Terminal.OR, token.tokenType());
        token = tokens.get(24);
        assertEquals(Terminal.AND, token.tokenType());
        token = tokens.get(27);
        assertEquals(Terminal.CONTINUE, token.tokenType());
    }

    @Test
    void test_entity_statement() {
        List<Token> tokens = lexer.scan(ProductionCases.ENTITY);
        Token token = tokens.get(0);
        assertEquals(Terminal.ENTITY, token.tokenType());
    }

    @Test
    void test_lexer_for_with_statement() {
        // "with(context,form){name = name;}"
        List<Token> tokens = lexer.scan(ProductionCases.WITH);
        Token token = tokens.get(0);
        assertEquals(Terminal.WITH, token.tokenType());
        token = tokens.get(2);
        assertEquals(Terminal.ID, token.tokenType());
        token = tokens.get(4);
        assertEquals(Terminal.FORM, token.tokenType());
    }

    @Test
    void test_lexer_with_unknown_token() {
        List<Token> tokens = lexer.scan(ProductionCases.UNKNOWN_TOKEN);
        Token token = tokens.get(0);
        assertEquals(Terminal.UNKNOWN, token.tokenType());
        assertEquals("00unknown", token.lexeme());
        assertEquals(1, token.start());
        assertEquals(10, token.end());
    }
}
