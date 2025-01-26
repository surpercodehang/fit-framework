/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.errors.ScriptExecutionException;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.ActivationContext;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts.GenericTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.ArrayTypeExpr;
import modelengine.fit.ohscript.util.EmptyValue;
import modelengine.fit.ohscript.util.OhFunction;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 脚本节点
 *
 * @since 1.0
 */
public class ScriptNode extends NonTerminalNode {
    // -----------------for map system methods----------------------
    private static final OhFunction<Map, Object> PUT = (host, value, env, context) -> host.put(value.get(0).value(),
            value.get(1));

    private static final OhFunction<Map, Object> GET = (host, value, env, context) -> {
        return host.get(value.get(0).value());
    };

    private static final OhFunction<ArrayList<ReturnValue>, Object> FOR_EACH = (host, value, env, context) -> {
        FunctionDeclareNode function = ObjectUtils.cast(value.get(0).value());
        for (ReturnValue item : host) {
            FunctionCallNode call = createFuncCall(function, item);
            call.interpret(env, context);
        }
        return null;
    };

    private static final OhFunction<ArrayList<ReturnValue>, Object> PARALLEL = (host, value, env, context) -> {
        FunctionDeclareNode function = ObjectUtils.cast(value.get(0).value());
        host.parallelStream().forEach(item -> {
            FunctionCallNode call = createFuncCall(function, item);
            try {
                call.interpret(env, context);
            } catch (OhPanic e) {
                throw new ScriptExecutionException(e);
            }
        });
        return null;
    };

    private static final OhFunction<ArrayList<ReturnValue>, List> MAP = (host, value, env, context) -> {
        FunctionDeclareNode function = ObjectUtils.cast(value.get(0).value());
        List result = new ArrayList();
        for (ReturnValue item : host) {
            FunctionCallNode call = createFuncCall(function, item);
            result.add(call.interpret(env, context));
        }
        return result;
    };

    private static final OhFunction<ArrayList<ReturnValue>, Boolean> IS_ARRAY_EMPTY = (host, value, env, context) ->
            host.isEmpty();

    private static final OhFunction<ArrayList<ReturnValue>, List> FILTER = (host, value, env, context) -> {
        FunctionDeclareNode function = ObjectUtils.cast(value.get(0).value());
        List result = new ArrayList();
        for (ReturnValue item : host) {
            FunctionCallNode call = createFuncCall(function, item);
            ReturnValue v = call.interpret(env, context);
            if (ObjectUtils.cast(v.value())) {
                result.add(item);
            }
        }
        return result;
    };

    // ------------------------for string system methods------------------------------
    private static final OhFunction<String, Object> SUB_STR = (host, value, env, context) -> host.substring(
            ObjectUtils.cast(value.get(0).value()), ObjectUtils.cast(value.get(1).value()));

    private static final OhFunction<String, Object> REPLACE = (host, value, env, context) -> host.replace(
            ObjectUtils.cast(value.get(0).value()), ObjectUtils.cast(value.get(1).value()));

    private static final OhFunction<String, Object> LEN = (host, value, env, context) -> host.length();

    private static final OhFunction<String, Object> IS_EMPTY = (host, value, env, context) -> host.isEmpty();

    private static final OhFunction<String, Object> UPPER = (host, value, env, context) -> host.toUpperCase();

    private static final OhFunction<String, Object> LOWER = (host, value, env, context) -> host.toLowerCase();

    private static final OhFunction<String, Object> START_WITH = (host, value, env, context) -> host.startsWith(
            ObjectUtils.cast(value.get(0).value()));

    private static final OhFunction<String, Object> END_WITH = (host, value, env, context) -> host.endsWith(
            ObjectUtils.cast(value.get(0).value()));

    private static final OhFunction<String, Object> INDEX_OF = (host, value, env, context) -> host.indexOf(
            ObjectUtils.cast(value.get(0).value()));

    private static final OhFunction<String, Object> TRIM = (host, value, env, context) -> host.trim();

