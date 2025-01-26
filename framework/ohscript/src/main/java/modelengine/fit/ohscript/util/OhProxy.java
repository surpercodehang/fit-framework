/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.util;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.errors.ScriptExecutionException;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.ActivationContext;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;
import modelengine.fit.ohscript.script.parser.nodes.EntityCallNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionCallNode;
import modelengine.fit.ohscript.script.parser.nodes.MemberNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * oh对象的代理
 *
 * @since 1.0
 */
public class OhProxy {
    private static final Logger LOG = Logger.get(OhProxy.class);

    private final Map<String, ReturnValue> value;

    private final Object base;

    private final ASTEnv env;

    public OhProxy(Map<String, ReturnValue> value, Object base, ASTEnv env) {
        this.value = value;
        this.base = base;
        this.env = env;
    }

    /**
     * 代理方法调用
     *
     * @param callable 可调用对象
     * @param method 方法
     * @param args 参数
     * @return 返回值
     * @throws RuntimeException 运行时异常
     * @throws OhPanic ohscript异常
     */
    public Object invoke(Callable<?> callable, Method method, Object[] args) throws RuntimeException, OhPanic {
        // 如果该方法没有覆盖到，则返回基对象方法
        if (!value.containsKey(Constants.DOT + method.getName())) {
            return this.noMethodInvoke(callable, method, args);
        }
        // 创建函数参数
        Map<SyntaxNode, Object> mockArgs = new HashMap<>();
        if (args == null || args.length == 0) {
            mockArgs.put(TerminalNode.mockId("arg_0"), null);
        } else {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                mockArgs.put(TerminalNode.mockId("arg_" + i), arg);
            }
        }
        // 创建对象调用
        TerminalNode entity = TerminalNode.mockId("entity");
        EntityCallNode eCall = EntityCallNode.mock(entity, new MemberNode(TerminalNode.mockId(method.getName())));
        FunctionCallNode fCall = FunctionCallNode.mock(eCall, mockArgs.keySet());
        fCall.setAst(this.env.ast());

        ActivationContext current = this.buildActivationContext(entity, mockArgs);
        ReturnValue result = fCall.interpret(env, current);
        return result.value();
    }

    private ActivationContext buildActivationContext(TerminalNode entity, Map<SyntaxNode, Object> mockArgs)
            throws OhPanic {
        ActivationContext current = env.push(0, env.rootContext());
        current.put(entity, new ReturnValue(current, TypeExprFactory.createExternal(entity), value));
        for (Map.Entry<SyntaxNode, Object> entry : mockArgs.entrySet()) {
            SyntaxNode node = entry.getKey();
            Object entityValue = entry.getValue();
            TypeExpr type;
            if (entityValue == null) {
                type = TypeExprFactory.createUnit();
            } else if (entityValue instanceof Number) {
                type = TypeExprFactory.createNumber(node);
            } else if (entityValue instanceof String) {
                type = TypeExprFactory.createString(node);
            } else {
                type = TypeExprFactory.createExternal(node);
            }
            current.put(ObjectUtils.cast(node), new ReturnValue(current, type, entityValue));
        }
        return current;
    }

    private Object noMethodInvoke(Callable<?> callable, Method method, Object[] args) {
        try {
            if (base == null) {
                // 没有基对象，则看当前自动代理是否是super方法
                if (callable == null) {
                    // 没有super方法，则抛出异常
                    throw new UnsupportedOperationException("No implementation for method: " + method.getName());
                } else {
                    // 有super方法，执行super方法
                    return callable.call();
                }
            } else {
                // 执行被代理对象方法
                method.setAccessible(true);
                return method.invoke(base, args);
            }
        } catch (Exception e) {
            LOG.error("noMethodInvoke error. method:{0}", method.getName(), e);
            throw new ScriptExecutionException(e.getMessage());
        }
    }
}
