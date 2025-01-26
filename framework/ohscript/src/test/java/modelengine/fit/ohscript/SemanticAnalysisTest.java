/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript;

import static modelengine.fit.ohscript.script.errors.SyntaxError.TYPE_MISMATCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.lexer.Lexer;
import modelengine.fit.ohscript.script.parser.AST;
import modelengine.fit.ohscript.script.parser.GrammarBuilder;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.parser.ParserBuilder;
import modelengine.fit.ohscript.script.parser.nodes.ArrayDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.EachNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityCallNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.ForNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionCallNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.GeneralNode;
import modelengine.fit.ohscript.script.parser.nodes.InitialAssignmentNode;
import modelengine.fit.ohscript.script.parser.nodes.ReturnNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.EntityEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.IdentifierEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.SymbolEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts.ExprTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts.GenericTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.ArrayTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.BoolTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.EntityTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.FunctionTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.NumberTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.StringTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.UnitTypeExpr;
import modelengine.fit.ohscript.util.Pair;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

/**
 * 语法分析测试
 *
 * @since 1.0
 */
class SemanticAnalysisTest {
    private static final int OFF = 1;

    private ParserBuilder parserBuilder;

    @BeforeEach
    void setup() {
        GrammarBuilder grammarBuilder = new GrammarBuilder();
        Lexer lexer = new Lexer();
        this.parserBuilder = new ParserBuilder(grammarBuilder, lexer);
    }

    @Test
    void test_var_declare() {
        AST ast = this.parserBuilder.parseString("", "let b = \"10\"; let a = b+10;");
        SyntaxNode b = ast.start().child(1).child(OFF);
        SyntaxNode a = ast.start().child(1).child(OFF + 1);
        assertInstanceOf(StringTypeExpr.class, b.child(0).child(0).typeExpr());
        assertInstanceOf(StringTypeExpr.class, a.child(0).child(0).typeExpr());
    }

    @Test
    void test_func_declare() {
        AST ast = this.parserBuilder.parseString("", "func f(x,y){x+y}");
        FunctionTypeExpr f0 = ObjectUtils.cast(ast.start().child(1).child(OFF).typeExpr());
        assertInstanceOf(FunctionTypeExpr.class, f0);
        assertInstanceOf(GenericTypeExpr.class, f0.argumentType());
        GenericTypeExpr arg0 = ObjectUtils.cast(f0.argumentType());
        FunctionTypeExpr f1 = ObjectUtils.cast(f0.returnType());
        GenericTypeExpr arg1 = ObjectUtils.cast(f1.argumentType());
        ExprTypeExpr r = ObjectUtils.cast(f1.returnType());
        assertEquals(2, arg0.couldBe().size());
        assertEquals(2, arg1.couldBe().size());
        assertEquals(2, r.couldBe().size());

        ast = this.parserBuilder.parseString("", "func f(x){1}");
        f0 = ObjectUtils.cast(ast.start().child(1).child(OFF).typeExpr());
        arg0 = ObjectUtils.cast(f0.argumentType());
        assertInstanceOf(NumberTypeExpr.class, f0.returnType());
        assertEquals(0, arg0.couldBe().size());

        ast = this.parserBuilder.parseString("", "func f(x){x-1}");
        f0 = ObjectUtils.cast(ast.start().child(1).child(OFF).typeExpr());
        assertInstanceOf(NumberTypeExpr.class, f0.argumentType());
        assertInstanceOf(NumberTypeExpr.class, f0.returnType());

        ast = this.parserBuilder.parseString("", "func f(x){!x}");
        f0 = ObjectUtils.cast(ast.start().child(1).child(OFF).typeExpr());
        assertInstanceOf(BoolTypeExpr.class, f0.argumentType());
        assertInstanceOf(BoolTypeExpr.class, f0.returnType());
    }

