/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * 为 {@link TypeMatcher.Context} 提供单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-10-29
 */
public class TypeMatcherContextTest {
    @Test
    public void should_return_redirected_argument() {
        TypeMatcher.Context context1 = TypeMatcher.Context.builder().setVariableValue("E1", Integer.class).build();
        TypeMatcher.Context context2 = TypeMatcher.Context.builder()
                .setVariableValue("E1", Short.class)
                .setVariableValue("E2", () -> context1.getVariableValue("E1").orElse(null))
                .build();
        TypeMatcher.Context context3 = TypeMatcher.Context.builder()
                .setVariableValue("E1", Byte.class)
                .setVariableValue("E2", () -> context2.getVariableValue("E1").orElse(null))
                .setVariableValue("E3", () -> context2.getVariableValue("E2").orElse(null))
                .build();
        assertEquals(Byte.class, context3.getVariableValue("E1").orElse(null));
        assertEquals(Short.class, context3.getVariableValue("E2").orElse(null));
        assertEquals(Integer.class, context3.getVariableValue("E3").orElse(null));
    }

    @Test
    public void should_return_empty() {
        TypeMatcher.Context context = TypeMatcher.Context.builder().build();
        Optional<Type> type = context.getVariableValue("E1");
        assertThat(type).isEmpty();
    }
}
