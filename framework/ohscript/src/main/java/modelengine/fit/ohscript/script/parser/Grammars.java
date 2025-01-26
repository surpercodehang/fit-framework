/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser;

import modelengine.fit.ohscript.util.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 整个文法的集合，它包含所有的语法规则，并且标识出开始的语法规则
 *
 * @since 1.0
 */
public class Grammars {
    /**
     * 存储所有的 Grammar 对象，以语法规则的名称为键
     */
    private final Map<String, Grammar> grammars;

    /**
     * 标识整个文法的开始符号（起始 Grammar）
     */
    private final Grammar start;

    /**
     * 构造函数，初始化所有的语法规则，并且找出开始的语法规则
     *
     * @param grammars 存储所有的语法规则的 Map
     */
    public Grammars(Map<String, Grammar> grammars) {
        this.grammars = grammars;
        this.grammars.values().forEach(grammar -> grammar.setGrammars(this));
        this.start = this.grammars.values().stream().filter(g -> g.type().isStart()).findFirst().get();
        this.start.first();
        this.start.follow();
    }

    /**
     * 获取开始的语法规则
     *
     * @return 返回开始的语法规则
     */
    public Grammar start() {
        return this.start;
    }

    /**
     * 根据名称获取语法规则
     *
     * @param name 语法规则的名称
     * @return 返回对应的语法规则，如果没有找到则返回 null
     */
    public Grammar get(String name) {
        return grammars.get(name);
    }

    /**
     * 根据非终结符获取语法规则
     *
     * @param nonTerminal 非终结符
     * @return 返回对应的语法规则，如果没有找到则返回 null
     */
    public Grammar getStandard(NonTerminal nonTerminal) {
        if (nonTerminal == NonTerminal.IGNORED) {
            Tool.grammarError("IGNORED is not a standard non terminal");
        }
        return this.get(nonTerminal.name());
    }

    /**
     * 检查所有的语法规则是否都已经被访问过
     *
     * @return 如果所有的语法规则都被访问过，返回 true，否则返回 false
     */
    public boolean isVerified() {
        return this.grammars.values().stream().allMatch(grammar -> grammar.traced());
    }

    /**
     * 返回所有的语法规则
     *
     * @return 返回所有的语法规则的列表
     */
    public List<Grammar> grammars() {
        return new ArrayList<>(grammars.values());
    }
}
