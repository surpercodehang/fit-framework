/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.ActivationContext;
import modelengine.fit.ohscript.script.interpreter.Interpreter;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;
import modelengine.fit.ohscript.script.parser.AST;
import modelengine.fit.ohscript.script.semanticanalyzer.SymbolScope;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.NodeType;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.util.Pair;
import modelengine.fit.ohscript.util.Tool;
import modelengine.fitframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 语法树中的节点，是语法树的基本组成单位。每个节点可以包含子节点，并且可以存储与节点相关的各种信息，例如作用域、返回类型等。
 *
 * @author 张群辉
 * @since 2023-05-01
 */
public abstract class SyntaxNode implements Interpretable, Serializable {
    private static final long serialVersionUID = 6354439108132505522L;

    /**
     * 存储该节点的子节点列表，表示语法树的结构
     */
    protected final List<SyntaxNode> nodes = new ArrayList<>();

    /**
     * 存储该节点的成员（变量）列表，通常用于表示语法树中某个作用域内的变量声明
     */
    protected final List<InitialAssignmentNode> members = new ArrayList<>();

    /**
     * 指向该节点的父节点，便于在语法树中进行上下导航
     */
    protected SyntaxNode parent;

    /**
     * 表示该节点所在的作用域编号，用于语义分析和变量作用域的管理
     */
    protected long scope = 0L;

    /**
     * 标识该节点是否可返回值，例如在函数或方法节点中设置为 true
     */
    protected boolean returnAble = false;

    /**
     * 表示该节点的类型表达式，用于类型检查和语义分析
     */
    protected TypeExpr typeExpr = null;

    private final long id = Tool.newId();

    private Pair<SyntaxError, String> error = null;

    private AST ast = null;

    /**
     * 获取当前节点的所有成员（变量），返回一个包含所有成员的列表。
     *
     * @return 当前节点的所有成员（变量）
     */
    public List<InitialAssignmentNode> members() {
        return this.members;
    }

    /**
     * 在指定的位置添加一个子节点到当前节点的子节点列表中，并设置当前节点为子节点的父节点
     *
     * @param child 需要添加的子节点
     * @param index 子节点在子节点列表中的位置
     */
    public void addChild(SyntaxNode child, int index) {
        child.setParent(this);
        child.setAst(this.ast());
        this.nodes.add(index, child);
    }

    /**
     * 添加一个子节点到当前节点的子节点列表中，并设置当前节点为子节点的父节点
     *
     * @param child 需要添加的子节点
     */
    public void addChild(SyntaxNode child) {
        this.addChild(child, this.nodes.size());
    }

    /**
     * 判断当前节点是否为返回单元，如果是，则表示在语法树中的某个作用域内，所有的子节点都会被当作返回值返回给父节点。
     * 例如，函数或方法节点通常被视为返回单元。
     *
     * @return 如果当前节点是返回单元，则返回 true，否则返回 false
     */
    public boolean isReturnUnit() {
        return false;
    }

    /**
     * if there is return in returnable node
     * means the return value will return to return unit
     * like for while.... return in them will  force function around the for, while return
     *
     * @return is returnable
     */
    public boolean returnAble() {
        return this.returnAble;
    }

    /**
     * 设置当前节点的名称，这通常在语义分析阶段使用，用于获取变量、常量或其他需要名称的节点的名称。
     *
     * @param name 需要设置的名称
     */
    public void setName(String name) {
    }

    /**
     * 获取当前节点的名称，这通常用于在语法树中定位节点，例如在语法分析阶段定位错误。
     *
     * @return 当前节点的名称
     */
    public String name() {
        return this.nodeType().name();
    }

    /**
     * 获取当前节点的类型，这通常用于在语义分析阶段进行类型检查。
     *
     * @return 当前节点的类型
     */
    public abstract <T extends NodeType> T nodeType();

    /**
     * 获取当前节点的位置信息，包括行号和列号，用于在语法树中定位错误。
     *
     * @return 当前节点的位置信息
     */
    public abstract Location location();

