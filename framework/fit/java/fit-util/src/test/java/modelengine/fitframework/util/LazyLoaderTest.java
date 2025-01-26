/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link LazyLoader} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-09-28
 */
public class LazyLoaderTest {
    @Nested
    @DisplayName("Test 1 thread")
    class Test1Thread {
        @Test
        @DisplayName("Given fetch twice then get the same result")
        void givenFetchTwiceThenReturnTheSameResult() {
            LazyLoader<Object> loader = new LazyLoader<>(Object::new);
            Object first = loader.get();
            Object second = loader.get();
            assertThat(first).isNotNull().isEqualTo(second);
        }
    }

    @Nested
    @DisplayName("Test 2 threads")
    class Test2Threads {
        private Object first;
        private Object second;

        @BeforeEach
        void setup() {
            this.first = null;
            this.second = null;
        }

        @AfterEach
        void teardown() {
            this.first = null;
            this.second = null;
        }

        @Test
        @DisplayName("Given fetch twice at the same time then get the same result")
        void givenFetchTwiceAtTheSameTimeThenReturnTheSameResult() {
            LazyLoader<Object> loader = new LazyLoader<>(() -> {
                ThreadUtils.sleep(10);
                return new Object();
            });
            Thread t1 = new Thread(() -> this.first = loader.get());
            Thread t2 = new Thread(() -> this.second = loader.get());
            t1.setName("LazyLoaderTest-Thread-1");
            t2.setName("LazyLoaderTest-Thread-2");
            t1.setUncaughtExceptionHandler((thread, ex) -> {});
            t2.setUncaughtExceptionHandler((thread, ex) -> {});
            t1.start();
            t2.start();
            ThreadUtils.join(t1);
            ThreadUtils.join(t2);
            assertThat(this.first).isNotNull().isEqualTo(this.second);
        }
    }
}
