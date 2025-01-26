/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.util.TypeUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 为 {@link ParameterizedTypeResolver} 提供单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-07-04
 */
@DisplayName("测试 ParameterizedTypeResolver 工具类")
class ParameterizedTypeResolverTest {
    @Test
    @DisplayName("当前类型未继承自所期望的类型，解析失败")
    void shouldThrowWhenCurrentTypeDoesNotInheritFromExpected() {
        ParameterizedType current = mock(ParameterizedType.class);
        when(current.getRawType()).thenReturn(List.class);
        when(current.getActualTypeArguments()).thenReturn(new Type[] {String.class});
        Class<?> expected = Set.class;
        ParameterizedTypeResolvingResult result = ParameterizedTypeResolver.resolve(current, expected);
        assertFalse(result.resolved());
    }

    @Test
    @DisplayName("当所期望的类型不是泛型类型时，返回空的类型参数列表")
    void shouldReturnEmptyListWhenExpectedTypeIsNotGeneric() {
        ParameterizedType current = mock(ParameterizedType.class);
        when(current.getRawType()).thenReturn(ArrayList.class);
        when(current.getActualTypeArguments()).thenReturn(new Type[] {String.class});
        Class<?> expected = Object.class;
        ParameterizedTypeResolvingResult result = ParameterizedTypeResolver.resolve(current, expected);
        assertTrue(result.resolved());
        assertTrue(result.parameters().isEmpty());
    }

    @Test
    @DisplayName("当前类型继承或实现了泛型类时，返回类型参数列表")
    void shouldReturnTypeParameters() {
        ParameterizedType current = mock(ParameterizedType.class);
        when(current.getRawType()).thenReturn(HashMap.class);
        when(current.getActualTypeArguments()).thenReturn(new Type[] {String.class, Integer.class});
        Class<?> expected = Map.class;
        ParameterizedTypeResolvingResult result = ParameterizedTypeResolver.resolve(current, expected);
        assertTrue(result.resolved());
        assertEquals(2, result.parameters().size());
        assertEquals(String.class, result.parameters().get(0));
        assertEquals(Integer.class, result.parameters().get(1));
    }

    @Test
    @DisplayName("当前类型实现了目标泛型接口，返回目标泛型接口的泛型参数列表")
    void shouldReturnCorrectListWhenImplementTargetInterface() {
        ParameterizedType current = TypeUtils.parameterized(C1.class, new Type[] {String.class});
        ParameterizedTypeResolvingResult result = ParameterizedTypeResolver.resolve(current, I1.class);
        assertThat(result.resolved()).isTrue();
        assertThat(result.parameters()).hasSize(2).containsExactly(Integer.class, Long.class);
    }

    @Test
    @DisplayName("当前类型的父类实现了目标泛型接口，返回目标泛型接口的泛型参数列表")
    void shouldReturnCorrectListWhenParentImplementTargetInterface() {
        ParameterizedType current = TypeUtils.parameterized(C1.class, new Type[] {String.class});
        ParameterizedTypeResolvingResult result = ParameterizedTypeResolver.resolve(current, I2.class);
        assertThat(result.resolved()).isTrue();
        assertThat(result.parameters()).hasSize(1).containsExactly(String.class);
    }

    @Test
    @DisplayName("当前类型的父类实现了目标泛型接口的子接口，返回目标泛型接口的泛型参数列表")
    void shouldReturnCorrectListWhenParentImplementTargetSubInterface() {
        ParameterizedType current = TypeUtils.parameterized(C1.class, new Type[] {String.class});
        ParameterizedTypeResolvingResult result = ParameterizedTypeResolver.resolve(current, I3.class);
        assertThat(result.resolved()).isTrue();
        assertThat(result.parameters()).hasSize(1).containsExactly(String.class);
    }

    @Test
    @DisplayName("当前类型存在泛型参数，返回泛型参数列表")
    void shouldReturnCorrectListWhenCurrentClassHasGenericTypes() {
        ParameterizedType current = TypeUtils.parameterized(C1.class, new Type[] {String.class});
        ParameterizedTypeResolvingResult result = ParameterizedTypeResolver.resolve(current, C1.class);
        assertThat(result.resolved()).isTrue();
        assertThat(result.parameters()).hasSize(1).containsExactly(String.class);
    }

    @Test
    @DisplayName("当前类型的父类存在泛型参数，返回泛型参数列表")
    void shouldReturnCorrectListWhenParentClassHasGenericTypes() {
        ParameterizedType current = TypeUtils.parameterized(C1.class, new Type[] {String.class});
        ParameterizedTypeResolvingResult result = ParameterizedTypeResolver.resolve(current, C2.class);
        assertThat(result.resolved()).isTrue();
        assertThat(result.parameters()).hasSize(1).containsExactly(Integer.class);
    }

    @SuppressWarnings("unused")
    static class C1<String> extends C2<Integer> implements I1<Integer, Long> {}

    @SuppressWarnings("unused")
    static class C2<T1> implements I2<String> {}

    @SuppressWarnings("unused")
    interface I1<T1, T2> {}

    interface I2<T1> extends I3<T1> {}

    @SuppressWarnings("unused")
    interface I3<T1> {}
}