    /**
     * 获取当前节点的所有子节点，返回一个包含所有子节点的列表。
     *
     * @return 当前节点的所有子节点
     */
    public List<SyntaxNode> children() {
        return new ArrayList<>(this.nodes);
    }

    /**
     * 获取指定位置的子节点，如果位置超出子节点列表的范围，则返回 null
     *
     * @param index 子节点在子节点列表中的位置
     * @return 指定位置的子节点，如果位置超出子节点列表的范围，则返回 null
     */
    public SyntaxNode child(int index) {
        if (index < 0 || index >= this.nodes.size()) {
            return null;
        }
        return this.nodes.get(index);
    }

    /**
     * 从当前节点的子节点列表中移除指定的子节点，并设置子节点的父节点为 null
     *
     * @param node 需要移除的子节点
     * @return 如果子节点已经存在于子节点列表中，并且已经被成功移除，则返回 true，否则返回 false
     */
    public synchronized boolean removeChild(SyntaxNode node) {
        boolean result = this.nodes.remove(node);
        node.setParent(null);
        return result;
    }

    /**
     * 获取当前节点的子节点数量
     *
     * @return 当前节点的子节点数量
     */
    public int childCount() {
        return this.nodes.size();
    }

    /**
     * 清空当前节点的子节点列表，并添加新的子节点到列表中
     *
     * @param nodes 需要添加的新的子节点列表
     */
    public void refreshChildren(List<SyntaxNode> nodes) {
        this.nodes.clear();
        nodes.forEach(this::addChild);
    }

    /**
     * 在当前节点的子节点列表中替换指定的子节点，并设置新的子节点的父节点为当前节点
     *
     * @param oldNode 需要被替换的子节点
     * @param newNode 新的子节点
     */
    public void replaceChild(SyntaxNode oldNode, SyntaxNode newNode) {
        int index = this.nodes.indexOf(oldNode);
        this.addChild(newNode, index);
        this.removeChild(oldNode);
    }

    /**
     * 获取当前节点的父节点，如果当前节点没有父节点，则返回 null。
     *
     * @return 当前节点的父节点，如果当前节点没有父节点，则返回 null
     */
    public SyntaxNode parent() {
        return this.parent;
    }

    /**
     * 设置当前节点的父节点，如果父节点为 null，则表示当前节点为语法树的根节点。
     *
     * @param parent 需要设置的父节点
     */
    protected void setParent(SyntaxNode parent) {
        this.parent = parent;
    }

    /**
     * 检查当前节点是否在语法树中形成循环，如果形成循环，则抛出异常。
     *
     * @param me 需要检查的节点
     * @param parent 父节点
     */
    private void checkRecursive(SyntaxNode me, SyntaxNode parent) {
        SyntaxNode parentparentNode = parent;
        while (parentparentNode != null) {
            if (this.parent == parentparentNode) {
                me.panic(SyntaxError.AST_CONFLICT);
                return;
            }
            parentparentNode = parentparentNode.parent();
        }
    }

    /**
     * 获取当前节点的唯一标识符，用于在语法树中唯一标识一个节点。
     *
     * @return 当前节点的唯一标识符
     */
    public long id() {
        return this.id;
    }

    /**
     * alpha is for syntax tree simplifying, focused on declining the layers of ast node
     * which will not change the original design of the grammar
     * black list: all non-terminals have the same optimizing logic. If not, override it in the non-terminal
     */
    public void optimizeAlpha() {
    }

    /**
     * similar function with alpha,second step of optimization
     * deal with children
     * by default, move only child up and delete self
     */
    public void optimizeBeta() {
    }

    /**
     * gama optimization is going to tune the structure of ast nod
     * which will change the original design of the grammar
     * indicate the design of semantics
     * function is the typical optimization from multi arguments function to single argument function with curry design
     */
    public void optimizeGama() {
    }

    /**
     * in semantic analysis, report error if there is semantic error
     *
     * @param error semantic error enum const
     */
    public void panic(SyntaxError error) {
        this.panic(error, "");
    }