    @Test
    void test_func_call() {
        AST ast = this.parserBuilder.parseString("", "func f(x){x} f(4); f(\"will\");");
        TypeExpr call1 = ast.start().child(1).child(OFF + 1).typeExpr();
        assertInstanceOf(NumberTypeExpr.class, call1);
        TypeExpr call2 = ast.start().child(1).child(OFF + 2).typeExpr();
        assertInstanceOf(StringTypeExpr.class, call2);

        ast = this.parserBuilder.parseString("", "func f(x,y){x+y} f(4,5); let f4 = f(4); f4(\"will\")");
        assertInstanceOf(NumberTypeExpr.class, ast.start().child(1).child(OFF + 1).typeExpr());
        assertInstanceOf(StringTypeExpr.class, ast.start().child(1).child(OFF + 3).typeExpr());

        FunctionTypeExpr f4 = ObjectUtils.cast(ast.start().child(1).child(OFF + 2).child(0).child(0).typeExpr());
        assertInstanceOf(ExprTypeExpr.class, f4.returnType());

        TypeExpr call4 = ast.start().child(1).child(OFF + 3).typeExpr();
        assertInstanceOf(StringTypeExpr.class, call4);

        ast = this.parserBuilder.parseString("", "func f(x,y){x-y} let f4 = f(4); f4(\"will\")");
        SyntaxNode call5 = ast.start().child(1).child(OFF + 2).child(0);
        assertSame(call5.error().get(call5.child(0)).first(), TYPE_MISMATCH);

        ast = this.parserBuilder.parseString("", "func f(x,y){y(x)} func f1(x){x+5} f(\"will\",f1)");
        SyntaxNode node2 = ast.start().child(1).child(OFF + 2).child(0);
        TypeExpr call7 = node2.typeExpr();
        assertInstanceOf(StringTypeExpr.class, call7);

        ast = this.parserBuilder.parseString("", "func f(x,y){y(x)-1} func f1(z){z+5} f(5,f1)");
        SyntaxNode node3 = ast.start().child(1).child(OFF + 2).child(0);
        TypeExpr call8 = node3.typeExpr();
        assertInstanceOf(NumberTypeExpr.class, call8);

        ast = this.parserBuilder.parseString("", "func f(x,y){y(x)-1} func f1(x){x+5} f(\"will\",f1)");
        SyntaxNode call9 = ast.start().child(1).child(OFF + 2).child(0).child(1);
        assertEquals(TYPE_MISMATCH, call9.error().get(call9).first());

        ast = this.parserBuilder.parseString("",
                "func f(x,y,z){z(x,y)} func f1(g,h){g+h+1*4} f(5,6,f1); f(6,\"will\",f1)");
        assertInstanceOf(NumberTypeExpr.class, ast.start().child(1).child(OFF + 2).typeExpr());

        ast = this.parserBuilder.parseString("",
                "func f(x,y,z){z(x,y)} func f1(g,h){g+h+1*4} f(7,8,f1); f(\"5\",\"6\",f1); "); // 注释：f("5","6",f1);
        assertInstanceOf(NumberTypeExpr.class, ast.start().child(1).child(OFF + 2).typeExpr());
        assertInstanceOf(StringTypeExpr.class, ast.start().child(1).child(OFF + 3).typeExpr());

        ast = this.parserBuilder.parseString("", "func f(x,y,z){z(x,y)} func f1(g,h){g+h-1*4} f(\"5\",\"6\",f1); ");
        assertTrue(ast.start().child(1).child(OFF + 2).error().size() > 0);

        ast = this.parserBuilder.parseString("", "func f(x,y,z){z(x,y)}; f(10,func(x)=>x+\" years\",func(x,y)=>y(x))");
        assertInstanceOf(StringTypeExpr.class, ast.start().child(1).child(OFF + 1).typeExpr());
    }

