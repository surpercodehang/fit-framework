/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.interpreter;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.ohscript.external.FitExecutionException;
import modelengine.fit.ohscript.external.FitExecutor;
import modelengine.fit.ohscript.external.HttpExecutionException;
import modelengine.fit.ohscript.external.HttpExecutor;
import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.script.parser.AST;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.parser.nodes.EntityDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.NodeType;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.ArrayTypeExpr;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fit.ohscript.util.ExternalWrapper;
import modelengine.fit.ohscript.util.OhUtil;
import modelengine.fit.ohscript.util.Tool;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.util.ArrayUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 用于管理和维护抽象语法树（AST）及其相关的执行上下文。
 * 在执行过程中，用于存储和管理语法树、上下文、执行器以及中间和最终结果
 *
 * @since 1.0
 */
public class ASTEnv {
    private final AST ast;

    /**
     * 存储抽象语法森林（ASF）的环境。用于处理在解析过程中存在多种有效解析方式的情况
     */
    private final ASFEnv asfEnv;

    /**
     * 导出的值。这些值可以在执行过程中被导出，以供其他部分使用
     */
    private final Map<String, ReturnValue> exportValues = new HashMap<>();

    private final ActivationContext root;

    private FitExecutor fitExecutor;

    private HttpExecutor httpExecutor;

    private Map<String, Object> ohs = new HashMap<>();

    /**
     * 存储运行时的语法节点，这些节点表示正在执行的代码片段
     */
    private List<SyntaxNode> runtimeNodes = new ArrayList<>();

    public ASTEnv(AST ast, ASFEnv asfEnv) {
        this.ast = ast;
        this.asfEnv = asfEnv;
        this.root = asfEnv.context();
    }

    /**
     * 创建一个新的 AST 环境。
     *
     * @param ast 表示抽象语法树（AST）的 {@link AST}。
     */
    public ASTEnv(AST ast) {
        this.ast = ast;
        this.asfEnv = new ASFEnv(this);
        this.root = this.asfEnv.context();
        OhUtil.AST_ENV_THREAD_LOCAL.set(this);
    }

    /**
     * 获取 FIT 调用的执行器。
     *
     * @return 表示 FIT 调用执行器的 {@link FitExecutor}。
     */
    public FitExecutor fitBroker() {
        return (genericableId, args) -> {
            if (ArrayUtils.isEmpty(args) || args.length < 1) {
                String message = StringUtils.format("No fit execution context. [genericableId={0}]", genericableId);
                throw new FitExecutionException(genericableId, message);
            }
            if (this.fitExecutor == null) {
                String message = StringUtils.format(
                        "No fit executor, please call ASTEnv#setBrokerClient(BrokerClient brokerClient) first. "
                                + "[genericableId={0}]", genericableId);
                throw new FitExecutionException(genericableId, message);
            }
            return this.fitExecutor.execute(genericableId, args);
        };
    }

    /**
     * 设置 FIT 调用代理。
     *
     * @param container 表示 bean 容器的 {@link BeanContainer}。
     * @param brokerClient 表示待设置的 FIT 调用代理的 {@link BrokerClient}。
     */
    public void setBrokerClient(BeanContainer container, BrokerClient brokerClient) {
        this.fitExecutor = FitExecutor.create(container, brokerClient);
    }

    /**
     * 获取 Http 调用的执行器。
     *
     * @return 表示 Http 调用执行器的 {@link HttpExecutor}。
     */
    public HttpExecutor httpBroker() {
        return (method, url, args) -> {
            if (this.httpExecutor == null) {
                String message = StringUtils.format(
                        "No http executor, please call ASTEnv#setHttpClient(HttpClassicClient httpClient) first. "
                                + "[method={0}, url={1}]", method, url);
                throw new HttpExecutionException(method, url, message);
            }
            return this.httpExecutor.execute(method, url, args);
        };
    }

    /**
     * 设置 Http 调用客户端。
     *
     * @param httpClient 表示待设置的 Http 调用客户端的 {@link HttpClassicClient}。
     */
    public void setHttpClient(HttpClassicClient httpClient) {
        this.httpExecutor = HttpExecutor.create(httpClient);
    }

    /**
     * 创建一个新的激活上下文。
     * 这个上下文将会被添加到执行栈中，以供后续的代码片段使用。
     *
     * @param scope 表示新的激活上下文的作用域。
     * @param env 表示新的激活上下文的环境。
     * @param current 表示当前的激活上下文。
     * @return 表示新的激活上下文的 {@link ActivationContext}。
     */
    public ActivationContext push(long scope, ASTEnv env, ActivationContext current) {
        if (current != null && current.scope() == scope) {
            return current;
        }
        return new ActivationContext(scope, current, env);
    }

