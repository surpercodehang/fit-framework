/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * {@link OptionalUtils} 的测试类.
 *
 * @author 张越
 * @since 2021-02-02
 */
public class OptionalUtilsTest {
    @Test
    public void when_get_result_is_1_and_default_is_2_then_return_1() {
        int result = OptionalUtils.get(() -> Optional.of(1)).orDefault(2);
        assertSame(1, result);
    }

    @Test
    public void when_get_result_is_empty_or_else_result_is_1_default_is_2_then_return_1() {
        int result = (int) OptionalUtils.get(Optional::empty).orElse(() -> Optional.of(1)).orDefault(2);
        assertSame(1, result);
    }

    @Test
    public void when_get_result_is_1_or_else_result_is_2_default_is_3_then_return_1() {
        int result = OptionalUtils.get(() -> Optional.of(1)).orElse(() -> Optional.of(2)).orDefault(3);
        assertSame(1, result);
    }

    @Test
    public void when_get_result_is_empty_and_or_else_result_is_empty_default_is_2_then_return_2() {
        int result = (int) OptionalUtils.get(Optional::empty).orElse(Optional::empty).orDefault(2);
        assertSame(2, result);
    }

    @Test
    public void when_get_result_is_empty_and_default_mapper_result_is_2_then_return_2() {
        int result = (int) OptionalUtils.get(Optional::empty).orGetDefault(() -> 2);
        assertSame(2, result);
    }

    @Test
    public void when_get_result_is_empty_and_multi_or_else_is_empty_and_default_is_3_then_return_3() {
        int result = (int) OptionalUtils.get(Optional::empty)
                .orElse(Optional::empty)
                .orElse(Optional::empty)
                .orElse(Optional::empty)
                .orElse(Optional::empty)
                .orElse(Optional::empty)
                .orDefault(3);
        assertSame(3, result);
    }

    @Test
    public void when_get_result_is_empty_and_middle_or_else_result_is_2_and_default_is_3_then_return_2() {
        int result = (int) OptionalUtils.get(Optional::empty)
                .orElse(Optional::empty)
                .orElse(Optional::empty)
                .orElse(() -> Optional.of(2))
                .orElse(Optional::empty)
                .orElse(Optional::empty)
                .orDefault(3);
        assertSame(2, result);
    }

    @Test
    public void when_result_is_empty_has_exception_supplier_then_throw_exception() {
        try {
            OptionalUtils.get(Optional::empty).orElseThrow(IllegalStateException::new);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalStateException);
        }
    }

    @Test
    public void when_result_is_not_empty_has_exception_supplier_then_get_result() {
        int actual = OptionalUtils.get(() -> Optional.of(1)).orElseThrow(IllegalStateException::new);
        Assertions.assertThat(actual).isEqualTo(1);
    }
}
