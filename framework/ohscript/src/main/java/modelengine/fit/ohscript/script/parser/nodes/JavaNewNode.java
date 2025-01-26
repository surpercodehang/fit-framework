/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 对应java语法中new方法的节点
 *
 * @since 1.0
 */
public class JavaNewNode extends NonTerminalNode {
    /**
     * 构造函数
     */
    public JavaNewNode() {
        super(NonTerminal.JAVA_NEW);
    }

    @Override
    public void optimizeAlpha() {
        super.optimizeAlpha();
        GeneralNode tmp = new GeneralNode(NonTerminal.IGNORED);
        tmp.refreshChildren(this.children());
        this.nodes.clear();

        // 得到类的别名
        TerminalNode javaClass = ObjectUtils.cast(this.parent.child(0));
        this.parent().removeChild(javaClass);
        this.addChild(javaClass, 0);
        this.addChild(tmp);
        this.parent().parent().replaceChild(this.parent(), this);
    }

    /**
     * 获取Java类的别名
     *
     * @return Java类的别名
     */
    public TerminalNode javaClass() {
        return ObjectUtils.cast(this.child(0));
    }

    /**
     * 获取实体声明节点
     *
     * @return 实体声明节点
     */
    public EntityDeclareNode entity() {
        return ObjectUtils.cast(this.child(1));
    }
}