    /**
     * 创建一个新的激活上下文，并将其推入栈中。
     * 这个方法用于在执行过程中创建新的激活上下文，并将其推入栈中。
     *
     * @param scope 表示新的激活上下文的作用域。
     * @param current 表示当前的激活上下文。
     * @return 表示新的激活上下文的 {@link ActivationContext}。
     */
    public ActivationContext push(long scope, ActivationContext current) {
        return this.push(scope, this, current);
    }

    /**
     * 执行语法树，并进行实际的外部调用。
     *
     * @return 表示执行结果的 {@link Object}。
     * @throws OhPanic 如果在执行过程中出现错误，将会抛出此异常。
     */
    public Object execute() throws OhPanic {
        return this.execute(null);
    }

    /**
     * 执行语法树，但不进行实际的外部调用。
     * 这个方法用于测试语法树的执行结果，但不会真正执行外部的调用，如 FIT 调用或 Http 调用。
     * 这个方法主要用于测试和调试。
     *
     * @return 表示执行结果的 {@link Object}。
     * @throws OhPanic 如果在执行过程中出现错误，将会抛出此异常。
     */
    public Object dryRun() throws OhPanic {
        return this.dryRun(null);
    }

    /**
     * 执行语法树。
     * 这个方法将会执行语法树，并且在执行过程中进行实际的外部调用，如 FIT 调用或 Http 调用。
     *
     * @param args 表示执行参数的 {@link Object} 数组。
     * @return 表示执行结果的 {@link Object}。
     * @throws OhPanic 如果在执行过程中出现错误，将会抛出此异常。
     */
    public Object eval(Object... args) throws OhPanic {
        this.ast.errors();
        this.updateAst(args);
        Object value = this.ast.interpret(this);
        while (this.runtimeNodes.size() > 0) {
            SyntaxNode node = this.runtimeNodes.remove(0);
            SyntaxNode parent = node.parent();
            if (parent != null) {
                parent.removeChild(node);
            }
            this.removeScope(node);
        }
        return value;
    }

    private void removeScope(SyntaxNode node) {
        node.children().forEach(this::removeScope);
        NodeType nodeType = node.nodeType();
        if (!(nodeType instanceof NonTerminal)) {
            return;
        }
        if (((NonTerminal) nodeType).ownScope()) {
            node.ast().symbolTable().removeScope(node.scope());
        }
    }

    /**
     * 执行语法树，但不进行实际的外部调用。
     * 这个方法用于测试语法树的执行结果，但不会真正执行外部的调用，如 FIT 调用或 Http 调用。
     * 这个方法主要用于测试和调试。
     *
     * @param args 表示执行参数的 {@link Object} 数组。
     * @return 表示执行结果的 {@link Object}。
     * @throws OhPanic 如果在执行过程中出现错误，将会抛出此异常。
     */
    public Object dryRun(Object... args) throws OhPanic {
        Object value = this.eval(args);
        if (value instanceof Map && ((Map<?, ?>) value).containsKey(Constants.EXTERNAL_VALUE)) {
            value = (ObjectUtils.<ReturnValue>cast(((Map<?, ?>) value).get(Constants.EXTERNAL_VALUE))).value();
        }
        return (value instanceof ExternalWrapper) ? ((ExternalWrapper) value).object(this) : value;
    }

    /**
     * 执行语法树，并进行实际的外部调用。
     *
     * @param args 表示执行参数的 {@link Object} 数组。
     * @return 表示执行结果的 {@link Object}。
     * @throws OhPanic 如果在执行过程中出现错误，将会抛出此异常。
     */
    public Object execute(Object... args) throws OhPanic {
        Object raw = this.dryRun(args);
        return this.convertOh(raw);
    }

    private Object convertOh(Object raw) {
        if (raw instanceof Map) {
            if (((Map<String, ?>) raw).keySet().stream().anyMatch(k -> k.startsWith(Constants.DOT))) {
                return this.convertEntity((Map<String, ReturnValue>) raw);
            } else {
                return this.convertMap((Map<String, ?>) raw);
            }
        } else if (raw instanceof List) {
            return this.convertList((List<ReturnValue>) raw);
        } else if (raw instanceof FunctionDeclareNode) {
            return this.convertFunc((FunctionDeclareNode) raw);
        } else {
            return raw;
        }
    }

