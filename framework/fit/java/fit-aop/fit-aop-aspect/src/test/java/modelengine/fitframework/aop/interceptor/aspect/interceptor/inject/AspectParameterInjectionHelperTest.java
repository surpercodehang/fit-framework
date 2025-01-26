/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.interceptor.inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.aop.JoinPoint;
import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.aop.Signature;
import modelengine.fitframework.aop.interceptor.MethodInvocation;
import modelengine.fitframework.aop.interceptor.MethodJoinPoint;
import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.aop.interceptor.MethodPointcut;
import modelengine.fitframework.aop.interceptor.aspect.interceptor.AspectMethodMatcher;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.DefaultPointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.test.TestService1;
import modelengine.fitframework.aop.interceptor.aspect.type.support.DefaultJoinPoint;
import modelengine.fitframework.aop.interceptor.aspect.type.support.DefaultSignature;
import modelengine.fitframework.aop.interceptor.support.DefaultMethodMatcherCollection;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;

/**
 * {@link AspectParameterInjectionHelper} 单元测试。
 *
 * @author 郭龙飞
 * @since 2023-04-03
 */
@DisplayName("测试 AspectParameterInjectionHelper")
class AspectParameterInjectionHelperTest {
    private Method joinPoint;
    private Method pointcut;

    @BeforeEach
    void setup() throws NoSuchMethodException {
        this.joinPoint = TestService1.class.getDeclaredMethod("m2", String.class);
    }

    @AfterEach
    void teardown() {
        this.joinPoint = null;
    }

    private String test1(JoinPoint joinPoint, String s1) {
        return s1;
    }

    private String test2(ProceedingJoinPoint proceedingJoinPoint, String s1) {
        return s1;
    }

    private String test3(Signature signature, String s1) {
        return s1;
    }

    @Nested
    @DisplayName("测试 JoinPoint")
    class TestJoinPoint {
        private Object[] injectionArgs;

        @BeforeEach
        void init() throws Throwable {
            AspectParameterInjectionHelperTest.this.pointcut =
                    AspectParameterInjectionHelperTest.class.getDeclaredMethod("test1", JoinPoint.class, String.class);
            Object[] args = new Object[] {"Hello"};
            this.injectionArgs =
                    AspectParameterInjectionHelperTest.this.getInjectionArgs("jointPoint", args, null, JoinPoint.class);
        }

        @Test
        @DisplayName("当定义的切面第一个参数是 JoinPoint 时，返回正常信息")
        void givenJoinPointShouldReturnParameter() {
            Object[] args = new Object[] {"Hello"};
            assertThat(this.injectionArgs).hasSize(2);
            DefaultJoinPoint joinPointTmp = ObjectUtils.cast(this.injectionArgs[0]);
            assertThat(joinPointTmp.getArgs()).isEqualTo(args);
            assertThat(joinPointTmp.getMethod()).isEqualTo(AspectParameterInjectionHelperTest.this.joinPoint);
            assertThat(joinPointTmp.getKind()).isEqualTo("method-execution");
            assertThat(joinPointTmp.getId()).isEqualTo(0);
            assertThat(joinPointTmp.toShortString()).isEqualTo("execution(TestService1.m2(..))");
            assertThat(joinPointTmp.toString()).isEqualTo(
                    "execution(String modelengine.fitframework.aop.interceptor.aspect.test.TestService1.m2(String))");
            assertThat(joinPointTmp.toLongString()).isEqualTo(
                    "execution(public java.lang.String modelengine.fitframework.aop.interceptor.aspect.test"
                            + ".TestService1.m2(java.lang.String))");
            assertThat(joinPointTmp.getStaticPart()).isEqualTo(joinPointTmp);
            assertThat(joinPointTmp.getTarget()).isEqualTo(TestService1.class);
            assertThat(joinPointTmp.getThis()).isEqualTo(TestService1.class);
        }

