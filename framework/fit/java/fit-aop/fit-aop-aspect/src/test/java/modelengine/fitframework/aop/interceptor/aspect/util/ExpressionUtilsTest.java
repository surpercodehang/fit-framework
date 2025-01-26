/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link ExpressionUtils} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-04-21
 */
@DisplayName("测试 ExpressionUtils 类")
class ExpressionUtilsTest {
    @Test
    @DisplayName("当提供空列表时，抛出异常")
    void GivenEmptyListThenThrowException() {
        assertThatThrownBy(() -> ExpressionUtils.computeBoolExpression(new ArrayList<>())).isInstanceOf(
                IllegalArgumentException.class).hasMessage("The List cannot be empty.");
    }

    @Test
    @DisplayName("当提供列表包含不期望的字符串时，抛出异常")
    void GivenNotExpectedStringThenThrowException() {
        List<String> list = Collections.singletonList("flase");
        assertThatThrownBy(() -> ExpressionUtils.computeBoolExpression(list))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("As bool expression, The List cannot be computed. [boolExpression=[flase]]");
    }

    @Test
    @DisplayName("当提供列表包含只有右括号时，抛出异常")
    void GivenNotPairBracket1ThenThrowException() {
        List<String> list = Arrays.asList("true", ")");
        assertThatThrownBy(() -> ExpressionUtils.computeBoolExpression(list))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("'(' and ')' must appear in pairs.");
    }

    @Test
    @DisplayName("当提供列表包含只有左括号时，抛出异常")
    void GivenNotPairBracket2ThenThrowException() {
        List<String> list = Arrays.asList("(", "true");
        assertThatThrownBy(() -> ExpressionUtils.computeBoolExpression(list))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The expression format error. [expression=[(, true]]");
    }

    @Test
    @DisplayName("当提供列表包含 1 个左括号，2 个右括号时，抛出异常")
    void GivenNotPairBracket3ThenThrowException() {
        List<String> list = Arrays.asList("(", "true", ")", ")");
        assertThatThrownBy(() -> ExpressionUtils.computeBoolExpression(list))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("'(' and ')' must appear in pairs.");
    }

    @Test
    @DisplayName("当提供列表不能被计算时，抛出异常")
    void GivenCannotComputeListThenThrowException() {
        List<String> list = Arrays.asList("false", "true");
        assertThatThrownBy(() -> ExpressionUtils.computeBoolExpression(list))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The expression format error. [expression=[false, true]]");
    }

    @Test
    @DisplayName("当提供列表只有 true 字符串时，返回 true")
    void GivenListOnlyIncludeTrueStringThenReturnTrue() {
        List<String> list = Collections.singletonList("true");
        final boolean operation = ExpressionUtils.computeBoolExpression(list);
        assertThat(operation).isTrue();
    }

    @Test
    @DisplayName("当提供列表有 && 字符串，期望 false 时，返回 false")
    void GivenListIncludeAndOperationThenReturnFalse() {
        List<String> list = Arrays.asList("!", "true", "&&", "true");
        final boolean operation = ExpressionUtils.computeBoolExpression(list);
        assertThat(operation).isFalse();
    }

    @Test
    @DisplayName("当提供列表有 || 字符串，期望 true 时，返回 true")
    void GivenListIncludeOrOperationThenReturnTrue() {
        List<String> list = Arrays.asList("true", "||", "false");
        final boolean operation = ExpressionUtils.computeBoolExpression(list);
        assertThat(operation).isTrue();
    }

    @Test
    @DisplayName("当提供列表有 () 字符串，期望 true 时，返回 true")
    void GivenListIncludeBracketOperationThenReturnTrue() {
        final List<String> list = Arrays.asList("true", "&&", "(", "true", "||", "false", ")");
        final boolean operation = ExpressionUtils.computeBoolExpression(list);
        assertThat(operation).isTrue();
    }
}