    private static final OhFunction<String, Object> CONTAINS = (host, value, env, context) -> host.contains(
            ObjectUtils.cast(value.get(0).value()));

    private static final OhFunction<String, Number> TO_NUM = (host, value, env, context) -> {
        if (host.contains(".")) {
            return Double.valueOf(host);
        } else {
            return Long.valueOf(host);
        }
    };

    private static final OhFunction<String, Boolean> IS_NUM = (host, value, env, context) -> {
        try {
            TO_NUM.apply(host, value, env, context);
            return true;
        } catch (Exception e) {
            return false;
        }
    };

    private static final OhFunction<String, List> SPLIT = (host, value, env, context) -> {
        List result = new ArrayList();
        for (String part : host.split(ObjectUtils.cast(value.get(0).value()))) {
            result.add(new ReturnValue(context, TypeExprFactory.createString(env.ast().start()), part));
        }
        return result;
    };

    // -----------------for number system methods---------------------------------
    private static final OhFunction<Number, Object> TO_INT = (host, value, env, context) -> host.longValue();

    private static final OhFunction<Number, Object> TO_FLOAT = (host, value, env, context) -> host.doubleValue();

    private static final OhFunction<Number, Object> TO_STRING = (host, value, env, context) -> host.toString();

    private static final OhFunction<Number, Object> CEIL = (host, value, env, context) -> {
        if (value.get(0).value() == EmptyValue.IGNORE) {
            return (ObjectUtils.<Number>cast(Math.ceil(host.doubleValue()))).longValue();
        }
        long base = Math.round(Math.pow(10, (int) value.get(0).value()));
        return Math.ceil(host.doubleValue() * base) / base;
    };

    private static final OhFunction<Number, Object> FLOOR = (host, value, env, context) -> {
        if (value.get(0).value() == EmptyValue.IGNORE) {
            return (ObjectUtils.<Number>cast(Math.floor(host.doubleValue()))).longValue();
        }
        long base = Math.round(Math.pow(10, (int) value.get(0).value()));
        return Math.floor(host.doubleValue() * base) / base;
    };

    private static final OhFunction<Number, Object> ROUND = (host, value, env, context) -> {
        if (value.get(0).value() == EmptyValue.IGNORE) {
            return Math.round(host.doubleValue());
        }
        long base = Math.round(Math.pow(10, (int) value.get(0).value()));
        return (ObjectUtils.<Number>cast(Math.round(host.doubleValue() * base))).doubleValue() / base;
    };

    private final Map<String, Map<String, DoubleFunctionDeclareNode>> methods = new HashMap<>();

    private String source;

    /**
     * 构造函数
     */
    public ScriptNode() {
        super(NonTerminal.SCRIPT);
    }

    // -----------------for array system methods----------------------------
    private static FunctionCallNode createFuncCall(FunctionDeclareNode function, ReturnValue item) {
        final ReturnValue arg = item;
        return new FunctionCallNode() {
            @Override
            public SyntaxNode functionName() {
                return function;
            }

            @Override
            public List<SyntaxNode> children() {
                SyntaxNode name = this.functionName();
                SyntaxNode child = buildChild(name, arg);
                List<SyntaxNode> children = new ArrayList<>();
                children.add(child);
                children.add(function);
                return children;
            }
        };
    }

    private static SyntaxNode buildChild(SyntaxNode name, ReturnValue arg) {
        return new SyntaxNode() {
            @Override
            public Terminal nodeType() {
                return Terminal.UNKNOWN;
            }

            @Override
            public Location location() {
                return name.location();
            }

            @Override
            public ReturnValue interpret(ASTEnv env, ActivationContext current) {
                return arg;
            }

            @Override
            public String lexeme() {
                return "";
            }
        };
    }

    private static void addMapMethods(ScriptNode start) {
        addMapPutMethod(start);
        addMapGetMethod(start);
    }

