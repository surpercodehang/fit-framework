/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.server.HttpClassicServer;
import modelengine.fit.http.server.HttpHandlerGroup;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.handler.HttpResponseStatusResolver;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fit.http.server.handler.PropertyValueMetadataResolver;
import modelengine.fit.http.server.handler.exception.RequestParamFetchException;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.annotation.support.EmptyAnnotationMetadata;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.TypeUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 表示 {@link DefaultHttpHandlerResolver} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-23
 */
@DisplayName("测试 DefaultHttpHandlerResolver 类")
public class DefaultHttpHandlerResolverTest {
    private DefaultHttpHandlerResolver defaultHttpHandlerResolver;
    private AnnotationMetadataResolver annotationMetadataResolver;

    @BeforeEach
    void setup() {
        HttpClassicServer httpServer = mock(HttpClassicServer.class);
        BeanContainer container = mock(BeanContainer.class);
        FitRuntime fitRuntime = mock(FitRuntime.class);
        this.annotationMetadataResolver = mock(AnnotationMetadataResolver.class);
        when(fitRuntime.resolverOfAnnotations()).thenReturn(this.annotationMetadataResolver);
        when(container.runtime()).thenReturn(fitRuntime);
        this.defaultHttpHandlerResolver = new DefaultHttpHandlerResolver(httpServer, container);
    }

    @Nested
    @DisplayName("测试 resolve() 方法")
    class TestResolve {
        private BeanFactory beanFactory;
        private List<HttpServerFilter> preFilters;
        private PropertyValueMapperResolver mapperResolver;
        private PropertyValueMetadataResolver metadataResolver;
        private HttpResponseStatusResolver responseStatusResolver;

        @BeforeEach
        void setUp() {
            this.beanFactory = this.initializeBeanFactory();
            this.preFilters = new ArrayList<>();
            HttpServerFilter httpServerFilter = mock(HttpServerFilter.class);
            this.preFilters.add(httpServerFilter);
            this.mapperResolver = mock(PropertyValueMapperResolver.class);
            this.metadataResolver = mock(PropertyValueMetadataResolver.class);
            this.responseStatusResolver = mock(HttpResponseStatusResolver.class);
        }

        private Optional<HttpHandlerGroup> getResolve() {
            return DefaultHttpHandlerResolverTest.this.defaultHttpHandlerResolver.resolve(this.beanFactory,
                    this.preFilters,
                    Optional::empty,
                    this.mapperResolver,
                    this.metadataResolver,
                    this.responseStatusResolver);
        }

        @Test
        @DisplayName("给定合理的参数，返回值不为空")
        void givenValidParametersThenReturnIsNotEmpty() {
            Optional<HttpHandlerGroup> resolve = this.getResolve();
            assertThat(resolve).isNotEmpty();
        }

        @Test
        @DisplayName("触发异常逻辑")
        void givenInvalidParameterThenThrowRequestParamFetchException() {
            when(this.mapperResolver.resolve(any())).thenThrow(new RequestParamFetchException("test"));
            RequestParamFetchException exception = assertThrows(RequestParamFetchException.class, this::getResolve);
            String expectedPattern = "Invalid request parameter.*";
            assertTrue(exception.getMessage().matches(expectedPattern),
                    "Exception message does not match the expected pattern");
        }

        private BeanFactory initializeBeanFactory() {
            BeanFactory candidate = mock(BeanFactory.class);
            when(candidate.get()).thenReturn("testsCandidate");
            BeanMetadata beanMetadata = mock(BeanMetadata.class);
            when(beanMetadata.name()).thenReturn("mock");
            when(candidate.metadata()).thenReturn(beanMetadata);
            AnnotationMetadata mock = EmptyAnnotationMetadata.INSTANCE;
            AnnotationMetadata annotationMetadata = mock(mock.getClass());
            when(annotationMetadata.isAnnotationPresent(RequestMapping.class)).thenReturn(true);
            RequestMapping requestMapping = mock(RequestMapping.class);
            when(annotationMetadata.getAnnotation(RequestMapping.class)).thenReturn(requestMapping);
            Type type = Boolean.class;
            String[] testPath = {"testResolve/main,testResolve/modify/main,testResolve/helloWord"};
            when(requestMapping.path()).thenReturn(testPath);
            when(DefaultHttpHandlerResolverTest.this.annotationMetadataResolver.resolve(TypeUtils.toClass(type)))
                    .thenReturn(annotationMetadata);
            when(beanMetadata.type()).thenReturn(type);
            Method[] methods = TypeUtils.toClass(candidate.metadata().type()).getDeclaredMethods();
            for (int i = methods.length - 1; i >= 0; i--) {
                when(DefaultHttpHandlerResolverTest.this.annotationMetadataResolver.resolve(
                        TypeUtils.toClass(candidate.metadata().type()).getDeclaredMethods()[i]))
                        .thenReturn(annotationMetadata);
            }
            return candidate;
        }
    }
}
