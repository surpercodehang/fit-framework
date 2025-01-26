/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.aop.interceptor.aspect.interceptor.inject.AspectParameterInjectionHelper;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import modelengine.fitframework.aop.interceptor.aspect.util.ExpressionUtils;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 解析切点表达式中关键字 @args 的解析器。
 * <P>用于匹配目标对象方法的参数类型所属类上有指定的注解，不支持通配符，不支持单独使用，支持嵌套注解查找，有以下 2 种用法：</p>
 * <ul>
 *     <li>参数过滤：匹配的是参数类型和个数，个数在 @args 括号中以逗号分隔，类型是在 @args 括号中声明。</li>
 *     <li>参数绑定：匹配的是参数类型和个数，个数在 @args 括号中以逗号分隔，类型是在增强方法中定义，并且必须在 {@code argsName} 中声明。</li>
 * </ul>
 *
 * @author 郭龙飞
 * @since 2023-03-14
 */
public class AtArgsParser extends BaseParser {
    private final PointcutParameter[] parameters;
    private final ClassLoader classLoader;

    public AtArgsParser(PointcutParameter[] parameters, ClassLoader classLoader) {
        this.parameters = nullIf(parameters, new PointcutParameter[0]);
        this.classLoader = classLoader;
    }

    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.AT_ARGS;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new AtArgsResult(content);
    }

    class AtArgsResult extends BaseParser.BaseResult {
        public AtArgsResult(String content) {
            super(content, AtArgsParser.this.classLoader);
        }

        @Override
        public boolean couldMatch(Class<?> beanClass) {
            return true;
        }

        @Override
        public boolean match(Method method) {
            Class<?> clazz;
            if (this.isBinding()) {
                Optional<PointcutParameter> parameter = Arrays.stream(AtArgsParser.this.parameters)
                        .filter(param -> Objects.equals(param.getName(), this.content()))
                        .findFirst();
                Validation.isTrue(parameter.isPresent(), "Pointcut params name can not be found.[name={0}]",
                        this.content);
                clazz = parameter.get().getType();
            } else {
                clazz = ExpressionUtils.getContentClass(this.content, AtArgsParser.this.classLoader);
            }
            return Arrays.stream(method.getParameterTypes())
                    .map(AspectParameterInjectionHelper::getAnnotationMetadata)
                    .anyMatch(annotationMetadata -> annotationMetadata.isAnnotationPresent(ObjectUtils.cast(clazz)));
        }
    }
}
