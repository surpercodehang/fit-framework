/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.DependencyResolvingResult;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanCreator;
import modelengine.fitframework.ioc.lifecycle.bean.ValueSupplier;
import modelengine.fitframework.type.TypeMatcher;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 为 {@link BeanCreator} 提供基类，以提供参数解析能力。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-05-12
 */
public abstract class AbstractBeanCreator implements BeanCreator {
    private final BeanMetadata metadata;

    /**
     * 使用待创建Bean的元数据初始化 {@link AbstractBeanCreator} 类的新实例。
     *
     * @param metadata 表示Bean的元数据的 {@link BeanMetadata}。
     * @throws IllegalArgumentException {@code metadata} 为 {@code null}。
     */
    public AbstractBeanCreator(BeanMetadata metadata) {
        this.metadata = Validation.notNull(metadata, "The metadata of bean to create cannot be null.");
    }

    /**
     * 表示待创建的Bean的元数据。
     *
     * @return 表示Bean的元数据的 {@link BeanMetadata}。
     */
    protected final BeanMetadata metadata() {
        return this.metadata;
    }

    /**
     * 解析指定参数的值。
     *
     * @param parameter 表示待解析的参数的 {@link Parameter}。
     * @return 若参数的值被成功解析，则为表示参数值的提供程序的 {@link Optional}{@code <}{@link ValueSupplier}{@code
     * >}；否则为 {@link Optional#empty()}。
     */
    private Optional<ValueSupplier> resolve(Parameter parameter) {
        return this.metadata().runtime().resolverOfBeans().parameter(this.metadata(), parameter);
    }

    /**
     * 获取指定类型的依赖。
     *
     * @param type 表示所依赖的值的类型的 {@link Type}。
     * @param annotations 表示所依赖的值所在位置的注解元数据的 {@link AnnotationMetadata}。
     * @return 表示依赖的解析结果的 {@link DependencyResolvingResult}。
     */
    private DependencyResolvingResult dependency(Type type, AnnotationMetadata annotations) {
        return this.metadata().runtime().resolverOfDependencies().resolve(this.metadata(), null, type, annotations);
    }

    /**
     * 为指定的可执行对象，获取用以执行的实际参数。
     *
     * @param executable 表示可执行对象的 {@link Executable}。
     * @param arguments 表示用户输入参数的 {@link Object}{@code []}。
     * @return 若当前入参可用以执行指定对象，则为表示执行使用的实际参数的 {@link Arguments}。
     */
    protected Arguments arguments(Executable executable, Object[] arguments) {
        Parameter[] parameters = executable.getParameters();
        if (parameters.length < arguments.length) {
            // 输入参数超出所需数量
            return Arguments.fail(arguments, "provided arguments is more than parameters");
        }
        Object[] actualArguments = new Object[parameters.length];
        int argumentIndex = arguments.length - 1;
        for (int i = parameters.length - 1; i >= 0; i--) {
            Parameter parameter = parameters[i];
            Optional<ValueSupplier> supplier = this.resolve(parameter);
            if (supplier.isPresent()) {
                // 使用BeanResolver解析到的值
                actualArguments[i] = ValueSupplier.real(supplier.get());
                continue;
            }
            if (argumentIndex >= 0 && match(parameter, arguments[argumentIndex])) {
                Object argumentValue = arguments[argumentIndex];
                // 使用用户输入的参数
                actualArguments[i] = ValueSupplier.real(argumentValue);
                argumentIndex--;
                continue;
            }
            AnnotationMetadata annotations = this.metadata.runtime().resolverOfAnnotations().resolve(parameter);
            DependencyResolvingResult dependency = this.dependency(parameter.getParameterizedType(), annotations);
            if (dependency.resolved()) {
                // 使用默认通过类型解析到的依赖
                actualArguments[i] = ValueSupplier.real(dependency.get());
            } else {
                // 某个参数不能解析
                return Arguments.fail(actualArguments, "the argument[" + i + "] cannot resolve");
            }
        }
        if (argumentIndex > -1) {
            // 所提供的参数多于预期
            return Arguments.fail(actualArguments, "provided arguments is more than required");
        }
        return Arguments.succeed(actualArguments);
    }

    /**
     * 配置入参是否可用于指定参数。
     *
     * @param parameter 表示目标参数的 {@link Parameter}。
     * @param argument 表示候选值的 {@link Object}。
     * @return 若可用于该参数，则为 {@code true}；否则为 {@code false}。
     */
    private static boolean match(Parameter parameter, Object argument) {
        if (argument == null) {
            return !parameter.getType().isPrimitive();
        } else {
            return TypeMatcher.match(argument.getClass(), parameter.getParameterizedType());
        }
    }

    /**
     * 表示解析获得的参数信息。
     */
    public static class Arguments implements Supplier<Object[]> {
        private final Object[] args;
        private final boolean isComplete;
        private final String message;

        private Arguments(Object[] args, boolean isComplete, String message) {
            this.args = ObjectUtils.getIfNull(args, () -> new Object[0]);
            this.isComplete = isComplete;
            this.message = ObjectUtils.nullIf(message, StringUtils.EMPTY);
        }

        /**
         * 获取解析成功后的参数信息。
         *
         * @param args 表示解析后的参数列表的 {@link Object}{@code []}。
         * @return 表示解析后的参数信息的 {@link Arguments}。
         */
        public static Arguments succeed(Object[] args) {
            return new Arguments(args, true, null);
        }

        /**
         * 获取解析失败后的参数信息。
         *
         * @param args 表示解析后的参数列表的 {@link Object}{@code []}。
         * @param message 表示解析失败的原因的 {@link String}。
         * @return 表示解析后的参数信息的 {@link Arguments}。
         */
        public static Arguments fail(Object[] args, String message) {
            return new Arguments(args, false, message);
        }

        @Override
        public Object[] get() {
            return this.args;
        }

        /**
         * 判定解析的参数信息是否完整。
         *
         * @return 如果解析成功，则返回 {@code true}，否则，返回 {@code false}。
         */
        public boolean isComplete() {
            return this.isComplete;
        }

        /**
         * 获取解析失败的原因。
         *
         * @return 表示解析失败原因的 {@link String}。
         */
        public String getMessage() {
            return this.message;
        }
    }
}
