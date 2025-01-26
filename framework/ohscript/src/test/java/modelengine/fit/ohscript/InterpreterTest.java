/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.alibaba.fastjson.JSONObject;

import modelengine.fit.ohscript.fixture.ExternalCall;
import modelengine.fit.ohscript.fixture.Female;
import modelengine.fit.ohscript.fixture.Human;
import modelengine.fit.ohscript.script.engine.OhScript;
import modelengine.fit.ohscript.script.engine.ScriptBuilder;
import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.interpreter.ASFEnv;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.Oh;
import modelengine.fit.ohscript.script.interpreter.OhType;
import modelengine.fit.ohscript.script.lexer.Lexer;
import modelengine.fit.ohscript.script.parser.ASF;
import modelengine.fit.ohscript.script.parser.AST;
import modelengine.fit.ohscript.script.parser.GrammarBuilder;
import modelengine.fit.ohscript.script.parser.ParserBuilder;
import modelengine.fit.ohscript.util.EmptyValue;
import modelengine.fit.ohscript.util.TestResource;
import modelengine.fitframework.util.ObjectUtils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解释器测试
 *
 * @since 1.0
 */
class InterpreterTest {
    private ParserBuilder parserBuilder;

    private OhScript script;

    @BeforeEach
    void setup() {
        script = new OhScript();
        this.parserBuilder = new ParserBuilder();
    }