    private static void addMapPutMethod(ScriptNode start) {
        addMapMethod(start, new MethodInfo(".put", 2, PUT, TypeExprFactory.createIgnore()));
    }

    private static void addMapGetMethod(ScriptNode start) {
        addMapMethod(start,
                new MethodInfo(".get", 1, GET, TypeExprFactory.createGeneric(new TerminalNode(Terminal.ID))));
    }

    private static void addMapMethod(ScriptNode start, MethodInfo methodInfo) {
        addMethod(start, Type.MAP.name(), methodInfo);
    }

    private static void addArrayMethods(ScriptNode start) {
        addArrayForEachMethod(start);
        addArrayParallelMethod(start);
        addArrayMapMethod(start);
        addArrayFilterMethod(start);
        addArrayIsEmptyMethod(start);
    }

    private static void addArrayForEachMethod(ScriptNode start) {
        String foreach = ".forEach";
        DoubleFunctionDeclareNode forEachFunc = new DoubleFunctionDeclareNode<>(foreach + "~" + Type.ARRAY.name(), 1,
                FOR_EACH, TypeExprFactory.createIgnore());
        forEachFunc.setProjectFunction(node -> {
            TypeExpr itemType = (ObjectUtils.<ArrayTypeExpr>cast(
                    (ObjectUtils.<EntityCallNode>cast((ObjectUtils.<SyntaxNode>cast(node)).child(1))).entity()
                            .typeExpr())).itemTypeExpr();
            new GenericTypeExpr(itemType.node());
            return null; // 注释：new FunctionTypeExpr(forEachFunc,argType,)
        });
        start.addMethod(Type.ARRAY.name(), foreach, forEachFunc);
    }

    private static void addArrayParallelMethod(ScriptNode start) {
        String parallel = ".parallel";
        DoubleFunctionDeclareNode parallelFunc = new DoubleFunctionDeclareNode<>(parallel + "~" + Type.ARRAY.name(), 1,
                PARALLEL, TypeExprFactory.createIgnore());
        parallelFunc.setProjectFunction(node -> {
            TypeExpr itemType = (ObjectUtils.<ArrayTypeExpr>cast(
                    (ObjectUtils.<EntityCallNode>cast((ObjectUtils.<SyntaxNode>cast(node)).child(1))).entity()
                            .typeExpr())).itemTypeExpr();
            new GenericTypeExpr(itemType.node());
            return null; // 注释：new FunctionTypeExpr(forEachFunc,argType,)
        });
        start.addMethod(Type.ARRAY.name(), parallel, parallelFunc);
    }

    private static void addArrayMapMethod(ScriptNode start) {
        addArrayMethod(start, new MethodInfo(".map", 0, MAP, TypeExprFactory.createArray(new ArrayDeclareNode())));
    }

    private static void addArrayIsEmptyMethod(ScriptNode start) {
        addArrayMethod(start,
                new MethodInfo(".isEmpty", 0, IS_ARRAY_EMPTY, TypeExprFactory.createArray(new ArrayDeclareNode())));
    }

    private static void addArrayFilterMethod(ScriptNode start) {
        addArrayMethod(start,
                new MethodInfo(".filter", 0, FILTER, TypeExprFactory.createArray(new ArrayDeclareNode())));
    }

    private static void addArrayMethod(ScriptNode start, MethodInfo methodInfo) {
        addMethod(start, Type.ARRAY.name(), methodInfo);
    }

    private static void addStringMethods(ScriptNode start) {
        addStringSubStrMethod(start);
        addStringReplaceMethod(start);
        addStringTrimMethod(start);
        addStringLenMethod(start);
        addStringIsEmptyMethod(start);
        addStringIndexOfMethod(start);
        addStringSplitMethod(start);
        addStringUpperMethod(start);
        addStringLowerMethod(start);
        addStringStartsWithMethod(start);
        addStringEndsWithMethod(start);
        addStringToNumMethod(start);
        addStringIsNumMethod(start);
        addStringContainsMethod(start);
    }

