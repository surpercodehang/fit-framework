/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser.support;

import static modelengine.fitframework.util.StringUtils.blankIf;

import modelengine.fitframework.aop.interceptor.aspect.parser.ExpressionParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import modelengine.fitframework.aop.interceptor.aspect.util.ExpressionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * 解析切点表达式中关键字 execution 的解析器。
 * <P>用于匹配方法执行的连接点，支持通配符，格式
 * execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern)throws-pattern?)
 * 用法如下：</p>
 * <ul>
 *     <li> modifiers-pattern （修饰符），非必选，取值有 pubilc、proected、private，支持 * 通配符。</li>
 *     <li>ret-type-pattern （返回类型），取值有 java 基础数据类型 、Object、数组类型，支持 * 通配符。</li>
 *     <li>declaring-type-pattern （切入点类，包路径 + 类型），支持 .*（一级包路径）、..*（多级包路径） 通配符。</li>
 *     <li>name-pattern （方法名），支持 * 通配符。</li>
 *     <li>param-pattern（方法参数），以逗号分隔，支持 *（任意一个参数）、..（任意个参数）通配符。</li>
 *     <li>throws-pattern（异常匹配），暂不支持。</li>
 * </ul>
 *
 * @author 郭龙飞
 * @since 2023-03-14
 */
public class ExecutionParser extends BaseParser {
    private final ClassLoader classLoader;

    public ExecutionParser(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.EXECUTION;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new ExecutionResult(content);
    }

    @Override
    public Result parse(String expression) {
        String content = ExpressionParser.getParseContent(expression, this.parserType().getValue());
        return new ExecutionResult(content);
    }

    class ExecutionResult extends BaseParser.BaseResult {
        public ExecutionResult(String content) {
            super(content, ExecutionParser.this.classLoader);
        }

        @Override
        public boolean couldMatch(Class<?> beanClass) {
            ExecuteExpression.ExecutionModel executionModel = ObjectUtils.cast(this.content());
            String classPath = executionModel.getClassPath();
            if (StringUtils.isEmpty(classPath)) {
                return true;
            }
            String beanPath = beanClass.getName();
            final String replaceRegex = ExpressionUtils.expressionReplaceRegex(classPath);
            return beanPath.matches(replaceRegex);
        }

        @Override
        public boolean match(Method method) {
            ExecuteExpression.ExecutionModel executionModel = ObjectUtils.cast(this.content());
            // 修饰符，非必须，支持 * 模糊匹配
            String modifier = blankIf(executionModel.getAccessModifier().trim(), "*");
            // 类路径，非必须，支持 .. * 模糊匹配
            String classPath = blankIf(ExpressionUtils.expressionReplaceRegex(executionModel.getClassPath()), ".*");
            // 方法名，必须，支持 * 模糊匹配
            String methodName = ExpressionUtils.expressionReplaceRegex(executionModel.getMethodName());
            // 返回值，必须，支持 * 模糊匹配
            String returnType = executionModel.getReturnType().trim();
            // 方法参数，必须，支持 .. * 模糊匹配
            String params = executionModel.getParamList().replaceAll(" ", "");
            // 以上必须全部匹配为 true，才认为匹配
            List<Supplier<Boolean>> result = Arrays.asList(() -> this.matchesModifier(method.getModifiers(), modifier),
                    () -> this.matchesReturnType(returnType, method.getReturnType(), classLoader),
                    () -> method.getDeclaringClass().getName().matches(classPath),
                    () -> method.getName().matches(methodName),
                    () -> new ArgsParser(null, classLoader).createConcreteParser(params).match(method));
            for (Supplier<Boolean> supplier : result) {
                if (!supplier.get()) {
                    return false;
                }
            }
            return true;
        }

        private boolean matchesModifier(int modifier, String pointcutModifier) {
            switch (pointcutModifier) {
                case "public":
                    return Modifier.isPublic(modifier);
                case "private":
                    return Modifier.isPrivate(modifier);
                case "protected":
                    return Modifier.isProtected(modifier);
                case "*":
                    return true;
                default:
                    return false;
            }
        }

        private boolean matchesReturnType(String returnType, Class<?> clazz, ClassLoader classLoader) {
            if ("*".equals(returnType)) {
                return true;
            }
            Class<?> contentClass = ExpressionUtils.getContentClass(returnType, classLoader);
            return contentClass == clazz;
        }

        @Override
        public boolean isBinding() {
            return false;
        }

        @Override
        public Object content() {
            return ExecuteExpression.parse(this.content);
        }
    }
}
