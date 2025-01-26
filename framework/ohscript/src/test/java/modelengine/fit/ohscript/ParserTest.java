/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.ohscript.script.errors.GrammarSyntaxException;
import modelengine.fit.ohscript.script.lexer.Lexer;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.AST;
import modelengine.fit.ohscript.script.parser.GrammarBuilder;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.parser.ParserBuilder;
import modelengine.fit.ohscript.script.parser.nodes.EntityDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionCallNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.ImportNode;
import modelengine.fit.ohscript.script.parser.nodes.NonTerminalNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.BoolTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.FunctionTypeExpr;
import modelengine.fitframework.util.ObjectUtils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 解析器测试
 *
 * @since 1.0
 */
class ParserTest {
    private static final int OFF = 1;

    private ParserBuilder parserBuilder;

    @BeforeEach
    void setup() {
        GrammarBuilder grammarBuilder = new GrammarBuilder();
        Lexer lexer = new Lexer();
        this.parserBuilder = new ParserBuilder(grammarBuilder, lexer);
    }

    @Test
    void test_import_statement() {
        final int importOff = 2;
        final String source = "part1";
        AST ast = parserBuilder.parseString(source,
                "import a,e,f,g from module1;" + System.lineSeparator() + " import c, b as b1,d as d3 from module2;"
                        + System.lineSeparator() + " import * from module3;" + System.lineSeparator() + " $");
        List<ImportNode> imports = ast.imports();
        assertEquals("a", imports.get(importOff).symbols().get(0).first().lexeme());
        assertEquals("a", imports.get(importOff).symbols().get(0).second().lexeme());
        assertEquals("c", imports.get(importOff + 1).symbols().get(0).first().lexeme());
        assertEquals("d", imports.get(importOff + 1).symbols().get(2).first().lexeme());
        assertEquals("d3", imports.get(importOff + 1).symbols().get(2).second().lexeme());
        assertEquals("b", imports.get(importOff + 1).symbols().get(1).first().lexeme());
        assertEquals("b1", imports.get(importOff + 1).symbols().get(1).second().lexeme());
        assertEquals("*", imports.get(importOff + 2).symbols().get(0).first().lexeme());
    }

    @Test
    void test_export_statement() {
        AST ast = parserBuilder.parseString("",
                "let will=entity{.age=47; .run=func(){};};" + System.lineSeparator() + " export will,evan;"
                        + System.lineSeparator() + " $");
        List<TerminalNode> exports = ast.exports();
        assertEquals("will", exports.get(0).lexeme());
        assertEquals("evan", exports.get(1).lexeme());
    }

