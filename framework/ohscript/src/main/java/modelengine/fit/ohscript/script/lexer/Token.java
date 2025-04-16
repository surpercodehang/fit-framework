/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.lexer;

import java.io.Serializable;

/**
 * tokenized code中的一个元素
 * in a token：
 * tokenType: is corresponding terminal, like: ID, IF, WHILE....
 * lexeme: the real word in the token, which is a word of source code
 * line: the line number of the lexeme in source code
 * start: the start column of the lexeme in source code
 * end: the end column of the lexeme in the source code
 *
 * @author 张群辉
 * @since 2023-05-01
 */
public class Token implements Serializable {
    private static final long serialVersionUID = 4293840596528932667L;

    /**
     * 终结符类型
     */
    private final Terminal terminal;

    /**
     * 词素，即源代码中的实际字符串
     */
    private final String lexeme;

    /**
     * 在源代码中的行号
     */
    private final int line;

    /**
     * 在源代码行中的起始列位置
     */
    private final int start;

    /**
     * 在源代码行中的结束列位置
     */
    private final int end;

    /**
     * 构造函数
     * 
     * @param terminal 终结符类型
     * @param lexeme 词素，源代码中的实际字符串
     * @param line 源代码中的行号
     * @param start 在源代码行中的起始列位置
     * @param end 在源代码行中的结束列位置
     */
    public Token(Terminal terminal, String lexeme, int line, int start, int end) {
        this.terminal = terminal;
        this.lexeme = lexeme;
        this.line = line;
        this.start = start;
        this.end = end;
    }

    /**
     * 获取token的终结符类型
     * 
     * @return 终结符类型
     */
    public Terminal tokenType() {
        return this.terminal;
    }

    /**
     * 源码中的实际单词
     *
     * @return 源码中的实际单词
     */
    public String lexeme() {
        return this.lexeme.trim();
    }

    /**
     * 对应源码的行数
     *
     * @return 行数
     */
    public int line() {
        return this.line + 1;
    }

    /**
     * 对应源码行中的起始位置
     *
     * @return 起始位置
     */
    public int start() {
        return this.start + 1;
    }

    /**
     * 对应源码行中的结束位置
     *
     * @return 结束位置
     */
    public int end() {
        return this.end + 1;
    }

    @Override
    public String toString() {
        return this.lexeme();
    }
}
