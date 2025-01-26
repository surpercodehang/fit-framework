/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.listener;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.test.annotation.Spy;
import modelengine.fitframework.test.domain.TestContext;
import modelengine.fitframework.test.domain.util.AnnotationUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 用于字段注入的监听类。
 *
 * @author 邬涨财
 * @author 季聿阶
 * @since 2023-01-17
 */
public abstract class InjectFieldTestListener implements TestListener {
    @Override
    public void prepareTestInstance(TestContext context) {
        this.injectDependencies(context);
    }

    private void injectDependencies(TestContext context) {
        Object instance = context.testInstance();
        Class<?> clazz = context.testClass();
        Field[] declaredFields = ReflectionUtils.getDeclaredFields(clazz);
        BeanContainer container = context.plugin().container();
        Arrays.stream(declaredFields)
                .filter(this::isInjectedFiled)
                .forEach(field -> ReflectionUtils.setField(instance, field, this.getBean(container, field.getType())));
    }

    /**
     * 判断字段是否需要注入。
     *
     * @param field 表示待判断的字段的 {@link Field}。
     * @return 如果字段需要注入，则返回 {@code true}，否则返回 {@code false}。
     */
    protected abstract boolean isInjectedFiled(Field field);

    /**
     * 获取需要注入的 Bean。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param clazz 表示 Bean 的类型的 {@link Class}{@code <?>}。
     * @return 返回需要注入的 Bean 的 {@link Object}。
     */
    protected Object getBean(BeanContainer container, Class<?> clazz) {
        return container.factory(clazz)
                .map(beanFactory -> clazz.cast(beanFactory.get()))
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "Failed to inject field: Not found available bean. [class={0}].",
                        clazz.getName())));
    }

    /**
     * 用于通过 {@link Fit} 注解进行字段注入的监听类。
     */
    public static class ByFit extends InjectFieldTestListener {
        @Override
        protected boolean isInjectedFiled(Field field) {
            return AnnotationUtils.getAnnotation(field, Fit.class).isPresent();
        }
    }

    /**
     * 用于通过 {@link Spy} 注解进行字段注入的监听类。
     */
    public static class BySpy extends InjectFieldTestListener {
        @Override
        protected boolean isInjectedFiled(Field field) {
            return AnnotationUtils.getAnnotation(field, Spy.class).isPresent();
        }
    }
}