    @Test
    void test_entity() {
        // 校验：f("5","6",f1);
        AST ast = this.parserBuilder.parseString("",
                "func f(x,y,z){let w=z(1,2)-1; entity{.name=z;}} func f1(g,h){g+h-1*4} f(7,8,f1); ");
        FunctionTypeExpr entityMember = ObjectUtils.cast(
                ast.start().child(1).child(OFF + 2).typeExpr().members().get(".name"));
        assertInstanceOf(NumberTypeExpr.class, entityMember.argumentType());
        assertInstanceOf(NumberTypeExpr.class,
                (ObjectUtils.<FunctionTypeExpr>cast(entityMember.returnType())).argumentType());
        assertInstanceOf(NumberTypeExpr.class,
                (ObjectUtils.<FunctionTypeExpr>cast(entityMember.returnType())).returnType());

        ast = this.parserBuilder.parseString("",
                "func f(x,y,z){z(y(x))} func f1(g){g} func f2(h){entity{.name=h;}} f(entity{.last=\"will\"; "
                        + ".first=\"zhang\";},f2,f1); ");
        assertInstanceOf(GenericTypeExpr.class,
                (ObjectUtils.<FunctionTypeExpr>cast(ast.start().child(1).child(OFF).typeExpr())).argumentType());
        assertInstanceOf(FunctionTypeExpr.class, (ObjectUtils.<FunctionTypeExpr>cast(
                (ObjectUtils.<FunctionTypeExpr>cast(
                        ast.start().child(1).child(OFF).typeExpr())).returnType())).argumentType());
        assertInstanceOf(FunctionTypeExpr.class, (ObjectUtils.<FunctionTypeExpr>cast(
                (ObjectUtils.<FunctionTypeExpr>cast(
                        ast.start().child(1).child(OFF).typeExpr())).returnType())).argumentType());
        EntityTypeExpr name = ObjectUtils.cast(
                ast.start().child(1).child(OFF + 3).typeExpr().members().get(".name").exactBe());
        assertInstanceOf(StringTypeExpr.class, name.members().get(".last"));
        assertInstanceOf(StringTypeExpr.class, name.members().get(".first"));

        ast = this.parserBuilder.parseString("", "let will = entity{._age = 48; .age=func(){this._age};}; will._age");

        assertEquals(1, ast.errors().values().size());
        assertEquals(SyntaxError.ENTITY_MEMBER_ACCESS_DENIED,
                ast.errors().get(new ArrayList<>(ast.errors().keySet()).get(0)).first());

        ast = this.parserBuilder.parseString("",
                "let will = entity{._age = 48; .name=func(){entity{.last=\"will\";}};}; will.name().last");
        assertInstanceOf(StringTypeExpr.class, ast.start().child(1).child(OFF + 1).typeExpr());
    }

    @Test
    void test_extension() {
        // array extension
        AST ast = this.parserBuilder.parseString("1", "let a = [1,2,3]; let b = a.size(); b");
        FunctionTypeExpr size = ObjectUtils.cast((ObjectUtils.<FunctionCallNode>cast(
                ast.start().child(1).child(OFF + 1).child(0).child(2))).functionName().typeExpr());
        assertInstanceOf(UnitTypeExpr.class, size.argumentType());
        assertInstanceOf(NumberTypeExpr.class, size.returnType());
        TypeExpr b = ast.start().child(1).child(OFF + 2).typeExpr();
        assertInstanceOf(NumberTypeExpr.class, b);

        ast = this.parserBuilder.parseString("2", "let a = [1,2,3]; a.insert(1,6)");
        FunctionTypeExpr insert = ObjectUtils.cast(
                (ObjectUtils.<FunctionCallNode>cast(ast.start().child(1).child(OFF + 1).child(0))).functionName()
                        .typeExpr());
        FunctionTypeExpr insertNext = ObjectUtils.cast(insert.returnType());
        assertInstanceOf(NumberTypeExpr.class, insert.argumentType());
        assertInstanceOf(NumberTypeExpr.class, insertNext.argumentType());
        assertInstanceOf(UnitTypeExpr.class, insertNext.returnType());

        ast = this.parserBuilder.parseString("2", "let a = [1,2,3]; a.push(4)");
        FunctionTypeExpr push = ObjectUtils.cast(
                (ObjectUtils.<FunctionCallNode>cast(ast.start().child(1).child(OFF + 1).child(0))).functionName()
                        .typeExpr());
        assertInstanceOf(NumberTypeExpr.class, push.argumentType());
        assertInstanceOf(UnitTypeExpr.class, push.returnType());

        ast = this.parserBuilder.parseString("2", "let a = [\"will\",\"zhang\"]; a[0]");
        TypeExpr get = ast.start().child(1).child(OFF + 1).child(0).typeExpr();
        assertInstanceOf(StringTypeExpr.class, get);

        ast = this.parserBuilder.parseString("2", "let a = [1,2,3]; a.push(\"will\")");
        assertEquals(1, ast.errors().size());
        SyntaxNode will = ast.start().child(1).child(OFF + 1).child(0).child(0);
        assertEquals(SyntaxError.TYPE_MISMATCH, ast.errors().get(will).first());

        ast = this.parserBuilder.parseString("2", "let a = [\"will\"]; a.remove");
        FunctionTypeExpr remove = ObjectUtils.cast(ast.start().child(1).child(OFF + 1).child(0).typeExpr());
        assertInstanceOf(StringTypeExpr.class, remove.returnType());
    }

