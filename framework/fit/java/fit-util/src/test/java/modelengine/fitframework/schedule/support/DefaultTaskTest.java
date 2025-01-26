/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.schedule.Task;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

/**
 * 表示 {@link DefaultTask} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-01-31
 */
@DisplayName("测试 DefaultTask 类")
public class DefaultTaskTest {
    @Nested
    @DisplayName("测试 Builder 类")
    class BuilderTest {
        @Test
        @DisplayName("构建一个非 null 的任务时，修改类变量值成功")
        void givenNotNullTaskWhenBuildTaskThenModifySuccessfully() {
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (thread, throwable) -> {};
            Callable<String> callable = () -> "finishCallable";
            Task task = Task.builder().callable(callable).uncaughtExceptionHandler(uncaughtExceptionHandler).build();
            Task.Builder builder = Task.builder(task);
            Thread.UncaughtExceptionHandler actualUncaughtExceptionHandler = builder.build().uncaughtExceptionHandler();
            assertThat(actualUncaughtExceptionHandler).isEqualTo(uncaughtExceptionHandler);
        }
    }
}
