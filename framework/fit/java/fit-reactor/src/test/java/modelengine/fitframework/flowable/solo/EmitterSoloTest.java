/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.solo;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.flowable.Solo;
import modelengine.fitframework.flowable.subscriber.RecordSubscriber;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link Solo#fromEmitter(Emitter)} 的单元测试。
 *
 * @author 何天放
 * @since 2024-05-06
 */
@DisplayName("测试 EmitterSolo")
public class EmitterSoloTest {
    @Test
    @DisplayName("当发送者仅发送单个元素且未发送正常终结信号时，结果符合预期")
    void shouldReturnCompleteWhenOnlySendOne() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>();
        Emitter<Integer> emitter = Emitter.create();
        Solo.fromEmitter(emitter).subscribe(subscriber);
        emitter.emit(1);
        assertThat(subscriber.getElements()).hasSize(1).contains(1);
        assertThat(subscriber.receivedCompleted()).isTrue();
    }

    @Test
    @DisplayName("当发送者发送超量的数据时，结果符合预期")
    void shouldReturnOnlyOneElementWhenSendSuperfluous() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>();
        Emitter<Integer> emitter = Emitter.create();
        Solo.fromEmitter(emitter).subscribe(subscriber);
        emitter.emit(1);
        emitter.emit(2);
        assertThat(subscriber.getElements()).hasSize(1).contains(1);
    }
}
