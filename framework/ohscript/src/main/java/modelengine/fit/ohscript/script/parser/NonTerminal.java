/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser;

import modelengine.fit.ohscript.script.parser.nodes.ArgumentNode;
import modelengine.fit.ohscript.script.parser.nodes.ArgumentsNode;
import modelengine.fit.ohscript.script.parser.nodes.ArrayAccessNode;
import modelengine.fit.ohscript.script.parser.nodes.ArrayDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.ArrayOrMapDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.AsyncBlockNode;
import modelengine.fit.ohscript.script.parser.nodes.BlockNode;
import modelengine.fit.ohscript.script.parser.nodes.CommentsNode;
import modelengine.fit.ohscript.script.parser.nodes.DoNode;
import modelengine.fit.ohscript.script.parser.nodes.EachNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityBodyNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityCallNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityExtensionNode;
import modelengine.fit.ohscript.script.parser.nodes.ExprStatementNode;
import modelengine.fit.ohscript.script.parser.nodes.ExpressBlockNode;
import modelengine.fit.ohscript.script.parser.nodes.ForNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionCallNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.GeneralNode;
import modelengine.fit.ohscript.script.parser.nodes.IfNode;
import modelengine.fit.ohscript.script.parser.nodes.IgnoredNode;
import modelengine.fit.ohscript.script.parser.nodes.ImportNode;
import modelengine.fit.ohscript.script.parser.nodes.InitialAssignmentNode;
import modelengine.fit.ohscript.script.parser.nodes.JavaNewNode;
import modelengine.fit.ohscript.script.parser.nodes.JavaStaticCallNode;
import modelengine.fit.ohscript.script.parser.nodes.JsonEntityBodyNode;
import modelengine.fit.ohscript.script.parser.nodes.JsonItemNode;
import modelengine.fit.ohscript.script.parser.nodes.LetStatementNode;
import modelengine.fit.ohscript.script.parser.nodes.LockBlockNode;
import modelengine.fit.ohscript.script.parser.nodes.LoopControlNode;
import modelengine.fit.ohscript.script.parser.nodes.MapDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.MatchStatementNode;
import modelengine.fit.ohscript.script.parser.nodes.MatchVarNode;
import modelengine.fit.ohscript.script.parser.nodes.NamespaceDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.NamespaceNode;
import modelengine.fit.ohscript.script.parser.nodes.NonTerminalNode;
import modelengine.fit.ohscript.script.parser.nodes.OhCallNode;
import modelengine.fit.ohscript.script.parser.nodes.PipeForwardNode;
import modelengine.fit.ohscript.script.parser.nodes.ReturnNode;
import modelengine.fit.ohscript.script.parser.nodes.SafeBlockNode;
import modelengine.fit.ohscript.script.parser.nodes.ScriptNode;
import modelengine.fit.ohscript.script.parser.nodes.StatementsNode;
import modelengine.fit.ohscript.script.parser.nodes.SystemExtensionNode;
import modelengine.fit.ohscript.script.parser.nodes.TernaryExpressionNode;
import modelengine.fit.ohscript.script.parser.nodes.TupleDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.TupleUnPackerNode;
import modelengine.fit.ohscript.script.parser.nodes.VarAssignmentNode;
import modelengine.fit.ohscript.script.parser.nodes.VarStatementNode;
import modelengine.fit.ohscript.script.parser.nodes.WhileNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.NodeType;

/**
 * 词法分析阶段的非终结符
 * all non-terminal handlers
 * script is the start symbol, labeled by SCRIPT("start")
 *
 * @author 张群辉
 * @since 2023-05-01
 */
public enum NonTerminal implements NodeType {
    /**
     * 表示整个脚本的入口非终结符。
     * <p>用于解析脚本的根节点，包含所有顶层声明和语句。</p>
     */
    SCRIPT(true, true) {
        @Override
        public NonTerminalNode parse() {
            return new ScriptNode();
        }
    },

    /**
     * 表示命名空间声明。
     * <p>用于解析类似 {@code namespace com.example;} 的语法结构。</p>
     */
    NAMESPACE_DECLARE {
        @Override
        public NonTerminalNode parse() {
            return new NamespaceDeclareNode();
        }
    },

