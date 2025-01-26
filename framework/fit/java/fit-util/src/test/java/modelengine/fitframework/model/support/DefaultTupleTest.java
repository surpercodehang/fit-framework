/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fitframework.model.Tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * 为 {@link DefaultTuple} 提供单元测试。
 *
 * @author 白鹏坤
 * @since 2023-01-31
 */
@DisplayName("测试 DefaultTuple 工具类")
class DefaultTupleTest {
    private Tuple tuple;

    @BeforeEach
    @DisplayName("实例化 Tuple 类")
    void setUp() {
        final List<Object> list = Arrays.asList("a", "b", "c");
        tuple = new DefaultTuple(list);
    }

    @Nested
    @DisplayName("获取元组元素")
    class TestGet {
        @Test
        @DisplayName("当提供已存在元素的序号，返回对应的元素")
        void givenExistElementIndexThenReturnElement() {
            final Object element = tuple.get(0).orElse(null);
            assertThat(element).isEqualTo("a");
        }

        @Test
        @DisplayName("当提供不存在元素的序号，抛出索引越界异常")
        void givenExistElementIndexThenThrowException() {
            assertThatThrownBy(() -> tuple.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> tuple.get(10)).isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @Test
    @DisplayName("获取元组大小")
    void testCapacity() {
        final Object element = tuple.capacity();
        assertThat(element).isEqualTo(3);
    }

    @Test
    @DisplayName("创建包含一个元素的元组")
    void testSole() {
        final Tuple solo = Tuple.solo("a");
        assertThat(solo).isNotNull();
        assertThat(solo.capacity()).isEqualTo(1);
    }

    @Test
    @DisplayName("创建包含两个元素的元组")
    void testDuet() {
        final Tuple duet = Tuple.duet("a", "b");
        assertThat(duet).isNotNull();
        assertThat(duet.capacity()).isEqualTo(2);
    }

    @Test
    @DisplayName("创建包含三个元素的元组")
    void testTrio() {
        final Tuple trio = Tuple.trio("a", 'b', Object.class);
        assertThat(trio).isNotNull();
        assertThat(trio.capacity()).isEqualTo(3);
    }

    @Test
    @DisplayName("创建包含四个元素的元组")
    void testQuartet() {
        final Tuple quartet = Tuple.quartet("a", 10, "c", 10);
        assertThat(quartet).isNotNull();
        assertThat(quartet.capacity()).isEqualTo(4);
    }

    @Test
    @DisplayName("创建包含五个元素的元组")
    void testQuintet() {
        final Tuple quintet = Tuple.quintet("a", 10, "c", 11, new BigInteger("12"));
        assertThat(quintet).isNotNull();
        assertThat(quintet.capacity()).isEqualTo(5);
    }
}