    @Test
    void test_inherited_entity() {
        AST ast = this.parserBuilder.parseString("",
                "let will = entity{.age=48; .add=func()=>this.age+2;}; let son = will::{add:func()=>base.add()+2, "
                        + "run:func(){add()+10}}; let grand=son::{add:func()=>base.base.add()}; son.run()");
        TypeExpr will = ast.start().child(1).child(OFF).child(0).child(0).typeExpr();
        assertInstanceOf(NumberTypeExpr.class,
                (ObjectUtils.<FunctionTypeExpr>cast(will.members().get(".add"))).returnType());
        assertInstanceOf(NumberTypeExpr.class, will.members().get(".age"));
        TypeExpr son = ast.start().child(1).child(OFF + 1).child(0).child(0).typeExpr();
        assertInstanceOf(NumberTypeExpr.class,
                (ObjectUtils.<FunctionTypeExpr>cast(son.members().get(".add"))).returnType());

        ast = this.parserBuilder.parseString("",
                "let will = entity{.age=48;}; let son = will::{add:func()=>age}; son.base.age");
        SyntaxNode baseCall = (ObjectUtils.<EntityCallNode>cast(
                ast.start().child(1).child(OFF + 2).child(0).child(0))).member();
        assertEquals(SyntaxError.ENTITY_MEMBER_ACCESS_DENIED, baseCall.error().get(baseCall).first());
    }

    @Test
    void test_each() {
        AST ast = this.parserBuilder.parseString("", "let arr = 43535; each(v,i) in arr {arr[i]+=1;} arr[0]");
        SyntaxNode arr = (ObjectUtils.<EachNode>cast(ast.start().child(1).child(OFF + 1))).array();
        assertEquals(1, arr.error().size());
        assertEquals(TYPE_MISMATCH, arr.error().get(arr).first());
        ast = this.parserBuilder.parseString("",
                "let arr = [\"will\",\"zhang\"]; each(v,i) in arr {arr[i]+=1;} arr[0]");
        SyntaxNode item = (ObjectUtils.<EachNode>cast(ast.start().child(1).child(OFF + 1))).item();
        SyntaxNode index = (ObjectUtils.<EachNode>cast(ast.start().child(1).child(OFF + 1))).index();
        assertInstanceOf(StringTypeExpr.class, item.typeExpr());
        assertInstanceOf(NumberTypeExpr.class, index.typeExpr());

        ast = this.parserBuilder.parseString("",
                "let arr = [\"will\",\"zhang\"]; each(v,i) in arr {let a = v-i;} arr[0]");
        assertEquals(1, ast.start().error().size());
        assertEquals(TYPE_MISMATCH, ast.start().error().values().stream().findFirst().get().first());
    }

    @Test
    void test_for() {
        AST ast = this.parserBuilder.parseString("", "var a=0; let b=\"will\"; for(var b=0; b<10; b++){a+=10;} a+b");
        SyntaxNode b1 = ast.start().child(1).child(OFF + 1).child(0).child(0);
        SyntaxNode b2 = (ObjectUtils.<ForNode>cast(ast.start().child(1).child(OFF + 2))).index();
        SyntaxNode returns = ast.start().child(1).child(OFF + 3);
        assertInstanceOf(StringTypeExpr.class, b1.typeExpr());
        assertInstanceOf(NumberTypeExpr.class, b2.typeExpr());
        assertInstanceOf(StringTypeExpr.class, returns.typeExpr());
    }

    @Test
    void test_import() {
        //        this.parserBuilder.parse("m5", "import c from m; var a=10; export a;"); todo
        this.parserBuilder.begin();
        AST ast1 = this.parserBuilder.parseString("m1",
                "import a as b,f3 from m3; " + System.lineSeparator() + " f3()+b");
        this.parserBuilder.parseString("m3", "let a=100; func f3(){\"will\"}; export a,f3;");
        this.parserBuilder.done();
        assertInstanceOf(NumberTypeExpr.class, ast1.start().child(0).child(2).child(3).typeExpr());
        FunctionTypeExpr f3 = ObjectUtils.cast(ast1.start().child(0).child(2).child(5).typeExpr());
        assertInstanceOf(StringTypeExpr.class, f3.returnType());
    }

