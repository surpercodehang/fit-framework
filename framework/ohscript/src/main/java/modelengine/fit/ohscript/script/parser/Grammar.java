/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.util.Tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ohScript grammar
 * begin from start grammar
 * and build a grammar tree/graph
 *
 * @author 张群辉 2023-05
 * @since 2023-05-01
 */
public class Grammar {
    /**
     * 自身持有的非终结符
     */
    private final NonTerminal nonTerminal;

    /**
     * 作用：存储该 Grammar 类对应的非终结符（NonTerminal）的所有产生式规则。
     * <p>
     * 关系：每个 Grammar 对象对应一个 NonTerminal，
     * 这个 NonTerminal 可以通过多个 Production（产生式）来定义。
     * 这个列表存储了该非终结符的所有产生式规则。
     */
    private final List<Production> productions = new ArrayList<>();

    private final String name;

    /**
     * 作用：存储整个语法集合的引用。
     * <p>
     * 关系：Grammars 类是一个包含多个 Grammar 对象的集合或容器，用来表示整个文法（grammar）。
     * 每个 Grammar 对象通过 grammars 引用可以访问或与其他 Grammar 对象进行关联。
     */
    private Grammars grammars;

    private Set<Terminal> follow;

    private boolean traced = false;

    /**
     * 构造函数
     *
     * @param nonTerminal 非终结符对象
     * @param name 非终结符名称
     */
    public Grammar(NonTerminal nonTerminal, String name) {
        this.nonTerminal = nonTerminal;
        this.name = name;
        this.follow = new HashSet<>();
        if (nonTerminal.isStart()) {
            this.follow.add(Terminal.END);
        }
    }

    /**
     * 获取当前Grammar对象对应的非终结符对象
     *
     * @return 非终结符对象
     */
    public NonTerminal type() {
        return this.nonTerminal;
    }

    /**
     * 获取非终结符的所有产生式
     * 每个非终结符都有一个或多个产生式，表示它可以如何产生终结符序列
     *
     * @return 产生式列表
     */
    public List<Production> productions() {
        return this.productions;
    }

    /**
     * 获取非终结符的名称
     *
     * @return 非终结符的名称
     */
    public String name() {
        return this.name;
    }

    /**
     * 获取非终结符的追踪状态
     * <p>
     * 关系：当需要调试或打印非终结符的信息时，可以设置该非终结符的追踪状态为true。
     *
     * @return 非终结符的追踪状态
     */
    public boolean traced() {
        return this.traced;
    }

    /**
     * 计算产生式的FIRST集合
     * 对于每个产生式，计算其FIRST集合，并将其添加到非终结符的FIRST集合中
     * 如果有重叠的终结符在不同的产生式中，抛出异常
     * LL解析器不支持重叠的文法
     * 返回包含所有产生式FIRST集合的终结符列表
     *
     * @return 终结符列表
     */
    public List<Terminal> first() {
        Set<Terminal> first = new HashSet<>();
        this.productions().forEach(production -> {
            List<Terminal> intersection = new ArrayList<>(first);
            intersection.retainAll(production.first());
            intersection.removeIf(t -> t == Terminal.EPSILON);
            if (!intersection.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                intersection.forEach(t -> sb.append(t.name()).append(" "));
                Tool.grammarError("there is duplicate production first: " + sb + "in grammar: " + this.name()
                        + ", which will cause FIRST-FIRST conflict in LL(1) parser");
            }
            first.addAll(production.first());
        });
        return new ArrayList<>(first);
    }

    /**
     * 计算非终结符的FOLLOW集合
     * 对于每个非终结符，计算其FOLLOW集合，并将其添加到非终结符的FOLLOW集合中
     * 如果非终结符的产生式可以产生空串（EPSILON），那么将产生式的FIRST集合添加到非终结符的FOLLOW集合中
     * 如果非终结符是开始符号，那么将终结符END添加到非终结符的FOLLOW集合中
     *
     * @return 终结符集合
     */
    public Set<Terminal> follow() {
        if (follow == null) {
            follow = new HashSet<>();
        }
        follow.removeIf(t -> t == Terminal.EPSILON);
        return this.follow;
    }

    /**
     * 设置整个语法集合的引用
     * <p>
     * 关系：Grammars 类是一个包含多个 Grammar 对象的集合或容器，用来表示整个文法（grammar）。
     * 每个 Grammar 对象通过 grammars 引用可以访问或与其他 Grammar 对象进行关联。
     *
     * @param grammars 整个语法集合的引用
     */
    public void setGrammars(Grammars grammars) {
        this.grammars = grammars;
    }

    /**
     * 获取整个语法集合的引用
     * <p>
     * 关系：Grammars 类是一个包含多个 Grammar 对象的集合或容器，用来表示整个文法（grammar）。
     * 每个 Grammar 对象通过 grammars 引用可以访问或与其他 Grammar 对象进行关联。
     *
     * @return 整个语法集合的引用
     */
    public Grammars grammars() {
        return this.grammars;
    }

    /**
     * 设置非终结符的追踪状态
     * <p>
     * 关系：当需要调试或打印非终结符的信息时，可以设置该非终结符的追踪状态为true。
     */
    public void trace() {
        this.traced = true;
    }
}
