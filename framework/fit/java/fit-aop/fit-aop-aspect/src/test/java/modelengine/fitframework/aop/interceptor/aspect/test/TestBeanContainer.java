/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.aop.annotation.Before;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.BeanRegistry;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.support.AbstractDisposable;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 用于测试的 Bean 容器。
 *
 * @author 季聿阶
 * @since 2022-05-15
 */
public class TestBeanContainer extends AbstractDisposable implements BeanContainer {
    private final Map<String, BeanFactory> beans = new HashMap<>();
    private final Plugin plugin;

    /**
     * 初始化 TestBeanContainer，并放入测试使用到的 Bean。
     */
    public TestBeanContainer() {
        this.plugin = mock(Plugin.class);
        FitRuntime runtime = mock(FitRuntime.class);
        when(this.plugin.runtime()).thenReturn(runtime);
        AnnotationMetadataResolver annotationResolver = mock(AnnotationMetadataResolver.class);
        when(runtime.resolverOfAnnotations()).thenReturn(annotationResolver);
        AnnotationMetadata annotations = mock(AnnotationMetadata.class);
        AnnotationMetadata otherAnnotations = mock(AnnotationMetadata.class);
        when(annotationResolver.resolve(any())).thenAnswer(ele -> {
            final Method methodCast = ObjectUtils.cast(ele.getArgument(0));
            if (Objects.equals(methodCast.getName(), "before1")) {
                return annotations;
            }
            return otherAnnotations;
        });
        when(annotations.isAnnotationPresent(Before.class)).thenReturn(true);
        when(otherAnnotations.isAnnotationPresent(Before.class)).thenReturn(false);
        Before before = mock(Before.class);
        when(annotations.getAnnotation(Before.class)).thenReturn(before);
        when(before.value()).thenReturn(
                "execution(String modelengine.fitframework.aop.interceptor.aspect.test.TestService1.m1())");
        AnnotationMetadata am1 = mock(AnnotationMetadata.class);
        when(am1.isAnnotationPresent(eq(Aspect.class))).thenReturn(true);
        Aspect aspectAnnotation = mock(Aspect.class);
        when(am1.getAnnotation(Aspect.class)).thenReturn(aspectAnnotation);
        when(aspectAnnotation.scope()).thenReturn(Scope.PLUGIN);
        BeanFactory bf1 = mock(BeanFactory.class);
        BeanMetadata bm1 = mock(BeanMetadata.class);
        when(bm1.container()).thenReturn(this);
        when(bm1.name()).thenReturn("testExecutionAspect");
        when(bm1.type()).thenReturn(TestExecutionAspect.class);
        when(bm1.singleton()).thenReturn(true);
        when(bm1.annotations()).thenReturn(am1);
        when(bf1.metadata()).thenReturn(bm1);
        TestExecutionAspect aspect = new TestExecutionAspect();
        when(bf1.get()).thenReturn(aspect);
        this.beans.put("testExecutionAspect", bf1);

        AnnotationMetadata am2 = mock(AnnotationMetadata.class);
        BeanFactory bf2 = mock(BeanFactory.class);
        BeanMetadata bm2 = mock(BeanMetadata.class);
        when(bm2.container()).thenReturn(this);
        when(bm2.name()).thenReturn("testService1");
        when(bm2.type()).thenReturn(TestService1.class);
        when(bm2.singleton()).thenReturn(true);
        when(bm2.annotations()).thenReturn(am2);
        when(bf2.metadata()).thenReturn(bm2);
        when(bf2.get()).thenReturn(new TestService1());
        this.beans.put("testService1", bf2);
    }

    @Nonnull
    @Override
    public String name() {
        return null;
    }

    @Override
    public @Nonnull
    Plugin plugin() {
        return this.plugin;
    }

    @Nonnull
    @Override
    public BeanRegistry registry() {
        return null;
    }

    @Override
    public Optional<BeanFactory> factory(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<BeanFactory> factory(Type type) {
        return Optional.empty();
    }

    @Override
    public List<BeanFactory> factories(Type type) {
        return null;
    }

    @Override
    public List<BeanFactory> factories() {
        return new ArrayList<>(this.beans.values());
    }

    @Override
    public Optional<BeanFactory> lookup(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<BeanFactory> lookup(Type type) {
        return Optional.empty();
    }

    @Override
    public List<BeanFactory> all(Type type) {
        return null;
    }

    @Override
    public List<BeanFactory> all() {
        return new ArrayList<>(this.beans.values());
    }

    @Override
    public void start() {}

    @Override
    public void stop() {}

    @Override
    public Beans beans() {
        return null;
    }

    @Override
    public void destroySingleton(String beanName) {
    }

    @Override
    public void removeBean(String beanName) {
    }
}
