/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.ArgumentEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts.GenericTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.ArrayTypeExpr;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fitframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 系统方法推断
 *
 * @since 1.0
 */
public class SystemMethodInfer {
    private static final Map<String, Function<SyntaxNode, TypeExpr>> methods = new HashMap<>();

    static {
        methods.put(Constants.ARRAY_SIZE, (method) -> TypeExprFactory.createNumber(method));
        methods.put(Constants.ARRAY_INSERT, (method) -> {
            TerminalNode index = ObjectUtils.cast(method.child(2));
            TerminalNode value = ObjectUtils.cast(method.child(3));
            // index should be number
            ArgumentEntry indexEntry = ObjectUtils.cast(index.symbolEntry());
            indexEntry.setTypeExpr(TypeExprFactory.createNumber(indexEntry.typeExpr().node()));
            // inserted item should be array.itemTypeExpr
            ArgumentEntry valueEntry = ObjectUtils.cast(value.symbolEntry());
            ArrayTypeExpr array = findArray(method);
            array.clearProjection();
            (ObjectUtils.<GenericTypeExpr>cast(valueEntry.typeExpr())).addHasToBe(array.itemTypeExpr());
            return TypeExprFactory.createUnit();
        });
        methods.put(Constants.ARRAY_REMOVE, (method) -> {
            TerminalNode index = ObjectUtils.cast(method.child(2));
            ArgumentEntry indexEntry = ObjectUtils.cast(index.symbolEntry());
            indexEntry.setTypeExpr(TypeExprFactory.createNumber(indexEntry.typeExpr().node()));
            ArrayTypeExpr array = findArray(method);
            array.clearProjection();
            return array.itemTypeExpr();
        });
    }

    private static ArrayTypeExpr findArray(SyntaxNode method) {
        SyntaxNode parent = method.parent();
        while (!(parent.typeExpr() instanceof ArrayTypeExpr)) {
            parent = parent.parent();
        }
        return (ArrayTypeExpr) parent.typeExpr();
    }

    /**
     * 根据系统方法名称推断类型
     *
     * @param method 系统方法
     * @return 推断出的类型
     */
    public static TypeExpr infer(SyntaxNode method) {
        String name = method.child(1).lexeme();
        return methods.get(name).apply(method);
    }
}
