/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.engine;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.parser.AST;
import modelengine.fit.ohscript.script.parser.ParserBuilder;
import modelengine.fit.ohscript.util.Serializer;

import java.io.IOException;

/**
 * start class to run script
 *
 * @since 1.0
 */
public class OhScript {
    private final ParserBuilder parserBuilder;

    private AST ast;

    private Serializer<AST> serializer;

    public OhScript() {
        this.parserBuilder = new ParserBuilder();
    }

    /**
     * 加载一段ohscript代码，并返回一个可以执行的环境
     *
     * @param code ohscript代码
     * @return 返回一个可以执行的环境
     */
    public ASTEnv load(String code) {
        this.serializer = null;
        this.ast = this.parserBuilder.parseString("", code);
        return new ASTEnv(ast);
    }

    /**
     * 创建一个新的环境，用于执行脚本
     *
     * @return 返回一个新的环境
     * @throws IOException io异常
     * @throws ClassNotFoundException 类找不到异常
     */
    public ASTEnv createEnv() throws IOException, ClassNotFoundException {
        if (this.serializer == null) {
            this.serializer = Serializer.serialize(ast);
        }
        return new ASTEnv(serializer.deSerialize());
    }

    /**
     * 向脚本引擎注册一个外部类，这样脚本就可以使用这个类的实例了
     *
     * @param key 类的别名
     * @param clazz 要注册的类
     */
    public void grant(String key, Class<?> clazz) {
        this.parserBuilder.addExternalClass(key, clazz);
    }

    /**
     * 向脚本引擎注册一个外部对象，这样脚本就可以使用这个对象了
     *
     * @param key 对象的别名
     * @param instance 要注册的对象
     */
    public void grant(String key, Object instance) {
        this.parserBuilder.addExternalOh(key, instance);
    }

    /**
     * 开始构建一个新的脚本
     *
     * @return 返回一个新的脚本构建器
     */
    public ScriptBuilder begin() {
        return new ScriptBuilder(this.parserBuilder);
    }

    /**
     * 执行一段ohscript代码
     *
     * @param code ohscript代码
     * @return 返回执行结果
     * @throws OhPanic 执行过程中可能会抛出的异常
     */
    public Object execute(String code) throws OhPanic {
        ASTEnv env = this.load(code);
        return env.execute();
    }

    /**
     * ohscript代码实现Java一个对象的扩展，返回一个扩展包装后的对象
     *
     * @param object 要扩展的对象
     * @param code ohscript代码
     * @return 扩展后的对象
     * @throws Exception 异常
     */
    public <T> T extend(T object, String code) throws Exception {
        ASTEnv env = this.load(code);
        return env.createOhProxy(object, null);
    }

    /**
     * ohscript代码实现Java一个类，并返回该实现的一个对象
     *
     * @param clazz 要实现的类
     * @param code ohscript代码
     * @return 一个实现类的对象
     * @throws Exception 异常
     */
    public <T> T implement(Class<T> clazz, String code) throws Exception {
        ASTEnv env = this.load(code);
        return env.createOhProxy(null, clazz);
    }
}
