/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.genericable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 用于为服务方法的标准测试提供工具类。
 *
 * @author 季聿阶
 * @since 2022-09-10
 */
public class GenericableTestUtils {
    /**
     * 获取指定测试类的 {@link BeforeEach} 方法。
     *
     * @param testClass 表示指定测试类的 {@link Class}{@code <?>}。
     * @return 表示指定测试类的 {@link BeforeEach} 方法的 {@link Optional}{@code <}{@link Method}{@code >}。
     */
    public static Optional<Method> getBeforeEachMethod(Class<?> testClass) {
        return Stream.of(testClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class))
                .findFirst();
    }

    /**
     * 获取指定测试类的 {@link AfterEach} 方法。
     *
     * @param testClass 表示指定测试类的 {@link Class}{@code <?>}。
     * @return 表示指定测试类的 {@link AfterEach} 方法的 {@link Optional}{@code <}{@link Method}{@code >}。
     */
    public static Optional<Method> getAfterEachMethod(Class<?> testClass) {
        return Stream.of(testClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(AfterEach.class))
                .findFirst();
    }

    /**
     * 获取指定测试类的所有测试方法。
     *
     * @param testClass 表示指定测试类的 {@link Class}{@code <?>}。
     * @return 表示指定测试类的所有测试方法的 {@link Stream}{@code <}{@link TestMethod}{@code >}。
     */
    public static Stream<TestMethod> getTestMethods(Class<?> testClass) {
        return Stream.of(testClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Test.class))
                .map(GenericableTestUtils::fromMethod);
    }

    private static TestMethod fromMethod(Method method) {
        DisplayName displayName = method.getDeclaredAnnotation(DisplayName.class);
        if (displayName != null) {
            return TestMethod.builder().displayName(displayName.value()).method(method).build();
        } else {
            return TestMethod.builder().displayName(method.getName()).method(method).build();
        }
    }
}
