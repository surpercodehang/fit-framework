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
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * 解析切点表达式中关键字 @annotation 的解析器。
 * <P>用于匹配当前执行方法持有指定注解的方法，不支持通配符，支持嵌套注解查找。</p>
 * <ul>
 *    <li>参数过滤：匹配的是参数类型和个数，个数在 @annotation 括号中以逗号分隔，类型是在 @annotation 括号中声明。</li>
 *    <li>参数绑定：匹配的是参数类型和个数，个数在 @annotation 括号中以逗号分隔，类型是在增强方法中定义，并且必须在 {@code argsName} 中声明。
 *    </li>
 * </ul>
 *
 * @author 郭龙飞
 * @since 2023-03-14
 */
public class AtAnnotationParser extends BaseParser {
    private final PointcutParameter[] parameters;
    private final ClassLoader classLoader;

    public AtAnnotationParser(PointcutParameter[] parameters, ClassLoader classLoader) {
        this.parameters = nullIf(parameters, new PointcutParameter[0]);
        this.classLoader = classLoader;
    }

    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.AT_ANNOTATION;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new AtAnnotationResult(content);
    }

    class AtAnnotationResult extends BaseParser.BaseResult {
        public AtAnnotationResult(String content) {
            super(content, AtAnnotationParser.this.classLoader);
        }

        @Override
        public boolean couldMatch(Class<?> beanClass) {
            return true;
        }

        @Override
        public boolean match(Method method) {
            AnnotationMetadata annotationMetadata = AspectParameterInjectionHelper.getAnnotationMetadata(method);
            if (this.isBinding()) {
                Optional<PointcutParameter> parameter = Arrays.stream(AtAnnotationParser.this.parameters)
                        .filter(param -> param.getName().equals(this.content()))
                        .findFirst();
                Validation.isTrue(parameter.isPresent(),
                        "Pointcut params name cannot be found. [name={0}]",
                        this.content);
                return parameter.filter(pointcutParameter -> annotationMetadata.isAnnotationPresent(ObjectUtils.cast(
                        pointcutParameter.getType()))).isPresent();
            } else {
                Class<?> contentClass =
                        ExpressionUtils.getContentClass(this.content().toString(), AtAnnotationParser.this.classLoader);
                return annotationMetadata.isAnnotationPresent(ObjectUtils.cast(contentClass));
            }
        }
    }
}
