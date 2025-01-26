/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.broker.LocalExecutorRepository;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanMetadata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * 表示 {@link FitableAnnotationResolver} 的单元测试。
 *
 * @author 邱晓霞
 * @since 2024-08-14
 */
@DisplayName("测试 FitableAnnotationResolver")
public class FitableAnnotationResolverTest {
    private BeanMetadata metadata;
    private BeanContainer container;
    private LocalExecutorRepository.Registry registry;
    private final String message = "Genericable id or fitable id does not meet the naming requirements: "
            + "only numbers, uppercase and lowercase letters, and '-', '_', '*', '.' are supported, "
            + "and the length is less than 128.";

    @BeforeEach
    void setup() {
        this.metadata = mock(BeanMetadata.class, RETURNS_DEEP_STUBS);
        this.container = mock(BeanContainer.class);
        this.registry = mock(LocalExecutorRepository.Registry.class);
    }

    @Test
    @DisplayName("校验 genericableId ，若 genericableId 不符合要求，抛出异常")
    void shouldThrowExceptionWhenGenericableIdIsNotAcceptable() throws NoSuchMethodException {
        Method test1 = FitableAnnotationResolverTest.class.getMethod("test1");
        when(this.metadata.runtime()
                .resolverOfAnnotations()
                .resolve(any())
                .getAnnotation(any())).thenReturn(test1.getAnnotation(Fitable.class));
        doNothing().when(this.registry).register(any(), any());
        FitableAnnotationResolver resolver = new FitableAnnotationResolver(container, registry);
        IllegalStateException exception =
                catchThrowableOfType(() -> resolver.resolve(metadata, test1), IllegalStateException.class);
        assertThat(exception).isNotNull().hasMessage(message);
    }

    @Test
    @DisplayName("校验 id ，若 id 不符合要求，抛出异常")
    void shouldThrowExceptionWhenIdIsNotAcceptable() throws NoSuchMethodException {
        Method test2 = FitableAnnotationResolverTest.class.getMethod("test2");
        when(this.metadata.runtime()
                .resolverOfAnnotations()
                .resolve(any())
                .getAnnotation(any())).thenReturn(test2.getAnnotation(Fitable.class));
        doNothing().when(this.registry).register(any(), any());
        FitableAnnotationResolver resolver = new FitableAnnotationResolver(container, registry);
        IllegalStateException exception =
                catchThrowableOfType(() -> resolver.resolve(metadata, test2), IllegalStateException.class);
        assertThat(exception).isNotNull().hasMessage(message);
    }

    @Test
    @DisplayName("校验 fitableId ，若 fitableId 不符合要求，抛出异常")
    void shouldThrowExceptionWhenFitableIdIsNotAcceptable() throws NoSuchMethodException {
        Method test3 = FitableAnnotationResolverTest.class.getMethod("test3");
        when(this.metadata.runtime()
                .resolverOfAnnotations()
                .resolve(any())
                .getAnnotation(any())).thenReturn(test3.getAnnotation(Fitable.class));
        doNothing().when(this.registry).register(any(), any());
        FitableAnnotationResolver resolver = new FitableAnnotationResolver(container, registry);
        IllegalStateException exception =
                catchThrowableOfType(() -> resolver.resolve(metadata, test3), IllegalStateException.class);
        assertThat(exception).isNotNull().hasMessage(message);
    }

    @Test
    @DisplayName("校验 id ，若其符合要求，程序正常运行")
    void shouldRunNormallyWhenIdIsAcceptable() throws NoSuchMethodException {
        Method test4 = FitableAnnotationResolverTest.class.getMethod("test4");
        when(this.metadata.runtime()
                .resolverOfAnnotations()
                .resolve(any())
                .getAnnotation(any())).thenReturn(test4.getAnnotation(Fitable.class));
        doNothing().when(this.registry).register(any(), any());
        FitableAnnotationResolver resolver = new FitableAnnotationResolver(container, registry);
        assertTrue(resolver.resolve(metadata, test4));
    }

    @Test
    @DisplayName("校验 fitableId ，若 fitableId 长度不符合要求，抛出异常")
    void shouldThrowExceptionWhenLengthIsNotAcceptable() throws NoSuchMethodException {
        Method test5 = FitableAnnotationResolverTest.class.getMethod("test5");
        when(this.metadata.runtime()
                .resolverOfAnnotations()
                .resolve(any())
                .getAnnotation(any())).thenReturn(test5.getAnnotation(Fitable.class));
        doNothing().when(this.registry).register(any(), any());
        FitableAnnotationResolver resolver = new FitableAnnotationResolver(container, registry);
        IllegalStateException exception =
                catchThrowableOfType(() -> resolver.resolve(metadata, test5), IllegalStateException.class);
        assertThat(exception).isNotNull().hasMessage(message);
    }

    /**
     * 创建 id 为 "test" 的 Fitable。
     *
     * @return 返回值为 0。
     */
    @Fitable(genericable = "demo/", id = "test")
    public int test1() {
        return 0;
    }

    /**
     * 创建 id 为 "test%" 的 Fitable。
     *
     * @return 返回值为 0。
     */
    @Fitable(genericable = "demo/", id = "test%")
    public int test2() {
        return 0;
    }

    /**
     * 创建 id 为 "test!" 的 Fitable。
     *
     * @return 返回值为 0。
     */
    @Fitable(genericable = "demo", id = "test!")
    public int test3() {
        return 0;
    }

    /**
     * 创建 id 为 "test-1" 的 Fitable。
     *
     * @return 返回值为 0。
     */
    @Fitable(genericable = "demo_", id = "test-1")
    public int test4() {
        return 0;
    }

    /**
     * 创建 id 为 "test-1" 的 Fitable。
     *
     * @return 返回值为 0。
     */
    @Fitable(genericable = "demo_", id = "test-111111111111111111111111111111111111111111111111111111111"
            + "1111111111111111111111111111111111111111111111111111111111111111111")
    public int test5() {
        return 0;
    }
}
