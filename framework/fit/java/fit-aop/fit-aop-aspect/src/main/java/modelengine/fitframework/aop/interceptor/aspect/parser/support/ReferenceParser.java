/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.aop.annotation.Pointcut;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolvers;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 解析切点 {@link Pointcut} 的解析器。
 * <P>表达式的组合，就是对应的表达式的逻辑运算，与、或、非。</p>
 *
 * @author 白鹏坤
 * @since 2023-03-17
 */
public class ReferenceParser extends BaseParser {
    // 方法匹配正则 方法名(参数)
    private static final String METHOD_REGEX = "[a-zA-Z0-9_$]*\\(.*\\)";

    private final Class<?> inScope;
    private final PointcutParameter[] parameters;

    public ReferenceParser(Class<?> inScope, PointcutParameter[] parameters) {
        this.inScope = inScope;
        this.parameters = nullIf(parameters, new PointcutParameter[0]);
    }

    @Override
    public boolean couldParse(String expression) {
        return expression.matches(METHOD_REGEX);
    }

    @Override
    public Result parse(String expression) {
        return new ReferenceParserResult(expression);
    }

    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.REFERENCE;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new ReferenceParserResult(content);
    }

    class ReferenceParserResult extends BaseParser.BaseResult {
        public ReferenceParserResult(String content) {
            super(content, null);
        }

        @Override
        public Object content() {
            final int index = this.content.indexOf("(");
            Validation.between(index,
                    0,
                    this.content.length() - 1,
                    "Pointcut format error. [pointcut={0}]",
                    this.content);
            final String paramsString =
                    this.content.substring(index + 1, this.content.length() - 1).replaceAll(" ", "");
            final String[] params = StringUtils.split(paramsString, ",");
            String methodName = this.content.substring(0, index);
            Method[] methods = ReferenceParser.this.inScope.getDeclaredMethods();
            final Method matchedMethod = Arrays.stream(methods)
                    .filter(method -> Objects.equals(method.getName(), methodName)
                            && Arrays.equals(method.getParameterTypes(),
                            this.getTypeList(ReferenceParser.this.parameters, params)))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Can not found matched method: " + methodName));
            AnnotationMetadataResolver metadataResolver = AnnotationMetadataResolvers.create();
            AnnotationMetadata annotationMetadata = metadataResolver.resolve(matchedMethod);
            Pointcut annotation = annotationMetadata.getAnnotation(Pointcut.class);
            Validation.notNull(annotation, "Can not found @Pointcut annotation.");
            return annotation.pointcut();
        }

        private Class<?>[] getTypeList(PointcutParameter[] parameters, String[] params) {
            final List<Class<?>> list = new ArrayList<>();
            for (String param : params) {
                for (PointcutParameter parameter : parameters) {
                    if (Objects.equals(parameter.getName(), param)) {
                        list.add(parameter.getType());
                        break;
                    }
                }
            }
            return list.toArray(new Class<?>[0]);
        }

        @Override
        public boolean isBinding() {
            return false;
        }
    }
}
