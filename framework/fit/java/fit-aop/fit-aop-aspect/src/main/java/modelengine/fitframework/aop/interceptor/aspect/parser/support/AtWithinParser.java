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
 * 解析切点表达式中关键字 @within 的解析器。
 * <P>用于匹配方法声明类上指定的注解类，方法所属类是本类或者子类，不支持通配符，支持嵌套注解查找，有以下 2 种用法：</p>
 * <ul>
 *     <li>参数过滤：匹配的是参数类型和个数，个数在 @within 括号中以逗号分隔，类型是在 @within 括号中声明。</li>
 *     <li>参数绑定：匹配的是参数类型和个数，个数在 @within 括号中以逗号分隔，类型是在增强方法中定义，并且必须在 {@code argsName} 中声明。</li>
 * </ul>
 *
 * @author 郭龙飞
 * @since 2023-03-14
 */
public class AtWithinParser extends BaseParser {
    private final PointcutParameter[] parameters;
    private final ClassLoader classLoader;

    /**
     * 表示初始化参数和类加载器的构造函数。
     *
     * @param parameters 表示参数数组的 {@link PointcutParameter}。
     * @param classLoader 表示类加载器的 {@link ClassLoader}。
     */
    public AtWithinParser(PointcutParameter[] parameters, ClassLoader classLoader) {
        this.parameters = nullIf(parameters, new PointcutParameter[0]);
        this.classLoader = classLoader;
    }

    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.AT_WITHIN;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new AtWithinResult(content);
    }

    class AtWithinResult extends BaseParser.BaseResult {
        private final Class<?> clazz;

        public AtWithinResult(String content) {
            super(content, AtWithinParser.this.classLoader);
            this.clazz = this.getAnnotationClass();
        }

        private Class<?> getAnnotationClass() {
            if (this.isBinding()) {
                Optional<PointcutParameter> parameter = Arrays.stream(AtWithinParser.this.parameters)
                        .filter(param -> param.getName().equals(this.content()))
                        .findFirst();
                Validation.isTrue(parameter.isPresent(),
                        "Pointcut params name can not be found.[name={0}]",
                        this.content);
                return parameter.get().getType();
            }
            return ExpressionUtils.getContentClass(this.content().toString(), AtWithinParser.this.classLoader);
        }

        @Override
        public boolean couldMatch(Class<?> beanClass) {
            // 判断当前类和递归父类有没有表达式中的注解
            Class<?> current = beanClass;
            while (current != Object.class) {
                AnnotationMetadata annotationMetadata = AspectParameterInjectionHelper.getAnnotationMetadata(current);
                if (annotationMetadata.isAnnotationPresent(ObjectUtils.cast(this.clazz))) {
                    return true;
                }
                current = current.getSuperclass();
            }
            return false;
        }

        @Override
        public boolean match(Method method) {
            // 方法声明类中检查表达式中的注解
            Class<?> declaringClass = method.getDeclaringClass();
            AnnotationMetadata annotationMetadata =
                    AspectParameterInjectionHelper.getAnnotationMetadata(declaringClass);
            return annotationMetadata.isAnnotationPresent(ObjectUtils.cast(this.clazz));
        }
    }
}
