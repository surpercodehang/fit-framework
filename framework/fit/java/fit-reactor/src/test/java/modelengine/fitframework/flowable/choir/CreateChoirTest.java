/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.choir;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.flowable.subscriber.RecordSubscriber;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 表示 {@link Choir#create(Consumer)} 的单元测试。
 *
 * @author 何天放
 * @since 2024-05-15
 */
@DisplayName("测试通过 create 方法创建的 Choir")
public class CreateChoirTest {
    @Test
    @DisplayName("当多次发送数据以及正常终结信号时结果符合预期")
    void shouldCallConsumeTwiceAndComplete() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>(1, 1);
        Choir<Integer> choir = Choir.create(emitter -> {
            emitter.emit(1);
            emitter.emit(2);
            emitter.complete();
        });
        choir.subscribe(subscriber);
        assertThat(subscriber.getElements()).hasSize(2).contains(1, 2);
        assertThat(subscriber.receivedCompleted()).isTrue();
    }

    @Test
    @DisplayName("当多次发送超量数据时结果符合预期")
    void shouldOnlyCallConsumeTwiceAndComplete() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>(2, 0);
        Choir<Integer> choir = Choir.create(emitter -> {
            emitter.emit(1);
            emitter.emit(2);
            emitter.emit(3);
            emitter.complete();
        });
        choir.subscribe(subscriber);
        assertThat(subscriber.getElements()).hasSize(2).contains(1, 2);
        assertThat(subscriber.receivedCompleted()).isTrue();
    }

    @Test
    @DisplayName("当发送异常终结信号时结果符合预期")
    void shouldCallConsumeTwiceAndFail() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>(2, 0);
        Choir<Integer> choir = Choir.create(emitter -> {
            emitter.emit(1);
            emitter.emit(2);
            emitter.fail(new Exception("Test message."));
        });
        choir.subscribe(subscriber);
        assertThat(subscriber.getElements()).hasSize(2).contains(1, 2);
        assertThat(subscriber.receivedFailed()).isTrue();
        assertThat(subscriber.getFailRecords().get(0).getData().getMessage()).isEqualTo("Test message.");
    }

    @Test
    @DisplayName("当取消订阅时结果符合预期")
    void shouldOnlyCallConsumeTwice() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>(10, 0, 2);
        Choir<Integer> choir = Choir.create(emitter -> {
            emitter.emit(1);
            emitter.emit(2);
            emitter.emit(3);
            emitter.complete();
        });
        choir.subscribe(subscriber);
        assertThat(subscriber.getElements()).hasSize(2).contains(1, 2);
        assertThat(subscriber.receivedCompleted()).isFalse();
    }

    @Test
    @DisplayName("当 consumer 执行发生异常时结果符合预期")
    void shouldCallConsumeOnceAndFail() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>(1, 1);
        Choir<Integer> choir = Choir.create(emitter -> {
            emitter.emit(1);
            throw new RuntimeException("Test message.");
        });
        choir.subscribe(subscriber);
        assertThat(subscriber.getElements()).hasSize(1).contains(1);
        assertThat(subscriber.receivedFailed()).isTrue();
        assertThat(subscriber.getFailRecords().get(0).getData().getMessage()).isEqualTo("Test message.");
    }

    @Test
    @DisplayName("当 consumer 完成后发送正常终止信号结果符合预期")
    void shouldCallConsumeTwiceAndCompleteWhenAsync() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>(1, 1);
        Map<Integer, Emitter<Integer>> emitterMap = new HashMap<Integer, Emitter<Integer>>();
        Choir<Integer> choir = Choir.create(emitter -> {
            emitter.emit(1);
            emitterMap.put(0, emitter);
        });
        choir.subscribe(subscriber);
        assertThat(subscriber.getElements()).hasSize(1).contains(1);
        assertThat(subscriber.receivedCompleted()).isFalse();
        emitterMap.get(0).emit(2);
        emitterMap.get(0).complete();
        assertThat(subscriber.getElements()).hasSize(2).contains(1, 2);
        assertThat(subscriber.receivedCompleted()).isTrue();
    }

    @Test
    @DisplayName("当 consumer 完成后发送异常终止信号结果符合预期")
    void shouldCallConsumeTwiceAndFailWhenAsync() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>(1, 1);
        Map<Integer, Emitter<Integer>> emitterMap = new HashMap<Integer, Emitter<Integer>>();
        Choir<Integer> choir = Choir.create(emitter -> {
            emitter.emit(1);
            emitterMap.put(0, emitter);
        });
        choir.subscribe(subscriber);
        assertThat(subscriber.getElements()).hasSize(1).contains(1);
        assertThat(subscriber.receivedFailed()).isFalse();
        emitterMap.get(0).emit(2);
        emitterMap.get(0).fail(new Exception("Test message."));
        assertThat(subscriber.getElements()).hasSize(2).contains(1, 2);
        assertThat(subscriber.getFailRecords().get(0).getData().getMessage()).isEqualTo("Test message.");
    }
}
