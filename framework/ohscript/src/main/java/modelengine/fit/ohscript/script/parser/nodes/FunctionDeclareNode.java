/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.FunctionTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.UnknownTypeExpr;
import modelengine.fit.ohscript.util.Tool;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 方法声明节点
 *
 * @since 1.0
 */
public class FunctionDeclareNode extends NonTerminalNode {
    private static final String PATTERN
            = "(\\b(?<arg3>[a-z]\\w*)\\s*|\\(\\s*(\\b(?<arg2>[a-z]\\w*\\s*),\\s*)*\\b(?<arg1>[a-z]\\w*)\\s*\\)\\s*)";

    /**
     * 函数名
     */
    protected TerminalNode funcName = null;

    private FunctionDeclareNode closure;

    private Boolean isAnonymous = null;

    /**
     * 构造函数
     */
    public FunctionDeclareNode() {
        super(NonTerminal.FUNC_DECLARE);
    }

    @Override
    public void initTypeExpr(TypeExpr expr) {
        if (expr instanceof UnknownTypeExpr) {
            return;
        }
        if (typeExpr == null) { // 注释：|| expr.is(typeExpr)
            super.initTypeExpr(expr);
        } else {
            TypeExpr returnType = (ObjectUtils.<FunctionTypeExpr>cast(expr)).returnType().polish();
            (ObjectUtils.<FunctionTypeExpr>cast(this.typeExpr)).setReturnType(returnType);
        }
    }

    @Override
    public void addChild(SyntaxNode child, int index) {
        if (child instanceof NonTerminalNode) {
            child.scope = 0; // function first level child is block which doesn't have his own scope
        }
        super.addChild(child, index);
    }

    @Override
    public void optimizeAlpha() {
        super.optimizeAlpha();
        if (this.nodes.size() > 2) {
            return;
        }
        SyntaxNode head = this.nodes.get(0);
        // 分解lambda head
        if (head.nodeType() == Terminal.LAMBDA_START) {
            Pattern pattern = Pattern.compile(PATTERN);
            String[] args = head.lexeme().replaceAll("\\(|\\)|=>", "").split(",");
            this.nodes.remove(0);
            SyntaxNode body = this.nodes.remove(0);
            this.mockFunc(args);
            this.addChild(body);
        }
    }

    private void mockFunc(String[] args) {
        this.addChild(TerminalNode.mock(Terminal.FUNC));
        this.addChild(TerminalNode.mock(Terminal.LEFT_PAREN));
        ArgumentsNode argsNode = new ArgumentsNode();
        this.addChild(argsNode);
        for (int i = 0; i < args.length; i++) {
            ArgumentNode argNode = new ArgumentNode();
            argNode.addChild(TerminalNode.mockId(args[i]));
            argsNode.addChild(argNode);
            if (i < args.length - 1) {
                argsNode.addChild(new TerminalNode(Terminal.COMMA));
            }
        }
        this.addChild(TerminalNode.mock(Terminal.RIGHT_PAREN));
        this.addChild(TerminalNode.mock(Terminal.EQUAL_GREATER));
    }

    @Override
    public void optimizeBeta() {
        super.optimizeBeta();
        if (isAnonymous != null) {
            return;
        }
        this.isAnonymous = this.child(1).nodeType() == Terminal.LEFT_PAREN;
        if (this.isAnonymous) {
            if (this.child(4).nodeType() == Terminal.EQUAL_GREATER) {
                TerminalNode equalGreater = ObjectUtils.cast(this.child(4));
                BlockNode body = new BlockNode();
                this.replaceChild(equalGreater, body);
                TerminalNode leftParen = new TerminalNode(Terminal.LEFT_PAREN);
                leftParen.setToken(
                        new Token(Terminal.LEFT_PAREN, Terminal.LEFT_PAREN.tokenName(), equalGreater.token().line(),
                                equalGreater.token().start(), equalGreater.token().end()));
                TerminalNode rightParen = new TerminalNode(Terminal.RIGHT_PAREN);
                SyntaxNode lambdaBody = this.child(5);
                this.removeChild(lambdaBody);
                rightParen.setToken(new Token(Terminal.RIGHT_PAREN, Terminal.RIGHT_PAREN.tokenName(),
                        lambdaBody.location().endLine(), lambdaBody.location().endPosition() + 1,
                        lambdaBody.location().endPosition() + 2));

                ReturnNode returnNode = new ReturnNode();
                returnNode.addChild(lambdaBody);

                body.addChild(leftParen);
                body.addChild(returnNode);
                body.addChild(rightParen);
            }
        } else {
            if (this.child(1).nodeType() == Terminal.EQUAL_GREATER) {
                this.child(1).panic(SyntaxError.LAMBDA_MUST_BE_ANONYMOUS);
            }
        }
    }

