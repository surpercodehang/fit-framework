/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser;

import static modelengine.fitframework.util.ObjectUtils.cast;

import lombok.SneakyThrows;
import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.errors.ScriptExecutionException;
import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.ActivationContext;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.script.parser.nodes.DoubleFunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityBodyNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.ExternalDataNode;
import modelengine.fit.ohscript.script.parser.nodes.ImportNode;
import modelengine.fit.ohscript.script.parser.nodes.InitialAssignmentNode;
import modelengine.fit.ohscript.script.parser.nodes.LetStatementNode;
import modelengine.fit.ohscript.script.parser.nodes.NonTerminalNode;
import modelengine.fit.ohscript.script.parser.nodes.ScriptNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.SemanticAnalyzer;
import modelengine.fit.ohscript.script.semanticanalyzer.SymbolTable;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fit.ohscript.util.EmptyValue;
import modelengine.fit.ohscript.util.ExternalWrapper;
import modelengine.fit.ohscript.util.OhFrom;
import modelengine.fit.ohscript.util.OhFunction;
import modelengine.fit.ohscript.util.Pair;
import modelengine.fit.ohscript.util.Tool;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AST is a container of node tree
 * when you get start node, you can actually get all abstract syntax tree nodes
 * however, in semantic process, tree node is hard to be located in resolve the reference between different AST
 * therefore, AST will flat tree structure into a map to improve semantic process performance
 * meanwhile, AST also provide access to the tree structure from start()
 *
 * @author 张群辉
 * @since 2023-06-01
 */
public class AST implements Serializable {
    private static final Logger LOG = Logger.get(AST.class);

    private static final long serialVersionUID = 6716195982974341437L;

    private static final Set<String> blackList = new HashSet<>();

    static {
        blackList.add("wait");
        blackList.add("toString");
        blackList.add("hashCode");
        blackList.add("getClass");
        blackList.add("notify");
        blackList.add("notifyAll");
        blackList.add("equals");
    }

    /**
     * start node, only one start node
     * via the start node, all children can be traversed
     */
    private final NonTerminalNode start;

    /**
     * all node flatted to be found easily
     */
    private final SymbolTable symbolTable;

    private final Map<String, ExternalWrapper> externalWrappers = new HashMap<>();

    private ASF asf;

    private List<SyntaxNode> ohs = null;

    /**
     * 是否启用“恐慌模式”, 用于处理严重错误或异常情况
     */
    private boolean panicEnabled = true;

    private boolean initialized = false;

