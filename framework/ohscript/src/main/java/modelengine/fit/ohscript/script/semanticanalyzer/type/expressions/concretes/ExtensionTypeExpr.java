/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import modelengine.fit.ohscript.script.parser.nodes.EntityExtensionNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

import java.util.HashMap;
import java.util.Map;

/**
 * 扩展类型表达式
 *
 * @since 1.0
 */
public class ExtensionTypeExpr extends EntityTypeExpr {
    /**
     * 构造函数
     *
     * @param entity 实体语法节点
     * @param members 成员
     */
    public ExtensionTypeExpr(SyntaxNode entity, Map<String, TypeExpr> members) {
        super(entity, members);
    }

    /**
     * 获取宿主类型表达式
     *
     * @return 宿主类型表达式
     */
    public TypeExpr hostTypeExpr() {
        SyntaxNode host = this.node();
        while (host instanceof EntityExtensionNode) {
            host = ((EntityExtensionNode) host).host();
        }
        return host.typeExpr();
    }

    @Override
    public TypeExpr polish() {
        EntityTypeExpr expr = new ExtensionTypeExpr(this.node(), new HashMap<>());
        return polish(expr);
    }

    @Override
    public Type type() {
        return Type.EXTENSION;
    }
}