    /**
     * 表示导入声明的集合。
     * <p>用于解析多个连续的导入声明语句。</p>
     */
    IMPORT_DECLARES,

    /**
     * 表示单个导入声明。
     * <p>用于解析类似 {@code import com.example.*;} 的语法结构。</p>
     */
    IMPORT_DECLARE {
        @Override
        public NonTerminalNode parse() {
            return new ImportNode();
        }
    },

    /**
     * 表示导出声明。
     * <p>用于解析模块导出语句（保留语法结构）。</p>
     */
    EXPORT_DECLARE,

    /**
     * 表示注释语句。
     * <p>用于解析代码中的单行或多行注释。</p>
     */
    COMMENT_STATEMENT {
        @Override
        public NonTerminalNode parse() {
            return new CommentsNode();
        }
    },

    /**
     * 表示语句集合。
     * <p>用于解析包含多个语句的代码块。</p>
     */
    STATEMENTS {
        @Override
        public NonTerminalNode parse() {
            return new StatementsNode();
        }
    },

    /**
     * 表示单个语句。
     * <p>用于解析各种类型的独立语句。</p>
     */
    STATEMENT,

    /**
     * 表示变量声明语句。
     * <p>用于解析类似 {@code var x = 10;} 的语法结构。</p>
     */
    VAR_STATEMENT {
        @Override
        public NonTerminalNode parse() {
            return new VarStatementNode();
        }
    },

    /**
     * 表示不可变变量声明语句。
     * <p>用于解析类似 {@code let x = 10;} 的语法结构。</p>
     */
    LET_STATEMENT {
        @Override
        public NonTerminalNode parse() {
            return new LetStatementNode();
        }
    },

    /**
     * 表示赋值语句。
     * <p>用于解析变量赋值操作。</p>
     */
    ASSIGNMENT_STATEMENT,

    /**
     * 表示表达式语句。
     * <p>用于解析独立的表达式语句。</p>
     */
    EXPRESSION_STATEMENT {
        @Override
        public NonTerminalNode parse() {
            return new ExprStatementNode();
        }
    },

    /**
     * 表示变量赋值操作。
     * <p>用于解析类似 {@code x = 10;} 的赋值语法。</p>
     */
    VAR_ASSIGNMENT {
        @Override
        public NonTerminalNode parse() {
            return new VarAssignmentNode();
        }
    },

    /**
     * 表示初始化赋值操作。
     * <p>用于解析变量声明时的初始化赋值。</p>
     */
    INITIAL_ASSIGNMENT {
        @Override
        public NonTerminalNode parse() {
            return new InitialAssignmentNode();
        }
    },

    /**
     * 表示命名空间引用。
     * <p>用于解析命名空间的使用和引用。</p>
     */
    NAMESPACE {
        @Override
        public NonTerminalNode parse() {
            return new NamespaceNode();
        }
    },

    /**
     * 表示返回语句。
     * <p>用于解析 {@code return} 关键字及其返回值。</p>
     */
    RETURN_STATEMENT {
        @Override
        public NonTerminalNode parse() {
            return new ReturnNode();
        }
    },

    /**
     * class animal(x,y,z){
     * .age =10;
     * .do(x,y){}
     * }
     * <p>
     * class human(x,y){
     * :animal(x,y,10),creature(y);
     * .run(x,y){
     * }
     * }
     */
    CLASS_DECLARE,