    @Test
    void test_array() {
        AST ast = this.parserBuilder.parseString("", "let arr = []; arr");
        SyntaxNode arr = ast.start().child(1).child(OFF + 1).child(0);
        assertInstanceOf(GenericTypeExpr.class, (ObjectUtils.<ArrayTypeExpr>cast(arr.typeExpr())).itemTypeExpr());

        ast = this.parserBuilder.parseString("", "let arr = [1,2,3]; arr");
        arr = ast.start().child(1).child(OFF + 1);
        assertInstanceOf(NumberTypeExpr.class, (ObjectUtils.<ArrayTypeExpr>cast(arr.typeExpr())).itemTypeExpr());

        ast = this.parserBuilder.parseString("", "let arr = [\"will\"]; arr");
        arr = ast.start().child(1).child(OFF + 1);
        assertInstanceOf(StringTypeExpr.class, (ObjectUtils.<ArrayTypeExpr>cast(arr.typeExpr())).itemTypeExpr());

        ast = this.parserBuilder.parseString("", "let arr = [\"will\",7]; arr");
        arr = ast.start().child(1).child(OFF + 1);
        assertInstanceOf(StringTypeExpr.class, (ObjectUtils.<ArrayTypeExpr>cast(arr.typeExpr())).itemTypeExpr());
        SyntaxNode seven = (ObjectUtils.<ArrayDeclareNode>cast(arr.typeExpr().node())).items().get(1);
        assertEquals(TYPE_MISMATCH, seven.error().get(seven).first());

        ast = this.parserBuilder.parseString("", "func f1(x,y){x-y}; func f2(x){x-10}; let arr = [f1,f2]; arr");
        arr = ast.start().child(1).child(OFF + 3);
        SyntaxNode f2 = ast.start().child(1).child(OFF + 1);
        assertInstanceOf(FunctionTypeExpr.class, (ObjectUtils.<ArrayTypeExpr>cast(arr.typeExpr())).itemTypeExpr());
        assertEquals(TYPE_MISMATCH, f2.error().get(f2).first());

        ast = this.parserBuilder.parseString("", "let arr = []; arr.put(100); arr");
        SyntaxNode put = ast.start().child(1).child(OFF + 1);
        assertEquals(2, put.error().size());

        ast = this.parserBuilder.parseString("",
                "let arr = [0]; arr.push(\"100\"); arr; let arr1 = [\"will\"]; arr1.push(\"zhang\")");
        //                "let arr = [0]; arr.push");
        arr = ast.start().child(1).child(OFF + 2);
        assertInstanceOf(NumberTypeExpr.class, (ObjectUtils.<ArrayTypeExpr>cast(arr.typeExpr())).itemTypeExpr());
        SyntaxNode push = ast.start().child(1).child(OFF + 1);
        assertEquals(1, push.error().size());

        ast = this.parserBuilder.parseString("", "let arr = [1,2,3]; arr[0];");
        assertInstanceOf(NumberTypeExpr.class, ast.start().child(1).child(OFF + 1).typeExpr());

        ast = this.parserBuilder.parseString("",
                "func f1(arr1,f){let arr2=[f(arr1[0])];arr2}; func f2(x){x+\"will\"}; let arr = [1]; f1(arr,f2) ");
        assertInstanceOf(StringTypeExpr.class,
                (ObjectUtils.<ArrayTypeExpr>cast(ast.start().child(1).child(OFF + 3).typeExpr())).itemTypeExpr());
    }

    @Test
    void test_map() {
        AST ast = this.parserBuilder.parseString("2", "let a = [1,2,3]; a.push(\"will\")");
        ast = this.parserBuilder.parseString("2", "let a = [\"will\"]; a.remove");
        FunctionTypeExpr remove = ObjectUtils.cast(ast.start().child(1).child(OFF + 1).child(0).typeExpr());
        assertInstanceOf(StringTypeExpr.class, remove.returnType());
    }

    @Test
    void test_function_symbol_entry() {
        AST ast = this.parserBuilder.parseString("", "func f1(x,y,z){x=x+3; x+y+z+1}; " + System.lineSeparator()
                + "let r3 = f1(5,6,7), r2 = f1(2,3),r1=f1(2);$");
        FunctionDeclareNode f1 = ObjectUtils.cast(ast.start().child(1).child(OFF));
        assertInstanceOf(FunctionTypeExpr.class, f1.child(1).typeExpr().exactBe());

        SyntaxNode let = ast.start().child(1).child(OFF + 1);
        SyntaxNode r3 = let.child(0).child(0);
        SyntaxNode r2 = let.child(1).child(0);
        SyntaxNode r1 = let.child(2).child(0);

        TypeExpr r3Type = r3.typeExpr();
        TypeExpr r2Type = r2.typeExpr();
        TypeExpr r1Type = r1.typeExpr();

        assertInstanceOf(FunctionTypeExpr.class, r2Type.exactBe());
        assertInstanceOf(FunctionTypeExpr.class, r1Type.exactBe());

        assertEquals(f1.closure(), (ObjectUtils.<FunctionTypeExpr>cast(r1.typeExpr().exactBe())).function());
        assertEquals(f1.closure().closure(), (ObjectUtils.<FunctionTypeExpr>cast(r2.typeExpr().exactBe())).function());
    }

