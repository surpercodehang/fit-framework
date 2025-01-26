/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import lombok.Setter;
import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.ActivationContext;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.DoubleFunctionTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.FunctionTypeExpr;
import modelengine.fit.ohscript.util.ExternalWrapper;
import modelengine.fit.ohscript.util.OhFunction;
import modelengine.fit.ohscript.util.Pair;
import modelengine.fit.ohscript.util.Tool;
import modelengine.fit.ohscript.util.Triple;
import modelengine.fitframework.util.ObjectUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * a double function declaration
 * 将系统方法（例如对象的方法）封装成一个双重函数调用
 * this function create a double function to invoke system method
 * like 4.toString()
 * this toString is a system method will be wrapped in a double function declaration toString()
 *
 * @since 1.0
 */
public class DoubleFunctionDeclareNode<V, T, R> extends FunctionDeclareNode {
    private static Map<String, Triple<OhFunction, Object, Function<FunctionCallNode, FunctionTypeExpr>>> transients
            = new HashMap<>();

    private final String serializedId = UUID.randomUUID().toString();

    private final BlockNode body;

    private final ArgumentNode argument;

    private transient OhFunction<V, R> handler;

    @Setter
    private transient Object hostValue;

    @Setter
    private transient Function<FunctionCallNode, FunctionTypeExpr> projectFunction = null;

    /**
     * 构造一个方法声明节点，方法有多个参数的情况，使用curry化构造
     *
     * @param name 函数名称
     * @param argumentNum 参数个数
     * @param handler 实际的方法执行
     * @param returnType 返回值类型
     */
    public DoubleFunctionDeclareNode(String name, int argumentNum, OhFunction handler, TypeExpr returnType) {
        super();
        this.addChild(new TerminalNode(Terminal.FUNC));
        this.funcName = new TerminalNode(Terminal.ID);
        this.funcName.setToken(new Token(Terminal.ID, name, -1, -1, -1));
        this.addChild(this.funcName);
        this.addChild(new TerminalNode(Terminal.LEFT_PAREN));
        NonTerminalNode args = new ArgumentsNode();
        this.addChild(args);
        this.argument = new ArgumentNode();
        TerminalNode arg = new TerminalNode(Terminal.ID);
        arg.setToken(new Token(Terminal.ID, String.valueOf(Tool.newId()), -1, -1, -1));
        this.argument.addChild(arg);
        args.addChild(this.argument);
        this.addChild(new TerminalNode(Terminal.RIGHT_PAREN));

        this.body = new BlockNode();
        this.body.addChild(new TerminalNode(Terminal.LEFT_BRACE));
        this.addChild(this.body);
        ReturnNode rNode = new ReturnNode();
        this.body.addChild(rNode);
        this.body.addChild(new TerminalNode(Terminal.RIGHT_BRACE));
        DoubleFunctionDeclareNode me = this;
        TypeExpr argumentType = argumentNum > 0
                ? TypeExprFactory.createGeneric(this.argument)
                : TypeExprFactory.createUnit();
        this.typeExpr = new DoubleFunctionTypeExpr(this, argumentType, returnType) {
            @Override
            public FunctionTypeExpr project(FunctionCallNode function) {
                if (me.projectFunction == null) {
                    return this;
                } else {
                    return ObjectUtils.cast(me.projectFunction.apply(function));
                }
            }
        };
        int nextArgumentNum = argumentNum;
        nextArgumentNum--;
        if (nextArgumentNum > 0) {
            DoubleFunctionDeclareNode nest = new DoubleFunctionDeclareNode(String.valueOf(Tool.newId()),
                    nextArgumentNum, handler, returnType);
            rNode.addChild(nest);
        } else {
            NonTerminalNode handlerNode = new HandlerNode(NonTerminal.BLOCK_STATEMENT, returnType, this);
            rNode.addChild(handlerNode);
            this.handler = handler;
        }
    }

    private static List<TypeExpr> createArguments(TypeExpr argumentType) {
        List<TypeExpr> types = new ArrayList<>();
        types.add(argumentType);
        return types;
    }

