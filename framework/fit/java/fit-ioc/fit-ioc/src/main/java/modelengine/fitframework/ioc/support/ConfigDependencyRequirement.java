/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import modelengine.fitframework.beans.convert.ConversionService;
import modelengine.fitframework.conf.ConfigValueSupplier;
import modelengine.fitframework.ioc.BeanDefinitionException;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.ValueSupplier;
import modelengine.fitframework.parameterization.ParameterizedString;
import modelengine.fitframework.parameterization.ParameterizedStringResolver;
import modelengine.fitframework.parameterization.ResolvedParameter;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Type;

/**
 * 为 {@link DependencyRequirement} 提供基于配置的依赖的实现。
 *
 * @author 梁济时
 * @since 2022-12-26
 */
final class ConfigDependencyRequirement extends DependencyRequirement {
    private final String expression;

    /**
     * 使用待注入依赖的 Bean 的元数据，及所依赖的配置的表达式初始化 {@link ConfigDependencyRequirement} 类的新实例。
     *
     * @param source 表示待注入依赖的 Bean 的元数据的 {@link BeanMetadata}。
     * @param expression 表示所依赖的配置的表达式 {@link String}。
     * @throws IllegalArgumentException {@code source} 为 {@code null}。
     */
    ConfigDependencyRequirement(BeanMetadata source, String expression) {
        super(source);
        this.expression = expression;
    }

    @Override
    ValueSupplier withType(Type targetType, AnnotationMetadata annotations) {
        return new Supplier(this.source(), this.expression, targetType);
    }

    private static final class Supplier implements ValueSupplier {
        private static final ParameterizedStringResolver RESOLVER = ParameterizedStringResolver.create("${", "}", '\0');
        private static final String DEFAULT_VALUE_SEPARATOR = ":";

        private final BeanMetadata source;
        private final String expression;
        private final Type targetType;

        private Supplier(BeanMetadata source, String expression, Type targetType) {
            this.source = source;
            this.expression = expression;
            this.targetType = targetType;
        }

        @Override
        public Object get() {
            ParameterizedString format = RESOLVER.resolve(this.expression);
            if (format.getParameters().size() == 1) {
                ResolvedParameter parameter = format.getParameters().get(0);
                if (parameter.getPosition() == 0 && parameter.getLength() == this.expression.length()) {
                    return this.getParameterValue(parameter);
                }
            }
            throw new BeanDefinitionException(StringUtils.format(
                    "Config expression injection is not supported. [bean={0}, value={1}]",
                    this.source.name(),
                    this.expression));
        }

        private Object getParameterValue(ResolvedParameter parameter) {
            String parameterName = parameter.getName();
            int index = parameterName.indexOf(DEFAULT_VALUE_SEPARATOR);
            if (index < 0) {
                return this.source.config().get(parameterName, this.targetType);
            }
            String realName = parameterName.substring(0, index);
            String defaultValue = parameterName.substring(index + 1);
            Object configValue = ConfigValueSupplier.get(this.source.config(), realName);
            Object realValue = ObjectUtils.nullIf(configValue, defaultValue);
            return ConversionService.forConfig().convert(realValue, this.targetType);
        }
    }
}
