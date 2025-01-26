/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 参数节点
 *
 * @since 1.0
 */
public class ArgumentNode extends NonTerminalNode {
    /**
     * 构造函数
     * 构造一个参数节点
     */
    public ArgumentNode() {
        super(NonTerminal.ARGUMENT);
    }

    @Override
    public void optimizeBeta() {
    }

    /**
     * 获取参数节点
     *
     * @return 参数节点
     */
    public TerminalNode argument() {
        return ObjectUtils.cast(this.child(0));
    }

    @Override
    public TypeExpr typeExpr() {
        if (this.argument().nodeType() == Terminal.UNIT) {
            return TypeExprFactory.createUnit();
        }
        return this.argument().symbolEntry().typeExpr();
    }
}
