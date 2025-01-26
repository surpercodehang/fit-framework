/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser.support;

import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.lessThan;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import modelengine.fitframework.aop.interceptor.aspect.util.ExpressionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

/**
 * 解析切点表达式中关键字 args 的解析器。
 * <P>用于匹配当前执行的方法传入的参数为指定类型的执行方法，支持通配符，有以下 2 种用法：</p>
 * <ul>
 *     <li>参数过滤：匹配的是参数类型和个数，个数在 args 括号中以逗号分隔，类型是在 args 括号中声明。</li>
 *     <li>参数绑定：匹配的是参数类型和个数，个数在 args 括号中以逗号分隔，类型是在增强方法中定义，并且必须在 {@code argsName} 中声明。</li>
 * </ul>
 *
 * @author 郭龙飞
 * @since 2023-03-14
 */
public class ArgsParser extends BaseParser {
    private static final String MULTIPLE_ARGS_REGEX = "..";
    private static final String SINGLE_ARGS_REGEX = "*";
    private static final String ARG_NAMES_SEPARATOR = ",";

    private final PointcutParameter[] parameters;
    private final ClassLoader classLoader;

    public ArgsParser(PointcutParameter[] parameters, ClassLoader classLoader) {
        this.parameters = nullIf(parameters, new PointcutParameter[0]);
        this.classLoader = classLoader;
    }

    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.ARGS;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new ArgsResult(content);
    }

    /**
     * args 解析结果。
     */
    public class ArgsResult extends BaseParser.BaseResult {
        private final Map<String, Integer> paramMapping = new HashMap<>();

        public ArgsResult(String content) {
            super(content, ArgsParser.this.classLoader);
        }

        @Override
        public boolean couldMatch(Class<?> beanClass) {
            return true;
        }

        @Override
        public boolean match(Method method) {
            final Class<?>[] types = method.getParameterTypes();
            if (StringUtils.isEmpty(this.content)) {
                return types.length == 0;
            }
            final String[] parts = StringUtils.split(this.content, ARG_NAMES_SEPARATOR);
            final boolean isBinding = this.isBinding();
            if (this.content.contains(MULTIPLE_ARGS_REGEX)) {
                return this.argsWithTwoDot(types, parts, isBinding);
            } else {
                return this.argsWithNoTwoDot(types, parts, isBinding);
            }
        }

        private boolean argsWithTwoDot(Class<?>[] types, String[] parts, boolean isBinding) {
            // 只支持 '..' 在开始或末尾位置，在中间位置不支持。 例：name,..,age 这种不支持。
            if (parts.length == 1) {
                return true;
            }
            if (types.length < parts.length - 1) {
                return false;
            }
            lessThan(Arrays.stream(parts).filter(MULTIPLE_ARGS_REGEX::equals).count(), 2,
                    "The args can only contains 1 '..'.");
            // '..' 的位置在开始位置。
            boolean isFirstPlace = MULTIPLE_ARGS_REGEX.equals(parts[0]);
            if (!isFirstPlace) {
                isTrue(MULTIPLE_ARGS_REGEX.equals(parts[parts.length - 1]),
                        "The args can only support '..' in first position or last position.");
            }
            return this.compareParamTypes(types, parts, isBinding, isFirstPlace);
        }

        private boolean argsWithNoTwoDot(Class<?>[] types, String[] parts, boolean isBinding) {
            if (types.length != parts.length) {
                return false;
            }
            return this.compareParamTypes(types, parts, isBinding, false);
        }

        private Class<?>[] getBindingParamClass(Class<?>[] types, String[] parts, PointcutParameter[] parameters,
                boolean isReverse) {
            Class<?>[] paramsClass = new Class<?>[types.length];
            int partIndex = 0;
            if (isReverse) {
                partIndex = parts.length - 1;
                for (int i = types.length - 1; i >= 0 && partIndex > 0; i--, partIndex--) {
                    this.setParamClass(parts[partIndex], parameters, paramsClass, i);
                }
            } else {
                for (int i = 0; i < types.length && partIndex < parts.length; i++, partIndex++) {
                    this.setParamClass(parts[partIndex], parameters, paramsClass, i);
                }
            }
            return paramsClass;
        }

        private void setParamClass(String argsName, PointcutParameter[] parameters, Class<?>[] paramsClass, int i) {
            if (SINGLE_ARGS_REGEX.equals(argsName) || MULTIPLE_ARGS_REGEX.equals(argsName)) {
                return;
            }
            Optional<PointcutParameter> pointcutParameter = Arrays.stream(parameters)
                    .filter(parameter -> Objects.equals(parameter.getName(), argsName))
                    .findFirst();
            isTrue(pointcutParameter.isPresent(),
                    "The args params name can not be found in pointcut parameters. [name={0}]", argsName);
            paramsClass[i] = pointcutParameter.get().getType();
            this.paramMapping.put(argsName, i);
        }

        private boolean isClassMatched(Class<?>[] methodParams, Class<?>[] paramClass,
                BiPredicate<Class<?>, Class<?>> predicate) {
            for (int i = 0; i < methodParams.length; i++) {
                if (paramClass[i] == null) {
                    continue;
                }
                if (!predicate.test(paramClass[i], methodParams[i])) {
                    return false;
                }
            }
            return true;
        }

        private boolean compareParamTypes(Class<?>[] types, String[] parts, boolean isBinding, boolean isFirstPlace) {
            Class<?>[] paramClass;
            BiPredicate<Class<?>, Class<?>> predicate;
            if (isBinding) {
                // 根据 pointcut 方法参数类型获取类型
                paramClass = this.getBindingParamClass(types, parts, ArgsParser.this.parameters, isFirstPlace);
                predicate = Class::isAssignableFrom;
            } else {
                // 根据 args(..) 字符串内容获取类型
                paramClass = Arrays.stream(parts).map(item -> ExpressionUtils.getContentClass(item,
                        ArgsParser.this.classLoader)).toArray(Class[]::new);
                predicate = Class::equals;
            }
            return this.isClassMatched(types, paramClass, predicate);
        }

        @Override
        public boolean isBinding() {
            if (StringUtils.isEmpty(this.content)) {
                return false;
            }
            String[] parts = StringUtils.split(this.content, ARG_NAMES_SEPARATOR);
            return Arrays.stream(parts)
                    .map(item -> ExpressionUtils.getContentClass(item, ArgsParser.this.classLoader))
                    .noneMatch(Objects::nonNull);
        }

        @Override
        public Object content() {
            return new ArgsModel(this.content, this.paramMapping);
        }

        /**
         * args 模型类。
         */
        public class ArgsModel {
            private final String expression;
            private final Map<String, Integer> paramMapping;

            public ArgsModel(String expression, Map<String, Integer> paramMapping) {
                this.expression = expression;
                this.paramMapping = paramMapping;
            }

            public String getExpression() {
                return this.expression;
            }

            public Map<String, Integer> getParamMapping() {
                return this.paramMapping;
            }
        }
    }
}