    private static void cacheTransient(String serializedId, OhFunction handler, Object hostValue,
            Function<FunctionCallNode, FunctionTypeExpr> projectFunction) {
        transients.put(serializedId, new Triple<>(handler, hostValue, projectFunction));
    }

    private static Triple<OhFunction, Object, Function<FunctionCallNode, FunctionTypeExpr>> getTransient(
            String serializedId) {
        return DoubleFunctionDeclareNode.transients.get(serializedId);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        DoubleFunctionDeclareNode.cacheTransient(this.serializedId, this.handler, this.hostValue, this.projectFunction);
    }

    // 自定义反序列化方法
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Triple<OhFunction, Object, Function<FunctionCallNode, FunctionTypeExpr>> transientsTriple
                = DoubleFunctionDeclareNode.getTransient(this.serializedId);
        this.handler = transientsTriple.first();
        this.hostValue = transientsTriple.second();
        this.projectFunction = transientsTriple.third();
    }

    private static class HandlerNode extends NonTerminalNode {
        private final TypeExpr returnType;

        private final DoubleFunctionDeclareNode function;

        /**
         * 构造一个处理节点，这个节点将被添加到函数声明的主体中
         * 这个节点将执行实际的方法调用
         *
         * @param nodeType 节点类型
         * @param returnType 返回值类型
         * @param function 双重函数声明节点
         */
        protected HandlerNode(NonTerminal nodeType, TypeExpr returnType, DoubleFunctionDeclareNode function) {
            super(nodeType);
            this.returnType = returnType;
            this.function = function;
        }

        @Override
        public TypeExpr typeExpr() {
            return this.returnType; // 注释：((FunctionTypeExpr) function.typeExpr().realTypeExpr()).returnType();
        }

        @Override
        public ReturnValue interpret(ASTEnv env, ActivationContext current) throws RuntimeException, OhPanic {
            List<ReturnValue> args = new ArrayList<>();
            DoubleFunctionDeclareNode funcTop = null;
            SyntaxNode func = this.function;
            while (!(func instanceof ScriptNode)) {
                if (func instanceof DoubleFunctionDeclareNode) {
                    funcTop = (DoubleFunctionDeclareNode) func;
                    args.add(0, current.get(funcTop.argument.argument()));
                }
                // this is for external call,
                // if external call return is an object, simply mock a function but without parent
                if (func.parent() == null) {
                    break;
                }
                func = func.parent();
            }
            Object tmpHostValue = funcTop.hostValue;
            if (funcTop.hostValue instanceof Pair
                    && ((Pair<?, ?>) funcTop.hostValue).first() instanceof ExternalWrapper) {
                tmpHostValue = new Pair<>(((ExternalWrapper) ((Pair<?, ?>) funcTop.hostValue).first()).object(env),
                        ((Pair<?, ?>) funcTop.hostValue).second());
            }
            Object value = function.handler.apply(tmpHostValue, args, env, current);
            if (value instanceof ReturnValue) {
                return (ReturnValue) value;
            } else {
                TypeExpr expr = this.typeExpr();
                if (expr == TypeExprFactory.createUnknown()) {
                    expr = TypeExprFactory.createExternal(this.ast().start());
                    if (value instanceof String) {
                        expr = TypeExprFactory.createString(this.ast().start());
                    }
                    if (value instanceof Number) {
                        expr = TypeExprFactory.createNumber(this.ast().start());
                    }
                }
                return new ReturnValue(current, expr, value);
            }
        }

        private Object getHostValue(ASTEnv env, DoubleFunctionDeclareNode funcTop) {
            Object tmpHostValue = funcTop.hostValue;
            if (funcTop.hostValue instanceof Pair) {
                Object first = ((Pair<?, ?>) funcTop.hostValue).first();
                if (first instanceof ExternalWrapper) {
                    Object object = ((ExternalWrapper) first).object(env);
                    Object method = ((Pair<?, ?>) funcTop.hostValue).second();
                    tmpHostValue = new Pair<>(object, method);
                }
            }
            return tmpHostValue;
        }
    }
}