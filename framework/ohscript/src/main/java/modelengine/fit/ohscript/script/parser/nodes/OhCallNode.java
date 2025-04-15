/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.util.OhFrom;
import modelengine.fitframework.util.ObjectUtils;

/**
 * oh对象调用节点
 *
 * @since 1.0
 */
public class OhCallNode extends NonTerminalNode {
    /**
     * oh调用来源
     */
    private OhFrom from;

    /**
     * 调用源节点
     */
    private TerminalNode source;

    /**
     * 构造函数
     * 初始化为OH_CALL类型的非终结符节点
     */
    public OhCallNode() {
        super(NonTerminal.OH_CALL);
    }

    @Override
    public void optimizeBeta() {
        if (this.child(0).nodeType() == Terminal.OH) {
            this.from = OhFrom.valueFrom(this.removeAt(0).lexeme());
            this.source = (ObjectUtils.cast(this.child(0).child(0)));
            Token old = this.source.token();
            this.source.setToken(
                    new Token(old.tokenType(), this.from.ohName() + old.lexeme(), old.line(), old.start(), old.end()));
        }
        super.optimizeBeta();
    }
}