    @Test
    void test_scope_block_return_symbol_entry() {
        AST ast = this.parserBuilder.parseString("", "var b = \"abc\";var a = {let b=1,c=2; b+c+1}+b;$");
        SyntaxNode varA = ast.start().child(1).child(OFF + 1);
        SyntaxNode aAssign = varA.child(0).child(2);
        TerminalNode b1 = ObjectUtils.cast(aAssign.child(2));
        SyntaxNode block = aAssign.child(0);
        ReturnNode returnNode = ObjectUtils.cast(block.child(1).child(1));
        TerminalNode b2 = ObjectUtils.cast(block.child(1).child(1).child(0).child(0));

        assertNotEquals(b1.scope(), b2.scope());
        assertEquals(varA.scope(), b1.scope());
        assertEquals(block.scope(), b2.scope());

        SymbolEntry b1Entry = b1.symbolEntry();
        assertEquals(Type.STRING, b1Entry.typeExpr().type());

        SymbolEntry b2Entry = b2.symbolEntry(); // 注释：table.getSymbol(b2.token().lexeme(), b2.scope());
        assertEquals(Type.NUMBER, b2Entry.typeExpr().type());

        assertEquals(Type.NUMBER, returnNode.typeExpr().type());
        assertEquals(Type.NUMBER, block.typeExpr().type());

        TerminalNode a = ObjectUtils.cast(varA.child(0).child(0));
        SymbolEntry aEntry = a.symbolEntry(); // 注释：table.getSymbol(a.token().lexeme(), a.scope());

        assertEquals(Type.STRING, aEntry.typeExpr().exactBe().type());
    }

    @Test
    void test_let_var_symbol_entry() {
        AST ast = this.parserBuilder.parseString("",
                "let e, a=1, b=2;" + System.lineSeparator() + " var c=90, d=\"100\", f,g;" + System.lineSeparator()
                        + " g=90; c=d; w=x; a+b;abc();c+d w+d; $");
        SyntaxNode startSecondChild = ast.start().child(1);
        SyntaxNode let = startSecondChild.child(OFF);
        assertEquals(NonTerminal.INITIAL_ASSIGNMENT, let.child(1).nodeType());
        assertEquals(NonTerminal.INITIAL_ASSIGNMENT, let.child(2).nodeType());

        SyntaxNode var = startSecondChild.child(OFF + 1);

        SyntaxNode c = var.child(0);
        assertEquals(NonTerminal.INITIAL_ASSIGNMENT, c.nodeType());

        SyntaxNode expr = startSecondChild.child(OFF + 5);
        assertEquals(NonTerminal.NUMERIC_EXPRESSION, expr.nodeType());

        SyntaxNode call = startSecondChild.child(OFF + 6);
        assertEquals(NonTerminal.FUNC_CALL, call.nodeType());
        SyntaxNode ret = startSecondChild.child(OFF + 7);
        assertEquals(NonTerminal.RETURN_STATEMENT, ret.nodeType());
        // check symbol table
        SymbolEntry cEntry = (ObjectUtils.<TerminalNode>cast(c.child(0))).symbolEntry();
        assertEquals(Type.NUMBER, cEntry.typeExpr().type());
        assertEquals(cEntry.id(), (ObjectUtils.<TerminalNode>cast(c.child(0))).symbolEntry().id());

        SyntaxNode d = var.child(1);
        SymbolEntry dEntry = (ObjectUtils.<TerminalNode>cast(d.child(0))).symbolEntry();
        assertEquals(Type.STRING, dEntry.typeExpr().type());

        SyntaxNode f = var.child(2);
        SymbolEntry fEntry = (ObjectUtils.<TerminalNode>cast(f.child(0))).symbolEntry();
        assertEquals(Type.UNKNOWN, fEntry.typeExpr().type());

        SyntaxNode g = var.child(3);
        SymbolEntry gEntry = (ObjectUtils.<TerminalNode>cast(g.child(0))).symbolEntry();
        assertEquals(Type.NUMBER, gEntry.typeExpr().type());
        assertTrue((ObjectUtils.<IdentifierEntry>cast(fEntry)).mutable());

        SymbolEntry eEntry = (ObjectUtils.<TerminalNode>cast(let.child(0).child(0))).symbolEntry();
        assertEquals(Type.UNKNOWN, eEntry.typeExpr().type());
        assertFalse((ObjectUtils.<IdentifierEntry>cast(eEntry)).mutable());

        this.assertLetVarSymbolEntryError(ast, let, startSecondChild, call);
    }

