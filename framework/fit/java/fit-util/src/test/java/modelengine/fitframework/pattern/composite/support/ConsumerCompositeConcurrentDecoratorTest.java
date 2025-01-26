/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.composite.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.pattern.composite.ConsumerComposite;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * {@link ConsumerCompositeConcurrentDecorator} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-02-17
 */
public class ConsumerCompositeConcurrentDecoratorTest {
    private ConsumerComposite<AtomicInteger> consumer;
    private Consumer<AtomicInteger> existConsumer;

    @BeforeEach
    void setup() {
        this.consumer = ConsumerComposite.createConcurrent();
        this.existConsumer = AtomicInteger::incrementAndGet;
        this.consumer.add(this.existConsumer);
    }

    @AfterEach
    void teardown() {
        this.consumer = null;
        this.existConsumer = null;
    }

    @Nested
    @DisplayName("验证 add(Consumer<T> consumer)")
    class TestAdd {
        @Test
        @DisplayName("当添加1个消费者时，一共有2个消费者")
        void given1MoreConsumerThenGet2Consumers() {
            Consumer<AtomicInteger> anotherConsumer = AtomicInteger::incrementAndGet;
            ConsumerCompositeConcurrentDecoratorTest.this.consumer.add(anotherConsumer);
            List<Consumer<AtomicInteger>> actual
                    = ConsumerCompositeConcurrentDecoratorTest.this.consumer.getConsumers();
            assertThat(actual).isNotNull().hasSize(2);
            AtomicInteger result = new AtomicInteger();
            ConsumerCompositeConcurrentDecoratorTest.this.consumer.accept(result);
            assertThat(result.get()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("验证 addAll(Collection<Consumer<T>> consumers)")
    class TestAddAll {
        @Test
        @DisplayName("当添加1个消费者时，一共有2个消费者")
        void given1MoreConsumerThenGet2Consumers() {
            Consumer<AtomicInteger> anotherConsumer = AtomicInteger::incrementAndGet;
            ConsumerCompositeConcurrentDecoratorTest.this.consumer.addAll(Collections.singletonList(anotherConsumer));
            List<Consumer<AtomicInteger>> actual
                    = ConsumerCompositeConcurrentDecoratorTest.this.consumer.getConsumers();
            assertThat(actual).isNotNull().hasSize(2);
            AtomicInteger result = new AtomicInteger();
            ConsumerCompositeConcurrentDecoratorTest.this.consumer.accept(result);
            assertThat(result.get()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("验证 remove(Consumer<T> consumer)")
    class TestRemove {
        @Test
        @DisplayName("当移除1个消费者时，一共有0个消费者")
        void givenRemove1ExistConsumerThenGet0Consumers() {
            ConsumerCompositeConcurrentDecoratorTest.this.consumer.remove(
                    ConsumerCompositeConcurrentDecoratorTest.this.existConsumer);
            List<Consumer<AtomicInteger>> actual
                    = ConsumerCompositeConcurrentDecoratorTest.this.consumer.getConsumers();
            assertThat(actual).isNotNull().hasSize(0);
            AtomicInteger result = new AtomicInteger();
            ConsumerCompositeConcurrentDecoratorTest.this.consumer.accept(result);
            assertThat(result.get()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("验证 removeAll(Collection<Consumer<T>> consumers)")
    class TestRemoveAll {
        @Test
        @DisplayName("当移除1个消费者时，一共有0个消费者")
        void givenRemove1ExistConsumerThenGet0Consumers() {
            ConsumerCompositeConcurrentDecoratorTest.this.consumer.removeAll(
                    Collections.singletonList(ConsumerCompositeConcurrentDecoratorTest.this.existConsumer));
            List<Consumer<AtomicInteger>> actual
                    = ConsumerCompositeConcurrentDecoratorTest.this.consumer.getConsumers();
            assertThat(actual).isNotNull().hasSize(0);
            AtomicInteger result = new AtomicInteger();
            ConsumerCompositeConcurrentDecoratorTest.this.consumer.accept(result);
            assertThat(result.get()).isEqualTo(0);
        }
    }
}
