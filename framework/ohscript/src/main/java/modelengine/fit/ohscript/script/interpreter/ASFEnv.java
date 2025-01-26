/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.interpreter;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.parser.ASF;
import modelengine.fit.ohscript.script.parser.AST;
import modelengine.fit.ohscript.util.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ASF的执行环境
 *
 * @since 1.0
 */
public class ASFEnv {
    private final ASF asf;

    private final Map<String, ASTEnv> envs = new HashMap<>();

    private final Map<String, Map<String, ReturnValue>> exportValues = new HashMap<>();

    private final ActivationContext context;

    /**
     * 构造函数
     *
     * @param astEnv AST执行环境
     */
    public ASFEnv(ASTEnv astEnv) {
        this.asf = astEnv.ast().asf();
        this.envs.put(astEnv.ast().source(), astEnv);
        this.context = new ActivationContext(Constants.ROOT_SCOPE, null, astEnv);
        this.link0();
    }

    /**
     * 构造函数
     *
     * @param asf 抽象语法树
     */
    public ASFEnv(ASF asf) {
        this.asf = asf;
        this.context = new ActivationContext(Constants.ROOT_SCOPE, null, null);
        this.link0();
    }

    /**
     * 链接AST
     * 链接AST，将AST与其执行环境关联起来
     */
    public void link() {
        this.link0();
    }

    /**
     * 链接AST
     * 链接AST，将AST与其执行环境关联起来
     * 如果AST已经存在，则不进行任何操作
     */
    private void link0() {
        for (AST ast : this.asf.asts()) {
            this.envs.putIfAbsent(ast.source(), new ASTEnv(ast, this));
        }
    }

    /**
     * 执行指定的AST
     *
     * @param name AST的名称
     * @return 执行结果
     * @throws OhPanic 执行过程中可能抛出的异常
     */
    public Object execute(String name) throws OhPanic {
        return this.envs.get(name).execute();
    }

    /**
     * 执行指定的AST
     *
     * @param args AST的名称和参数
     * @return 执行结果
     * @throws OhPanic 执行过程中可能抛出的异常
     */
    public Object execute(String... args) throws OhPanic {
        List<String> all = Arrays.asList(args);
        return this.envs.get(all.remove(0)).execute(all);
    }

    /**
     * 执行指定的AST
     *
     * @param ast 抽象语法树
     * @return 执行结果
     * @throws OhPanic 执行过程中可能抛出的异常
     */
    public Object execute(AST ast) throws OhPanic {
        return this.envs.get(ast.source()).execute();
    }

    /**
     * 获取指定AST的导出值
     *
     * @param source AST的名称
     * @return 导出值
     * @throws OhPanic 执行过程中可能抛出的异常
     */
    public Map<String, ReturnValue> exportValues(String source) throws OhPanic {
        Map<String, ReturnValue> values = this.exportValues.get(source);
        if (values == null) {
            ASTEnv env = envs.get(source);
            env.execute();
            this.exportValues.put(source, env.exportValues());
            return this.exportValues(source);
        }
        return values;
    }

    /**
     * 获取激活上下文
     *
     * @return 激活上下文
     */
    public ActivationContext context() {
        return this.context;
    }
}