        @Test
        @DisplayName("测试 Signature 类时，返回正常信息")
        void givenSignatureShouldReturnParameter() {
            assertThat(this.injectionArgs).hasSize(2);
            DefaultJoinPoint joinPointTmp = ObjectUtils.cast(this.injectionArgs[0]);
            DefaultSignature signature = ObjectUtils.cast(joinPointTmp.getSignature());
            assertThat(signature.getDeclaringTypeName()).isEqualTo(TestService1.class.getName());
            assertThat(signature.getExceptionTypes()).isEqualTo(new Class[] {});
            assertThat(signature.getModifiers()).isEqualTo(1);
            assertThat(signature.getName()).isEqualTo("m2");
            assertThat(signature.getReturnType()).isEqualTo(String.class);
            assertThat(signature.getParameterNames()).isEqualTo(new String[] {"s1"});
            assertThat(signature.getParameterTypes()).isEqualTo(new Class[] {String.class});
            assertThat(signature.getDeclaringTypeName()).isEqualTo(TestService1.class.getName());
        }
    }

    @Test
    @DisplayName("当定义的切面第一个参数是 ProceedingJoinPoint 时，返回正常信息")
    void givenProceedingJoinPointShouldReturnParameter() throws Throwable {
        this.pointcut = this.getClass().getDeclaredMethod("test2", ProceedingJoinPoint.class, String.class);
        Object[] args = new Object[] {"Hello"};
        Object result = "123";
        Object[] injectionArgs = this.getInjectionArgs("proceedingJoinPoint", args, result, ProceedingJoinPoint.class);
        assertThat(injectionArgs).hasSize(2);
        ProceedingJoinPoint joinPointTmp = ObjectUtils.cast(injectionArgs[0]);
        assertThat(joinPointTmp.proceed()).isEqualTo(result);
        assertThat(joinPointTmp.proceed(args)).isEqualTo(result);
    }

    private Object[] getInjectionArgs(String internalParameters, Object[] args, Object result, Class<?> classType)
            throws Throwable {
        MethodJoinPoint methodJoinPoint = mock(MethodJoinPoint.class);
        when(methodJoinPoint.proceed()).thenReturn(result);
        when(methodJoinPoint.proceed(any())).thenReturn(result);
        MethodInvocation proxied = mock(MethodInvocation.class);
        when(proxied.getMethod()).thenReturn(this.joinPoint);
        when(proxied.getArguments()).thenReturn(args);
        when(proxied.getTarget()).thenReturn(TestService1.class);
        MethodInvocation proxy = mock(MethodInvocation.class);
        when(proxy.getTarget()).thenReturn(TestService1.class);
        when(methodJoinPoint.getProxiedInvocation()).thenReturn(proxied);
        when(methodJoinPoint.getProxyInvocation()).thenReturn(proxy);
        ParameterInjection parameterInjection = Mockito.mock(ParameterInjection.class);
        when(parameterInjection.getJoinPoint()).thenReturn(methodJoinPoint);
        MethodPointcut methodPointcut = mock(MethodPointcut.class);
        DefaultMethodMatcherCollection collection = new DefaultMethodMatcherCollection();
        String exp = "args(s1)";
        PointcutParameter[] parameters = new PointcutParameter[2];
        parameters[0] = new DefaultPointcutParameter(internalParameters, classType);
        parameters[1] = new DefaultPointcutParameter("s1", String.class);
        AspectMethodMatcher matcher =
                new AspectMethodMatcher(exp, AspectParameterInjectionHelperTest.class, parameters);
        MethodMatcher.MatchResult isMatch = matcher.match(this.joinPoint);
        matcher.choose(this.joinPoint, isMatch);
        collection.add(matcher);
        when(methodPointcut.matchers()).thenReturn(collection);
        when(parameterInjection.getPointcut()).thenReturn(methodPointcut);
        ValueInjection returnInjection = new ValueInjection("s1", "jack");
        ValueInjection throwInjection = new ValueInjection("throw", "123");
        return AspectParameterInjectionHelper.getInjectionArgs(this.pointcut,
                new String[] {internalParameters, "s1"},
                parameterInjection,
                returnInjection,
                throwInjection);
    }
}
