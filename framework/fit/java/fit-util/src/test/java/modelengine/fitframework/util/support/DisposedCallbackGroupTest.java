/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fitframework.util.Disposable;
import modelengine.fitframework.util.DisposedCallback;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link DisposedCallbackGroup} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-02-03
 */
public class DisposedCallbackGroupTest {
    @Nested
    @DisplayName("Test method: onDisposed(Disposable disposable)")
    class TestOnDisposed {
        @Test
        @DisplayName("Given 2 disposables then each one disposed 1 times")
        void given2DisposablesThenEachDisposed1Times() {
            DisposedCallback callback1 = mock(DisposedCallback.class);
            DisposedCallback callback2 = mock(DisposedCallback.class);
            TestDisposable testDisposable = new TestDisposable();
            testDisposable.subscribe(callback1);
            testDisposable.subscribe(callback2);
            testDisposable.onDisposed();
            verify(callback1, times(1)).onDisposed(eq(testDisposable));
            verify(callback2, times(1)).onDisposed(eq(testDisposable));
        }
    }

    @Nested
    @DisplayName("测试方法: safeDispose(Disposable disposableObject)")
    class TestSafeDispose {
        @Test
        @DisplayName("当提供 null 时，方法直接返回")
        void givenNullThenMethodReturn() {
            assertDoesNotThrow(() -> Disposable.safeDispose(null));
        }

        @Test
        @DisplayName("当提供指定对象时，安全销毁该对象")
        void givenDisposablesThenDisposed1Times() {
            DisposedCallback callback = mock(DisposedCallback.class);
            TestDisposable testDisposable = new TestDisposable();
            testDisposable.subscribe(callback);
            Disposable.safeDispose(testDisposable);
            verify(callback, times(1)).onDisposed(eq(testDisposable));
        }
    }

    @Nested
    @DisplayName("Test method: combine(DisposedCallback current, DisposedCallback another)")
    class TestCombine {
        @Test
        @DisplayName("Given 1 disposable and null then return the disposable itself")
        void given1DisposableAndNullThenReturnTheDisposableItself() {
            DisposedCallback callback1 = mock(DisposedCallback.class);
            TestDisposable testDisposable = new TestDisposable();
            testDisposable.subscribe(callback1);
            testDisposable.subscribe(null);
            testDisposable.onDisposed();
            verify(callback1, times(1)).onDisposed(eq(testDisposable));
        }
    }

    @Nested
    @DisplayName("Test method: remove(DisposedCallback current, DisposedCallback another)")
    class TestRemove {
        @Test
        @DisplayName("Given the 1st disposable and remove the 2nd then return the 1st")
        void givenTheFirstDisposableAndRemoveSecondThenReturnFirst() {
            DisposedCallback callback1 = mock(DisposedCallback.class);
            DisposedCallback callback2 = mock(DisposedCallback.class);
            TestDisposable testDisposable = new TestDisposable();
            testDisposable.subscribe(callback1);
            testDisposable.unsubscribe(callback2);
            testDisposable.onDisposed();
            verify(callback1, times(1)).onDisposed(eq(testDisposable));
            verify(callback2, times(0)).onDisposed(eq(testDisposable));
        }

        @Test
        @DisplayName("Given the 1st and 2nd disposables and remove the 2nd then return the 1st")
        void givenTheFirstAndSecondDisposablesAndRemoveSecondThenReturnFirst() {
            DisposedCallback callback1 = mock(DisposedCallback.class);
            DisposedCallback callback2 = mock(DisposedCallback.class);
            TestDisposable testDisposable = new TestDisposable();
            testDisposable.subscribe(callback1);
            testDisposable.subscribe(callback2);
            testDisposable.unsubscribe(callback2);
            testDisposable.onDisposed();
            verify(callback1, times(1)).onDisposed(eq(testDisposable));
            verify(callback2, times(0)).onDisposed(eq(testDisposable));
        }

        @Test
        @DisplayName("Given the 1st and 2nd disposables and remove the 1st then return the 2nd")
        void givenTheFirstAndSecondDisposablesAndRemoveFirstThenReturnSecond() {
            DisposedCallback callback1 = mock(DisposedCallback.class);
            DisposedCallback callback2 = mock(DisposedCallback.class);
            TestDisposable testDisposable = new TestDisposable();
            testDisposable.subscribe(callback1);
            testDisposable.subscribe(callback2);
            testDisposable.unsubscribe(callback1);
            testDisposable.onDisposed();
            verify(callback1, times(0)).onDisposed(eq(testDisposable));
            verify(callback2, times(1)).onDisposed(eq(testDisposable));
        }

        @Test
        @DisplayName("Given the 1st, 2nd and 3rd disposables and remove the 3rd then return the 1st and 2nd")
        void givenTheFirstSecondAndThirdDisposablesAndRemoveThirdThenReturnFirstAndSecond() {
            DisposedCallback callback1 = mock(DisposedCallback.class);
            DisposedCallback callback2 = mock(DisposedCallback.class);
            DisposedCallback callback3 = mock(DisposedCallback.class);
            TestDisposable testDisposable = new TestDisposable();
            testDisposable.subscribe(callback1);
            testDisposable.subscribe(callback2);
            testDisposable.unsubscribe(callback3);
            testDisposable.onDisposed();
            verify(callback1, times(1)).onDisposed(eq(testDisposable));
            verify(callback2, times(1)).onDisposed(eq(testDisposable));
        }
    }

    static class TestDisposable extends AbstractDisposable {}
}