    private Object convertFunc(FunctionDeclareNode raw) {
        return null;
    }

    private Object convertMap(Map<String, ?> raw) {
        Map<String, Object> fields = new HashMap<>();
        raw.forEach((k, v) -> {
            Object value = raw.get(k);
            if (value instanceof ReturnValue) {
                Object converted = convertOh(((ReturnValue) value).value());
                if (converted == null) {
                    return;
                }
                fields.put(k, converted);
            } else {
                fields.put(k, value);
            }
        });
        return new Oh(raw, OhType.MAP, fields);
    }

    private Object convertList(List<?> raw) {
        Map<String, Object> fields = new HashMap<>();
        for (int i = 0; i < raw.size(); i++) {
            Object value = raw.get(i);
            if (value instanceof ReturnValue) {
                Object converted = convertOh(((ReturnValue) value).value());
                if (converted == null) {
                    continue;
                }
                fields.put(Integer.toString(i), converted);
            } else {
                fields.put(Integer.toString(i), value);
            }
        }
        return new Oh(raw, OhType.LIST, fields);
    }

    private Oh convertEntity(Map<String, ReturnValue> raw) {
        AtomicReference<OhType> type = new AtomicReference<>(OhType.ENTITY);
        Map<String, Object> fields = new HashMap<>();
        raw.keySet().stream().filter(k -> k.startsWith(Constants.DOT)).forEach(key -> {
            if (".0".equals(key)) {
                type.set(OhType.TUPLE);
            }
            Object value = convertOh(raw.get(key).value());
            if (value == null) {
                return;
            }
            fields.put(key.substring(1), value);
        });
        return new Oh(raw, type.get(), fields);
    }

    /**
     * 用ohscript输出的entity构造代理一个baseInterface类型对象
     *
     * @param baseInterface 要构造对象的接口
     * @param baseObject 该对象为代理的基础对象，ohscript可以只实现部分方法
     * @param <T> 接口类型
     * @return 该接口的代理对象，对象实际调用oshcript内实现
     * @throws Exception 抛出异常
     */
    public <T> T implement(Class<T> baseInterface, T baseObject) throws Exception {
        Object proxyObj = this.dryRun();
        // if it is already the T type, return it directly
        if (baseInterface.isAssignableFrom(proxyObj.getClass())) {
            return (T) proxyObj;
        }
        return Tool.createOhProxy(baseObject, (Map<String, ReturnValue>) proxyObj, this);
    }

    /**
     * 创建一个代理对象，该对象实现了指定的接口，并且该对象的行为由ohscript脚本决定。
     * 如果ohscript脚本没有实现某个方法，那么代理对象将会调用接口的默认实现。
     *
     * @param object 要代理的对象，如果ohscript脚本没有实现某个方法，那么该方法将会调用这个对象的默认实现
     * @param clazz 要创建的代理对象实现的接口
     * @param <T> 接口类型
     * @return 返回创建的代理对象
     * @throws Exception 如果创建过程中出现错误，将会抛出此异常
     */
    public <T> T createOhProxy(T object, Class<T> clazz) throws Exception {
        Object proxyObj = this.eval();
        if (object == null) {
            return Tool.createOhProxy(clazz, (Map<String, ReturnValue>) proxyObj, this);
        } else {
            return Tool.createOhProxy(object, (Map<String, ReturnValue>) proxyObj, this);
        }
    }

    /**
     * 创建一个代理对象，该对象实现了指定的接口，并且该对象的行为由ohscript脚本决定。
     * 如果ohscript脚本没有实现某个方法，那么代理对象将会调用接口的默认实现。
     *
     * @param object 要代理的对象，如果ohscript脚本没有实现某个方法，那么该方法将会调用这个对象的默认实现
     * @param <T> 接口类型
     * @return 返回创建的代理对象
     * @throws Exception 如果创建过程中出现错误，将会抛出此异常
     */
    public <T> T createOhProxy(T object) throws Exception {
        return this.createOhProxy(object, null);
    }

    /**
     * 创建一个代理对象，该对象实现了指定的接口，并且该对象的行为由ohscript脚本决定。
     * 如果ohscript脚本没有实现某个方法，那么代理对象将会调用接口的默认实现。
     *
     * @param clazz 要创建的代理对象实现的接口
     * @param <T> 接口类型
     * @return 返回创建的代理对象
     * @throws Exception 如果创建过程中出现错误，将会抛出此异常
     */
    public <T> T createOhProxy(Class<T> clazz) throws Exception {
        return this.createOhProxy(null, clazz);
    }

