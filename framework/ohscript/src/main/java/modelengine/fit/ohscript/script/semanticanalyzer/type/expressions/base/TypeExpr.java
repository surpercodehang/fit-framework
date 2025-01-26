/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base;

import modelengine.fit.ohscript.script.parser.nodes.ScriptNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.UnitTypeExpr;
import modelengine.fit.ohscript.util.Tool;
import modelengine.fitframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 类型表达式，用于描述类型的元数据。它不仅仅是简单的类型标识，而是能够详细描述类型的各种属性和关系
 * type expression is to describe metadata of a type
 * type is not strict enough to know two variables are same or not
 * function(a), function(a,b) are both type.function. but they are completely different. in HOF, there should be type
 * mismatch error
 * the method "is" is the most important factor to know is  a type expression is b expression
 * huizi 2023
 *
 * @since 1.0
 */
public abstract class TypeExpr implements Serializable {
    private static final long serialVersionUID = -125345532655668319L;

    private static int counter = 1;

    /**
     * 对应的语法树节点
     */
    protected SyntaxNode node;

    /**
     * 基础类型表达式
     */
    protected TypeExpr base = null;

    private final String key;

    private final Map<String, TypeExpr> members = new HashMap<>();

    private Map<String, TypeExpr> sysMembers;

    /**
     * 构造函数
     *
     * @param node 语法节点
     */
    public TypeExpr(SyntaxNode node) {
        this(Tool.uuid(), node);
    }

    /**
     * 构造函数
     *
     * @param key 键值
     * @param node 语法节点
     */
    public TypeExpr(String key, SyntaxNode node) {
        this.key = key;
        this.node = node;
    }

    /**
     * 获取一个新的ID
     * 这个方法用于生成一个新的ID，用于标识一个类型表达式
     *
     * @return 新的ID
     */
    protected static Integer newId() {
        return counter++;
    }

    /**
     * 加载系统方法
     * 这个方法用于加载系统方法，系统方法是在语法分析的时候，根据类型生成的方法
     */
    public void loadSystemMethods() {
        if (node == null) {
            return;
        }
        if (sysMembers != null) {
            return;
        }
        sysMembers = new HashMap<>();
        (ObjectUtils.<ScriptNode>cast(node.ast().start())).getMethods(this.type().name()).forEach((name, function) -> {
            sysMembers.put(name, function.typeExpr());
        });
    }

    /**
     * 对应的语法树节点
     *
     * @return 节点
     */
    public SyntaxNode node() {
        return this.node;
    }

    /**
     * 设置对应的语法树的节点
     *
     * @param node 节点
     */
    public void setNode(SyntaxNode node) {
        this.node = node;
    }

    /**
     * 获取基础类型
     * 用以描述其基础类型，如是一个Array、还是String、还是一个Function
     *
     * @return 类型
     */
    public abstract Type type();

    /**
     * 关键方法，用于判断两个类型表达式是否相同
     *
     * @param expr 待判定的表达式
     * @return 是否是同样的类型
     */
    public boolean is(TypeExpr expr) {
        if (this == TypeExprFactory.createUnknown()) {
            return true;
        }
        TypeExpr newExpr = expr.exactBe();
        if (newExpr == TypeExprFactory.createUnknown()) {
            return true; // 注释：not sure if this logic is correct
        }
        if (this.key().equals(newExpr.key())) {
            return true;
        }
        if (this instanceof UnitTypeExpr) {
            return false;
        }
        if (!(newExpr instanceof AbstractTypeExpr)) {
            return false;
        }
        if (newExpr.couldBe().isEmpty()) {
            return true;
        }

        for (Object e : newExpr.couldBe()) {
            TypeExpr could = ObjectUtils.cast(e);
            if (this.is(could)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有的成员，包括系统成员和自定义成员
     *
     * @return 所有成员
     */
    public Map<String, TypeExpr> allMembers() {
        // system members
        this.loadSystemMethods();
        Map<String, TypeExpr> all = new HashMap<>();
        if (this.sysMembers != null) {
            all.putAll(this.sysMembers);
        }
        all.putAll(this.members());
        return all;
    }

    /**
     * 获取类型表达式的成员
     * 这个方法返回一个类型表达式的成员，包括系统成员和自定义成员
     *
     * @return 所有成员
     */
    public Map<String, TypeExpr> members() {
        if (this.base() != null) {
            Map<String, TypeExpr> memberMap = new HashMap<>();
            // base members
            memberMap.putAll(base().members());
            // my members
            memberMap.putAll(this.members);
            memberMap.put(".base", this.base);
            return memberMap;
        } else {
            return this.members;
        }
    }

    /**
     * 获取类型表达式的自定义成员
     * 这个方法返回一个类型表达式的自定义成员，不包括系统成员
     *
     * @return 自定义成员
     */
    public Map<String, TypeExpr> myMembers() {
        return this.members;
    }

    /**
     * 获取基础类型表达式
     *
     * @return 基础类型表达式
     */
    public TypeExpr base() {
        return this.base;
    }

    /**
     * 获取可能的类型表达式
     * 这个类型表达式可能在不同的上下文中是一个类型列表
     *
     * @return 类型集合
     */
    public Set<TypeExpr> couldBe() {
        Set<TypeExpr> couldBe = new HashSet<>();
        couldBe.add(this);
        return couldBe;
    }

    /**
     * 使类型表达式失效
     * 这个方法用于在类型推断的过程中，当类型表达式的基础类型发生改变的时候，使得当前的类型表达式失效
     */
    public void invalidate() {
    }

    /**
     * 获取类型表达式的精确形式
     * 这个方法返回一个精确的类型表达式，如果当前的类型表达式是一个可能的类型表达式，那么就返回其中的一个
     *
     * @return 精确的类型表达式
     */
    public TypeExpr exactBe() {
        return this;
    }

    /**
     * 对类型表达式进行优化
     * 这个方法用于在类型推断的过程中，对类型表达式进行优化，如果类型表达式是可以投影的，那么就清除投影
     *
     * @return 优化后的类型表达式
     */
    public TypeExpr polish() {
        if (this instanceof Projectable) {
            ((Projectable) this).clearProjection();
        }
        return this.exactBe();
    }

    /**
     * 获取类型表达式的键值
     * 这个方法返回一个类型表达式的键值，键值是唯一的，用于在类型推断的过程中标识一个类型表达式
     *
     * @return 键值
     */
    public String key() {
        return this.key;
    }

    @Override
    public String toString() {
        return this.type().name().toLowerCase();
    }

    /**
     * 复制一个新的类型表达式
     * 这个方法用于创建一个新的类型表达式，并且新的类型表达式的基础类型是当前的类型表达式
     *
     * @param node 语法节点
     * @return 新的类型表达式
     */
    public abstract TypeExpr duplicate(TerminalNode node);

    /**
     * 扩展一个新的类型表达式
     * 这个方法用于创建一个新的类型表达式，并且新的类型表达式的基础类型是当前的类型表达式
     *
     * @param node 语法节点
     * @return 新的类型表达式
     */
    public TypeExpr extend(TerminalNode node) {
        TypeExpr duplicated = this.duplicate(node);
        duplicated.base = this;
        return duplicated;
    }
}
