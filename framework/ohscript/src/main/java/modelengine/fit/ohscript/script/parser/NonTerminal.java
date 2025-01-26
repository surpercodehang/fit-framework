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
    SCRIPT(true, true) {
        @Override
        public NonTerminalNode parse() {
            return new ScriptNode();
        }
    },
    NAMESPACE_DECLARE {
        @Override
        public NonTerminalNode parse() {
            return new NamespaceDeclareNode();
        }
    },
    IMPORT_DECLARES,
    IMPORT_DECLARE {
        @Override
        public NonTerminalNode parse() {
            return new ImportNode();
        }
    },
    EXPORT_DECLARE,
    COMMENT_STATEMENT {
        @Override
        public NonTerminalNode parse() {
            return new CommentsNode();
        }
    },
    STATEMENTS {
        @Override
        public NonTerminalNode parse() {
            return new StatementsNode();
        }
    },
    STATEMENT,
    VAR_STATEMENT {
        @Override
        public NonTerminalNode parse() {
            return new VarStatementNode();
        }
    },
    LET_STATEMENT {
        @Override
        public NonTerminalNode parse() {
            return new LetStatementNode();
        }
    },
    ASSIGNMENT_STATEMENT,
    EXPRESSION_STATEMENT {
        @Override
        public NonTerminalNode parse() {
            return new ExprStatementNode();
        }
    },
    VAR_ASSIGNMENT {
        @Override
        public NonTerminalNode parse() {
            return new VarAssignmentNode();
        }
    },
    INITIAL_ASSIGNMENT {
        @Override
        public NonTerminalNode parse() {
            return new InitialAssignmentNode();
        }
    },
    NAMESPACE {
        @Override
        public NonTerminalNode parse() {
            return new NamespaceNode();
        }
    },
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
    FUNC_DECLARE(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new FunctionDeclareNode();
        }
    },
    ARGUMENTS {
        @Override
        public NonTerminalNode parse() {
            return new ArgumentsNode();
        }
    },
    ARGUMENT {
        @Override
        public NonTerminalNode parse() {
            return new ArgumentNode();
        }
    },
    PIPE_FORWARD {
        @Override
        public NonTerminalNode parse() {
            return new PipeForwardNode();
        }
    },
    FUNC_CALL {
        @Override
        public NonTerminalNode parse() {
            return new FunctionCallNode();
        }
    },
    IF_STATEMENT(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new IfNode();
        }
    },
    IF_BRANCH(false, true),
    CONDITION_EXPRESSION,
    ELSE_STATEMENT(false, true),
    BLOCK_STATEMENT(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new BlockNode();
        }
    },
    EXPRESS_BLOCK_STATEMENT(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new ExpressBlockNode();
        }
    },
    LOCK_BLOCK(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new LockBlockNode();
        }
    },
    ASYNC_BLOCK(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new AsyncBlockNode();
        }
    },
    SAFE_BLOCK(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new SafeBlockNode();
        }
    },
    DO_STATEMENT(false, true, true) {
        @Override
        public NonTerminalNode parse() {
            return new DoNode();
        }
    },
    WHILE_STATEMENT(false, true, true) {
        @Override
        public NonTerminalNode parse() {
            return new WhileNode();
        }
    },
    EACH_STATEMENT(false, true, true) {
        @Override
        public NonTerminalNode parse() {
            return new EachNode();
        }
    },
    FOR_STATEMENT(false, true, true) {
        @Override
        public NonTerminalNode parse() {
            return new ForNode();
        }
    },
    LOOP_CONTROL() {
        @Override
        public NonTerminalNode parse() {
            return new LoopControlNode();
        }
    },
    LAMBDA_EXPRESSION,
    RELATIONAL_CONDITION,
    NEGATION,
    WHILE_STATE,
    EXPRESSION,
    TERNARY_EXPRESSION {
        @Override
        public NonTerminalNode parse() {
            return new TernaryExpressionNode();
        }
    },
    NUMERIC_EXPRESSION,
    TERM_EXPRESSION,
    UNARY_EXPRESSION,
    FACTOR_EXPRESSION {
    },
    TUPLE_DECLARE(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new TupleDeclareNode();
        }
    },
    IGNORED {
        @Override
        public NonTerminalNode parse() {
            return new IgnoredNode();
        }
    },
    ENTITY_CALL(false, false) {
        @Override
        public NonTerminalNode parse() {
            return new EntityCallNode();
        }
    },
    ENTITY_DECLARE(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new EntityDeclareNode();
        }
    },
    ENTITY_BODY(false, false) {
        @Override
        public NonTerminalNode parse() {
            return new EntityBodyNode();
        }
    },
    JSON_ENTITY_BODY(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new JsonEntityBodyNode();
        }
    },
    JSON_ITEM(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new JsonItemNode();
        }
    },
    JAVA_NEW(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new JavaNewNode();
        }
    },
    JAVA_STATIC_CALL(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new JavaStaticCallNode();
        }
    },
    ARRAY_ACCESS(false, false) {
        @Override
        public NonTerminalNode parse() {
            return new ArrayAccessNode();
        }
    },
    ARRAY_MAP_DECLARE(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new ArrayOrMapDeclareNode();
        }
    },
    ARRAY_DECLARE(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new ArrayDeclareNode();
        }
    },
    MAP_DECLARE(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new MapDeclareNode();
        }
    },
    TUPLE_UNPACKER(false, false, false) {
        @Override
        public NonTerminalNode parse() {
            return new TupleUnPackerNode();
        }
    },
    OH_CALL(false, false, false) {
        @Override
        public NonTerminalNode parse() {
            return new OhCallNode();
        }
    },
    MATCH_STATEMENT(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new MatchStatementNode();
        }
    },
    MATCH_BRANCH,
    MATCH_VAR {
        @Override
        public NonTerminalNode parse() {
            return new MatchVarNode();
        }
    },
    SYS_METHOD,
    MATCH_WHEN,
    MATCH_BLOCK,
    MATCH_ELSE_BRANCH {
    },
    SYSTEM_EXTENSION(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new SystemExtensionNode();
        }
    },
    ENTITY_EXTENSION(false, true) {
        @Override
        public NonTerminalNode parse() {
            return new EntityExtensionNode();
        }
    },
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