    /**
     * 在语义分析阶段报告语法错误，如果发生语法错误，则将错误信息存储在当前节点的错误信息中。
     *
     * @param error 语法错误枚举常量
     * @param message 错误信息
     */
    public void panic(SyntaxError error, String message) {
        if (!this.ast().panicEnabled()) {
            return;
        }
        if (this.error != null && this.error.first() == error) {
            return;
        }
        this.error = new Pair(error, message);
    }

    /**
     * 获取当前节点和其所有子节点的语法错误信息，返回一个包含所有错误信息的映射。
     *
     * @return 当前节点和其所有子节点的语法错误信息
     */
    public Map<SyntaxNode, Pair<SyntaxError, String>> error() {
        Map<SyntaxNode, Pair<SyntaxError, String>> errors = new HashMap<>();
        if (this.error != null) {
            errors.put(this, this.error);
        }
        this.children().forEach(node -> {
            errors.putAll(node.error());
        });
        return errors;
    }

    /**
     * 获取当前节点的作用域编号，用于语义分析和变量作用域的管理。
     * 如果当前节点没有设置作用域编号，则从父节点继承作用域编号。
     *
     * @return 当前节点的作用域编号
     */
    public long scope() {
        if (this.scope != 0) {
            return this.scope;
        }
        SyntaxNode parentNode = this.parent();
        if (parentNode == null) {
            return 0;
        }
        if (ast.initialized()) {
            this.scope = parentNode.scope();
            return this.scope;
        } else {
            return parentNode.scope();
        }
    }

    /**
     * 获取当前节点的父节点的作用域编号，用于在语义分析和变量作用域的管理。
     *
     * @return 当前节点的父节点的作用域编号
     */
    public long parentScope() {
        SyntaxNode parentNode = this.parent;
        while (parentNode != null) {
            if (parentNode.scope() != this.scope()) {
                return parentNode.scope();
            } else {
                parentNode = parentNode.parent();
            }
        }
        return 0;
    }

    /**
     * 初始化当前节点的类型表达式，如果当前节点的类型表达式为 null 或者已经被设置，则不进行任何操作。
     * 如果当前节点的类型表达式为未知或者忽略，则将新的类型表达式设置为当前节点的类型表达式。
     *
     * @param expr 需要设置的类型表达式
     */
    public void initTypeExpr(TypeExpr expr) {
        if (expr == null || this.typeExpr == expr) {
            return;
        }
        if (typeExpr == null) { // 注释：|| expr.is(typeExpr)
            this.typeExpr = expr.polish();
        } else {
            if (this.typeExpr == TypeExprFactory.createUnknown() || this.typeExpr == TypeExprFactory.createIgnore()) {
                this.typeExpr = expr.polish();
            }
        }
    }

    /**
     * 获取当前节点的类型表达式，用于类型检查和语义分析。
     *
     * @return 当前节点的类型表达式
     */
    public TypeExpr typeExpr() {
        return typeExpr;
    }

    /**
     * 设置当前节点的语法树，如果当前节点的语法树为 null 或者已经被设置，则不进行任何操作。
     * 如果当前节点的语法树为空，则将新的语法树设置为当前节点的语法树，并将新的语法树设置为所有子节点的语法树。
     * 如果当前节点的作用域编号在符号表中不存在，则在符号表中添加新的作用域编号，并将新的作用域编号设置为当前节点的作用域编号。
     * 如果当前节点的作用域编号在符号表中已经存在，则将新的作用域编号设置为当前节点的作用域编号的父作用域编号。
     *
     * @param ast 需要设置的语法树
     */
    public void setAst(AST ast) {
        if (ast == null) {
            return;
        }
        if (this.ast == null) {
            this.ast = ast;
            this.children().forEach(node -> node.setAst(ast));
        }
        SymbolScope symbolScope = this.ast.symbolTable().getScope(this.scope());
        if (symbolScope == null) {
            this.ast.symbolTable().addScope(this.scope(), this.parentScope());
        } else {
            if (symbolScope.id() != 0) {
                symbolScope.setParent(this.parentScope());
            }
        }
    }

