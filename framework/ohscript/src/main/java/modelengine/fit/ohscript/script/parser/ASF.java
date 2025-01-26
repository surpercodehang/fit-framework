/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser;

import modelengine.fit.ohscript.script.semanticanalyzer.SymbolScope;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fit.ohscript.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract Syntax Forest，抽象语法森林
 * 是多个抽象语法树的集合。通常用于表示在解析过程中存在多种有效解析方式的情况
 *
 * @since 1.0
 */
public class ASF implements Serializable {
    private static final long serialVersionUID = -8932995151261532460L;

    private final List<AST> asts = new ArrayList<>();

    /**
     * external functions and object
     */
    private final Map<String, Object> externalOhs = new HashMap<>();

    /**
     * fit function name with namespace allowed to be invoked in ohscript
     */
    private final Map<String, Pair<String, Integer>> fitOhs = new HashMap<>();

    /**
     * http url allowed to be invoked in ohscript
     */
    private final Map<String, Pair<String, String>> httpOhs = new HashMap<>();

    private final SymbolScope scope;

    private Map<String, Class<?>> externalClasses = new HashMap<>();

    /**
     * 构造函数
     * 初始化一个空的抽象语法森林，并创建一个根作用域
     */
    public ASF() {
        this.scope = new SymbolScope(Constants.ROOT_SCOPE, Constants.ROOT_SCOPE);
    }

    /**
     * 添加一个抽象语法树到森林中
     * 并设置这个抽象语法树的父亲为当前森林
     *
     * @param ast 要添加的抽象语法树
     * @return 被添加的抽象语法树
     */
    public AST add(AST ast) {
        this.asts.add(ast);
        ast.setASF(this);
        return ast;
    }

    public SymbolScope scope() {
        return this.scope;
    }

    /**
     * 返回森林中的所有抽象语法树
     *
     * @return 森林中的所有抽象语法树
     */
    public List<AST> asts() {
        return this.asts;
    }

    /**
     * 根据源代码文本查找对应的抽象语法树
     * 如果找到，返回一个包含该抽象语法树的Optional对象
     * 如果没有找到，返回一个空的Optional对象
     *
     * @param source 源代码文本
     * @return 对应的抽象语法树，如果没有找到则返回空
     */
    public Optional<AST> ast(String source) {
        return this.asts.stream().filter(a -> a.source().equals(source)).findFirst();
    }

    /**
     * 添加一个外部对象到外部对象的映射中
     * 这些对象可以在ohscript中被引用
     *
     * @param key 对象的别名
     * @param oh 对象本身
     */
    public void addExternalOh(String key, Object oh) {
        this.externalOhs.put(key, oh);
    }

    /**
     * 添加一个外部类到外部类的映射中
     * 这些类可以在ohscript中被实例化
     *
     * @param key 类的别名
     * @param clazz 类的Class对象
     */
    public void addExternalClass(String key, Class<?> clazz) {
        this.externalClasses.put(key.substring(0, 1).toUpperCase(Locale.ROOT) + key.substring(1), clazz);
    }

    public Map<String, Object> externalOhs() {
        return this.externalOhs;
    }

    public Map<String, Class<?>> externalClasses() {
        return this.externalClasses;
    }

    /**
     * 添加一个fit函数到fit函数的映射中
     * 这些函数可以在ohscript中被调用
     *
     * @param alias fit函数的别名
     * @param fitGenericableId fit函数的ID
     * @param argNum 函数的参数数量
     */
    public void addFitOh(String alias, String fitGenericableId, Integer argNum) {
        this.fitOhs.put(alias, new Pair<>(fitGenericableId, argNum));
    }

    public Map<String, Pair<String, Integer>> fitOhs() {
        return this.fitOhs;
    }

    /**
     * 添加一个http url到http url的映射中
     * 这些url可以在ohscript中被调用
     *
     * @param alias http url的别名
     * @param url http url
     * @param method http请求方法
     */
    public void addHttpOh(String alias, String url, String method) {
        this.httpOhs.put(alias, new Pair<>(url, method));
    }

    public Map<String, Pair<String, String>> httpOhs() {
        return this.httpOhs;
    }
}
