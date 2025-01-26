/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fitframework.util.DisposedCallback;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link AbstractDisposable} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2021-04-08
 */
@DisplayName("Dispose object")
public class AbstractDisposableTest {
    @Test
    @DisplayName("then disposed changed.")
    public void stateChanged() {
        try (AbstractDisposable disposable = new AbstractDisposable() {}) {
            disposable.dispose();
            assertTrue(disposable.disposed());
        }
    }

    @Test
    @DisplayName("when dispose twice then actually disposed only once")
    void givenDisposedTwiceThenItOnlyDisposedOnce() {
        try (AbstractDisposable disposable = new AbstractDisposable() {}) {
            assertThat(disposable.disposed()).isFalse();
            disposable.dispose();
            assertThat(disposable.disposed()).isTrue();
            disposable.dispose();
            assertThat(disposable.disposed()).isTrue();
        }
    }

    @Nested
    @DisplayName("when a callback is attached")
    public class CallbackAttached {
        @Test
        @DisplayName("then trigger the callback.")
        public void triggerCallback() {
            AbstractDisposable disposable = new AbstractDisposable() {};
            DisposedCallback callback = mock(DisposedCallback.class);
            disposable.subscribe(callback);
            disposable.dispose();
            verify(callback, times(1)).onDisposed(disposable);
        }

        @Test
        @DisplayName("then trigger the callback. (AutoCloseable)")
        public void triggerCallbackWhenAutoClosed() {
            DisposedCallback callback = mock(DisposedCallback.class);
            try (AbstractDisposable disposable = new AbstractDisposable() {}) {
                disposable.subscribe(callback);
            }
            verify(callback, times(1)).onDisposed(any());
        }
    }

    @Nested
    @DisplayName("when a callback is detached")
    public class CallbackDetached {
        @Test
        @DisplayName("then don't trigger the callback.")
        public void notTriggerCallback() {
            try (AbstractDisposable disposable = new AbstractDisposable() {}) {
                DisposedCallback callback = mock(DisposedCallback.class);
                disposable.subscribe(callback);
                disposable.unsubscribe(callback);
                disposable.dispose();
                verify(callback, times(0)).onDisposed(any());
            }
        }
    }
}
