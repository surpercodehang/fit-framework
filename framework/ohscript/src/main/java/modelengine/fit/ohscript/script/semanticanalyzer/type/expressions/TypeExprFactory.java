/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions;

import modelengine.fit.ohscript.script.parser.nodes.EntityDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityExtensionNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionCallNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.MapDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TupleDeclareNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts.GenericFunctionTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts.GenericTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.ArrayTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.BoolTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.EntityTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.ErrorTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.ExtensionTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.ExternalTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.FunctionTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.IgnoreTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.MapTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.NullTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.NumberTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.StringTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.TupleTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.UndefinedTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.UnitTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.UnknownTypeExpr;

import java.util.Map;

/**
 * provides methods to create type expressions.
 *
 * @author 张群辉
 * @since 2023/12/15
 */
public class TypeExprFactory {
    private static final UnitTypeExpr UNIT_INSTANCE = new UnitTypeExpr();

    private static final UnknownTypeExpr UNKNOWN_INSTANCE = new UnknownTypeExpr();

    private static final IgnoreTypeExpr IGNORE_INSTANCE = new IgnoreTypeExpr();

    private static final NullTypeExpr NULL_INSTANCE = new NullTypeExpr();

    private static final UndefinedTypeExpr UNDEFINED_INSTANCE = new UndefinedTypeExpr();

    /**
     * 创建一个单位类型表达式
     *
     * @return 表达式
     */
    public static UnitTypeExpr createUnit() {
        return UNIT_INSTANCE;
    }

    /**
     * 创建一个未知类型表达式
     *
     * @return 表达式
     */
    public static UnknownTypeExpr createUnknown() {
        return UNKNOWN_INSTANCE;
    }

    /**
     * 创建一个忽略类型表达式
     *
     * @return 表达式
     */
    public static IgnoreTypeExpr createIgnore() {
        return IGNORE_INSTANCE;
    }

    /**
     * 创建一个错误类型表达式
     *
     * @param node 节点
     * @return 表达式
     */
    public static ErrorTypeExpr createError(SyntaxNode node) {
        return new ErrorTypeExpr(node);
    }

    /**
     * 创建一个外部类型表达式
     *
     * @param node 节点
     * @return 表达式
     */
    public static ExternalTypeExpr createExternal(SyntaxNode node) {
        return new ExternalTypeExpr(node);
    }

    /**
     * 创建一个空类型表达式
     *
     * @return 表达式
     */
    public static NullTypeExpr createNull() {
        return NULL_INSTANCE;
    }

    public static UndefinedTypeExpr createUndefined() {
        return UNDEFINED_INSTANCE;
    }

    /**
     * 创建一个实体类型表达式
     *
     * @param node 节点
     * @param members 成员
     * @return 表达式
     */
    public static EntityTypeExpr createEntity(EntityDeclareNode node, Map<String, TypeExpr> members) {
        return new EntityTypeExpr(node, members);
    }

    /**
     * 创建一个扩展类型表达式
     *
     * @param node 节点
     * @param members 成员
     * @return 表达式
     */
    public static ExtensionTypeExpr createExtension(EntityExtensionNode node, Map<String, TypeExpr> members) {
        return new ExtensionTypeExpr(node, members);
    }

    /**
     * 创建一个元组类型表达式
     *
     * @param node 节点
     * @param members 成员
     * @return 表达式
     */
    public static TypeExpr createTuple(TupleDeclareNode node, Map<String, TypeExpr> members) {
        return new TupleTypeExpr(node, members);
    }

    /**
     * 创建一个通用的（泛型的）类型表达式
     *
     * @param node 节点
     * @return 表达式
     */
    public static GenericTypeExpr createGeneric(SyntaxNode node) {
        return new GenericTypeExpr(node);
    }

    /**
     * 创建一个字符串类型表达式
     *
     * @param node 节点
     * @return 表达式
     */
    public static StringTypeExpr createString(SyntaxNode node) {
        return new StringTypeExpr(node);
    }

    /**
     * 创建一个数字类型表达式
     *
     * @param node 节点
     * @return 表达式
     */
    public static NumberTypeExpr createNumber(SyntaxNode node) {
        return new NumberTypeExpr(node);
    }

    /**
     * 创建一个布尔类型表达式
     *
     * @param node 节点
     * @return 表达式
     */
    public static BoolTypeExpr createBool(SyntaxNode node) {
        return new BoolTypeExpr(node);
    }

    /**
     * 创建一个函数类型表达式
     *
     * @param function 函数声明节点
     * @param argumentType 参数类型
     * @param returnType 返回类型
     * @return 表达式
     */
    public static FunctionTypeExpr createFunction(FunctionDeclareNode function, TypeExpr argumentType,
            TypeExpr returnType) {
        return new FunctionTypeExpr(function, argumentType, returnType);
    }

    /**
     * 创建一个函数类型表达式，其参数和返回类型都是泛型类型
     *
     * @return 表达式
     */
    public static FunctionTypeExpr createFunction() {
        return new FunctionTypeExpr(null, createGeneric(null), createGeneric(null));
    }

    /**
     * 创建一个映射类型表达式
     *
     * @param node 节点
     * @return 表达式
     */
    public static MapTypeExpr createMap(MapDeclareNode node) {
        return new MapTypeExpr(node);
    }

    /**
     * 创建一个数组类型表达式
     *
     * @param node 节点
     * @return 表达式
     */
    public static ArrayTypeExpr createArray(SyntaxNode node) {
        return new ArrayTypeExpr(node);
    }

    /**
     * 创建一个泛型函数类型表达式
     *
     * @param node 函数调用节点
     * @param child 子节点
     * @return 表达式
     */
    public static GenericFunctionTypeExpr createGenericFunction(FunctionCallNode node, SyntaxNode child) {
        FunctionDeclareNode newFunc = new FunctionDeclareNode() {
            @Override
            public String lexeme() {
                return node.lexeme();
            }
        };
        return new GenericFunctionTypeExpr(newFunc, child);
    }
}