    /**
     * 表示函数声明。
     * <p>用于解析函数定义语句，包括函数名、参数列表和函数体。</p>
     * <p>示例: function add(a, b) { return a + b; }</p>
     * <p>参数说明:</p>
     * <ul>
     *   <li>false - 非开始符号</li>
     *   <li>true - 拥有独立作用域</li>
     * </ul>
     */
    FUNC_DECLARE(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new FunctionDeclareNode();
        }
    },

    /**
     * 表示函数或方法的参数列表。
     * <p>用于解析函数声明或函数调用中的参数集合。</p>
     */
    ARGUMENTS {
        @Override
        public NonTerminalNode parse() {
            return new ArgumentsNode();
        }
    },

    /**
     * 表示函数或方法的单个参数。
     * <p>用于解析函数声明或函数调用中的单个参数。</p>
     */
    ARGUMENT {
        @Override
        public NonTerminalNode parse() {
            return new ArgumentNode();
        }
    },

    /**
     * 表示管道转发表达式。
     * <p>用于解析类似 {@code x |> func()} 的管道操作语法。</p>
     */
    PIPE_FORWARD {
        @Override
        public NonTerminalNode parse() {
            return new PipeForwardNode();
        }
    },

    /**
     * 表示函数调用表达式。
     * <p>用于解析普通函数调用语法。</p>
     */
    FUNC_CALL {
        @Override
        public NonTerminalNode parse() {
            return new FunctionCallNode();
        }
    },

    /**
     * 表示Java静态方法调用表达式。
     * <p>用于解析Java静态方法的调用语法。</p>
     */
    IF_STATEMENT(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new IfNode();
        }
    },

    /**
     * 表示条件分支语句。
     */
    IF_BRANCH(false, true),

    /**
     * 表示条件表达式。
     * <p>用于解析布尔逻辑判断表达式。</p>
     */
    CONDITION_EXPRESSION,

    /**
     * 表示else语句块。
     * <p>用于解析if语句中的else分支。</p>
     * <p>示例: if(condition){} else {}</p>
     */
    ELSE_STATEMENT(false, true),

    /**
     * 表示代码块语句。
     * <p>用于解析由大括号包围的代码块，可以包含多个语句。</p>
     * <p>示例: { statement1; statement2; }</p>
     */
    BLOCK_STATEMENT(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new BlockNode();
        }
    },

    /**
     * 表示表达式块语句。
     * <p>用于解析可以作为表达式使用的代码块，通常返回最后一个表达式的值。</p>
     * <p>示例: { let x = 1; x + 2 }</p>
     */
    EXPRESS_BLOCK_STATEMENT(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new ExpressBlockNode();
        }
    },

    /**
     * 表示锁同步代码块。
     */
    LOCK_BLOCK(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new LockBlockNode();
        }
    },

    /**
     * 表示异步执行代码块。
     */
    ASYNC_BLOCK(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new AsyncBlockNode();
        }
    },

    /**
     * 表示安全执行代码块。
     */
    SAFE_BLOCK(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new SafeBlockNode();
        }
    },

    /**
     * 表示do-while循环语句。
     */
    DO_STATEMENT(false, true, true) {
        @Override
        public NonTerminalNode parse() {
            return new DoNode();
        }
    },

    /**
     * 表示while循环语句。
     * <p>用于解析while循环结构，包含循环条件和循环体。</p>
     */
    WHILE_STATEMENT(false, true, true) {
        @Override
        public NonTerminalNode parse() {
            return new WhileNode();
        }
    },

    /**
     * 表示each迭代语句。
     * <p>用于解析集合遍历的循环结构。</p>
     */
    EACH_STATEMENT(false, true, true) {
        @Override
        public NonTerminalNode parse() {
            return new EachNode();
        }
    },

    /**
     * 表示for循环语句。
     * <p>用于解析标准for循环结构，包含初始化、条件和迭代部分。</p>
     */
    FOR_STATEMENT(false, true, true) {
        @Override
        public NonTerminalNode parse() {
            return new ForNode();
        }
    },

    /**
     * 表示循环控制语句。
     * <p>用于解析break和continue等循环控制语句。</p>
     */
    LOOP_CONTROL() {
        @Override
        public NonTerminalNode parse() {
            return new LoopControlNode();
        }
    },

    /**
     * 表示Lambda表达式。
     * <p>用于解析匿名函数表达式。</p>
     */
    LAMBDA_EXPRESSION,

    /**
     * 表示关系条件表达式。
     * <p>用于解析比较运算表达式。</p>
     */
    RELATIONAL_CONDITION,

    /**
     * 表示否定表达式。
     * <p>用于解析逻辑非运算。</p>
     */
    NEGATION,

    /**
     * 表示while语句的状态。
     * <p>用于跟踪while循环的执行状态。</p>
     */
    WHILE_STATE,

    /**
     * 表示通用表达式。
     * <p>用于解析各类表达式。</p>
     */
    EXPRESSION,

    /**
     * 表示三元条件表达式。
     * <p>用于解析形如 condition ? expr1 : expr2 的表达式。</p>
     */
    TERNARY_EXPRESSION {
        @Override
        public NonTerminalNode parse() {
            return new TernaryExpressionNode();
        }
    },

    /**
     * 表示数值表达式。
     * <p>用于解析数学计算表达式。</p>
     */
    NUMERIC_EXPRESSION,

    /**
     * 表示项表达式。
     * <p>用于解析乘除等运算的表达式。</p>
     */
    TERM_EXPRESSION,

    /**
     * 表示一元表达式。
     * <p>用于解析正负号等一元运算符。</p>
     */
    UNARY_EXPRESSION,

    /**
     * 表示因子表达式。
     * <p>用于解析基本运算单元。</p>
     */
    FACTOR_EXPRESSION {
    },

    /**
     * 表示元组声明。
     * <p>用于解析元组数据结构的声明。</p>
     */
    TUPLE_DECLARE(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new TupleDeclareNode();
        }
    },

    /**
     * 表示被忽略的语法元素。
     * <p>用于处理语法分析中需要忽略的部分。</p>
     */
    IGNORED {
        @Override
        public NonTerminalNode parse() {
            return new IgnoredNode();
        }
    },

    /**
     * 表示实体调用。
     * <p>用于解析对实体对象的方法调用。</p>
     */
    ENTITY_CALL(false, false) {
        @Override
        public NonTerminalNode parse() {
            return new EntityCallNode();
        }
    },

    /**
     * 表示实体声明。
     * <p>用于解析实体类型的定义。</p>
     */
    ENTITY_DECLARE(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new EntityDeclareNode();
        }
    },

    /**
     * 表示实体主体。
     * <p>用于解析实体定义的内容部分。</p>
     */
    ENTITY_BODY(false, false) {
        @Override
        public NonTerminalNode parse() {
            return new EntityBodyNode();
        }
    },

    /**
     * 表示JSON格式的实体主体。
     * <p>用于解析JSON格式的实体定义。</p>
     */
    JSON_ENTITY_BODY(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new JsonEntityBodyNode();
        }
    },

    /**
     * 表示JSON项。
     * <p>用于解析JSON对象中的键值对。</p>
     */
    JSON_ITEM(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new JsonItemNode();
        }
    },

    /**
     * 表示Java对象实例化。
     * <p>用于解析Java new关键字创建对象。</p>
     */
    JAVA_NEW(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new JavaNewNode();
        }
    },

    /**
     * 表示Java静态方法调用。
     * <p>用于解析对Java类的静态方法调用。</p>
     */
    JAVA_STATIC_CALL(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new JavaStaticCallNode();
        }
    },

    /**
     * 表示数组访问。
     * <p>用于解析数组索引访问操作。</p>
     */
    ARRAY_ACCESS(false, false) {
        @Override
        public NonTerminalNode parse() {
            return new ArrayAccessNode();
        }
    },

    /**
     * 表示数组或映射声明。
     * <p>用于解析数组或映射类型的声明。</p>
     */
    ARRAY_MAP_DECLARE(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new ArrayOrMapDeclareNode();
        }
    },

    /**
     * 表示数组声明。
     * <p>用于解析数组类型的声明。</p>
     */
    ARRAY_DECLARE(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new ArrayDeclareNode();
        }
    },

    /**
     * 表示映射声明。
     * <p>用于解析映射类型的声明。</p>
     */
    MAP_DECLARE(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new MapDeclareNode();
        }
    },

    /**
     * 表示元组解包器。
     * <p>用于解析元组解构赋值操作。</p>
     */
    TUPLE_UNPACKER(false, false, false) {
        @Override
        public NonTerminalNode parse() {
            return new TupleUnPackerNode();
        }
    },

    /**
     * 表示OH调用。
     * <p>用于解析OH特定的函数调用。</p>
     */
    OH_CALL(false, false, false) {
        @Override
        public NonTerminalNode parse() {
            return new OhCallNode();
        }
    },

    /**
     * 表示匹配语句。
     * <p>用于解析模式匹配结构。</p>
     */
    MATCH_STATEMENT(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new MatchStatementNode();
        }
    },

    /**
     * 表示匹配分支。
     * <p>用于解析匹配语句中的单个分支。</p>
     */
    MATCH_BRANCH,

    /**
     * 表示匹配变量。
     * <p>用于解析匹配语句中的变量声明。</p>
     */
    MATCH_VAR {
        @Override
        public NonTerminalNode parse() {
            return new MatchVarNode();
        }
    },

    /**
     * 表示系统方法。
     * <p>用于解析系统内置方法调用。</p>
     */
    SYS_METHOD,

    /**
     * 表示匹配条件。
     * <p>用于解析匹配语句中的when条件。</p>
     */
    MATCH_WHEN,

    /**
     * 表示匹配块。
     * <p>用于解析匹配语句中的代码块。</p>
     */
    MATCH_BLOCK,

    /**
     * 表示匹配else分支。
     * <p>用于解析匹配语句中的默认分支。</p>
     */
    MATCH_ELSE_BRANCH {
    },

    /**
     * 表示系统扩展。
     * <p>用于解析系统级别的扩展功能。</p>
     */
    SYSTEM_EXTENSION(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new SystemExtensionNode();
        }
    },

    /**
     * 表示实体扩展。
     * <p>用于解析实体类型的扩展功能。</p>
     */
    ENTITY_EXTENSION(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new EntityExtensionNode();
        }
    },

    /**
     * 表示外部数据。
     * <p>用于解析和处理来自外部系统或文件的数据结构。</p>
     * <p>这个非终结符用于处理与外部系统进行数据交换的场景，比如读取配置文件、解析外部API返回等。</p>
     */
    EXTERNAL_DATA;

    private final boolean isStart;

    private final boolean ownScope;

    private final boolean loopAble;

    /**
     * 构造函数
     * constructor
     * 非开始符号，拥有自己的作用域，不允许循环
     * not start symbol, own scope, not loop able
     *
     * @param isStart 是否为开始符号
     * is start symbol
     * @param ownScope 是否拥有自己的作用域
     * own scope
     * @param loopAble 是否允许循环
     * loop able
     */
    NonTerminal(boolean isStart, boolean ownScope, boolean loopAble) {
        this.isStart = isStart;
        this.ownScope = ownScope;
        this.loopAble = loopAble;
    }

    /**
     * 构造函数
     * constructor
     * 非开始符号，拥有自己的作用域，不允许循环
     * not start symbol, own scope, not loop able
     *
     * @param isStart 是否为开始符号
     * is start symbol
     * @param ownScope 是否拥有自己的作用域
     * own scope
     */
    NonTerminal(boolean isStart, boolean ownScope) {
        this(isStart, ownScope, false);
    }

    /**
     * 默认构造函数
     * default constructor
     * 默认非开始符号，不拥有自己的作用域，不允许循环
     * default is not start symbol, not own scope, not loop able
     */
    NonTerminal() {
        this(false, false);
    }

    /**
     * 根据名称获取非终结符枚举
     *
     * @param name 非终结符名称
     * @return 非终结符枚举
     */
    public static NonTerminal valueFrom(String name) {
        if (name.endsWith("'")) {
            return NonTerminal.IGNORED;
        } else {
            return NonTerminal.valueOf(name);
        }
    }

    /**
     * 判断是否允许循环
     *
     * @return 是否允许循环
     */
    public boolean loopAble() {
        return this.loopAble;
    }

    /**
     * 判断是否为列表
     * is list to help grammar parser avoid FIRST-FIRST conflict and left recursion
     *
     * @return is list
     */
    public boolean isList() {
        return this.name().indexOf("_LIST") == (this.name().length() - 5) && this.name().length() > 5;
    }

    /**
     * 获取节点类型
     * get node type
     *
     * @return 节点类型
     */
    @Override
    public NonTerminalNode parse() {
        return new GeneralNode(this);
    }

    /**
     * 判断是否为开始符号
     *
     * @return 是否为开始符号
     */
    public boolean isStart() {
        return this.isStart;
    }

    /**
     * 判断是否拥有自己的作用域
     *
     * @return 是否拥有自己的作用域
     */
    public boolean ownScope() {
        return this.ownScope;
    }
}