    @Override
    public void optimizeGama() {
        if (this.funcName != null) {
            return;
        }
        // check anonymous function or named function
        if (this.isAnonymous) {
            Token declareToken = (ObjectUtils.<TerminalNode>cast(this.child(0))).token();
            this.funcName = new TerminalNode(Terminal.ID);
            this.funcName.setToken(
                    new Token(Terminal.ID, String.valueOf(Tool.newId()), declareToken.line(), declareToken.end(),
                            declareToken.end()));
            this.addChild(this.funcName, 1);
        } else {
            this.funcName = ObjectUtils.cast(this.child(1));
        }
        // check function arguments, function arguments is fixed in 3rd child
        NonTerminalNode arguments = ObjectUtils.cast(this.child(3));
        if (arguments.childCount() == 1) {
            ast().optimizeGama(this.body());
            return; // allow one argument;
        }
        // func keyword
        TerminalNode func = ObjectUtils.cast(this.child(0));
        // (
        TerminalNode leftParen = ObjectUtils.cast(this.child(2));
        // )
        TerminalNode rightParen = ObjectUtils.cast(this.child(4));
        // function body is a block, is fixed in 5th child
        NonTerminalNode body = ObjectUtils.cast(this.child(5));

        // create a nested function is argument number is more than one
        List<SyntaxNode> newArgs = new ArrayList<>();
        while (arguments.childCount() > 1) {
            SyntaxNode argument = arguments.child(1);
            newArgs.add(argument);
            arguments.removeChild(argument);
        }
        // create new function for the rest args
        this.createNewFunction(newArgs, func, leftParen, rightParen, body);
    }

    private void createNewFunction(List<SyntaxNode> newArgs, TerminalNode func, TerminalNode leftParen,
            TerminalNode rightParen, NonTerminalNode body) {
        if (!newArgs.isEmpty()) {
            FunctionDeclareNode newFunc = new FunctionDeclareNode();
            newFunc.addChild(func);
            newFunc.addChild(leftParen);
            NonTerminalNode args = new ArgumentsNode(); // 注释：new NonTerminalNode(NonTerminal.ARGUMENTS);
            args.refreshChildren(newArgs);
            newFunc.addChild(args);
            newFunc.addChild(rightParen);
            NonTerminalNode newBody = new BlockNode(); // 注释：new NonTerminalNode(NonTerminal.BLOCK_STATEMENT);
            newBody.addChild(new TerminalNode(Terminal.LEFT_BRACE));
            NonTerminalNode newReturn = new ReturnNode(); // 注释：new NonTerminalNode(NonTerminal.RETURN_STATEMENT);
            newReturn.addChild(new TerminalNode(Terminal.RETURN));
            newReturn.addChild(newFunc);
            newReturn.addChild(new TerminalNode(Terminal.SEMICOLON));
            newBody.addChild(newReturn);
            newBody.addChild(new TerminalNode(Terminal.RIGHT_BRACE));
            this.replaceChild(body, newBody);
            newFunc.addChild(body);
            newFunc.optimizeBeta();
            newFunc.optimizeGama();
            this.closure = newFunc;
        }
    }

    /**
     * 获取函数名
     *
     * @return 函数名
     */
    public TerminalNode functionName() {
        return ObjectUtils.cast(this.child(1));
    }

    /**
     * 获取闭包函数
     *
     * @return 闭包函数
     */
    public FunctionDeclareNode closure() {
        return this.closure;
    }

    /**
     * 获取函数参数
     *
     * @return 函数参数
     */
    public ArgumentNode argument() {
        return ObjectUtils.cast(this.child(3).child(0));
    }

    @Override
    public boolean isReturnUnit() {
        return true;
    }

    /**
     * 获取函数体
     *
     * @return 函数体
     */
    public BlockNode body() {
        return ObjectUtils.cast(this.child(5));
    }

    /**
     * 判断此方法是否为匿名函数
     *
     * @return 如果是匿名函数，返回true，否则返回false
     */
    public boolean isAnonymous() {
        return this.isAnonymous;
    }
}
