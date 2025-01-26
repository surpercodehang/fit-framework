/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.type;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

/**
 * @author 梁济时
 * @since 2020-11-17
 */
public class TypeBoundaryMatcherTest {
    private static Interface1<Integer> field;

    private static Type getGenericType() {
        try {
            return TypeBoundaryMatcherTest.class.getDeclaredField("field").getGenericType();
        } catch (NoSuchFieldException ex) {
            throw new IllegalStateException("Field not found: field");
        }
    }

    @Test
    public void should_match_when_implements_with_boundary() {
        assertTrue(TypeMatcher.match(ConcreteClass.class, getGenericType()));
    }

    private interface Interface1<T extends Number> {}

    private static class ConcreteClass implements Interface1<Integer> {}
}
