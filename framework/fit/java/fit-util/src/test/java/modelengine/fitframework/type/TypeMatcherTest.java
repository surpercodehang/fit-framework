/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.type;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

/**
 * 为 {@link TypeMatcher} 提供单元测试。
 *
 * @author 梁济时
 * @since 2020-10-29
 */
public class TypeMatcherTest {
    private static Interface1<Byte, Short, Integer> field;

    private static Type getGenericType() {
        try {
            return TypeMatcherTest.class.getDeclaredField("field").getGenericType();
        } catch (NoSuchFieldException ex) {
            throw new IllegalStateException("Field not found: field");
        }
    }

    @Test
    public void should_match_direct_implemented_interface() {
        boolean matched = TypeMatcher.match(ConcreteClass.class, Interface1.class);
        assertTrue(matched);
    }

    @Test
    public void should_match_propagated_type_arguments() {
        Type genericType = getGenericType();
        boolean matched = TypeMatcher.match(AbstractClass2.class, genericType);
        assertTrue(matched);
    }

    @Test
    public void should_not_match_when_type_arguments_not_match() {
        Type genericType = getGenericType();
        boolean matched = TypeMatcher.match(AbstractClass3.class, genericType);
        assertFalse(matched);
    }

    @Test
    public void should_match_class_of_generic() {
        Type genericType = getGenericType();
        boolean matched = TypeMatcher.match(genericType, Interface1.class);
        assertTrue(matched);
    }

    private interface Interface1<E1, E2, E3> {}

    private interface Interface2<E1, E2> extends Interface1<Byte, E1, E2> {}

    private abstract static class AbstractClass1<E1> implements Interface2<Short, E1> {}

    private abstract static class AbstractClass2 extends AbstractClass1<Integer> {}

    private abstract static class AbstractClass3 extends AbstractClass1<String> {}

    private static class ConcreteClass implements Interface1<Byte, Short, Integer> {}
}