    /**
     * 更新语法树，使用新的执行参数。
     *
     * @param args 表示新的执行参数的 {@link Object} 数组。
     * @throws OhPanic 如果在更新过程中出现错误，将会抛出此异常。
     */
    private void updateAst(Object[] args) throws OhPanic {
        if (args == null || args.length == 1 && args[0] == null) {
            return;
        }
        this.inputArgs(args);
    }

    /**
     * 将新的执行参数添加到语法树中。
     * 这个方法将新的执行参数添加到语法树的根激活上下文中，并且将这些参数作为一个数组存储。
     * 这个方法用于在执行过程中更新语法树的参数。
     *
     * @param args 表示新的执行参数的 {@link Object} 数组。
     * @throws OhPanic 如果在更新过程中出现错误，将会抛出此异常。
     */
    public void inputArgs(Object... args) throws OhPanic {
        List<ReturnValue> argsValue = new ArrayList<>();
        TerminalNode argId = new TerminalNode(Terminal.ID);
        argId.setToken(new Token(Terminal.ID, Constants.ARGS, 1, 1, 1));
        argId.setAst(this.ast);
        if (this.ast.symbolTable().getSymbol(argId.lexeme(), this.root.scope()) == null) {
            this.ast.symbolTable().getScope(this.root.scope()).addIdentifier(argId, false);
        }
        this.root.put(argId, new ReturnValue(this.root, new ArrayTypeExpr(null), argsValue));
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            EntityDeclareNode mockNode = this.ast.mockExternalOh("arg" + i, arg).first();
            mockNode.setAst(this.ast);
            this.ast.optimize(mockNode);
            this.ast.symbolize(mockNode, true, true);
            ReturnValue argValue = mockNode.interpret(this, this.rootContext());
            argsValue.add(argValue);
        }
    }

    /**
     * 获取 ASF 环境。
     *
     * @return 表示 ASF 环境的 {@link ASFEnv}。
     */
    public ASFEnv asfEnv() {
        return this.asfEnv;
    }

    /**
     * 获取抽象语法树（AST）。
     *
     * @return 表示抽象语法树（AST）的 {@link AST}。
     */
    public AST ast() {
        return this.ast;
    }

    /**
     * 添加一个导出值。
     * 这些值可以在执行过程中被导出，以供其他部分使用
     *
     * @param node 表示节点的 {@link TerminalNode}。
     * @param value 表示返回值的 {@link ReturnValue}。
     */
    public void addExportValue(TerminalNode node, ReturnValue value) {
        this.addExportValue(node.lexeme(), value);
    }

    /**
     * 添加一个导出值。
     * 这些值可以在执行过程中被导出，以供其他部分使用
     *
     * @param nodeName 表示节点名称的 {@link String}。
     * @param value 表示返回值的 {@link ReturnValue}。
     */
    public void addExportValue(String nodeName, ReturnValue value) {
        this.exportValues.put(nodeName, value);
    }

    /**
     * 获取导出的值。
     * 这些值可以在执行过程中被导出，以供其他部分使用
     *
     * @return 表示导出值的 {@link Map}。
     */
    public Map<String, ReturnValue> exportValues() {
        return this.exportValues;
    }

    /**
     * 获取键值对应的oh对象
     *
     * @param key 表示键的 {@link String}。
     * @return 表示对应的oh对象的 {@link Object}。
     */
    public Object getOh(String key) {
        return this.ohs.get(key);
    }

    /**
     * 将键值对应的oh对象存入oh环境
     *
     * @param key 表示键的 {@link String}。
     * @param oh 表示对应的oh对象的 {@link Object}。
     */
    public void grant(String key, Object oh) {
        this.ohs.put(key, oh);
    }

    /**
     * 获取运行时的语法节点列表。
     * 这些节点表示正在执行的代码片段。
     *
     * @return 表示运行时的语法节点列表的 {@link List}。
     */
    public List<SyntaxNode> runtimeNodes() {
        return this.runtimeNodes;
    }

    /**
     * 获取根激活上下文。
     * 这个上下文在执行过程中作为全局上下文使用。
     *
     * @return 表示根激活上下文的 {@link ActivationContext}。
     */
    public ActivationContext rootContext() {
        return this.root;
    }
}