    private static void addStringSubStrMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".substr", 2, SUB_STR, TypeExprFactory.createString(start)));
    }

    private static void addStringReplaceMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".replace", 2, REPLACE, TypeExprFactory.createString(start)));
    }

    private static void addStringTrimMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".trim", 0, TRIM, TypeExprFactory.createString(start)));
    }

    private static void addStringLenMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".len", 0, LEN, TypeExprFactory.createNumber(start)));
    }

    private static void addStringIsEmptyMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".is_empty", 0, IS_EMPTY, TypeExprFactory.createBool(start)));
    }

    private static void addStringIndexOfMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".index_of", 1, INDEX_OF, TypeExprFactory.createNumber(start)));
    }

    private static void addStringSplitMethod(ScriptNode start) {
        ArrayTypeExpr splitReturn = TypeExprFactory.createArray(new ArrayDeclareNode());
        splitReturn.setItemTypeExpr(TypeExprFactory.createString(start));
        addStringMethod(start, new MethodInfo(".split", 0, SPLIT, splitReturn));
    }

    private static void addStringUpperMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".upper", 0, UPPER, TypeExprFactory.createString(start)));
    }

    private static void addStringLowerMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".lower", 0, LOWER, TypeExprFactory.createString(start)));
    }

    private static void addStringStartsWithMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".starts_with", 1, START_WITH, TypeExprFactory.createBool(start)));
    }

    private static void addStringEndsWithMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".ends_with", 1, END_WITH, TypeExprFactory.createBool(start)));
    }

    private static void addStringToNumMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".to_num", 0, TO_NUM, TypeExprFactory.createNumber(start)));
    }

    private static void addStringIsNumMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".is_num", 0, IS_NUM, TypeExprFactory.createBool(start)));
    }

    private static void addStringContainsMethod(ScriptNode start) {
        addStringMethod(start, new MethodInfo(".contains", 1, CONTAINS, TypeExprFactory.createBool(start)));
    }

    private static void addStringMethod(ScriptNode start, MethodInfo methodInfo) {
        addMethod(start, Type.STRING.name(), methodInfo);
    }

    private static void addNumberMethods(ScriptNode start) {
        addNumberToIntMethod(start);
        addNumberToFloatMethod(start);
        addNumberFloorMethod(start);
        addNumberCeilMethod(start);
        addNumberRoundMethod(start);
        addNumberToStrMethod(start);
    }

    private static void addNumberToIntMethod(ScriptNode start) {
        addNumberMethod(start, new MethodInfo(".to_int", 0, TO_INT, TypeExprFactory.createNumber(start)));
    }

    private static void addNumberToFloatMethod(ScriptNode start) {
        addNumberMethod(start, new MethodInfo(".to_float", 0, TO_FLOAT, TypeExprFactory.createNumber(start)));
    }

    private static void addNumberFloorMethod(ScriptNode start) {
        addNumberMethod(start, new MethodInfo(".floor", 1, FLOOR, TypeExprFactory.createNumber(start)));
    }

    private static void addNumberCeilMethod(ScriptNode start) {
        addNumberMethod(start, new MethodInfo(".ceil", 1, CEIL, TypeExprFactory.createNumber(start)));
    }

    private static void addNumberRoundMethod(ScriptNode start) {
        addNumberMethod(start, new MethodInfo(".round", 1, ROUND, TypeExprFactory.createNumber(start)));
    }

    private static void addNumberToStrMethod(ScriptNode start) {
        addNumberMethod(start, new MethodInfo(".to_str", 0, TO_STRING, TypeExprFactory.createString(start)));
    }

    private static void addNumberMethod(ScriptNode start, MethodInfo methodInfo) {
        addMethod(start, Type.NUMBER.name(), methodInfo);
    }

    private static void addMethod(ScriptNode start, String type, MethodInfo methodInfo) {
        start.addMethod(type, methodInfo.getMethodName(),
                new DoubleFunctionDeclareNode<>(methodInfo.getMethodName() + "~" + type, methodInfo.getArgNum(),
                        methodInfo.getHandler(), methodInfo.getReturnType()));
    }

    /**
     * 设置源代码
     *
     * @param source 源代码
     * 这个方法用于设置脚本的源代码。源代码是一个字符串，表示脚本的内容。
     * 这个方法将源代码保存在一个私有变量中，以便于后续的解析和执行。
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 获取源代码
     *
     * @return 返回脚本的源代码。源代码是一个字符串，表示脚本的内容。
     * 这个方法从私有变量中获取源代码，并返回给调用者。
     */
    public String source() {
        return this.source;
    }

    @Override
    public void optimizeDelta() {
        super.optimizeDelta();
        ScriptNode start = ObjectUtils.cast(this.ast().start());

        addStringMethods(start);
        addNumberMethods(start);
        addArrayMethods(start);
        addMapMethods(start);
    }

    /**
     * 添加方法
     *
     * @param nodeType 节点类型
     * @param name 方法名
     * @param method 方法
     * 这个方法用于向脚本节点添加系统方法。首先，它会检查是否已经存在同名的方法，如果存在，则不进行任何操作。
     * 如果不存在，则将方法添加到系统节点的子节点中，并将方法添加到methods映射中，以便于后续查找。
     */
    public void addMethod(String nodeType, String name, DoubleFunctionDeclareNode method) {
        if (methods.containsKey(nodeType) && methods.get(nodeType).containsKey(name)) {
            return;
        }
        IgnoredNode system = this.getAndCreateSystemNode();
        system.addChild(method);
        methods.putIfAbsent(nodeType, new HashMap<>());
        methods.get(nodeType).put(name, method);
    }

    /**
     * 获取方法
     *
     * @param nodeType 节点类型
     * @return 返回系统方法的映射，如果不存在则返回空映射
     * 这个方法用于获取系统方法的映射。首先，它会检查是否存在给定类型的系统方法，如果存在，则返回这些方法的映射。
     * 如果不存在，则返回一个空的映射。
     */
    public Map<String, DoubleFunctionDeclareNode> getMethods(String nodeType) {
        if (methods.get(nodeType) == null) {
            return new HashMap<>();
        } else {
            return new HashMap<>(methods.get(nodeType));
        }
    }

    /**
     * 参数对象，用于传递方法的相关信息
     *
     * @since 1.0
     */
    private static class MethodInfo {
        private final String methodName;

        private final int argNum;

        private final OhFunction handler;

        private final TypeExpr returnType;

        private MethodInfo(String methodName, int argNum, OhFunction handler, TypeExpr returnType) {
            this.methodName = methodName;
            this.argNum = argNum;
            this.handler = handler;
            this.returnType = returnType;
        }

        /**
         * 获取方法名
         *
         * @return 返回方法的名称，这是一个字符串，表示方法的名称。
         * 这个方法从MethodInfo对象中获取方法名，并返回给调用者。
         */
        public String getMethodName() {
            return methodName;
        }

        /**
         * 获取方法参数数量
         *
         * @return 返回方法的参数数量，这是一个整数，表示方法的参数数量。
         * 这个方法从MethodInfo对象中获取方法的参数数量，并返回给调用者。
         */
        public int getArgNum() {
            return argNum;
        }

        /**
         * 获取方法处理器
         *
         * @return 返回方法的处理器，这是一个OhFunction对象，表示方法的处理器。
         * 这个方法从MethodInfo对象中获取方法的处理器，并返回给调用者。
         */
        public OhFunction getHandler() {
            return handler;
        }

        /**
         * 获取方法返回类型
         *
         * @return 返回方法的返回类型，这是一个TypeExpr对象，表示方法的返回类型。
         * 这个方法从MethodInfo对象中获取方法的返回类型，并返回给调用者。
         */
        public TypeExpr getReturnType() {
            return returnType;
        }
    }
}