    @Test
    void test_interpret_operations() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "let a=true; !a");
        ASTEnv env = new ASTEnv(ast);
        env.execute();
        assertEquals(false, env.execute());

        ast = this.parserBuilder.parseString("", "var a=1,c=1; let b = a++, d=c--; {a:a, b:b, c:c, d:d}");
        env = new ASTEnv(ast);
        Oh result = ObjectUtils.cast(env.execute());
        assertEquals(2, result.get("a"));
        assertEquals(1, result.get("b"));
        assertEquals(0, result.get("c"));
        assertEquals(1, result.get("d"));

        ast = this.parserBuilder.parseString("",
                "var a=1,c=1; let b = ++a, d=--c, e=-a; return {a:a, b:b, c:c, d:d, e:e};");
        env = new ASTEnv(ast);
        result = ObjectUtils.cast(env.execute());
        assertEquals(2, result.get("a"));
        assertEquals(2, result.get("b"));
        assertEquals(0, result.get("c"));
        assertEquals(0, result.get("d"));
        assertEquals(-2, result.get("e"));

        ast = this.parserBuilder.parseString("", "let a=-100; -(a+1)");
        env = new ASTEnv(ast);
        assertEquals(99, env.execute());
    }

    @Test
    void test_interpret_import_node() throws OhPanic {
        this.parserBuilder.begin();
        this.parserBuilder.parseString("m1", "import f1,f2 from m2; f2(); f2(); f1()");
        this.parserBuilder.parseString("m2",
                "import a as b,f3 from m3; var a=10; func f1(){" + System.lineSeparator() + "a+b"
                        + System.lineSeparator() + "}; func f2(){a+=10} f3();  export f1,f2;");
        this.parserBuilder.parseString("m3", "let a=100; func f3(){a+=100}; export a,f3;");
        ASF asf = this.parserBuilder.done();

        ASFEnv env = new ASFEnv(asf);
        assertEquals(230, env.execute("m1"));
    }

    @Test
    void test_if_statement() throws OhPanic {
        AST ast = this.parserBuilder.parseString("",
                "let a=4, b=6,c=3; var d; if(a>b){d=\"a\";}else if(a>c){d=\"c\";}else{d=\"b\";}"
                        + System.lineSeparator() + " d");
        ASTEnv env = new ASTEnv(ast);
        assertEquals("c", env.execute());
    }

    @Test
    void test_while_statement() throws OhPanic {
        AST ast = this.parserBuilder.parseString("",
                "var a=4,b=0; while(a>0){b += 10;a--;}" + System.lineSeparator() + " b");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(40, env.execute());

        ast = this.parserBuilder.parseString("",
                "var a=4; while(a>0){a--;if(a==1){break;}}" + System.lineSeparator() + " a");
        env = new ASTEnv(ast);
        assertEquals(1, env.execute());

        ast = this.parserBuilder.parseString("",
                "var a=4; while(a>0){a--;if(a>1){continue;}a=100; break;}" + System.lineSeparator() + " a");
        env = new ASTEnv(ast);
        assertEquals(100, env.execute());
    }

    @Test
    void test_do_statement() throws OhPanic {
        AST ast = this.parserBuilder.parseString("",
                "var a=0, b=0; do{b+=1; a++;}while(a<10)" + System.lineSeparator() + " a");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(10, env.execute());
    }

    @Test
    void test_array() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "var a=[[10,20,30],[1,2],[100]]; a[0][0]+a[1][0]+a[2][0]");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(111, env.execute());

        ast = this.parserBuilder.parseString("", "var a=[1,2,(3+5)]; a[2]+a[1]");
        env = new ASTEnv(ast);
        assertEquals(10, env.execute());

        ast = this.parserBuilder.parseString("", "let a=[{age:20}]; a[0].age");
        env = new ASTEnv(ast);
        assertEquals(20, env.execute());

        ast = this.parserBuilder.parseString("",
                "let a={books:[{name:\"book1\"}]}; a.books[0].name =\"my \" + a.books[0].name; a" + ".books[0].name");
        env = new ASTEnv(ast);
        assertEquals("my book1", env.execute());

        ast = this.parserBuilder.parseString("", "var a=[1,2,3]; a[1+1]");
        env = new ASTEnv(ast);
        assertEquals(3, env.execute());

        ast = this.parserBuilder.parseString("", "var a=[],b=[1,2,3]; a.push(5); b[1]+a[0]");
        env = new ASTEnv(ast);
        assertEquals(7, env.execute());

        ast = this.parserBuilder.parseString("", "var a=[1,2,3,8]; a.size()");
        env = new ASTEnv(ast);
        assertEquals(4, env.execute());

        ast = this.parserBuilder.parseString("", "var a=[]; a.isEmpty()");
        env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        ast = this.parserBuilder.parseString("", "var a=[1,2,3,8]; a.isEmpty()");
        env = new ASTEnv(ast);
        assertEquals(false, env.execute());
    }

    @Test
    void test_bool() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "true");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        ast = this.parserBuilder.parseString("", "false");
        env = new ASTEnv(ast);
        assertEquals(false, env.execute());

        ast = this.parserBuilder.parseString("", "true==false");
        env = new ASTEnv(ast);
        assertEquals(false, env.execute());

        ast = this.parserBuilder.parseString("", "false==false && true==true");
        env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        ast = this.parserBuilder.parseString("", "let a = null; false==true && true==a.b");
        env = new ASTEnv(ast);
        assertEquals(false, env.execute());

        ast = this.parserBuilder.parseString("", "let a = null; a==null || true==a.b");
        env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        ast = this.parserBuilder.parseString("", "if(true){1}else {2}");
        env = new ASTEnv(ast);
        assertEquals(1, env.execute());

        ast = this.parserBuilder.parseString("", "if(false){1}else {2}");
        env = new ASTEnv(ast);
        assertEquals(2, env.execute());
    }

    @Test
    void test_and_or() throws OhPanic {
        this.testBoolExpression("true && true", true);
        this.testBoolExpression("true && false", false);
        this.testBoolExpression("false && true", false);
        this.testBoolExpression("false && false", false);
        this.testBoolExpression("true && true && true", true);
        this.testBoolExpression("true && true && false", false);
        this.testBoolExpression("true && true && true && true", true);
        this.testBoolExpression("true && true && true && false", false);
        this.testBoolExpression("(true && true) && true", true);
        this.testBoolExpression("true && (true && false)", false);
        this.testBoolExpression("true && (true && true) && true", true);
        this.testBoolExpression("true && true && (true && false)", false);
        this.testBoolExpression("true && true && (true && false)", false);
        this.testBoolExpression("true && (false && true) && true", false);

        this.testBoolExpression("true || true", true);
        this.testBoolExpression("true || false", true);
        this.testBoolExpression("false || true", true);
        this.testBoolExpression("false || false", false);
        this.testBoolExpression("(false || false) || true", true);
        this.testBoolExpression("false || (false || false)", false);
        this.testBoolExpression("false || (false || false) || true", true);
        this.testBoolExpression("false || false || (false || false)", false);
        this.testBoolExpression("false || false || false || false", false);

        this.testBoolExpression("(true && true) || true", true);
        this.testBoolExpression("true && (true || false)", true);
        this.testBoolExpression("true && (true || true) && true", true);
        this.testBoolExpression("true && true || (true && false)", true);
        this.testBoolExpression("true && false || (true && false)", false);
        this.testBoolExpression("true && false || (true && true)", true);
        this.testBoolExpression("true && (false || true) && true", true);
        this.testBoolExpression("(false && false) || true", true);
        this.testBoolExpression("false || (false && false)", false);
        this.testBoolExpression("false || (true && false)", false);
        this.testBoolExpression("true || (true && false)", true);
        this.testBoolExpression("false || (true && true)", true);
        this.testBoolExpression("false || (false && false) || true", true);
        this.testBoolExpression("false || false && (false || false)", false);
        this.testBoolExpression("false || false || false && false", false);
    }

    private void testBoolExpression(String expression, boolean expected) throws OhPanic {
        AST ast = this.parserBuilder.parseString("", expression);
        ASTEnv env = new ASTEnv(ast);
        assertEquals(expected, env.execute());
    }

    @Test
    void test_map() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "var a=[:]; a.put(\"name\",\"wi\\\"1\\\"ll\"); a.get(\"name\")");
        ASTEnv env = new ASTEnv(ast);
        assertEquals("wi\"1\"ll", env.execute());

        ast = this.parserBuilder.parseString("",
                "var a=[:]; a.put(\"will\",[:]); a.get(\"will\").put(\"age\",47); a.get(\"will\").get(\"age\")");
        env = new ASTEnv(ast);
        assertEquals(47, env.execute());

        ast = this.parserBuilder.parseString("",
                "var a=[:]; a.put(\"people\",[\"will\",\"evan\"]); a.get(\"people\")[1]");
        env = new ASTEnv(ast);
        assertEquals("evan", env.execute());

        ast = this.parserBuilder.parseString("",
                "var a=[]; a.push({map:[:]}); a[0].map.put(\"name\",\"will\"); a[0].map.get(\"name\")");
        env = new ASTEnv(ast);
        assertEquals("will", env.execute());

        Oh result = ObjectUtils.cast(script.load("[\"name\":\"Will\",\"age\":47]").execute());
        assertEquals("Will", result.get("name"));
        assertEquals(47, result.get("age"));

        assertEquals("Will", script.load("let a=[:]; a[\"name\"]=\"Will\"; a[\"name\"]").execute());
    }

    @Test
    void test_tuple() throws OhPanic {
        AST ast = this.parserBuilder.parseString("",
                "let c=(10), d=(\"oh\"); c+d"); // one item is not tuple, (10) will be 10;
        ASTEnv env = new ASTEnv(ast);
        assertEquals("10oh", env.execute());

        ast = this.parserBuilder.parseString("", "let a=(10,20,30,40), (_,b,_,d) = a; b+d");
        env = new ASTEnv(ast);
        assertEquals(60, env.execute());

        ast = this.parserBuilder.parseString("", "let will = {age:47, height:168}, (_,height) = will; height");
        env = new ASTEnv(ast);
        assertEquals(168, env.execute());

        ast = this.parserBuilder.parseString("",
                "let will = (47,168,\"male\",(\"will\",\"zhang\")), (age,..,(_,last)) = will; last+age");
        env = new ASTEnv(ast);
        assertEquals("zhang47", env.execute());

        ast = this.parserBuilder.parseString("",
                "func someone(last_name,first_name,birthday,calculate_age){" + System.lineSeparator()
                        + "    let name = last_name+\" \"+first_name;" + System.lineSeparator()
                        + "    let age = calculate_age(birthday);" + System.lineSeparator() + "    (name,age)"
                        + System.lineSeparator() + "}" + System.lineSeparator()
                        + "func birthday_to_age(year){2023-year}" + System.lineSeparator()
                        + "let me = someone(\"will\",\"zhang\",1976,birthday_to_age); me.1");
        env = new ASTEnv(ast);
        assertEquals(47, env.execute());
    }

    @Test
    void test_each() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "let a=[1,2,3,4]; var c=0; each (b,i) in a {c+=b+i;} c");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(16, env.execute());

        ast = this.parserBuilder.parseString("", "let a=[1,2,3,4]; var c=0; each (b,i) in a {c+=b+i;if(c==4) {i}}");
        env = new ASTEnv(ast);
        assertEquals(1, env.execute());

        ast = this.parserBuilder.parseString("", "let a=[1,2,3,4]; var c=0; each (b,i) in a {c+=b+i;if(c==4) {i}}");
        env = new ASTEnv(ast);
        assertEquals(1, env.execute());
    }

    @Test
    void test_array_forEach() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "let a=[1,2,3,4]; var c=0; a.forEach(i=>{c+=i}); c");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(10, env.execute());
    }

    @Test
    void test_array_parallel_with_lock() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "let a=[1,2,3,4]; var c=0; warning(\"parallel starts....\"); "
                + "a.parallel(i=>{lock{c+=i}}); warning(\"parallel ends....\"); c");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(10, env.execute());
    }

    @Test
    void test_async() throws OhPanic {
        ASTEnv env = script.load("let promise = async{log(\"before sleeping...\"); sleep(10); "
                + "log(\"after sleeping...\"); 100}; log(\"main threading...\"); promise.await()");
        assertEquals(100, env.execute());

        env = script.load("let promise = async{sleep(100); log(\"time is up.\"); 100}; var r = 0;"
                + " log(\"async calling....\"); promise.then(result=>{log(\"the result is:\"+result); r = result}); "
                + "while(r==0){sleep(10);} log(\"get result.\"); r");
        assertEquals(100, env.execute());
    }

    @Test
    void test_array_map() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "let a=[1,2,3,4]; var c = a.map(i=>i*10+\"abc\"); c[1]");
        ASTEnv env = new ASTEnv(ast);
        assertEquals("20abc", env.execute());
    }

    @Test
    void test_array_filter() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "let a=[1,2,3,4]; var c = a.filter(i=>i>1); c.size()");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(3, env.execute());
    }

    @Test
    void test_pipe_forward() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "func f1(x){x+1};func f2(x){x*2};func f3(x){x*3}; 3>>f1>>f2>>f3");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(24, env.execute());

        ast = this.parserBuilder.parseString("",
                "func f1(x){x+1};func f2(x){x*2};func f3(x,y){x*3+y}; 3>>f1>>f2>>f3(10)");
        env = new ASTEnv(ast);
        assertEquals(38, env.execute());

        ast = this.parserBuilder.parseString("",
                "func f1(x){x+1};func f2(x){x*2};func f3(x,y){x*3+y}; (3>>f1>>f2>>f3)(10)");
        env = new ASTEnv(ast);
        assertEquals(34, env.execute());
    }

    @Test
    void test_interpret_return_number_or_string() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "10");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(10, env.execute());

        ast = this.parserBuilder.parseString("", "\"result\"");
        env = new ASTEnv(ast);
        assertEquals("result", env.execute());

        ast = this.parserBuilder.parseString("",
                "let me = \"   will zhang   \"; me.trim().split(\" \")[0].upper().lower().ends_with(\"ill\")");
        env = new ASTEnv(ast);
        assertEquals(true, env.execute());
        ast = this.parserBuilder.parseString("", "let me = 10.111; me.ceil().to_str()");
        env = new ASTEnv(ast);
        assertEquals("11", env.execute());
    }

    @Test
    void test_interpret_return_expression() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "return 10+30/5;");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(16, env.execute());

        ast = this.parserBuilder.parseString("", "(10+30)/5");
        env = new ASTEnv(ast);
        assertEquals(8, env.execute());

        ast = this.parserBuilder.parseString("", "\"abc\"+30/5");
        env = new ASTEnv(ast);
        assertEquals("abc6", env.execute());
    }

    @Test
    void test_interpret_return_variable() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "let x=10,y=x*3;let z=x+y; x*(10+y)+z+20.5");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(460.5, env.execute());

        ast = this.parserBuilder.parseString("", "let x=10,y=5; x>y?x:y");
        env = new ASTEnv(ast);
        assertEquals(10, env.execute());

        ast = this.parserBuilder.parseString("", "let f1 = {let w=10; return ()=>{w+1};}; f1()");
        env = new ASTEnv(ast);
        assertEquals(11, env.execute());
    }

    @Test
    void test_function_call() throws OhPanic {
        AST ast = this.parserBuilder.parseString("",
                "let x=50; func func1(x){x+10};" + System.lineSeparator() + " func1(x+10)");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(70, env.execute());

        ast = this.parserBuilder.parseString("",
                "let y=50; func func1(x){x+y+10};" + System.lineSeparator() + " func1(10)");
        env = new ASTEnv(ast);
        assertEquals(70, env.execute());

        ast = this.parserBuilder.parseString("",
                "let y=50; func func1(x){let y=40;x+y+10};" + System.lineSeparator() + " func1(10)");
        env = new ASTEnv(ast);
        assertEquals(60, env.execute());

        ast = this.parserBuilder.parseString("",
                "let y=50; func func1(x){let y={let x=10;x};x+y+10};" + System.lineSeparator() + " func1(10)");
        env = new ASTEnv(ast);
        assertEquals(30, env.execute());
    }

    @Test
    void test_curry_function_call() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "func func1(x,y,z,w){x+y+z+w};" + System.lineSeparator()
                + " let r1 = func1(10,20), r2=r1(10), r3=r2(\"abc\"); r3");
        ASTEnv env = new ASTEnv(ast);
        assertEquals("40abc", env.execute());

        ast = this.parserBuilder.parseString("", "func func1(x,y,z,w){x+y+z+w};" + System.lineSeparator()
                + " let r1 = func1(20)(30), r2=r1(10), r3=r2(40); r3");
        env = new ASTEnv(ast);
        assertEquals(100, env.execute());

        ast = this.parserBuilder.parseString("",
                "func func1(x){let x1=x+1; (y,z,w)=>x1+y+z+w};" + System.lineSeparator()
                        + " let r1 = func1(1)(10), r2=r1(100), r3=r2(1000); " + "r3");
        env = new ASTEnv(ast);
        assertEquals(1112, env.execute());
    }

    @Test
    void test_anti_seq_func_call() throws OhPanic {
        AST ast = this.parserBuilder.parseString("",
                "func func2(y){func1(y)+10};" + System.lineSeparator() + " func func1(x){x+10};"
                        + System.lineSeparator() + " func2(10)");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(30, env.execute());
    }

    @Test
    void test_entity() throws OhPanic {
        AST ast = this.parserBuilder.parseString("",
                "let will = {age:{var age=47; age++; age},create:()=>{" + "let age=this.age; {get:func(){age+2}}}};"
                        + System.lineSeparator() + " will.create().get()");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(50, env.execute());
    }

    @Test
    void test_inherited_entity() throws OhPanic {
        AST ast = this.parserBuilder.parseString("",
                "let will = {age:48, add:()=>this.age+2}; let son = will::{add:()=>this.base.add()+2}; "
                        + "let grand_son=son::{add:()=>base.base.add()+this.base.add()}; son.age+=2; grand_son"
                        + ".add()");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(106, env.execute());

        ast = this.parserBuilder.parseString("",
                "let will = {age:{old:10, young:20}}; let son = will::{add:()=>age}; let grand = son::{add:func"
                        + "()=>age}; grand.add().old");
        env = new ASTEnv(ast);
        assertEquals(10, env.execute());
    }

    @Test
    void test_for() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "var a=0; let b=\"will\"; for(var b=0; b<10; b++){a+=10;} a+b");
        ASTEnv env = new ASTEnv(ast);
        assertEquals("100will", env.execute());
    }

    @Test
    void test_multi_level_entity() throws OhPanic {
        AST ast = this.parserBuilder.parseString("",
                "let will = {son:{ func born(){{age:2*8}}; born()}, age:48, run:()=>{this" + ".age/2+this.son.age}};"
                        + System.lineSeparator() + " will.run()");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(40, env.execute());
    }

    @Test
    void test_function_return_entity() throws OhPanic {
        String code = "func human(age,height){let real_age = age+1; " + System.lineSeparator()
                + "return {_age: real_age, height:height, age:()=>{(_age+this._age)/2},"
                + "run:()=>{this.height = this.height+10; this.height+height}};};" + System.lineSeparator()
                + "let will = human(47,168), evan = human(18,175);" + System.lineSeparator()
                + "will.age()+evan.age()+will.run()";
        AST ast = this.parserBuilder.parseString("", code);
        ASTEnv env = new ASTEnv(ast);
        assertEquals(67 + 346, env.execute());
    }

    @Test
    void test_private_properties_in_entity() throws OhPanic {
        AST ast = this.parserBuilder.parseString("",
                "let will = {_age : 48, age:()=>{this._age = _age+10; (_age+this._age)/2}};" + System.lineSeparator()
                        + " will.age()");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(58, env.execute());
    }

    @Test
    void test_lambda() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "let fun1 = x=>x+10; fun1(10)");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(20, env.execute());

        ast = this.parserBuilder.parseString("", "let fun1 = f=>f(10,2,10)[0]+10; fun1((x,y,z)=>[x*y*10+z,1,1])");
        env = new ASTEnv(ast);
        assertEquals(220, env.execute());
    }

    @Test
    void test_all_returns() throws OhPanic {
        AST ast = this.parserBuilder.parseString("", "let age = {let age=10; age+10}; age");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(20, env.execute());

        ast = this.parserBuilder.parseString("", "let age = {let age=10; }; age");
        env = new ASTEnv(ast);
        assertEquals(EmptyValue.IGNORE, env.execute());

        ast = this.parserBuilder.parseString("", "var age = 10; let b=1; if(b==1){age = 30;} age");
        env = new ASTEnv(ast);
        assertEquals(30, env.execute());

        ast = this.parserBuilder.parseString("", "var age = 10; let b=1; if(b==1){40}else{30} age");
        env = new ASTEnv(ast);
        assertEquals(40, env.execute());

        ast = this.parserBuilder.parseString("", "var age = 10; let b=2; if(b==1){40}else{30} age");
        env = new ASTEnv(ast);
        assertEquals(30, env.execute());

        ast = this.parserBuilder.parseString("", "var age = 10; let b=2; while(b>0){50} age");
        env = new ASTEnv(ast);
        assertEquals(50, env.execute());

        ast = this.parserBuilder.parseString("", "var age = 10; let b=2; while(b>0){break;} age");
        env = new ASTEnv(ast);
        assertEquals(10, env.execute());

        ast = this.parserBuilder.parseString("", "let b=2; let age = {while(b>0){30} 20}; age");
        env = new ASTEnv(ast);
        assertEquals(30, env.execute());

        ast = this.parserBuilder.parseString("", "let b=2; let age = {while(b>2){30} 20}; age");
        env = new ASTEnv(ast);
        assertEquals(20, env.execute());

        ast = this.parserBuilder.parseString("", "let b=2; let age = {while(b>0){break;} 20}; age");
        env = new ASTEnv(ast);
        assertEquals(20, env.execute());
    }

    @Test
    public void test_custom_data_invoke() throws IOException, ClassNotFoundException, OhPanic {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        this.script.grant("context", map);

        ASTEnv env = this.script.load("ext::context.put(\"b\",300); let a = ext::context.get(\"a\"); a");
        Map<String, Integer> customMap = new HashMap<>();
        customMap.put("a", 200);
        env.grant("context", customMap);
        assertEquals(200, env.execute());
        assertEquals(300, customMap.get("b").intValue());

        env = this.script.createEnv();
        customMap = new HashMap<>();
        customMap.put("a", 300);
        env.grant("context", customMap);
        assertEquals(300, env.execute());
        assertEquals(300, customMap.get("b").intValue());
    }

    @Test
    void test_complicate_external_data_as_arg() throws OhPanic {
        ExternalCall call = new ExternalCall(1);
        this.parserBuilder.addExternalOh("b", call);
        AST ast = this.parserBuilder.parseString("", "let m = [:]; m.put(\"me\",{strvalue:\"nest_map\"});"
                + "b.run8({strvalue:\"ohscript\",numvalue:100,fix:5," + "nest:{strvalue:\"nest\",numvalue:200,fix:6}, "
                + "list:[{strvalue:\"nest_list\"}]," + "map:m," + "set:[\"will\",\"zhang\"]" + "}); b");
        ASTEnv env = new ASTEnv(ast);
        ExternalCall result = ObjectUtils.cast(env.execute());
        assertEquals("meohscript", result.getStringValue());
        assertEquals(205, result.getNumValue());
        assertEquals(1, result.getFix());
        assertEquals(100, result.getNest().getNumValue());
        assertEquals(100, result.getNest().getNumValue());
        assertEquals("nest_list", result.getList().get(0).getStringValue());
        assertEquals("nest_map", result.getMap().get("me").getStringValue());
        assertEquals(2, result.getSet().size());
    }

    @Test
    void test_external_invoke() throws OhPanic {
        this.testExternalContext();
        this.testExternalCall();
        this.testExternalSourceTarget();
    }

    private void testExternalSourceTarget() throws OhPanic {
        ASTEnv env;
        AST ast;
        List<String> source = new ArrayList<>();
        source.add("will");
        source.add("zhang");
        List<String> target = new ArrayList<>();
        this.parserBuilder.addExternalOh("source", source);
        this.parserBuilder.addExternalOh("target", target);
        ast = this.parserBuilder.parseString("",
                "let source = ext::source, target = ext::target; var i=0; while(i<source.size()){target.add(source"
                        + ".get(i)); i++;} target.get(1)");
        env = new ASTEnv(ast);
        assertEquals("zhang", env.execute());
    }

    private void testExternalCall() throws OhPanic {
        ASTEnv env;
        AST ast;
        ExternalCall call = new ExternalCall(1);
        this.parserBuilder.addExternalOh("b", call);
        ast = this.parserBuilder.parseString("", "let a = b.run1().run2().get(\"a\"); a");
        env = new ASTEnv(ast);
        assertEquals(200, env.execute());

        ast = this.parserBuilder.parseString("",
                "b.run8({strvalue:\"ohscript\", numvalue:100, fix:5,}); b.getStringValue()" + "+b.getNumValue()");
        env = new ASTEnv(ast);
        assertEquals("meohscript205", env.execute());

        ast = this.parserBuilder.parseString("", "let a=[:];a.put(\"name\",\"will\"); b.run4(a); b.getStringValue()");
        env = new ASTEnv(ast);
        assertEquals("meohscriptwill", env.execute());

        ast = this.parserBuilder.parseString("", "b.run5([100,200]); b.getStringValue()");
        env = new ASTEnv(ast);
        assertEquals("meohscriptwill200", env.execute());

        ast = this.parserBuilder.parseString("", "let c = b.run10()>>b.run11; c.get(1)");
        env = new ASTEnv(ast);
        Byte v = 10;
        assertEquals(v, env.execute());

        ast = this.parserBuilder.parseString("", "(b.run12()>>b.run13>>b.run14).get(0).getStringValue()");
        env = new ASTEnv(ast);
        assertEquals("ohscript", env.execute());

        ast = this.parserBuilder.parseString("", " b");
        env = new ASTEnv(ast);
        assertEquals(call, env.execute());
    }

    private void testExternalContext() throws OhPanic {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        this.parserBuilder.addExternalOh("context", map);
        AST ast1 = this.parserBuilder.parseString("",
                "context.put(\"a\",200);context.put(\"b\",300); let a = context.get(\"a\"); a");
        ASTEnv env1 = new ASTEnv(ast1);
        assertEquals(200, env1.execute());
        assertEquals(300, map.get("b").intValue());

        Map<String, Integer> mapInAst = new HashMap<>();
        mapInAst.put("a", 1);
        Map<String, Integer> mapInEnv = new HashMap<>();
        mapInEnv.put("a", 1);
        this.parserBuilder.addExternalOh("context", mapInAst);
        AST ast11 = this.parserBuilder.parseString("", "ext::context.put(\"a\",200);");
        ASTEnv env11 = new ASTEnv(ast11);
        env11.grant("context", mapInEnv);
        env11.execute();
        assertEquals(1, mapInAst.get("a"));
        assertEquals(200, mapInEnv.get("a"));
        AST ast;
        ASTEnv env;

        ast = this.parserBuilder.parseString("", "let as1 = context.put(\"a\"); as1(150); context.get(\"a\")");
        env = new ASTEnv(ast);
        assertEquals(150, env.execute());
    }

    @Test
    void test_while_to_travel_external_list() throws OhPanic {
        List<String> source = new ArrayList<>();
        source.add("will");
        source.add("zhang");
        List<String> target = new ArrayList<>();
        this.parserBuilder.addExternalOh("source", source);
        this.parserBuilder.addExternalOh("target", target);
        AST ast = this.parserBuilder.parseString("", "let source = ext::source; source.isEmpty()");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(false, env.execute());

        ast = this.parserBuilder.parseString("", "let target = ext::target; target.isEmpty()");
        env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        ast = this.parserBuilder.parseString("",
                "let source = ext::source, target = ext::target; var i=0; while(i<source.size()){target.add(source"
                        + ".get(i)); i++;} target.get(1)");
        env = new ASTEnv(ast);
        assertEquals("zhang", env.execute());
    }

    @Test
    void test_serialize() throws IOException, ClassNotFoundException, OhPanic {
        ScriptBuilder builder = this.script.begin();
        builder.loadFile("service", TestResource.getFilePath("fixture/functions.oh"));
        builder.loadFile("handler1", TestResource.getFilePath("fixture/caller.oh"));
        builder.loadFile("handler2", TestResource.getFilePath("fixture/someone_caller.oh"));
        builder.done();
        // for test deserializer
        ASFEnv env = builder.newEnv();

        assertEquals(233, env.execute("handler1"));
        assertEquals("will zhang-47", env.execute("handler2"));
    }

    @Test
    void test_external_util() throws IOException, ClassNotFoundException, OhPanic {
        this.script.load(
                "let map = ext::util.newMap(); map.put(\"will\",{name:\"will\", age:47}); map.get(\"will\")" + ".name");
        ASTEnv env = this.script.createEnv();

        assertEquals("will", env.execute());
    }

    @Test
    void test_load_file() throws IOException, OhPanic {
        this.parserBuilder.begin();
        AST ast1 = this.parserBuilder.parseFile("service", TestResource.getFilePath("fixture/functions.oh"));
        AST ast2 = this.parserBuilder.parseFile("handler1", TestResource.getFilePath("fixture/caller.oh"));
        ASFEnv env = new ASFEnv(this.parserBuilder.done());
        assertEquals(233, env.execute(ast2));

        AST ast3 = this.parserBuilder.parseFile("handler2", TestResource.getFilePath("fixture/someone_caller.oh"));
        env.link();
        assertEquals("will zhang-47", env.execute(ast3));
    }

    @Test
    void test_match_statement() throws OhPanic {
        AST ast = this.parserBuilder.parseString("",
                "let a=(1,2,3); var c=0; match a{|(b,..,d)if(b>0)=>c=b |_=>c=100} c");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(1, env.execute());

        ast = this.parserBuilder.parseString("",
                "let a=(-11,2,3); var c=0; match a{|(b,..,d)if(b>0)=>c=b |_=>c=100} c");
        env = new ASTEnv(ast);
        assertEquals(100, env.execute());

        ast = this.parserBuilder.parseString("",
                "let a=(-11,2,3); var c=0; match a{|(b,..,d)if(b>0)=>c=b |(..,b,d)if(b==2)=>c=d|_=>c=100} c");
        env = new ASTEnv(ast);
        assertEquals(3, env.execute());

        ast = this.parserBuilder.parseString("", "let a=10; var c=0; match a{|10=>c=a|_=>c=100} c");
        env = new ASTEnv(ast);
        assertEquals(10, env.execute());

        ast = this.parserBuilder.parseString("", "let e=(1,2,3);var c=0; match e{|(1,b,_)=>c=b|_=>c=100} c");
        env = new ASTEnv(ast);
        assertEquals(2, env.execute());

        ast = this.parserBuilder.parseString("",
                "let e=(1,2,5);var c=0; match e{|(2,b,_)=>c=b|(_,2,d)=>{c=d;}|_=>c=100} c");
        env = new ASTEnv(ast);
        assertEquals(5, env.execute());

        ast = this.parserBuilder.parseString("",
                "let e=(1,2,5);var c=0; match e{|(2,b,_)=>c=b|(_,2,d)=>{c=d;500}|_=>c=100} c");
        env = new ASTEnv(ast);
        assertEquals(500, env.execute());

        ast = this.parserBuilder.parseString("",
                "let e=(1,2,5);var c=0; match e{|(2,b,_)=>c=b|(_,2,d)=>500|_=>c=100} c");
        env = new ASTEnv(ast);
        assertEquals(500, env.execute());

        ast = this.parserBuilder.parseString("",
                "let type=\"2\"; var r=0; match type{|\"1\"=>r=1|\"2\"=>r=2|\"3\"=>r=3 |_=>r=10} r");
        env = new ASTEnv(ast);
        assertEquals(2, env.execute());

        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> m1 = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        Map<String, Object> m3 = new HashMap<>();
        data.add(m1);
        m1.put("passData", m2);
        m2.put("meta", m3);
        m3.put("fileType", "doc");
        this.parserBuilder.addExternalOh("context", data);
        ast = this.parserBuilder.parseString("",
                "var type = ext::context.get(0).get(\"passData\").get(\"meta\").get(\"fileType\"); match "
                        + "type{|\"doc\"=>\"word\"|\"xls\"=>\"excel\" |_=>\"other\" }");
        env = new ASTEnv(ast);
        assertEquals("word", env.execute());
    }

    @Test
    void test_try_correct() throws OhPanic {
        Map<String, Object> m1 = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        Map<String, Object> m3 = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(m1);
        m1.put("passData", m2);
        m2.put("meta", m3);
        m3.put("fileType", "doc");
        this.parserBuilder.addExternalOh("context", data);
        AST ast = this.parserBuilder.parseString("", "ext::context.get(0)");
        ASTEnv env = new ASTEnv(ast);
        Assertions.assertThat(env.execute()).isNotNull();
    }

    @Test
    @Disabled
    void test_try_incorrect() throws OhPanic {
        Map<String, Object> m1 = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        Map<String, Object> m3 = new HashMap<>();
        List<Map<String, Object>> data = Collections.singletonList(m1);
        m1.put("passData", m2);
        m2.put("meta", m3);
        m3.put("fileType", "doc");
        this.parserBuilder.addExternalOh("context", data);
        AST ast = this.parserBuilder.parseString("", "ext::context.get(0)");
        ASTEnv env = new ASTEnv(ast);
        Assertions.assertThat(env.execute()).isNotNull();
    }

    @Test
    void test_json_string_entity() throws OhPanic {
        this.parserBuilder.addExternalOh("external", new ExternalCall(1));
        AST ast = this.parserBuilder.parseString("",
                "let json = ext::util.stringToJson(\"{'age':47,'name':{'last':'will','first':'zhang'}}\"); ext::util"
                        + ".jsonToEntity(json).name.last");
        ASTEnv env = new ASTEnv(ast);
        assertEquals("will", env.execute());
    }

    @Test
    void test_json_boolean_entity() throws OhPanic {
        this.parserBuilder.addExternalOh("external", new ExternalCall(1));
        AST ast = this.parserBuilder.parseString("",
                "let json = ext::util.stringToJson(\"{'test':true}\"); ext::util.jsonToEntity(json).test");
        ASTEnv env = new ASTEnv(ast);
        assertTrue((boolean) env.execute());
    }

    @Test
    void test_execute_with_arguments() throws OhPanic {
        ExternalCall arg = new ExternalCall(1);
        arg.setStringValue("will");
        arg.setNumValue(200);
        AST ast = this.parserBuilder.parseString("", "args[0].getStringValue()");
        ASTEnv env = new ASTEnv(ast);
        assertEquals("will", env.execute(arg));

        ast = this.parserBuilder.parseString("", "args[0].external_value");
        env = new ASTEnv(ast);
        assertEquals(10, env.execute(10));
    }

    @Test
    void test_externalOh_as_argument() throws OhPanic {
        this.parserBuilder.addExternalOh("external", new ExternalCall(1));
        ExternalCall arg = new ExternalCall(1);
        arg.setStringValue("will");
        arg.setNumValue(200);
        this.parserBuilder.addExternalOh("arg", arg);
        AST ast = this.parserBuilder.parseString("", "(ext::arg>>ext::external.run13).get(0).getStringValue()");
        ASTEnv env = new ASTEnv(ast);
        assertEquals("will", env.execute());

        arg = new ExternalCall(1);
        arg.setStringValue("zhang");
        arg.setNumValue(1000);
        ast.replaceExternalOh("arg", arg);
        assertEquals("zhang", env.execute());
    }

    @Test
    void test_log_warning_error_and_panic() {
        AST ast = this.parserBuilder.parseString("me",
                "log(\"log test\"); warning(\"warning test\"); error(\"error test\"); panic(); 3");
        ASTEnv env = new ASTEnv(ast);
        try {
            env.execute();
            fail();
        } catch (OhPanic ex) {
            assertTrue(true);
        }
    }

    @Test
    void test_type_compare() throws IOException, OhPanic {
        AST ast = this.parserBuilder.parseString("me", "func f1(){}; f1()<:unit");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        try {
            ast = this.parserBuilder.parseString("me", "func f1(){ext::a()}; f1()");
            env = new ASTEnv(ast);
            env.execute();
            assertTrue(false);
        } catch (OhPanic p) {
        }

        ast = this.parserBuilder.parseString("me", "let a = 3; a<:number");
        env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        ast = this.parserBuilder.parseString("me", "let a = null; a<:null");
        env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        ast = this.parserBuilder.parseString("me", "let a = \"someone\"; a<:string");
        env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        ast = this.parserBuilder.parseString("me", "func f(){}; f<:function");
        env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        ast = this.parserBuilder.parseString("me", "let a = {name:\"will\"}; a<:object");
        env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        ast = this.parserBuilder.parseString("me", "let a = []; a<:array");
        env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        ast = this.parserBuilder.parseString("me", "let a = (1,2); a<:tuple");
        env = new ASTEnv(ast);
        assertEquals(true, env.execute());

        try {
            ast = this.parserBuilder.parseFile("me", TestResource.getFilePath("fixture/test.oh"));
            env = new ASTEnv(ast);
            env.execute();
            fail();
        } catch (OhPanic p) {
            assertEquals(1, p.code());
        }
    }

    @Test
    void test_panic() throws OhPanic {
        ASTEnv env = script.load("func f1(){ext::a()}; let result = safe{f1()}; result.panic_code()");
        // 106:VAR_NOT_FOUND
        assertEquals(106, env.execute());

        env = script.load("func f1(){ext::a()}; let result = safe{f1()}; result.get()");
        // 忽略错误码，强行拿结果，为null
        assertEquals(null, env.execute());

        env = script.load("func f1(){100}; let result = safe{f1()}; if(!result.panic_code()){result.get()}");
        assertEquals(100, env.execute());

        env = script.load("func f1(){ext::a()}; let result = f1();");
        try {
            env.execute();
            assertTrue(false);
        } catch (OhPanic p) {
            System.out.println(p.getMessage());
        }
    }

    @Test
    void test_extension() throws OhPanic {
        AST ast = this.parserBuilder.parseString("me", "let b = [1,2,3,4]; b.size()");
        ASTEnv env = new ASTEnv(ast);
        assertEquals(4, env.execute());

        ast = this.parserBuilder.parseString("me", "let b = [1,2,3,4]; b.insert(1,6); b[1]");
        env = new ASTEnv(ast);
        assertEquals(6, env.execute());

        ast = this.parserBuilder.parseString("me", "let b = [1,2,3,4]; b.push(6); b[4]");
        env = new ASTEnv(ast);
        assertEquals(6, env.execute());

        ast = this.parserBuilder.parseString("me", "let b = [1,2,3,4]; b.remove(1); b.size()");
        env = new ASTEnv(ast);
        assertEquals(3, env.execute());
    }

    @Test
    void test_java_new() throws Exception {
        script.grant("Woman", Female.class);
        // 新对象没有覆盖Female方法，在ohscript中执行
        assertEquals("ivy zhao47",
                script.execute("let ivy = Woman{name:\"ivy\",age:47}; ivy.getName(\"zhao\")+ivy.getAge()"));
        // 新对象覆盖Female的getAge方法，在ohscript中执行
        assertEquals("ivy zhao88", script.execute(
                "let ivy = Woman{name:\"ivy\",age:47,getAge:()=>88}; ivy.getName(\"zhao\")+ivy.getAge()"));
        // 新对象覆盖Female的getAge方法，在Java中执行
        Human person = script.implement(Human.class, "Woman{name:\"ivy\",age:47,getAge:()=>88}");
        assertEquals("ivy zhao", person.getName("zhao"));
        assertEquals(88, person.getAge());
    }

    @Test
    void test_java_static_call() throws OhPanic {
        script.grant("Will", ExternalCall.class);
        ASTEnv env = script.load("let will = Will.create(300,\"static me\"); will");
        ExternalCall call = ObjectUtils.cast(env.execute());
        assertEquals("static me", call.getStringValue());
        assertEquals(300, call.getFix());
    }

    @Test
    void test_implement_java_simple_interface() throws Exception {
        String code = "let me = {_name:\"Will\"," + "getName:first=>this._name+\"|\"+first,add:(x,y,z)=>x+y+z}; "
                + "let x=1; me.add(x,2,3); me";
        // Ohscript实现了Human，但只实现了getName，没有实现getAge
        Human person = script.implement(Human.class, code);
        assertEquals("Will|Zhang", person.getName("Zhang"));
        try {
            // 由于没有实现getAge，所以这里报错
            person.getAge();
            fail("exception expected");
        } catch (Exception e) {

        }

        // 直接对Female类生成代理，此时已经最野生生成了Female对象，其中的属性皆为空
        person = script.implement(Female.class, code);
        assertEquals("Will|Zhang", person.getName("Zhang"));
        // 由于ohscript没有覆盖getAge，所以会调用原类的方法
        // 野生属性为空，所以返回空
        assertNull(person.getAge());

        // Ohscript扩展了一个Female对象
        person = script.extend(new Female(), code);
        // 由于实现了getName，所以getName是ohscript的行为，而不是Female的行为，female的返回应该是“elsa zhang”
        assertEquals("Will|Zhang", person.getName("Zhang"));
        assertEquals(200, person.getAge());
    }

    @Test
    void test_java_ohscript_summary() throws Exception {
        // 授权一个Java对象访问
        script.grant("elsa", new Female());
        ASTEnv env = script.load("elsa");
        Object elsa = env.execute();

        // 授权Java类范根
        // ohscript直接创建java object
        script.grant("Female", Female.class);
        // 直接新建了Java对象
        env = script.load("Female{age:100,name:\"Ivy\",getAge:()=>200}");
        // 所以可以直接得到该对象,该对象是Female{..}直接创建的Java对象
        Human ivy = ObjectUtils.cast(env.execute());
        // 调用Java.getAge,该对象没有ohscript.getAge的扩展,如果要得到扩展，使用implement，参考下一段测试
        assertEquals(100, ivy.getAge());
        assertEquals("Ivy Zhao", ivy.getName("Zhao"));
        assertEquals(100, ivy.getAge());

        // 通过implement得到的是Java对象的Ohscript扩展，跟上面的区别是,该扩展持有了ohscript.getAge
        ivy = env.createOhProxy(Human.class);
        // getName没有ohscript扩展，所以调用Java.getName
        assertEquals("Ivy Zhao", ivy.getName("Zhao"));
        // getAge有ohscript扩展，所以直接调用ohscript.getAge
        assertEquals(200, ivy.getAge());

        // 通过ohscript entity实现一个Java接口
        // implement方法决定了引擎将通过proxy把ohscript对象适配上一个Human代理
        ivy = script.implement(Human.class, "{_name:\"Ivy\",getName:(first)=>this._name+\"|\"+first}");
        // assertEquals(100,ivy.getAge()); 该实现是没有getAge能力的
        assertEquals("Ivy|Zhao", ivy.getName("Zhao"));

        // 通过ohscript entity实现对一个Java对象的扩展
        ivy = script.extend(new Female(), "{getAge:()=>300}");
        assertEquals("Elsa Zhao", ivy.getName("Zhao"));
        assertEquals(300, ivy.getAge());
    }

    @Test
    void test_complex_oh_return() throws OhPanic {
        ASTEnv env = script.load("let m=[:]; m.put(\"me\",\"will\"); m.put(\"you\",\"elsa\");"
                + "{_str:\"str\",_num:40,_tup:(\"will\",20)," + "_entity:{name:\"will\",age:20},_arr:[1,2,3],_m:m,"
                + "getName:first=>this._name+\" \"+first}");
        Oh result = ObjectUtils.cast(env.execute());
        assertEquals(40, result.get("_num"));
        assertEquals("str", result.get("_str"));
        assertEquals(OhType.TUPLE, ObjectUtils.<Oh>cast(result.get("_tup")).type());
        assertEquals(OhType.MAP, ObjectUtils.<Oh>cast(result.get("_m")).type());
        assertEquals(OhType.ENTITY, ObjectUtils.<Oh>cast(result.get("_entity")).type());
        assertEquals(OhType.LIST, ObjectUtils.<Oh>cast(result.get("_arr")).type());
    }

    @Test
    void test_get_last_json_object_null_field() throws OhPanic {
        Map<String, Object> businessData = new HashMap<>();
        HashMap<Object, Object> internal = new HashMap<>();
        businessData.put("_internal", internal);

        HashMap<Object, Object> outputScope = new HashMap<>();
        internal.put("outputScope", outputScope);

        HashMap<Object, Object> jadew98cnn = new HashMap<>();
        outputScope.put("jadea0rbki", jadew98cnn);

        HashMap<Object, Object> output = new HashMap<>();
        jadew98cnn.put("output", output);

        output.put("needTime", false);
        output.put("needProduct", false);
        output.put("needIndicator", true);
        output.put("groupBy", "");

        String userDataKey = "userData";
        String businessDataKey = "businessData";
        Map<String, Object> userData = new HashMap<>();
        userData.put(businessDataKey, JSONObject.toJSONString(businessData));

        ParserBuilder newParserBuilder = new ParserBuilder(new GrammarBuilder(), new Lexer());
        newParserBuilder.addExternalOh(userDataKey, userData);

        String code = "let businessData = ext::util.stringToJson(ext::userData.get(\"businessData\"));  "
                + "let passData = ext::util.stringToJson(ext::userData.get(\"passData\")); "
                + "(businessData.get(\"_internal\").get(\"outputScope\").get(\"jadea0rbki\").get(\"output\")"
                + ".get(\"needTime\") == false) " + "&& "
                + "(businessData.get(\"_internal\").get(\"outputScope\").get(\"jadea0rbki\").get(\"output\")"
                + ".get(\"needProduct\") == false) " + "&& "
                + "(businessData.get(\"_internal\").get(\"outputScope\").get(\"jadea0rbki\").get(\"output\")"
                + ".get(\"needIndicator\") == false) " + "&& "
                + "(businessData.get(\"_internal\").get(\"outputScope\").get(\"jadea0rbki\").get(\"output\")"
                + ".get(\"groupBy\") == null)";
        AST ast = newParserBuilder.parseString("", code);
        ASTEnv env = new ASTEnv(ast);
        assertEquals(false, env.execute());
    }

    @Test
    void test_external_null_equals_null() throws OhPanic {
        ParserBuilder newParserBuilder = new ParserBuilder(new GrammarBuilder(), new Lexer());
        Map<String, Object> map = new HashMap<>();
        map.put("abc", null);
        newParserBuilder.addExternalOh("map", map);
        assertNullEquals(newParserBuilder, true, "ext::map.get(\"abc\") == null");
        assertNullEquals(newParserBuilder, true, "null == ext::map.get(\"abc\")");
        assertNullEquals(newParserBuilder, false, "\"aa\" == ext::map.get(\"abc\")");

        assertNullEquals(newParserBuilder, false, "ext::map.get(\"abc\") != null");
        assertNullEquals(newParserBuilder, false, "null != ext::map.get(\"abc\")");
        assertNullEquals(newParserBuilder, true, "\"aa\" != ext::map.get(\"abc\")");
    }

    @Test
    void test_compare_with_null() throws OhPanic {
        ParserBuilder newParserBuilder = new ParserBuilder(new GrammarBuilder(), new Lexer());
        Map<String, Object> map = new HashMap<>();
        map.put("abc", null);
        newParserBuilder.addExternalOh("map", map);
        assertThrowsExactly(OhPanic.class, () -> runCompareWithNull(newParserBuilder, "3 > ext::map.get(\"abc\")"));
        assertThrowsExactly(OhPanic.class, () -> runCompareWithNull(newParserBuilder, "3 >= ext::map.get(\"abc\")"));
        assertThrowsExactly(OhPanic.class, () -> runCompareWithNull(newParserBuilder, "3 < ext::map.get(\"abc\")"));
        assertThrowsExactly(OhPanic.class, () -> runCompareWithNull(newParserBuilder, "3 <= ext::map.get(\"abc\")"));

        assertThrowsExactly(OhPanic.class, () -> runCompareWithNull(newParserBuilder, "ext::map.get(\"abc\") > 3"));
        assertThrowsExactly(OhPanic.class, () -> runCompareWithNull(newParserBuilder, "ext::map.get(\"abc\") >= 3"));
        assertThrowsExactly(OhPanic.class, () -> runCompareWithNull(newParserBuilder, "ext::map.get(\"abc\") < 3"));
        assertThrowsExactly(OhPanic.class, () -> runCompareWithNull(newParserBuilder, "ext::map.get(\"abc\") <= 3"));

        assertThrowsExactly(OhPanic.class, () -> runCompareWithNull(newParserBuilder, "-3 > ext::map.get(\"abc\")"));
        assertThrowsExactly(OhPanic.class, () -> runCompareWithNull(newParserBuilder, "-3 >= ext::map.get(\"abc\")"));
        assertThrowsExactly(OhPanic.class, () -> runCompareWithNull(newParserBuilder, "-3 < ext::map.get(\"abc\")"));
        assertThrowsExactly(OhPanic.class, () -> runCompareWithNull(newParserBuilder, "-3 <= ext::map.get(\"abc\")"));
    }

    private void runCompareWithNull(ParserBuilder newParserBuilder, String code) throws OhPanic {
        AST ast = newParserBuilder.parseString("", code);
        ASTEnv env = new ASTEnv(ast);
        env.execute();
    }

    private void assertNullEquals(ParserBuilder newParserBuilder, boolean expected, String code) throws OhPanic {
        AST ast = newParserBuilder.parseString("", code);
        ASTEnv env = new ASTEnv(ast);
        assertEquals(expected, env.execute());
    }

    @Test
    void test_get_json_property() throws OhPanic {
        String result = "123";
        Map<String, Object> businessData = new HashMap<>();
        HashMap<Object, Object> key2 = new HashMap<>();
        businessData.put("key2", key2);
        key2.put("Key21", result);

        String userDataKey = "userData";
        String businessDataKey = "businessData";
        Map<String, Object> userData = new HashMap<>();
        userData.put(businessDataKey, JSONObject.toJSONString(businessData));

        ParserBuilder newParserBuilder = new ParserBuilder(new GrammarBuilder(), new Lexer());
        newParserBuilder.addExternalOh(userDataKey, userData);

        String code = "let businessDataJson = ext::util.stringToJson(ext::userData.get(\"businessData\")); "
                + "(businessDataJson.get(\"key2\").get(\"Key21\"))";
        AST ast = newParserBuilder.parseString("", code);
        ASTEnv env = new ASTEnv(ast);
        assertEquals(result, env.execute());
    }

    @Test
    void test_number_compare() throws OhPanic {
        Map<String, Object> businessData = new HashMap<>();
        String testInt = "testInt";
        String testFloat10 = "testFloat1.0";
        String testFloat11 = "testFloat1.1";
        String testDouble10 = "testDouble1.0";
        String testDouble11 = "testDouble1.1";
        businessData.put(testInt, 1);
        businessData.put(testFloat10, 1.0F);
        businessData.put(testFloat11, 1.1F);
        businessData.put(testDouble10, 1.0D);
        businessData.put(testDouble11, 1.1D);

        String userDataKey = "userData";
        String businessDataKey = "businessData";
        Map<String, Object> userData = new HashMap<>();
        userData.put(businessDataKey, JSONObject.toJSONString(businessData));

        ParserBuilder newParserBuilder = new ParserBuilder(new GrammarBuilder(), new Lexer());
        newParserBuilder.addExternalOh(userDataKey, userData);

        this.compareNumberTrue(newParserBuilder, testInt, testFloat10, testDouble10);
        this.compareNumberFalse(newParserBuilder, testInt, testFloat11, testDouble11, testFloat10);
    }

    private void compareNumberFalse(ParserBuilder newParserBuilder, String testInt, String testFloat11,
            String testDouble11, String testFloat10) throws OhPanic {
        compareTwoJsonNumber(newParserBuilder, testInt, testFloat11, false);
        compareTwoJsonNumber(newParserBuilder, testInt, testDouble11, false);
        compareTwoJsonNumber(newParserBuilder, testFloat11, testInt, false);
        compareTwoJsonNumber(newParserBuilder, testFloat10, testDouble11, false);
        compareTwoJsonNumber(newParserBuilder, testDouble11, testInt, false);
        compareTwoJsonNumber(newParserBuilder, testDouble11, testFloat10, false);
        compareLeftJsonNumber(newParserBuilder, testInt, "1.1", false);
        compareLeftJsonNumber(newParserBuilder, testFloat11, "1", false);
        compareLeftJsonNumber(newParserBuilder, testFloat11, "1.0", false);
        compareLeftJsonNumber(newParserBuilder, testDouble11, "1", false);
        compareLeftJsonNumber(newParserBuilder, testDouble11, "1.0", false);
        compareRightJsonNumber(newParserBuilder, "1.1", testInt, false);
        compareRightJsonNumber(newParserBuilder, "1", testFloat11, false);
        compareRightJsonNumber(newParserBuilder, "1.0", testFloat11, false);
        compareRightJsonNumber(newParserBuilder, "1", testDouble11, false);
        compareRightJsonNumber(newParserBuilder, "1.0", testDouble11, false);
        compareNumber(newParserBuilder, "1", "1.1", false);
        compareNumber(newParserBuilder, "1.1", "1", false);
        compareNumber(newParserBuilder, "1.0", "1.1", false);
        compareNumber(newParserBuilder, "1.1", "1.0", false);
    }

    private void compareNumberTrue(ParserBuilder newParserBuilder, String testInt, String testFloat10,
            String testDouble10) throws OhPanic {
        compareTwoJsonNumber(newParserBuilder, testInt, testFloat10, true);
        compareTwoJsonNumber(newParserBuilder, testInt, testDouble10, true);
        compareTwoJsonNumber(newParserBuilder, testFloat10, testInt, true);
        compareTwoJsonNumber(newParserBuilder, testFloat10, testDouble10, true);
        compareTwoJsonNumber(newParserBuilder, testDouble10, testInt, true);
        compareTwoJsonNumber(newParserBuilder, testDouble10, testFloat10, true);
        compareLeftJsonNumber(newParserBuilder, testInt, "1", true);
        compareLeftJsonNumber(newParserBuilder, testInt, "1.0", true);
        compareLeftJsonNumber(newParserBuilder, testFloat10, "1", true);
        compareLeftJsonNumber(newParserBuilder, testFloat10, "1.0", true);
        compareLeftJsonNumber(newParserBuilder, testDouble10, "1", true);
        compareLeftJsonNumber(newParserBuilder, testDouble10, "1.0", true);
        compareRightJsonNumber(newParserBuilder, "1", testInt, true);
        compareRightJsonNumber(newParserBuilder, "1.0", testInt, true);
        compareRightJsonNumber(newParserBuilder, "1", testFloat10, true);
        compareRightJsonNumber(newParserBuilder, "1.0", testFloat10, true);
        compareRightJsonNumber(newParserBuilder, "1", testDouble10, true);
        compareRightJsonNumber(newParserBuilder, "1.0", testDouble10, true);
        compareNumber(newParserBuilder, "1", "1", true);
        compareNumber(newParserBuilder, "1", "1.0", true);
        compareNumber(newParserBuilder, "1.0", "1", true);
    }

    private void compareRightJsonNumber(ParserBuilder newParserBuilder, String left, String right, boolean expected)
            throws OhPanic {
        compareNumber(newParserBuilder, left, "businessDataJson.get(\"" + right + "\")", expected);
    }

    private void compareLeftJsonNumber(ParserBuilder newParserBuilder, String left, String right, boolean expected)
            throws OhPanic {
        compareNumber(newParserBuilder, "businessDataJson.get(\"" + left + "\")", right, expected);
    }

    private void compareTwoJsonNumber(ParserBuilder newParserBuilder, String left, String right, boolean expected)
            throws OhPanic {
        compareNumber(newParserBuilder, "businessDataJson.get(\"" + left + "\")",
                "businessDataJson.get(\"" + right + "\")", expected);
    }

    private void compareNumber(ParserBuilder newParserBuilder, String left, String right, boolean expected)
            throws OhPanic {
        String code = "let businessDataJson = ext::util.stringToJson(ext::userData.get(\"businessData\"));" + left
                + " == " + right;
        AST ast = newParserBuilder.parseString("", code);
        ASTEnv env = new ASTEnv(ast);
        Object execute = env.execute();
        assertEquals(expected, execute);

        code = "let businessDataJson = ext::util.stringToJson(ext::userData.get(\"businessData\"));" + left
                + " != " + right;
        ast = newParserBuilder.parseString("", code);
        env = new ASTEnv(ast);
        execute = env.execute();
        assertEquals(!expected, execute);
    }

    @Test
    void test_string_methods_from_json() throws OhPanic {
        Map<String, String> map = new HashMap<>();
        map.put("testString", "testString");
        this.parserBuilder.addExternalOh("context", JSONObject.toJSONString(map));

        assertExecution(true, "json.get(\"testString\").contains(\"stStr\")");
        assertExecution(false, "json.get(\"testString\").contains(\"gni\")");
        assertExecution(10, "json.get(\"testString\").len()");
        assertExecution(true, "json.get(\"testString\").starts_with(\"testS\")");
        assertExecution(false, "json.get(\"testString\").starts_with(\"estSt\")");
        assertExecution(true, "json.get(\"testString\").ends_with(\"tring\")");
        assertExecution(false, "json.get(\"testString\").ends_with(\"Strin\")");
    }

    @Test
    void test_array_methods_from_json() throws OhPanic {
        Map<String, String> map = new HashMap<>();
        map.put("testString", "testString");
        this.parserBuilder.addExternalOh("context", JSONObject.toJSONString(map));

        assertExecution(true, "json.get(\"testString\").contains(\"stStr\")");
        assertExecution(false, "json.get(\"testString\").contains(\"gni\")");
        assertExecution(10, "json.get(\"testString\").len()");
        assertExecution(true, "json.get(\"testString\").starts_with(\"testS\")");
        assertExecution(false, "json.get(\"testString\").starts_with(\"estSt\")");
        assertExecution(true, "json.get(\"testString\").ends_with(\"tring\")");
        assertExecution(false, "json.get(\"testString\").ends_with(\"Strin\")");
    }

    private void assertExecution(Object expected, String codeSuffix) throws OhPanic {
        String code = "let json = ext::util.stringToJson(context); " + codeSuffix;
        AST ast = this.parserBuilder.parseString("", code);
        ASTEnv env = new ASTEnv(ast);
        assertEquals(expected, env.execute());
    }
}