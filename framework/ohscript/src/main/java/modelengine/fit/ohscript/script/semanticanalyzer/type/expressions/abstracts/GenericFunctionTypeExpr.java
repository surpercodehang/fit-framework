/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts;

import modelengine.fit.ohscript.script.parser.nodes.FunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.SyntaxException;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.FunctionTypeExpr;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 通用方法类型表达式
 *
 * @since 1.0
 */
public class GenericFunctionTypeExpr extends FunctionTypeExpr {
    public GenericFunctionTypeExpr(FunctionDeclareNode node, SyntaxNode child) {
        super(node, new GenericTypeExpr(child), new GenericTypeExpr(null));
    }

    @Override
    public TypeExpr project(TypeExpr projection) throws SyntaxException {
        FunctionTypeExpr base = ObjectUtils.cast(super.project(projection));
        return convertGeneric(base);
    }

    @Override
    public TypeExpr project(TypeExpr funcArg, TypeExpr projection) throws SyntaxException {
        FunctionTypeExpr base = ObjectUtils.cast(super.project(funcArg, projection));
        return convertGeneric(base);
    }

    private GenericFunctionTypeExpr convertGeneric(FunctionTypeExpr base) {
        GenericFunctionTypeExpr result = new GenericFunctionTypeExpr(base.function(), base.argumentType().node());
        result.setArgumentType(base.argumentType());
        result.setReturnType(base.returnType());
        return result;
    }
}
