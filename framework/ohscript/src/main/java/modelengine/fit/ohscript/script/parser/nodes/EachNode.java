/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fitframework.util.ObjectUtils;

/**
 * each关键字节点
 *
 * @since 1.0
 */
public class EachNode extends NonTerminalNode {
    private ArgumentNode itemArg;

    private ArgumentNode indexArg;

    /**
     * 构造函数
     */
    public EachNode() {
        super(NonTerminal.EACH_STATEMENT);
        this.returnAble = true;
    }

    @Override
    public void optimizeGama() {
        if (this.itemArg != null) {
            return;
        }
        TupleUnPackerNode args = ObjectUtils.cast(this.child(1));
        this.itemArg = new ArgumentNode();
        this.itemArg.addChild(args.items().get(0));
        this.indexArg = new ArgumentNode();
        this.indexArg.addChild(args.items().get(1));
        this.addChild(this.indexArg, 0);
        this.addChild(this.itemArg, 0);
        this.removeChild(args);
    }

    /**
     * 获取item语法节点
     *
     * @return item语法节点
     */
    public TerminalNode item() {
        return this.itemArg.argument();
    }

    /**
     * 获取索引语法节点
     *
     * @return 索引语法节点
     */
    public TerminalNode index() {
        return this.indexArg.argument();
    }

    /**
     * 获取数组语法节点
     *
     * @return 数组语法节点
     */
    public SyntaxNode array() {
        return this.child(4);
    }

    /**
     * 获取循环体语法节点
     *
     * @return 循环体语法节点
     */
    public BlockNode body() {
        return ObjectUtils.cast(this.child(5));
    }
}
