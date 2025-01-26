/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.choir;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.flowable.Emitter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link Choir#fromEmitter(Emitter)} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-02-14
 */
@DisplayName("测试 EmitterChoir")
public class EmitterChoirTest {
    @Test
    @DisplayName("当发送者向多条响应式流同时发送数据时，结果符合预期")
    void shouldReturnMultipleCorrectReactiveFlow() {
        Emitter<Integer> emitter = Emitter.create();
        List<Integer> l1 = new ArrayList<>();
        Choir.fromEmitter(emitter).subscribe((subscription, i) -> l1.add(i));
        emitter.emit(1);
        List<Integer> l2 = new ArrayList<>();
        Choir.fromEmitter(emitter).subscribe((subscription, i) -> {
            l2.add(i);
        });
        emitter.emit(2);
        assertThat(l1).hasSize(2).contains(1, 2);
        assertThat(l2).hasSize(1).contains(2);
    }

    @Test
    @DisplayName("当发送者发送数据后取消时，结果符合预期")
    void shouldReturnCorrectReactiveFlowAfterCancelling() {
        Emitter<Integer> emitter = Emitter.create();
        emitter.emit(1);
        List<Integer> l2 = new ArrayList<>();
        Choir.fromEmitter(emitter).subscribe((subscription, i) -> {
            l2.add(i);
            subscription.cancel();
        });
        emitter.emit(2);
        emitter.emit(3);
        assertThat(l2).hasSize(1).contains(2);
    }
}