    @Test
    void test_function_declare_statements() {
        AST ast = parserBuilder.parseString("",
                "func fun1(){return x+y>z;};" + System.lineSeparator() + " func fun2(a,b,c){return a+b+c;};"
                        + System.lineSeparator() + " func func3(a){return func(b){return"
                        + " func(c1){return a+b+c1;};};};$");
        NonTerminalNode statements = ObjectUtils.cast(ast.start().child(1));
        NonTerminalNode func1 = ObjectUtils.cast(statements.child(OFF));
        assertEquals(NonTerminal.FUNC_DECLARE, func1.nodeType());
        NonTerminalNode arguments1 = ObjectUtils.cast(func1.child(3));
        assertEquals(1, arguments1.childCount());
        TerminalNode unit = ObjectUtils.cast(arguments1.child(0).child(0));
        assertEquals(Terminal.UNIT, unit.nodeType());
        SyntaxNode block = func1.child(5);
        SyntaxNode returnStatement = block.child(1);
        assertEquals(Terminal.RETURN, returnStatement.child(0).nodeType());
        assertEquals(NonTerminal.RELATIONAL_CONDITION, returnStatement.child(1).nodeType());

        NonTerminalNode func2 = ObjectUtils.cast(statements.child(OFF + 1));
        assertEquals(NonTerminal.FUNC_DECLARE, func2.nodeType());
        TerminalNode funName = ObjectUtils.cast(func2.child(1));
        assertEquals("fun2", funName.token().lexeme());
        NonTerminalNode arguments2 = ObjectUtils.cast(func2.child(3));
        assertEquals(NonTerminal.ARGUMENTS, arguments2.nodeType());
        assertEquals(1, arguments2.childCount());
        assertEquals(NonTerminal.ARGUMENT, arguments2.child(0).nodeType());
        assertEquals(Terminal.ID, arguments2.child(0).child(0).nodeType());

        // fun2 should have same structure of func3
        // fun2 will be changed to func3 style in ast
        NonTerminalNode func3 = ObjectUtils.cast(statements.child(OFF + 2));
        assertEquals(func2.childCount(), func3.childCount());
        assertEquals(ObjectUtils.<Object>cast(func2.child(3).nodeType()),
                func3.child(3).nodeType()); // first layer argument
        assertEquals(func2.child(3).childCount(), func3.child(3).childCount());
        // check second layer function
        NonTerminalNode func2Child2 = ObjectUtils.cast(func2.child(5).child(1).child(1));
        NonTerminalNode func3Child2 = ObjectUtils.cast(func3.child(5).child(1).child(1));
        assertEquals(NonTerminal.FUNC_DECLARE, func3Child2.nodeType());
        assertTrue((ObjectUtils.<FunctionDeclareNode>cast(func3Child2)).isAnonymous());
        assertEquals(func2Child2.nodeType(), func3Child2.nodeType());

        NonTerminalNode func2Child3 = ObjectUtils.cast(func2Child2.child(5).child(1).child(1));
        NonTerminalNode func3Child3 = ObjectUtils.cast(func3Child2.child(5).child(1).child(1));
        assertEquals(NonTerminal.FUNC_DECLARE, func3Child3.nodeType());
        assertTrue((ObjectUtils.<FunctionDeclareNode>cast(func3Child3)).isAnonymous());
        assertEquals(func2Child3.nodeType(), func3Child3.nodeType());

        NonTerminalNode return2 = ObjectUtils.cast(func2Child3.child(5).child(1));
        NonTerminalNode return3 = ObjectUtils.cast(func2Child3.child(5).child(1));
        assertEquals(NonTerminal.RETURN_STATEMENT, return2.nodeType());
        assertEquals(return2.nodeType(), return3.nodeType());

        NonTerminalNode numeric2 = ObjectUtils.cast(return2.child(1));
        NonTerminalNode numeric3 = ObjectUtils.cast(return3.child(1));
        assertEquals(NonTerminal.NUMERIC_EXPRESSION, numeric2.nodeType());
        assertEquals(numeric2.childCount(), numeric3.childCount());
    }

    @Test
    void test_function_call() {
        AST ast = parserBuilder.parseString("", "func func1(x,y,z){return x+y>z;}" + System.lineSeparator()
                + " let f1=func1(1), f2=func1(1,2),f3=func1(1)(2),f4=func1(1,2,3);$");
        NonTerminalNode statements = ObjectUtils.cast(ast.start().child(1));
        SyntaxNode let = statements.child(OFF + 1);
        FunctionCallNode f1 = ObjectUtils.cast(let.child(0).child(2));
        FunctionCallNode f2 = ObjectUtils.cast(let.child(1).child(2));
        FunctionCallNode f3 = ObjectUtils.cast(let.child(2).child(2));
        FunctionCallNode f4 = ObjectUtils.cast(let.child(3).child(2));

        TypeExpr f1Type = f1.typeExpr();
        assertInstanceOf(FunctionTypeExpr.class, f1Type);
        TypeExpr f1Return = (ObjectUtils.<FunctionTypeExpr>cast(f1Type)).returnType().exactBe();
        assertInstanceOf(FunctionTypeExpr.class, f1Return);
        assertInstanceOf(BoolTypeExpr.class, (ObjectUtils.<FunctionTypeExpr>cast(f1Return)).returnType().exactBe());

        TypeExpr f2Type = f2.typeExpr();
        assertInstanceOf(FunctionTypeExpr.class, f2Type.exactBe());
        assertInstanceOf(BoolTypeExpr.class, (ObjectUtils.<FunctionTypeExpr>cast(f2Type)).returnType().exactBe());

        TypeExpr f4Type = f4.typeExpr();
        assertInstanceOf(BoolTypeExpr.class, f4Type.exactBe());
    }

