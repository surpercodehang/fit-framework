/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码块节点
 *
 * @since 1.0
 */
public class BlockNode extends NonTerminalNode {
    public BlockNode() {
        super(NonTerminal.BLOCK_STATEMENT);
    }

    @Override
    public void optimizeBeta() {

    }

    @Override
    public void semanticCheck() {
        List<ReturnNode> returns = this.returns();
        if (returns.size() == 0) {
            return;
        }
        ReturnNode first = returns.get(0);
        for (int i = 1; i < returns.size(); i++) {
            if (!first.typeExpr().is(returns.get(i).typeExpr())) {
                this.panic(SyntaxError.AMBIGUOUS_RETURN);
                return;
            }
        }
    }

    @Override
    public TypeExpr typeExpr() {
        List<ReturnNode> returns = this.returns();
        if (returns.size() > 0) {
            return returns.get(0).typeExpr();
        } else {
            return TypeExprFactory.createUnit();
        }
    }

    @Override
    public boolean isReturnUnit() {
        return this.parent() instanceof BlockNode || this.parent() instanceof FunctionDeclareNode;
    }

    /**
     * 判断代码块是否为空
     *
     * @return 如果代码块为空，返回true，否则返回false
     */
    public boolean isEmpty() {
        return this.childCount() == 2;
    }

    /**
     * 获取代码块中的所有返回节点
     *
     * @return 返回代码块中的所有返回节点的列表
     */
    public List<ReturnNode> returns() {
        // returns should be checked for same type expression
        List<ReturnNode> returns = new ArrayList<>();
        SyntaxNode root = this.child(1);
        if (root instanceof ReturnNode) {
            returns.add((ReturnNode) root);
        } else {
            root.children().forEach(child -> {
                if (child instanceof ReturnNode) {
                    returns.add((ReturnNode) child);
                }
                if (child instanceof BlockNode && !child.isReturnUnit()) {
                    // if, while....non function level block
                    returns.addAll(((BlockNode) child).returns());
                }
            });
        }
        return returns;
    }
}