    private void assertLetVarSymbolEntryError(AST ast, SyntaxNode let, SyntaxNode startSecondChild, SyntaxNode call) {
        SyntaxNode c1 = startSecondChild.child(OFF + 3);
        SyntaxNode unReach = startSecondChild.child(OFF + 8);
        SyntaxNode w = startSecondChild.child(OFF + 4);
        SyntaxNode x = w.child(2);
        Map<SyntaxNode, Pair<SyntaxError, String>> errors = ast.start().error();
        assertEquals(8, errors.size());
        assertEquals(SyntaxError.CONST_NOT_INITIALIZED, errors.get(let.child(0).child(0)).first());
        assertEquals(TYPE_MISMATCH, errors.get(c1.child(0)).first());
        assertEquals(SyntaxError.VARIABLE_NOT_DEFINED, errors.get(w.child(0)).first());
        assertEquals(SyntaxError.VARIABLE_NOT_DEFINED, errors.get(x).first());
        assertEquals(SyntaxError.UN_REACHABLE, errors.get(unReach).first());
        assertEquals(SyntaxError.FUNCTION_NOT_DEFINED, errors.get(call).first());
        assertEquals(SyntaxError.VARIABLE_NOT_DEFINED,
                errors.get((ObjectUtils.<FunctionCallNode>cast(call)).functionName()).first());
    }

    @Test
    void test_anonymous_function_symbol_entry() {
        AST ast = this.parserBuilder.parseString("", "let f2=func(x,y){x+y+2},b=1;" + System.lineSeparator()
                + "let r0 = f2(9),r1=f2(9,10),r2=f2(10)(\"9\");$");
        SyntaxNode f2 = ast.start().child(1).child(OFF).child(0);
        assertInstanceOf(InitialAssignmentNode.class, f2);
        assertInstanceOf(FunctionTypeExpr.class, f2.child(0).typeExpr().exactBe());

        SyntaxNode let2 = ast.start().child(1).child(OFF + 1);
        SyntaxNode r0 = let2.child(0).child(0);
        SyntaxNode r1 = let2.child(1).child(0);
        SyntaxNode r2 = let2.child(2).child(0);

        TypeExpr r0Type = r0.typeExpr();
        TypeExpr r1Type = r1.typeExpr();
        TypeExpr r2Type = r2.typeExpr();

        assertEquals((ObjectUtils.<FunctionTypeExpr>cast(f2.child(0).typeExpr().exactBe())).function().closure(),
                (ObjectUtils.<FunctionTypeExpr>cast(r0.typeExpr().exactBe())).function());

        assertInstanceOf(FunctionTypeExpr.class, r0Type.exactBe());
    }

    @Test
    void test_nested_scope_symbol_entry() {
        AST ast = this.parserBuilder.parseString("",
                "let f=100, e=\"90\"; var a = {let b=\"1\",e=2; let c={let d=3; d+e+f};b+c};$");
        SyntaxNode a = ast.start().child(1).child(OFF + 1);
        SyntaxNode block1 = a.child(0).child(2);
        TerminalNode eDeclare = ObjectUtils.cast(block1.child(1).child(0).child(1).child(0));
        SyntaxNode c = block1.child(1).child(1);
        TerminalNode cDeclare = ObjectUtils.cast(c.child(0).child(0));
        SyntaxNode returnInBlock1 = block1.child(1).child(2);
        SyntaxNode block2 = c.child(0).child(2);
        SyntaxNode returnInBlock2 = block2.child(1).child(1);
        TerminalNode eInBlock2 = ObjectUtils.cast(returnInBlock2.child(0).child(2));
        TerminalNode fInBlock2 = ObjectUtils.cast(returnInBlock2.child(0).child(4));
        TerminalNode fDeclare = ObjectUtils.cast(ast.start().child(1).child(OFF).child(0).child(0));

        TerminalNode cInBlock1 = ObjectUtils.cast(returnInBlock1.child(0).child(2));

        assertEquals(fDeclare.symbolEntry(), fInBlock2.symbolEntry());
        assertEquals(eDeclare.symbolEntry(), eInBlock2.symbolEntry());
        assertEquals(cDeclare.symbolEntry(), cInBlock1.symbolEntry());

        TerminalNode aDeclare = ObjectUtils.cast(a.child(0).child(0));
        assertInstanceOf(NumberTypeExpr.class, cDeclare.typeExpr().exactBe());
        assertInstanceOf(StringTypeExpr.class, aDeclare.typeExpr().exactBe());
    }

