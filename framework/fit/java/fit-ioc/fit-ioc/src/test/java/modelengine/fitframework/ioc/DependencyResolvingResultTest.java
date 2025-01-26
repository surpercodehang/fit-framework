/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("测试 DependencyResolvingResult 接口")
class DependencyResolvingResultTest {
    @Test
    @DisplayName("表示失败的结果应返回正确的值")
    void should_return_correct_values_of_failure() {
        DependencyResolvingResult result = DependencyResolvingResult.failure();
        assertFalse(result.resolved());
        assertNull(result.get());
        assertEquals("non-dependency", result.toString());
    }

    @Test
    @DisplayName("表示成功的结果应返回正确的值")
    void should_return_correct_values_of_success() {
        DependencyResolvingResult result = DependencyResolvingResult.success(() -> null);
        assertTrue(result.resolved());
        assertNull(result.get());
        assertEquals("dependency=", result.toString());
    }

    @Test
    @DisplayName("多个表示失败的结果指向相同的实例")
    void should_return_same_instance_of_failure() {
        DependencyResolvingResult r1 = DependencyResolvingResult.failure();
        DependencyResolvingResult r2 = DependencyResolvingResult.failure();
        assertSame(r1, r2);
    }

    @Test
    @DisplayName("表示成功的结果指向不同的实例")
    void should_return_different_instance_of_success() {
        Object dependency = new byte[0];
        DependencyResolvingResult r1 = DependencyResolvingResult.success(() -> dependency);
        DependencyResolvingResult r2 = DependencyResolvingResult.success(() -> dependency);
        assertNotSame(r1, r2);
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    @DisplayName("当比较的两个表示成功的结果为相同的实例时返回 true")
    void should_return_true_when_equals_same_success_result() {
        DependencyResolvingResult result = DependencyResolvingResult.success(() -> new byte[0]);
        assertEquals(result, result);
    }

    @Test
    @DisplayName("当与表示成功的结果比较的实例不是相同类型的实例时返回 false")
    void should_return_false_when_equals_with_success_is_not_success() {
        DependencyResolvingResult success = DependencyResolvingResult.success(() -> new byte[0]);
        DependencyResolvingResult failure = DependencyResolvingResult.failure();
        assertNotEquals(success, failure);
    }
}
