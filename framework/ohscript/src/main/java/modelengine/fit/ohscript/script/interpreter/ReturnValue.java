/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.interpreter;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.errors.RuntimeError;
import modelengine.fit.ohscript.script.parser.nodes.DoubleFunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.IgnoredNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.VainTypeExpr;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fit.ohscript.util.EmptyValue;
import modelengine.fitframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 包装过的返回值
 *
 * @since 1.0
 */
public class ReturnValue {
    /**
     * UNIT返回值
     */
    public static final ReturnValue UNIT = new ReturnValue(TypeExprFactory.createUnit(), EmptyValue.UNIT);

    /**
     * Ignore的返回值
     */
    public static final ReturnValue IGNORE = new ReturnValue(TypeExprFactory.createIgnore(), EmptyValue.IGNORE);

    /**
     * 中断返回值
     */
    public static final ReturnValue BREAK = new ReturnValue(TypeExprFactory.createIgnore(), EmptyValue.BREAK);

    /**
     * 继续返回值
     */
    public static final ReturnValue CONTINUE = new ReturnValue(TypeExprFactory.createIgnore(), EmptyValue.CONTINUE);

    /**
     * 错误返回值
     */
    public static final ReturnValue ERROR = new ReturnValue(TypeExprFactory.createError(null), EmptyValue.ERROR);

    /**
     * unknown返回值
     */
    public static final ReturnValue UNKNOWN = new ReturnValue(TypeExprFactory.createUnknown(), EmptyValue.UNKNOWN);

    /**
     * 空返回值
     */
    public static final ReturnValue NULL = new ReturnValue(TypeExprFactory.createNull(), EmptyValue.NULL);

    /**
     * 未定义返回值，类似javaScript中的undefined
     */
    public static final ReturnValue UNDEFINED = new ReturnValue(TypeExprFactory.createUndefined(),
            EmptyValue.UNDEFINED);

    /**
     * for block has been gone through
     * in if statement, if the return value of a branch is declared
     * means the branch is selected
     */
    public static final ReturnValue DECLARED = new ReturnValue(TypeExprFactory.createUnknown(), null);

    private final Map<String, ReturnValue> methods = new HashMap<>();

    private Object value;

    private TypeExpr typeExpr;

    private ActivationContext context;

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param typeExpr 类型表达式
     * @param value 值
     */
    public ReturnValue(ActivationContext context, TypeExpr typeExpr, Object value) {
        this.typeExpr = typeExpr;
        this.value = value;
        this.context = context;
    }

    /**
     * 构造函数
     *
     * @param typeExpr 类型表达式
     * @param value 值
     */
    private ReturnValue(TypeExpr typeExpr, Object value) {
        this(null, typeExpr, value);
    }

    /**
     * 获取值
     *
     * @return 值
     */
    public Object value() {
        // if there is base value, dig into the bottom value
        if (this.value instanceof ReturnValue) {
            return ((ReturnValue) this.value).value();
        }
        return this.value;
    }

    /**
     * 获取基础值
     *
     * @return 基础值
     */
    public ReturnValue base() {
        Object base = this.value;
        if (base instanceof ReturnValue) {
            return (ReturnValue) base;
        } else {
            return null;
        }
    }

    /**
     * 克隆一个新的返回值
     *
     * @return 克隆的返回值
     */
    public ReturnValue clone() {
        ReturnValue newValue = new ReturnValue(this.context(), this.typeExpr, this.value);
        newValue.methods.putAll(this.myMethods());
        return newValue;
    }

    /**
     * 获取自身的方法
     *
     * @return 自身的方法
     */
    public Map<String, ReturnValue> myMethods() {
        return this.methods;
    }

    /**
     * 获取所有方法
     *
     * @return 所有方法
     */
    public Map<String, ReturnValue> methods() {
        Map<String, ReturnValue> methodMap = new HashMap<>();
        if (this.base() == null) {
            if (this.typeExpr.node() != null) {
                String meta = Constants.UNDER_LINE + this.typeExpr + Constants.UNDER_LINE;
                ReturnValue metaValue = this.context.env().asfEnv().context().get(meta, Constants.ROOT_SCOPE);
                if (metaValue != null) {
                    methodMap.putAll((Map<? extends String, ? extends ReturnValue>) metaValue.value());
                }
            }
        } else {
            methodMap.putAll(this.base().methods());
        }
        methodMap.putAll(this.methods);
        return methodMap;
    }

    /**
     * 更新返回值
     *
     * @param value 新的返回值
     * @throws OhPanic 抛出恐慌异常
     */
    public void update(ReturnValue value) throws OhPanic {
        if (this.typeExpr() instanceof VainTypeExpr || value.typeExpr().is(this.typeExpr())) {
            this.value = value.value();
            this.context = value.context();
            this.methods.putAll(value.methods);
            this.typeExpr = value.typeExpr;
        } else {
            RuntimeError.TYPE_MISMATCH.raise();
        }
    }

    /**
     * 获取类型表达式
     *
     * @return 类型表达式
     */
    public TypeExpr typeExpr() {
        return this.typeExpr;
    }

    /**
     * 获取上下文
     *
     * @return 上下文
     */
    public ActivationContext context() {
        return this.context;
    }

    /**
     * 尝试获取属性
     *
     * @param key 属性名
     * @return 返回值
     * @throws OhPanic 抛出恐慌异常
     */
    public ReturnValue get(String key) throws OhPanic {
        return this.tryGet(key, false);
    }

    /**
     * 尝试获取属性
     *
     * @param key 属性名
     * @param enablePanic 是否抛出不可恢复异常
     * @return 返回值
     * @throws OhPanic 抛出不可恢复异常
     */
    public ReturnValue tryGet(String key, boolean enablePanic) throws OhPanic {
        // 得到扩展属性
        ReturnValue actualValue = this.methods().get(key);
        if (actualValue != null) {
            return actualValue;
        }
        // 如果自身就是entity，得到自身自己的属性
        if (this.value() instanceof Map) {
            actualValue = ((Map<String, ReturnValue>) this.value()).get(key);
        }
        if (actualValue != null) {
            return actualValue;
        }
        // 如果都没有，尝试去得到系统给这种类型带的属性
        String systemKey = key + "~" + this.typeExpr().type().name();
        IgnoredNode system = this.context().env().ast().start().getSystemNode();
        if (system != null) {
            Optional<SyntaxNode> possible = system.children()
                    .stream()
                    .filter(f -> (ObjectUtils.<FunctionDeclareNode>cast(f)).functionName().lexeme().equals(systemKey))
                    .findFirst();
            if (possible.isPresent()) {
                DoubleFunctionDeclareNode function = ObjectUtils.cast(possible.get());
                function.setHostValue(this.value());
                return new ReturnValue(this.context, function.typeExpr().exactBe(), function);
            }
        }
        if (enablePanic) {
            RuntimeError.FIELD_NOT_FOUND.raise();
        }
        return ReturnValue.NULL;
    }
}
