/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions;

import modelengine.fit.ohscript.script.errors.SyntaxError;

/**
 * 语法分析阶段的语法错误
 *
 * @author 张群辉
 * @since 2023/11/20
 */
public class SyntaxException extends Exception {
    /**
     * 语法错误对象，用于存储具体的语法错误信息
     */
    private final SyntaxError syntaxError;

    /**
     * 错误提示消息，用于向用户展示友好的错误说明
     */
    private final String message;

    /**
     * 构造函数
     * 
     * @param syntaxError 语法错误对象，包含具体的语法错误信息
     * @param message 错误提示消息，用于向用户展示友好的错误说明
     */
    public SyntaxException(SyntaxError syntaxError, String message) {
        this.syntaxError = syntaxError;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    /**
     * 获取语法错误信息
     *
     * @return 返回语法错误信息
     */
    public SyntaxError syntaxError() {
        return this.syntaxError;
    }
}