    /**
     * 设置当前节点的语法树
     *
     * @param ast 需要设置的语法树
     * @param env 运行时环境，用于存储所有的运行时节点
     */
    public synchronized void setAst(AST ast, ASTEnv env) {
        this.setAst(ast);
        env.runtimeNodes().add(this);
    }

    /**
     * 获取当前节点所在的语法树对象，语法树对象包含了语法树的所有信息，例如节点列表、作用域列表等。
     *
     * @return 当前节点所在的语法树对象
     */
    public AST ast() {
        return this.ast;
    }

    /**
     * 在语义分析阶段检查当前节点，如果发生语义错误，则将错误信息存储在当前节点的错误信息中。
     */
    public void semanticCheck() {
    }

    @Override
    public ReturnValue interpret(ASTEnv env, ActivationContext context) throws OhPanic {
        return Interpreter.DEFAULT.interpret(this, env, context);
    }

    /**
     * 为当前节点分配一个值，这通常在语义分析阶段使用，用于为变量、常量或其他需要赋值的节点分配一个值。
     *
     * @param value 需要分配的值
     * @param env 当前的语法分析环境
     * @param current 当前的激活上下文
     * @throws OhPanic 如果在分配过程中发
     */
    public void assignValue(ReturnValue value, ASTEnv env, ActivationContext current) throws OhPanic {
        Interpreter.DEFAULT.assignValue(this, value, env, current);
    }

    /**
     * delta优化
     */
    public void optimizeDelta() {
    }

    /**
     * 获取当前节点的系统子节点，如果不存在，则创建一个新的系统子节点并添加到当前节点的子节点列表中。
     *
     * @return 当前节点的系统子节点
     */
    public IgnoredNode getAndCreateSystemNode() {
        final String system = "system";
        IgnoredNode node = this.getSystemNode();
        if (node == null) {
            node = new IgnoredNode();
            node.setName(system);
            this.addChild(node);
        }
        return node;
    }

    /**
     * 获取当前节点的系统子节点，如果不存在，则返回 null。
     *
     * @return 当前节点的系统子节点，如果不存在，则返回 null
     */
    public IgnoredNode getSystemNode() {
        final String system = "system";
        Optional<SyntaxNode> possible = this.children()
                .stream()
                .filter(c -> c instanceof IgnoredNode && c.name().equals(system))
                .findFirst();
        return possible.<IgnoredNode>map(ObjectUtils::cast).orElse(null);
    }

    /**
     * 获取当前节点的词素，这通常用于在语法分析阶段定位错误。
     *
     * @return 当前节点的词素
     */
    public abstract String lexeme();

    /**
     * 获取当前节点的所有子节点，返回一个包含所有子节点的列表。
     *
     * @return 当前节点的所有子节点
     */
    public List<SyntaxNode> childrenNeedsInfer() {
        return this.children();
    }

    /**
     * 获取当前节点的已声明名称，这通常在语义分析阶段使用，用于获取变量、常量或其他需要名称的节点的名称。
     *
     * @return 当前节点的已声明名称
     */
    public TerminalNode declaredName() {
        return null;
    }

    /**
     * 在类型推断阶段忽略当前节点，如果返回 true，则在类型推断阶段不会对当前节点进行类型推断。
     *
     * @return 如果在类型推断阶段忽略当前节点，则返回 true
     */
    public boolean typeInferIgnored() {
        return false;
    }

    /**
     * 判断当前节点是否为元数据节点，元数据节点通常用于存储一些额外的信息，例如注释、文档等。
     *
     * @return 如果当前节点是元数据节点，则返回 true，否则返回 false
     */
    public boolean isMeta() {
        return false;
    }

    /**
     * 将当前节点转换为字符串，这通常在打印语法树或者生成代码时使用。
     *
     * @return 当前节点的字符串表示
     */
    public String toString() {
        return this.lexeme();
    }
}