    @Test
    void test_oh_entity_expression() {
        this.testEntityExpression(
                "let will = entity{.name=\"will zhang\"; " + ".age=48; " + ".get_age=func(){this.age}; "
                        + ".get_brief = func(){this.get_age()+this.name};};");
    }

    @Test
    void test_json_entity_expression() {
        this.testEntityExpression("let will = {name:\"will zhang\", " + "age:48, get_age:func(){this.age}, "
                + "get_brief:func(){this.get_age()+this.name}};");
    }

    void testEntityExpression(String code) {
        AST ast = this.parserBuilder.parseString("", code);
        EntityDeclareNode entity = ObjectUtils.cast(ast.start().child(1).child(OFF).child(0).child(2));

        // entity structure verification
        SymbolEntry e1 = entity.declaredName().symbolEntry();
        assertInstanceOf(EntityEntry.class, e1);
        for (InitialAssignmentNode member : entity.members()) {
            assertInstanceOf(IdentifierEntry.class, member.variable().symbolEntry());
        }

        // entity member type verification
        assertInstanceOf(StringTypeExpr.class, entity.members().get(0).variable().typeExpr().exactBe());
        assertInstanceOf(NumberTypeExpr.class, entity.members().get(1).variable().typeExpr().exactBe());
        assertInstanceOf(FunctionTypeExpr.class, entity.members().get(2).variable().typeExpr().exactBe());
        assertInstanceOf(FunctionTypeExpr.class, entity.members().get(3).variable().typeExpr().exactBe());

        // entity type expression detail verification
        SyntaxNode will = ast.start().child(1).child(OFF).child(0).child(0);
        assertInstanceOf(EntityTypeExpr.class, will.typeExpr().exactBe());
        EntityTypeExpr typeExpr = ObjectUtils.cast(will.typeExpr().exactBe());
        TypeExpr name = typeExpr.members().get(".name");
        TypeExpr age = typeExpr.members().get(".age");
        TypeExpr getAge = typeExpr.members().get(".get_age");
        TypeExpr getBrief = typeExpr.members().get(".get_brief");
        assertInstanceOf(StringTypeExpr.class, name);
        assertInstanceOf(NumberTypeExpr.class, age);
        assertEquals(entity.members().get(2).variable().symbolEntry().typeExpr().exactBe(), getAge);
        assertEquals(entity.members().get(3).variable().symbolEntry().typeExpr().exactBe(), getBrief);

        // get_age verification
        assertInstanceOf(FunctionTypeExpr.class, getAge.exactBe());
        FunctionTypeExpr realAgeType = ObjectUtils.cast(getAge);
        assertInstanceOf(NumberTypeExpr.class, realAgeType.returnType().exactBe());
        // check inside:this.age
        EntityCallNode getAgeBody = ObjectUtils.cast(
                (ObjectUtils.<FunctionDeclareNode>cast(entity.members().get(2).child(2))).body().child(1).child(0));
        TypeExpr thisType = getAgeBody.entity().typeExpr();
        assertEquals(e1.typeExpr(), thisType);

        TerminalNode meAge = getAgeBody.member(); // age
        SymbolEntry meAgeEntry = meAge.symbolEntry();
        assertEquals(entity.members().get(1).variable().symbolEntry(), meAgeEntry);

        // get_brief verification
        assertInstanceOf(FunctionTypeExpr.class, getBrief.exactBe());
        FunctionTypeExpr realBriefType = ObjectUtils.cast(getBrief.exactBe());
        assertInstanceOf(StringTypeExpr.class, realBriefType.returnType().exactBe());
        // check inside: this.get_age()+this.name
        GeneralNode getBriefBody = ObjectUtils.cast(
                (ObjectUtils.<FunctionDeclareNode>cast(entity.members().get(3).child(2))).body().child(1).child(0));
        FunctionCallNode first = ObjectUtils.cast(getBriefBody.child(0));
        SyntaxNode me2 = first.functionName(); // this
        assertEquals(getAge, me2.typeExpr());
    }
}
