/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.aop.MethodSignature;
import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.retry.annotation.Recover;
import modelengine.fitframework.retry.annotation.Retryable;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.ReflectionUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * {@link RetryableHandler} 的单元测试类。
 *
 * @author 邬涨财
 * @since 2023-10-13
 */
public class RetryableHandlerTest {
    @Nested
    @DisplayName("当调用的方法有 Retryable 注解，且设置了 recover 方法和最大尝试次数")
    class MethodWithRetryable {
        @Test
        @DisplayName("超过设置最大尝试次数，recover 方法被调用")
        void retryGreaterLimitedTimes() throws Throwable {
            // given
            ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
            MethodSignature signature = mock(MethodSignature.class);
            RetryableCalled retryableCalled = new RetryableCalled();
            Method retryableMethod = ReflectionUtils.getDeclaredMethod(RetryableCalled.class,
                    "retryableMethod",
                    String.class,
                    String.class,
                    int.class);
            Method recoverMethod = ReflectionUtils.getDeclaredMethod(RetryableCalled.class,
                    "recoverMethod",
                    Exception.class,
                    String.class,
                    String.class,
                    int.class);
            Object[] args = new Object[] {"param1", "param2", 1};
            BeanContainer beanContainer = mock(BeanContainer.class);
            FitRuntime fitRuntime = mock(FitRuntime.class);
            AnnotationMetadataResolver annotationMetadataResolver = mock(AnnotationMetadataResolver.class);
            AnnotationMetadata retryableAm = mock(AnnotationMetadata.class);
            AnnotationMetadata recoverAm = mock(AnnotationMetadata.class);

            // when
            when(joinPoint.getTarget()).thenReturn(retryableCalled);
            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.getName()).thenReturn(retryableMethod.getName());
            when(signature.getParameterTypes()).thenReturn(retryableMethod.getParameterTypes());
            when(joinPoint.getArgs()).thenReturn(args);
            when(joinPoint.proceed(args)).thenThrow(new IllegalStateException());
            when(beanContainer.runtime()).thenReturn(fitRuntime);
            when(fitRuntime.resolverOfAnnotations()).thenReturn(annotationMetadataResolver);
            when(annotationMetadataResolver.resolve(retryableMethod)).thenReturn(retryableAm);
            when(annotationMetadataResolver.resolve(recoverMethod)).thenReturn(recoverAm);
            when(retryableAm.isAnnotationPresent(Retryable.class)).thenReturn(true);
            when(retryableAm.getAnnotation(Retryable.class)).thenReturn(retryableMethod.getAnnotation(Retryable.class));
            when(recoverAm.isAnnotationPresent(Recover.class)).thenReturn(true);
            when(recoverAm.getAnnotation(Recover.class)).thenReturn(recoverMethod.getAnnotation(Recover.class));
            RetryableHandler handler = new RetryableHandler(beanContainer);
            Method handleMethod =
                    ReflectionUtils.getDeclaredMethod(RetryableHandler.class, "handle", ProceedingJoinPoint.class);
            ReflectionUtils.invoke(handler, handleMethod, joinPoint);

            // then
            assertThat(retryableCalled.getInvokeTimes()).isEqualTo(1);
        }
    }

    static class RetryableCalled {
        private int invokeTimes = 0;

        public int getInvokeTimes() {
            return this.invokeTimes;
        }

        /**
         * 用于重试的方法。
         *
         * @param param1 表示第一个参数的 {@link String}。
         * @param param2 表示第二个参数的 {@link String}。
         * @param param3 表示第三个参数的 {@link String}。
         */
        @Retryable(recover = "recoverMethod", maxAttempts = 4)
        public void retryableMethod(String param1, String param2, int param3) {
            // 不做任何操作
        }

        @Recover
        private void recoverMethod(Exception exception, String param1, String param2, int param3) {
            ++this.invokeTimes;
        }
    }
}
