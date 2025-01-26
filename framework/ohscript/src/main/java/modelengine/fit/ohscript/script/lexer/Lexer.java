/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.lexer;

import java.util.ArrayList;
import java.util.List;

/**
 * 词法分析器
 * </p>
 * 将源代码转换为一系列标记（tokens），这些标记是代码的基本组成单位
 *
 * @since 1.0
 */
public class Lexer {
    /**
     * 读取代码，生成token列表
     *
     * @param codeSnap 待解析的代码
     * @return 生产的token列表
     */
    public List<Token> scan(String codeSnap) {
        List<Token> tokens = new ArrayList<>();
        String[] lines = this.buildLines(codeSnap);
        for (int i = 0; i < lines.length; i++) {
            this.tokenize(tokens, lines[i], i);
        }
        return tokens;
    }

    private String[] buildLines(String codeSnap) {
        return codeSnap.split("\n");
    }

    private void tokenize(List<Token> tokens, String line, int lineNum) {
        tokens.addAll(Terminal.match(line, lineNum));
    }
}
