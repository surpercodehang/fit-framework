/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.annotation.ExceptionHandler;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpServerResponseException;
import modelengine.fit.http.server.handler.HttpExceptionHandler;
import modelengine.fit.http.server.handler.comparator.ClassComparator;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.exception.MethodInvocationException;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.merge.ConflictException;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.TypeUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 表示 {@link DefaultExceptionHandlerRegistry} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-27
 */
@DisplayName("测试 HttpExceptionHandlerResolver 类")
public class ExceptionHandlerSupplierTest {
    private DefaultExceptionHandlerRegistry exceptionHandlerResolver;
    private BeanFactory beanFactory;
    private BeanContainer beanContainer;
    private final Type type = Integer.class.getGenericSuperclass();

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
        this.initializeBeanFactory();
        Plugin plugin = mock(Plugin.class);
        BeanContainer mockedBeanContainer = mock(BeanContainer.class);
        when(plugin.container()).thenReturn(mockedBeanContainer);
        FitRuntime fitRuntime = mock(FitRuntime.class);
        when(mockedBeanContainer.runtime()).thenReturn(fitRuntime);
        AnnotationMetadataResolver annotationMetadataResolver = mock(AnnotationMetadataResolver.class);
        when(fitRuntime.resolverOfAnnotations()).thenReturn(annotationMetadataResolver);
        AnnotationMetadata annotationMetadata = mock(AnnotationMetadata.class);

        Method[] declaredMethod = TypeUtils.toClass(this.type).getDeclaredMethods();
        for (Method method : declaredMethod) {
            when(annotationMetadataResolver.resolve(method)).thenReturn(annotationMetadata);
        }
        when(annotationMetadata.isAnnotationPresent(ExceptionHandler.class)).thenReturn(true);
        when(annotationMetadata.isAnnotationPresent(ResponseStatus.class)).thenReturn(true);
        ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);
        ResponseStatus responseStatus = mock(ResponseStatus.class);
        when(annotationMetadata.getAnnotation(ExceptionHandler.class)).thenReturn(exceptionHandler);
        when(annotationMetadata.getAnnotation(ResponseStatus.class)).thenReturn(responseStatus);
        Class<? extends Throwable>[] clazz = new Class[] {HttpServerResponseException.class};
        when(exceptionHandler.value()).thenReturn(clazz);
        when(exceptionHandler.scope()).thenReturn(Scope.PLUGIN);
        when(responseStatus.code()).thenReturn(HttpResponseStatus.OK);

        List<BeanFactory> beanFactoryList = new ArrayList<>();
        beanFactoryList.add(this.beanFactory);
        when(mockedBeanContainer.factories()).thenReturn(beanFactoryList);
        when(mockedBeanContainer.all()).thenReturn(beanFactoryList);
        this.beanContainer = mockedBeanContainer;
        this.exceptionHandlerResolver = new DefaultExceptionHandlerRegistry(mockedBeanContainer);
    }

    private void initializeBeanFactory() {
        this.beanFactory = mock(BeanFactory.class);
        BeanMetadata beanMetadata = mock(BeanMetadata.class);
        when(this.beanFactory.metadata()).thenReturn(beanMetadata);
        when(beanMetadata.type()).thenReturn(this.type);
        when(this.beanFactory.get()).thenReturn("beanFactoryTarget");
    }

    @Nested
    @DisplayName("测试 resolveMethod() 方法")
    class TestResolveMethod {
        @Test
        @DisplayName("给定不可解的参数，抛出异常")
        void givenCanNotResolveParametersThenThrowException() {
            Method method = ReflectionUtils.getDeclaredMethod(exceptionHandlerResolver.getClass(),
                    "resolve",
                    BeanContainer.class,
                    BeanFactory.class);
            MethodInvocationException methodInvocationException = catchThrowableOfType(() -> ReflectionUtils.invoke(
                    exceptionHandlerResolver,
                    method,
                    beanContainer,
                    beanFactory), MethodInvocationException.class);
            assertThat(methodInvocationException.getCause() instanceof ConflictException).isTrue();
            assertThat(methodInvocationException.getCause()
                    .getMessage()
                    .startsWith("Conflict in merge map process.")).isTrue();
        }
    }

    @Test
    @DisplayName("获取全局异常处理器，返回不可修改的 Map")
    void getGlobalExceptionHandlersThenReturnUnmodifiableMap() {
        Map<Class<Throwable>, Map<String, HttpExceptionHandler>> globalExceptionHandlers =
                this.exceptionHandlerResolver.getGlobalExceptionHandlers();
        assertThrows(UnsupportedOperationException.class,
                () -> globalExceptionHandlers.put(Throwable.class,
                        MapBuilder.<String, HttpExceptionHandler>get()
                                .put("1", Mockito.mock(HttpExceptionHandler.class))
                                .build()));
    }

    @Test
    @DisplayName("获取全部键时，返回按照期望排序")
    void shouldOkWhenGetSortedKeySet() {
        Map<Class<Throwable>, String> myMap = new ConcurrentSkipListMap<>(ClassComparator.INSTANCE);
        myMap.put(ObjectUtils.cast(IllegalArgumentException.class), "1");
        myMap.put(ObjectUtils.cast(IllegalAccessException.class), "1");
        myMap.put(ObjectUtils.cast(RuntimeException.class), "1");
        myMap.put(ObjectUtils.cast(FitException.class), "1");
        myMap.put(ObjectUtils.cast(ReflectiveOperationException.class), "1");
        assertThat(myMap.keySet()).containsExactly(ObjectUtils.cast(IllegalAccessException.class),
                ObjectUtils.cast(IllegalArgumentException.class),
                ObjectUtils.cast(ReflectiveOperationException.class),
                ObjectUtils.cast(FitException.class),
                ObjectUtils.cast(RuntimeException.class));
    }
}