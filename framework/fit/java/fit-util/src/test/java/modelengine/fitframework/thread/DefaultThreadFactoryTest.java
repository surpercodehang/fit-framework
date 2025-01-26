/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.thread;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link DefaultThreadFactory} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-01-03
 */
@DisplayName("测试 DefaultThreadFactory 工具类")
class DefaultThreadFactoryTest {
    @Test
    @DisplayName("提供 DefaultThreadFactory 构造参数，返回一个线程对象")
    void givenConstructParamThenReturnDefaultThreadFactory() {
        Thread.UncaughtExceptionHandler exceptionHandler = mock(Thread.UncaughtExceptionHandler.class);
        DefaultThreadFactory threadFactory = new DefaultThreadFactory("random",
                false, exceptionHandler);
        Runnable runnable = () -> {};
        Thread thread = threadFactory.newThread(runnable);
        assertThat(thread).isNotNull();
        assertThat(thread.isDaemon()).isFalse();
        assertThat(thread.getName()).contains("random");
        assertThat(thread.getUncaughtExceptionHandler()).isEqualTo(exceptionHandler);
    }
}
