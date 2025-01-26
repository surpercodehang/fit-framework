/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.interpreter;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fit.ohscript.util.TriFunction;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统方法解释器
 *
 * @since 1.0
 */
public class SystemMethodInterpreters {
    /**
     * 系统方法的映射表
     * <p>
     * 这是一个映射表，用于存储系统方法的名称和对应的解释器
     */
    private static final Map<String, TriFunction<List<ReturnValue>, ASTEnv, ActivationContext, ReturnValue>> METHODS
            = new HashMap<>();

    static {
        METHODS.put(Constants.ARRAY_SIZE, (args, env, current) ->
                new ReturnValue(current, TypeExprFactory.createNumber(null),
                (ObjectUtils.<List>cast(current.getThis().value())).size()));
        METHODS.put(Constants.ARRAY_REMOVE, (args, env, current) ->
                new ReturnValue(current, TypeExprFactory.createGeneric(null),
                (ObjectUtils.<List>cast(current.getThis().value())).remove((int) args.get(0).value())));
        METHODS.put(Constants.ARRAY_INSERT, (args, env, current) -> {
            (ObjectUtils.<List>cast(current.getThis().value())).add(ObjectUtils.cast(args.get(0).value()), args.get(1));
            return ReturnValue.UNIT;
        });
    }

    /**
     * 解释执行系统方法
     * 这个方法用于解释系统方法，根据方法的名称和参数，调用对应的解释器，并返回解释结果
     *
     * @param method 系统方法的语法节点
     * @param env 抽象语法树环境
     * @param current 激活上下文
     * @return 返回解释结果
     * @throws OhPanic 当解释过程中出现错误时抛出
     */
    public static ReturnValue interpret(SyntaxNode method, ASTEnv env, ActivationContext current) throws OhPanic {
        String name = method.child(1).lexeme();
        List<ReturnValue> args = new ArrayList<>();
        for (int i = 2; i < method.childCount() - 1; i++) {
            args.add(method.child(i).interpret(env, current));
        }
        return METHODS.get(name).apply(args, env, current);
    }
}