    @Test
    void test_if_statements() {
        AST ast = parserBuilder.parseString("", "if(true){}" + System.lineSeparator() + "if(false){}else{if(true){}}$");
        NonTerminalNode ifStatement1 = ObjectUtils.cast(ast.start().child(1).child(OFF));
        assertEquals(NonTerminal.IF_STATEMENT, ifStatement1.nodeType());
        TerminalNode condition = ObjectUtils.cast(ifStatement1.child(0).child(0));
        assertEquals(Terminal.TRUE, condition.nodeType());
        assertEquals(1, condition.location().startLine());
        assertEquals(1, condition.location().endLine());

        assertEquals(4, condition.location().startPosition());
        assertEquals(8, condition.location().endPosition());
        NonTerminalNode ifStatement2 = ObjectUtils.cast(ast.start().child(1).child(OFF + 1));

        assertEquals(NonTerminal.IF_STATEMENT, ifStatement2.nodeType());
        NonTerminalNode elseStatement = ObjectUtils.cast(ifStatement2.child(1));
        assertEquals(NonTerminal.IF_BRANCH, elseStatement.nodeType());
        NonTerminalNode ifStatement3 = ObjectUtils.cast(elseStatement.child(1).child(1));
        assertEquals(NonTerminal.IF_STATEMENT, ifStatement3.nodeType());
    }

    @Test
    void test_do_statement_and_block_statement() {
        AST ast = parserBuilder.parseString("", "do{let a=0;}while(true)$");
        SyntaxNode doStatement = ast.start().child(1).child(OFF);
        assertEquals(NonTerminal.DO_STATEMENT, doStatement.nodeType());
        SyntaxNode block = doStatement.child(1);
        assertEquals(NonTerminal.BLOCK_STATEMENT, block.nodeType());
    }

    @Test
    void test_while_statement() {
        AST ast = parserBuilder.parseString("", "var i=0; while(i<10){i=i+1;} i$");
        NonTerminalNode whileStatement = ObjectUtils.cast(ast.start().child(1).child(OFF + 1));
        assertEquals(NonTerminal.WHILE_STATEMENT, whileStatement.nodeType());
    }

    @Test
    void test_condition_expression() {
        AST ast = parserBuilder.parseString("", "while(true||a==1&&b<=9&&!c){}$");
        NonTerminalNode conditionStatement = ObjectUtils.cast(ast.start().child(1).child(OFF).child(2));
        assertEquals(NonTerminal.CONDITION_EXPRESSION, conditionStatement.nodeType());
        assertEquals(Terminal.TRUE, conditionStatement.child(0).nodeType());
        assertEquals(Terminal.OR_OR, conditionStatement.child(1).nodeType());
        NonTerminalNode rq = ObjectUtils.cast(conditionStatement.child(2));
        assertEquals(NonTerminal.RELATIONAL_CONDITION, rq.nodeType());
        assertEquals(Terminal.ID, rq.child(0).nodeType());
        assertEquals("a", (ObjectUtils.<TerminalNode>cast(rq.child(0))).token().lexeme());
        assertEquals(Terminal.EQUAL_EQUAL, rq.child(1).nodeType());
        assertEquals(Terminal.NUMBER, rq.child(2).nodeType());
        assertEquals("1", (ObjectUtils.<TerminalNode>cast(rq.child(2))).token().lexeme());

        NonTerminalNode neg = ObjectUtils.cast(conditionStatement.child(6));
        assertEquals(NonTerminal.NEGATION, neg.nodeType());
        assertEquals(Terminal.BANG, neg.child(0).nodeType());
        assertEquals(Terminal.ID, neg.child(1).nodeType());
        assertEquals("c", (ObjectUtils.<TerminalNode>cast(neg.child(1))).token().lexeme());
    }

