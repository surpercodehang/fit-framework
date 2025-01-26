/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.util.counter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link ThreadSafeCounter} 的单元测试。
 *
 * @author 何天放
 * @since 2024-02-20
 */
@DisplayName("线程安全的计数器测试")
class ThreadSafeCounterTest {
    @Nested
    @DisplayName("测试数值变化操作，创建一个计数器")
    class TestValueChange {
        Counter counter;

        @BeforeEach
        void createCounter() {
            counter = Counter.create();
        }

        @Test
        @DisplayName("增加默认数值")
        void createCounterAndIncreaseDefault() {
            assertThat(counter.increase()).isEqualTo(1);
            assertThat(counter.getValue()).isEqualTo(1);
        }

        @Test
        @DisplayName("增加非默认数值")
        void increaseNonDefaultValue() {
            assertThat(counter.increase(2)).isEqualTo(2);
            assertThat(counter.getValue()).isEqualTo(2);
        }

        @Test
        @DisplayName("先增加至截止再减少到截止，过程中的值变化和最终计数值符合预期")
        void shouldReturnZeroWhenIncreaseAndDecreaseMaxThenGetValue() {
            assertThat(counter.increase()).isEqualTo(1);
            assertThat(counter.getValue()).isEqualTo(1);
            assertThat(counter.increase(Long.MAX_VALUE - 1)).isEqualTo(Long.MAX_VALUE - 1);
            assertThat(counter.getValue()).isEqualTo(Long.MAX_VALUE);
            assertThat(counter.decrease()).isEqualTo(1);
            assertThat(counter.getValue()).isEqualTo(Long.MAX_VALUE - 1);
            assertThat(counter.decrease(Long.MAX_VALUE - 1)).isEqualTo(Long.MAX_VALUE - 1);
            assertThat(counter.getValue()).isEqualTo(0);
        }

        @Test
        @DisplayName("尝试增加负数时，应当抛出异常")
        void shouldThrowIllegalArgumentExceptionWhenIncreaseNegativeNumber() {
            assertThatThrownBy(() -> {
                counter.increase(-1);
            }).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The value of increase must be positive. [value=-1]");
        }
    }

    private static class ObserverForTest implements CounterValueChangedObserver {
        private Counter counter;
        private long before;
        private long after;
        private long calledCount;

        @Override
        public void onValueChanged(Counter counter, long pre, long next) {
            this.counter = counter;
            this.before = pre;
            this.after = next;
            this.calledCount++;
        }
    }

    @Nested
    @DisplayName("测试计数器观察者功能，创建一个计数器并添加一个观察者")
    class TestCounterValueChangedObserver {
        Counter counter;
        ObserverForTest observer1;
        ObserverForTest observer2;

        @BeforeEach
        void createCounterAndAddOneObserver() {
            counter = Counter.create();
            observer1 = new ObserverForTest();
            counter.observeValueChanged(observer1);
        }

        @Test
        @DisplayName("调用数值增加接口将触发对于观察者的通知")
        void observerShouldBeNotifiedWhenIncrease() {
            this.counter.increase();
            assertThat(observer1.counter).isSameAs(counter);
            assertThat(observer1.before).isEqualTo(0);
            assertThat(observer1.after).isEqualTo(1);
            assertThat(observer1.calledCount).isEqualTo(1);
        }

        @Test
        @DisplayName("再增加一个相同的观察者，调用数值增加接口将触发对于观察者的两次通知")
        void addAnotherObserverAndBothShouldBeNotifiedWhenIncrease() {
            this.counter.observeValueChanged(this.observer1);
            this.counter.increase();
            assertThat(observer1.counter).isSameAs(counter);
            assertThat(observer1.before).isEqualTo(0);
            assertThat(observer1.after).isEqualTo(1);
            assertThat(observer1.calledCount).isEqualTo(2);
        }

        @Nested
        @DisplayName("再增加一个不同的观察者，并且调用数值增加接口")
        class TestDifferentObserver {
            @BeforeEach
            void addAnotherObserverAndIncrease() {
                observer2 = new ObserverForTest();
                counter.observeValueChanged(observer2);
                counter.increase();
            }

            @Test
            @DisplayName("调用数值增加接口将触发对于两个观察者的调用")
            void bothObserverShouldBeNotified() {
                assertThat(observer1.counter).isSameAs(counter);
                assertThat(observer1.before).isEqualTo(0);
                assertThat(observer1.after).isEqualTo(1);
                assertThat(observer1.calledCount).isEqualTo(1);
                assertThat(observer2.counter).isSameAs(counter);
                assertThat(observer2.before).isEqualTo(0);
                assertThat(observer2.after).isEqualTo(1);
                assertThat(observer2.calledCount).isEqualTo(1);
            }

            @Test
            @DisplayName("取消其中一个观察者的订阅，并且调用数值增加接口")
            void unobservedObserverShouldNotBeNotifiedWhenCancelled() {
                counter.unobserveValueChanged(observer1);
                counter.increase();
                assertThat(observer1.counter).isSameAs(counter);
                assertThat(observer1.before).isEqualTo(0);
                assertThat(observer1.after).isEqualTo(1);
                assertThat(observer1.calledCount).isEqualTo(1);
                assertThat(observer2.counter).isSameAs(counter);
                assertThat(observer2.before).isEqualTo(1);
                assertThat(observer2.after).isEqualTo(2);
                assertThat(observer2.calledCount).isEqualTo(2);
            }
        }
    }
}
