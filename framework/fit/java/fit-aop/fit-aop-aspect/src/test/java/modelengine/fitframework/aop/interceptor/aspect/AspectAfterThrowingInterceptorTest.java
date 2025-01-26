/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.aop.interceptor.aspect.interceptor.AspectAfterThrowingInterceptor;
import modelengine.fitframework.ioc.BeanFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * {@link AspectAfterThrowingInterceptor} 单元测试。
 *
 * @author 季聿阶
 * @since 2022-05-23
 */
@DisplayName("测试 AspectAfterThrowingInterceptor")
public class AspectAfterThrowingInterceptorTest {
    @Test
    @DisplayName("当 @AfterThrowing 定义的切面第一个参数是 ProceedingJoinPoint 时，生成方法拦截器失败")
    void givenAfterThrowingMethodWithProceedingJoinPointAsThe1stParamThenFailToGenerateMethodInterceptor()
            throws NoSuchMethodException {
        Method method = AspectAfterThrowingInterceptorTest.class.getDeclaredMethod(
                "afterThrowingWithProceedingJoinPoint",
                ProceedingJoinPoint.class);
        BeanFactory aspectFactory = mock(BeanFactory.class);
        Object aspect = new AspectAfterThrowingInterceptorTest();
        when(aspectFactory.get()).thenReturn(aspect);
        IllegalArgumentException exception =
                catchThrowableOfType(() -> new AspectAfterThrowingInterceptor(aspectFactory, method),
                        IllegalArgumentException.class);
        assertThat(exception).hasMessage(
                "The 1st parameter of @AfterThrowing interceptor in Aspect cannot be ProceedingJoinPoint.");
    }

    void afterThrowingWithProceedingJoinPoint(@SuppressWarnings("unused") ProceedingJoinPoint joinPoint) {}
}