    private final transient OhFunction<Pair<Object, Method>, Object> externalFunction = (host, args, env, current) -> {
        try {
            Method method = host.second();
            Object[] argValues = args.stream().map(ReturnValue::value).toArray();
            if (argValues.length == 1 && argValues[0] == null) {
                argValues = null;
            }
            Object[] matchedArgs = this.matchArgs(method.getParameters(), argValues, env);
            for (int i = 0; i < method.getParameters().length; i++) {
                Parameter p = method.getParameters()[i];
                if (p.getType().isArray()) {
                    ArrayList argList = ObjectUtils.cast(matchedArgs[i]);
                    Object arg = Array.newInstance(p.getType().getComponentType(), argList.size());
                    for (int j = 0; j < argList.size(); j++) {
                        Array.set(arg, j, argList.get(j));
                    }
                    // change arraylist to array
                    matchedArgs[i] = arg;
                }
            }
            Object obj = host.first();
            method.setAccessible(true);
            Object value = method.invoke(obj, matchedArgs);
            return mockReturnValue(env, value, current);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof OhPanic) {
                throw (OhPanic) e.getTargetException();
            }
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            return null;
        }
    };

    private final transient OhFunction<Object, Object> fitFunction = (genericableId, args, env, current) -> {
        Object[] argValues = args.stream().map(ReturnValue::value).toArray();
        Object[] matchedArgs = this.matchArgs(argValues, env);
        Object value = env.fitBroker().execute(genericableId.toString(), matchedArgs);
        return mockReturnValue(env, value, current);
    };

    private final transient OhFunction<Pair<String, String>, Object> httpFunction
            = (urlMethodPair, args, env, current) -> {
        Object argValue = this.matchArg((args.get(0)).value(), env);
        Object value = env.httpBroker().execute(urlMethodPair.second(), urlMethodPair.first(), cast(argValue));
        return mockReturnValue(env, value, current);
    };

    /**
     * 构造函数
     *
     * @param start AST的开始节点
     */
    public AST(NonTerminalNode start) {
        this.start = start;
        this.symbolTable = new SymbolTable();
        this.start.setAst(this);
    }

    /**
     * micro an ast node to wrap the java object operations
     * simulate let ext::key = mocked entity
     *
     * @param key the key of let statement
     * @param ohNode the right node of let statement
     * @return the new created let statement
     */
    private static LetStatementNode createLetOh(String key, SyntaxNode ohNode) {
        LetStatementNode let = new LetStatementNode();
        let.addChild(new TerminalNode(Terminal.LET));
        TerminalNode left = new TerminalNode(Terminal.ID);
        left.setToken(new Token(Terminal.ID, key, 0, 0, 0));
        InitialAssignmentNode initial = InitialAssignmentNode.mock(left, ohNode);
        let.addChild(initial);
        return let;
    }

    /**
     * 获取外部函数
     *
     * @return 外部函数
     */
    public OhFunction<Pair<Object, Method>, Object> externalFunction() {
        return this.externalFunction;
    }

    private void matchArrayArg(Object[] args, int i, List argValue) {
        List<ReturnValue> list = argValue;
        args[i] = new ArrayList<>();
        for (ReturnValue item : list) {
            (ObjectUtils.<List>cast(args[i])).add(item.value());
        }
    }

    private void matchMapArg(Object[] args, int i, Map<String, ReturnValue> map) {
        args[i] = new HashMap<String, Object>();
        for (String key : map.keySet()) {
            ((Map<String, Object>) args[i]).put(key, map.get(key).value());
        }
    }

    private void matchEntityArg(Parameter[] parameters, Object[] args, int i, Map<String, ReturnValue> map) {
        try {
            if (parameters[i].getType() == Object.class) {
                args[i] = map;
            } else {
                Class<?> clazz = (Class<?>) parameters[i].getParameterizedType();
                args[i] = Tool.createInstance(clazz);
                matchEntityFields(map, args[i]);
            }
        } catch (Exception e) {
            args[i] = map;
            throw new ScriptExecutionException(e);
        }
    }

    /**
     * 匹配实体字段
     *
     * @param map 字段值映射
     * @param entity 实体对象
     */
    public void matchEntityFields(Map<String, ReturnValue> map, Object entity) {
        Class<?> clazz = entity.getClass();
        for (String key : map.keySet()) {
            if (!".".equals(key.substring(0, 1))) {
                continue;
            }
            // get field name
            String k = key.substring(1);
            // get field meta data
            try {
                Field field = clazz.getDeclaredField(k);
                // get field value
                ReturnValue value = map.get(key);
                field.setAccessible(true);
                field.set(entity, matchFieldValue(field, value.value()));
            } catch (Exception e) {
                LOG.debug("matchEntityFields error", e);
            }
        }
    }

    private Object matchFieldValue(Field field, Object value) throws Exception {
        Class<?> fieldType = field.getType();
        if (Map.class.isAssignableFrom(fieldType)) {
            return matchMapFieldValue(field.getGenericType(), (Map<String, ReturnValue>) value);
        } else if (List.class.isAssignableFrom(fieldType)) {
            return matchListFieldValue(field.getGenericType(), (List<ReturnValue>) value);
        } else if (Set.class.isAssignableFrom(fieldType)) {
            return matchSetFieldValue(field.getGenericType(), (List<ReturnValue>) value);
        } else if (Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive()) {
            return matchNumberFieldValue(fieldType, value);
        } else if (String.class.isAssignableFrom(fieldType)) {
            return value;
        } else {
            return matchEntityFieldValue(fieldType, (Map<String, ReturnValue>) value);
        }
    }

    private Map matchMapFieldValue(Type genericType, Map<String, ReturnValue> values) throws Exception {
        Type typeValue = ObjectUtils.<ParameterizedType>cast(genericType).getActualTypeArguments()[1];
        Map map = new HashMap<>();
        for (String k : values.keySet()) {
            map.put(k, matchItem((Class<?>) typeValue, values.get(k).value()));
        }
        return map;
    }

    private List matchListFieldValue(Type genericType, List<ReturnValue> values) throws Exception {
        Type itemType = ObjectUtils.<ParameterizedType>cast(genericType).getActualTypeArguments()[0];
        List list = new ArrayList<>();
        for (ReturnValue value : values) {
            list.add(matchItem(ObjectUtils.cast(itemType), value.value()));
        }
        return list;
    }

    private Set matchSetFieldValue(Type genericType, List<ReturnValue> values) throws Exception {
        Type itemType = ObjectUtils.<ParameterizedType>cast(genericType).getActualTypeArguments()[0];
        Set set = new HashSet<>();
        for (ReturnValue value : values) {
            set.add(matchItem((Class<?>) itemType, value.value()));
        }
        return set;
    }

    private Object matchEntityFieldValue(Class<?> fieldType, Map<String, ReturnValue> value) throws Exception {
        Object obj = Tool.createInstance(fieldType);
        matchEntityFields(value, obj);
        return obj;
    }

    private Object matchItem(Class<?> itemType, Object value) throws Exception {
        if (String.class.isAssignableFrom(itemType)) {
            return value;
        }
        if (Number.class.isAssignableFrom(itemType) || itemType.isPrimitive()) {
            return matchNumberFieldValue(itemType, value);
        }

        Object item = Tool.createInstance(itemType);
        matchEntityFields((Map<String, ReturnValue>) value, item);
        return item;
    }

    private Object matchNumberFieldValue(Class<?> fieldType, Object value) {
        if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
            return Integer.valueOf(value.toString());
        } else if (Float.class.equals(fieldType) || float.class.equals(fieldType)) {
            return Float.valueOf(value.toString());
        } else if (Double.class.equals(fieldType) || double.class.equals(fieldType)) {
            return Double.valueOf(value.toString());
        } else if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
            return Long.valueOf(value.toString());
        } else if (Short.class.equals(fieldType) || short.class.equals(fieldType)) {
            return Short.valueOf(value.toString());
        } else if (Byte.class.equals(fieldType) || byte.class.equals(fieldType)) {
            return Byte.valueOf(value.toString());
        } else if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
            return Boolean.valueOf(value.toString());
        } else {
            return value;
        }
    }

    /**
     * 初始化AST
     *
     * @param inTransaction 是否在事务中
     */
    public void init(boolean inTransaction) {
        loadOhs();
        optimize(start);
        symbolize(start, true, false);
        symbolize(start, false, !inTransaction);
        this.initialized = true;
    }

    /**
     * 优化AST
     *
     * @param node 需要进行优化的节点
     */
    public void optimize(SyntaxNode node) {
        optimizeBeta(optimizeAlpha(node));
        optimizeGama(node);
        optimizeDelta(node);
    }

    /**
     * 检查AST是否已经初始化
     *
     * @return 如果已经初始化，返回true，否则返回false
     */
    public boolean initialized() {
        return this.initialized;
    }

    /**
     * 符号化AST
     *
     * @param start 父节点
     * @param needSymbolize 是否需要进行符号化
     * @param panicEnable 是否抛出不可恢复错误
     */
    public void symbolize(NonTerminalNode start, boolean needSymbolize, boolean panicEnable) {
        List<SyntaxNode> nodes = new ArrayList<>();
        nodes.add(start);
        this.panicEnabled = panicEnable;
        this.symbolize(null, nodes, needSymbolize);
    }

    private void symbolize(SyntaxNode parent, List<SyntaxNode> nodes, boolean needSymbolize) {
        if (!nodes.isEmpty()) {
            List<Pair<SyntaxNode, List<SyntaxNode>>> children = new ArrayList<>();
            nodes.forEach(node -> {
                if (needSymbolize) {
                    SemanticAnalyzer.trySymbolize(node);
                }
                children.add(new Pair<>(node, node.childrenNeedsInfer()));
            });
            children.forEach(pair -> this.symbolize(pair.first(), pair.second(), needSymbolize));
        }
        if (parent != null) {
            parent.initTypeExpr(SemanticAnalyzer.tryTypeInfer(parent));
        }
    }

    private void loadOhs() {
        if (this.ohs != null) {
            return;
        }
        this.ohs = new ArrayList<>();
        this.asf().fitOhs().forEach((alias, pair) -> this.addFitOh(alias, pair.first(), pair.second()));
        this.asf().httpOhs().forEach((alias, pair) -> this.addFHttpOh(alias, pair.first(), pair.second()));
        this.asf().externalOhs().forEach(this::addExternalOh);
    }

    /**
     * 获取导入声明节点列表
     *
     * @return 导入声明节点列表
     */
    public List<ImportNode> imports() {
        for (SyntaxNode child : this.start().children()) {
            if (child.nodeType() == NonTerminal.IMPORT_DECLARES) {
                return child.children().stream().map(ObjectUtils::<ImportNode>cast).collect(Collectors.toList());
            }
            if (child.nodeType() == NonTerminal.IMPORT_DECLARE) {
                List<ImportNode> imports = new ArrayList<>();
                imports.add(ObjectUtils.cast(child));
                return imports;
            }
        }
        return new ArrayList<>();
    }

    /**
     * 获取导出的变量列表
     *
     * @return 导出的变量列表
     */
    public List<TerminalNode> exports() {
        List<TerminalNode> exports = new ArrayList<>();
        this.start()
                .children()
                .stream()
                .filter(s -> s.nodeType() == NonTerminal.EXPORT_DECLARE)
                .forEach(export -> exports.addAll(export.children()
                        .stream()
                        .filter(e -> e.nodeType() == Terminal.ID)
                        .map(ObjectUtils::<TerminalNode>cast)
                        .collect(Collectors.toList())));
        return exports;
    }

    /**
     * 替换external的oh对象
     *
     * @param key 对象的key
     * @param object 对象
     */
    public void replaceExternalOh(String key, Object object) {
        this.externalWrappers.get(key).setObject(object);
    }

    /**
     * 优化Delta，对AST进行深度优化
     *
     * @param node 需要优化的节点
     */
    public void optimizeDelta(SyntaxNode node) {
        node.optimizeDelta();
        node.children().forEach(this::optimizeDelta);
    }

    private void optimizeBeta(List<SyntaxNode> nodes) {
        nodes.forEach(SyntaxNode::optimizeBeta);
    }

    /**
     * 优化Gamma，对AST进行深度优化
     *
     * @param node 需要优化的节点
     */
    public void optimizeGama(SyntaxNode node) {
        node.optimizeGama();
        node.children().forEach(this::optimizeGama);
    }

    /**
     * 语义检查
     *
     * @param node 需要进行语义检查的节点
     */
    public void semanticCheck(SyntaxNode node) {
        node.semanticCheck();
    }

    /**
     * 获取符号表
     *
     * @return 符号表
     */
    public SymbolTable symbolTable() {
        return this.symbolTable;
    }

    /**
     * preOrder traverse all nodes to make a list
     * invoke non-terminal node.optimizeAlpha
     *
     * @param node parent node
     * @return list
     */
    private List<SyntaxNode> optimizeAlpha(SyntaxNode node) {
        List<SyntaxNode> nodes = new ArrayList<>();
        nodes.add(node);
        node.optimizeAlpha();
        node.children().forEach(c -> nodes.addAll(this.optimizeAlpha(c)));
        return nodes.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 获取开始节点，这是第一个被放入栈中以开始解析的节点
     *
     * @return 返回值必须是一个脚本节点
     */
    public NonTerminalNode start() {
        return this.start;
    }

    /**
     * 获取源代码
     *
     * @return 源代码
     */
    public String source() {
        return (ObjectUtils.<ScriptNode>cast(this.start)).source();
    }

    /**
     * 解释AST并返回结果
     *
     * @param env 环境变量
     * @return 返回解释结果
     * @throws OhPanic 如果发生严重错误或异常，抛出OhPanic
     */
    public Object interpret(ASTEnv env) throws OhPanic {
        ReturnValue result = this.start().interpret(env, env.rootContext());
        return result.value();
    }

    /**
     * 获取语法错误
     *
     * @return 返回一个映射，映射的键是出现错误的节点，值是一个包含错误信息和错误提示的对
     */
    public Map<SyntaxNode, Pair<SyntaxError, String>> errors() {
        return this.start().error();
    }

    /**
     * 设置ASF对象
     *
     * @param asf ASF对象
     */
    public void setASF(ASF asf) {
        this.asf = asf;
        this.symbolTable().addScope(asf.scope());
        this.symbolTable().addScope(0, asf.scope().id());
    }

    /**
     * 获取ASF对象
     *
     * @return ASF对象
     */
    public ASF asf() {
        return this.asf;
    }

    /**
     * 添加一个fit oh
     *
     * @param alias oh的别名
     * @param fitName fit函数名
     * @param argNum 函数参数数量
     */
    public void addFitOh(String alias, String fitName, Integer argNum) {
        DoubleFunctionDeclareNode func = new DoubleFunctionDeclareNode(String.valueOf(Tool.newId()), argNum,
                this.fitFunction, TypeExprFactory.createUnknown());
        func.setHostValue(fitName);
        LetStatementNode let = createLetOh(OhFrom.FIT.ohName() + alias, func);
        this.ohs.add(let);
    }

    /**
     * 添加一个http oh
     *
     * @param alias oh的别名
     * @param url http请求的url
     * @param method http请求的方法
     */
    public void addFHttpOh(String alias, String url, String method) {
        DoubleFunctionDeclareNode func = new DoubleFunctionDeclareNode(String.valueOf(Tool.newId()), 1,
                this.httpFunction, TypeExprFactory.createUnknown());
        func.setHostValue(new Pair<>(url, method));
        LetStatementNode let = createLetOh(OhFrom.HTTP.ohName() + alias, func);
        this.ohs.add(let);
    }

    /**
     * add a java object into ohscript
     * ohscript grammar is able to invoke the java object
     *
     * @param key java object name, given by user
     * @param oh java object reference
     */
    public void addExternalOh(String key, Object oh) {
        Pair<EntityDeclareNode, ExternalWrapper> entity = mockExternalOh(key, oh);
        LetStatementNode let = createLetOh(OhFrom.EXT.ohName() + key, entity.first());
        this.externalWrappers.put(key, entity.second());
        this.ohs.add(let);

        TerminalNode cloned = new TerminalNode(Terminal.ID);
        cloned.setToken(new Token(Terminal.ID, OhFrom.EXT.ohName() + key, 0, 0, 0));
        LetStatementNode letSimple = createLetOh(key, cloned);
        this.ohs.add(letSimple);
    }

    /**
     * mock一个return值
     *
     * @param env astEnv
     * @param value 值
     * @param current 上下文
     * @return mock的对象
     */
    public Object mockReturnValue(ASTEnv env, Object value, ActivationContext current) {
        Object newValue = value;
        if (newValue instanceof String || newValue instanceof Number || newValue instanceof Boolean
                || newValue == null) {
            return newValue;
        }
        if (newValue.getClass().isArray()) {
            Object[] old = (Object[]) newValue;
            newValue = new ArrayList<>();
            for (Object item : old) {
                ((ArrayList<Object>) newValue).add(item);
            }
        }
        if (newValue instanceof Map) {
            Map<String, ReturnValue> map = (Map<String, ReturnValue>) newValue;
            if (map.size() > 0 && map.keySet().toArray(new String[1])[0].indexOf(".") == 0) { // it is an object
                return newValue;
            }
        }
        Map<String, ReturnValue> object = new HashMap<>();
        for (Method method : newValue.getClass().getMethods()) {
            if (method.getModifiers() != 1) {
                continue;
            }
            if (blackList.contains(method.getName())) {
                continue;
            }
            String name = method.getName();

            // simply the wrapper function for next layer call.
            // this node will isolate without looping in ast
            DoubleFunctionDeclareNode doubleFunc = new DoubleFunctionDeclareNode(name, method.getParameterCount(),
                    this.externalFunction, TypeExprFactory.createUnknown());
            doubleFunc.setHostValue(new Pair<>(newValue, method));
            doubleFunc.setAst(this, env);
            object.put("." + name, new ReturnValue(current, doubleFunc.typeExpr(), doubleFunc));
        }
        object.put(Constants.EXTERNAL_VALUE, new ReturnValue(current, TypeExprFactory.createIgnore(), newValue));
        return object;
    }

    private Object matchArg(Object argValue, ASTEnv env) {
        Object arg = argValue;
        if (argValue instanceof Map && ((Map<String, ReturnValue>) argValue).containsKey(Constants.EXTERNAL_VALUE)) {
            arg = ((Map<String, ReturnValue>) argValue).get(Constants.EXTERNAL_VALUE).value();
        }
        if (arg instanceof ExternalWrapper) {
            return ((ExternalWrapper) arg).object(env);
        } else {
            return arg;
        }
    }

    private Object[] matchArgs(Object[] argValues, ASTEnv env) {
        if (argValues == null) {
            return new Object[0];
        }
        Object[] args = new Object[argValues.length];
        for (int i = 0; i < argValues.length; i++) {
            args[i] = this.matchArg(argValues[i], env);
        }
        return args;
    }

    /**
     * 匹配参数
     *
     * @param parameters 方法参数
     * @param argValues 参数值
     * @param env 环境变量
     * @return 匹配后的参数
     */
    @SneakyThrows
    private Object[] matchArgs(Parameter[] parameters, Object[] argValues, ASTEnv env) {
        if (argValues == null) {
            return new Object[0];
        }
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < argValues.length; i++) {
            this.handleArg(parameters, argValues, env, i, args);
        }
        return args;
    }

    private void handleArg(Parameter[] parameters, Object[] argValues, ASTEnv env, int i, Object[] args) {
        Object argValue = argValues[i];
        if (argValue instanceof Map) {
            Map<String, ReturnValue> map = (Map<String, ReturnValue>) argValue;
            if (map.containsKey(Constants.EXTERNAL_VALUE)) {
                args[i] = map.get(Constants.EXTERNAL_VALUE).value();
            } else {
                if (map.keySet().stream().anyMatch(k -> k.startsWith(Constants.DOT))) { // it is an object
                    matchEntityArg(parameters, args, i, map);
                } else { // it is a map
                    matchMapArg(args, i, map);
                }
            }
        } else if (argValue instanceof List) {
            matchArrayArg(args, i, (List) argValue);
        } else {
            if (!(argValue instanceof EmptyValue)) {
                args[i] = argValue;
            }
        }
        if (i < args.length && args[i] instanceof ExternalWrapper) {
            args[i] = ((ExternalWrapper) args[i]).object(env);
        }
    }

    /**
     * create real entity wrapper for external object
     *
     * @param key key
     * @param externalOh the external object to be wrapped
     * @return pair
     */
    public Pair<EntityDeclareNode, ExternalWrapper> mockExternalOh(String key, Object externalOh) {
        Set<String> whiteList = new HashSet<>();
        if (externalOh instanceof Map) {
            whiteList.add("get");
            whiteList.add("put");
            whiteList.add("size");
            whiteList.add("isEmpty");
        }
        boolean isList = externalOh instanceof List;
        if (isList) {
            whiteList.add("get");
            whiteList.add("add");
            whiteList.add("size");
            whiteList.add("isEmpty");
            whiteList.add("remove");
        }
        EntityDeclareNode entity = EntityDeclareNode.mock();
        ExternalWrapper wrapper = new ExternalWrapper(key, externalOh);
        Class clazz = externalOh.getClass();
        Map<String, Integer> names = new HashMap<>();
        Map<Method, DoubleFunctionDeclareNode> methods = new HashMap<>();
        for (Method method : clazz.getMethods()) {
            if (method.getModifiers() != Modifier.PUBLIC) {
                continue;
            }
            if (!whiteList.isEmpty() && !whiteList.contains(method.getName())) {
                continue;
            }
            // 兼容处理list的add方法重载问题
            if (isList && method.getParameterCount() == 2 && "add".equals(method.getName())) {
                continue;
            }
            if (blackList.contains(method.getName())) {
                continue;
            }
            String name = method.getName();
            if (names.containsKey(name)) {
                names.put(name, names.get(name) + 1);
                name += names.get(name);
            } else {
                names.put(name, 0);
            }
            DoubleFunctionDeclareNode doubleFunc = new DoubleFunctionDeclareNode(name, method.getParameterCount(),
                    externalFunction, TypeExprFactory.createGeneric(null));
            doubleFunc.setHostValue(new Pair<>(wrapper, method));
            this.mockEntityMethod(entity, doubleFunc);
            methods.put(method, doubleFunc);
        }
        this.mockEntityField(entity, Constants.EXTERNAL_VALUE.substring(1), new ExternalDataNode(wrapper));
        return new Pair<>(entity, wrapper);
    }

    private void mockEntityMethod(EntityDeclareNode entity, DoubleFunctionDeclareNode right) {
        EntityBodyNode body = ObjectUtils.cast(entity.child(1));
        body.addChild(new TerminalNode(Terminal.DOT), body.childCount() - 1);
        TerminalNode left = new TerminalNode(Terminal.ID);
        left.setToken(new Token(Terminal.ID, right.functionName().lexeme(), 0, 0, 0));
        InitialAssignmentNode initial = InitialAssignmentNode.mock(left, right);
        body.addChild(initial, body.childCount() - 1);
        body.addChild(new TerminalNode(Terminal.SEMICOLON), body.childCount() - 1);
    }

    private void mockEntityField(EntityDeclareNode entity, String name, SyntaxNode node) {
        EntityBodyNode body = ObjectUtils.cast(entity.child(1));
        body.addChild(new TerminalNode(Terminal.DOT), body.childCount() - 1);
        TerminalNode left = new TerminalNode(Terminal.ID);
        left.setToken(new Token(Terminal.ID, name, 0, 0, 0));
        InitialAssignmentNode initial = InitialAssignmentNode.mock(left, node);
        body.addChild(initial, body.childCount() - 1);
        body.addChild(new TerminalNode(Terminal.SEMICOLON), body.childCount() - 1);
    }

    /**
     * 获取oh脚本节点列表
     *
     * @return oh脚本节点列表
     */
    public List<SyntaxNode> ohs() {
        return this.ohs;
    }

    /**
     * 获取“恐慌模式”是否启用
     *
     * @return 如果启用，返回true，否则返回false
     */
    public boolean panicEnabled() {
        return this.panicEnabled;
    }
}