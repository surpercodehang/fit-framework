/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.engine;

import modelengine.fit.ohscript.script.interpreter.ASFEnv;
import modelengine.fit.ohscript.script.parser.ASF;
import modelengine.fit.ohscript.script.parser.ParserBuilder;
import modelengine.fit.ohscript.util.Serializer;

import java.io.IOException;

/**
 * 脚本构建器
 *
 * @since 1.0
 */
public class ScriptBuilder {
    private final ParserBuilder parserBuilder;

    private ASF asf;

    private Serializer<ASF> serializer;

    /**
     * 构造函数
     *
     * @param parserBuilder 解析器构建器
     */
    public ScriptBuilder(ParserBuilder parserBuilder) {
        this.parserBuilder = parserBuilder;
        this.parserBuilder.begin();
    }

    /**
     * 加载文件形式的脚本
     *
     * @param source 脚本来源
     * @param filePath 脚本文件路径
     * @throws IOException 加载失败时抛出
     */
    public void loadFile(String source, String filePath) throws IOException {
        this.parserBuilder.parseFile(source, filePath);
    }

    /**
     * 加载字符串形式的脚本
     *
     * @param source 脚本来源
     * @param code 脚本内容
     * @throws IOException 加载失败时抛出
     */
    public void load(String source, String code) throws IOException {
        this.parserBuilder.parseString(source, code);
    }

    /**
     * 完成脚本构建，返回脚本环境
     *
     * @return 脚本环境
     */
    public ASFEnv done() {
        this.asf = this.parserBuilder.done();
        return new ASFEnv(this.asf);
    }

    /**
     * 创建新的脚本环境
     *
     * @return 新的脚本环境
     * @throws IOException 如果序列化或反序列化失败
     * @throws ClassNotFoundException 如果类未找到
     */
    public ASFEnv newEnv() throws IOException, ClassNotFoundException {
        if (this.serializer == null) {
            this.serializer = Serializer.serialize(this.asf);
        }
        return new ASFEnv(this.serializer.deSerialize());
    }
}