    @Test
    void test_numeric_expression_and_fun_call_in_expression() {
        AST ast = parserBuilder.parseString("",
                "let a=4*(5+9+10),b=g*(4/6),c=3+b*7,d=9*(c+b),e;" + System.lineSeparator()
                        + " let m=1,w=!m,k=++m,j=m--;" + System.lineSeparator() + " func n(a,b,c){} let f="
                        + " n(a,b,c), w=z;" + System.lineSeparator() + " let x=1;$");
        SyntaxNode let1 = ast.start().child(1).child(OFF);
        assertEquals(NonTerminal.LET_STATEMENT, let1.nodeType());
        SyntaxNode assignStatement1 = let1.child(0);
        assertEquals("a", (ObjectUtils.<TerminalNode>cast(assignStatement1.child(0))).token().lexeme());
        assertEquals(Terminal.EQUAL, assignStatement1.child(1).nodeType());
        SyntaxNode term = assignStatement1.child(2);
        assertEquals(NonTerminal.TERM_EXPRESSION, term.nodeType());
        SyntaxNode factor = term.child(2);
        assertEquals(NonTerminal.NUMERIC_EXPRESSION, factor.nodeType());

        SyntaxNode assignStatement2 = ast.start().child(1).child(OFF + 1);
        SyntaxNode singleAssign2 = assignStatement2.child(1);
        SyntaxNode unary = singleAssign2.child(2);
        assertEquals(NonTerminal.NEGATION, unary.nodeType());
        assertEquals(Terminal.BANG, unary.child(0).nodeType());

        SyntaxNode let3 = ast.start().child(1).child(OFF + 3);
        SyntaxNode call = let3.child(0).child(2);
        assertEquals(NonTerminal.FUNC_CALL, call.nodeType());
    }

    @Test
    void test_ternary_expression() {
        AST ast = parserBuilder.parseString("", "if((a+2>b?    c:d+4)>-9){}$");
        SyntaxNode ternary = ast.start().child(1).child(OFF).child(0).child(0).child(0);
        assertEquals(NonTerminal.TERNARY_EXPRESSION, ternary.nodeType());
        // 校验：a+2>b ==(a+2)>b
        SyntaxNode condition = ternary.child(0);
        assertEquals(NonTerminal.RELATIONAL_CONDITION, condition.nodeType());
        SyntaxNode numeric1 = condition.child(0);
        assertEquals(NonTerminal.NUMERIC_EXPRESSION, numeric1.nodeType());
        // 校验：b?c:d+4 == b?c:(d+4)
        SyntaxNode numeric2 = ternary.child(4);
        assertEquals(NonTerminal.NUMERIC_EXPRESSION, numeric2.nodeType());
        assertEquals("d", (ObjectUtils.<TerminalNode>cast(numeric2.child(0))).token().lexeme());
        assertEquals(Terminal.PLUS, numeric2.child(1).nodeType());
        assertEquals("4", (ObjectUtils.<TerminalNode>cast(numeric2.child(2))).token().lexeme());
    }

    @Test
    void test_entity_expression() {
        AST ast = parserBuilder.parseString("",
                "let will = entity{.name=\"will zhang\"; .age=48; .get_age=func(){this.age};}; $");
        SyntaxNode will = ast.start().child(1).child(OFF).child(0).child(0);
        SyntaxNode e1 = ast.start().child(1).child(OFF).child(0).child(2);
        assertEquals(Terminal.ID, will.nodeType());
        assertEquals(NonTerminal.ENTITY_DECLARE, e1.nodeType());
        EntityDeclareNode entity = ObjectUtils.cast(e1);
        assertEquals(3, entity.members().size());
    }

    @Test
    void test_wrong_code_raise_grammar_exception() {
        GrammarSyntaxException cause = catchThrowableOfType(() -> parserBuilder.parseString("", "let a = 100$"),
                GrammarSyntaxException.class);
        Assertions.assertThat(cause).isNotNull();
    }

    @Test
    void test() {
        parserBuilder.parseString("", "let a=[1,2,3,4];");
    }
}
