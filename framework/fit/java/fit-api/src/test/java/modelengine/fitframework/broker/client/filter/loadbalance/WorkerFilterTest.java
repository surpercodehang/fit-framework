/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.filter.loadbalance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.GenericableMetadata;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.broker.client.Invoker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * {@link WorkerFilter} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-03-18
 */
@DisplayName("验证指定进程唯一标识的负载均衡的过滤器")
public class WorkerFilterTest {
    private Invoker.Filter filter;
    private FitableMetadata fitable;

    @BeforeEach
    void setup() {
        this.filter = new WorkerFilter("workerId");
        this.fitable = mock(FitableMetadata.class);
        GenericableMetadata genericable = mock(GenericableMetadata.class);
        when(this.fitable.genericable()).thenReturn(genericable);
        when(genericable.id()).thenReturn("gid");
        when(this.fitable.id()).thenReturn("fid");
    }

    @AfterEach
    void teardown() {
        this.filter = null;
        this.fitable = null;
    }

    @Nested
    @DisplayName("当服务实现为 Null 时")
    class GivenFitableIsNull {
        private final String workerId = "workerId";

        @Test
        @DisplayName("抛出参数异常")
        void throwIllegalArgumentException() {
            IllegalArgumentException exception = catchThrowableOfType(() -> WorkerFilterTest.this.filter.filter(
                    WorkerFilterTest.this.fitable, this.workerId, null, new HashMap<>()),
                    IllegalArgumentException.class);
            assertThat(exception).isNotNull()
                    .hasMessage("The targets to balance load cannot be null. [genericableId=gid, fitableId=fid]");
        }
    }

    @Nested
    @DisplayName("当服务实现不为 Null 时")
    class GivenFitableIsNotNull {
        @Nested
        @DisplayName("当本地进程唯一标识为空白字符串时")
        class GivenWorkerIsBlank {
            @Test
            @DisplayName("抛出参数异常")
            void throwIllegalArgumentException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> WorkerFilterTest.this.filter.filter(
                        WorkerFilterTest.this.fitable,
                        null,
                        null,
                        new HashMap<>()), IllegalArgumentException.class);
                assertThat(exception).isNotNull()
                        .hasMessage("The local worker id to balance load cannot be blank. [genericableId=gid, "
                                + "fitableId=fid]");
            }
        }

        @Nested
        @DisplayName("当本地进程唯一标识不为空白字符串时")
        class GivenWorkerIdIsNotBlank {
            private final String workerId = "workerId";

            @Test
            @DisplayName("当待过滤的服务地址列表为 Null 时，抛出参数异常")
            void givenToFilterTargetsIsNullThenThrowIllegalArgumentException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> WorkerFilterTest.this.filter.filter(
                        WorkerFilterTest.this.fitable, this.workerId, null, new HashMap<>()),
                        IllegalArgumentException.class);
                assertThat(exception).isNotNull()
                        .hasMessage("The targets to balance load cannot be null. [genericableId=gid, fitableId=fid]");
            }

            @Test
            @DisplayName("当待过滤的服务地址列表包含特定进程唯一标识时，返回特定进程唯一标识的服务地址列表")
            void givenToFilterTargetsContainsSpecifiedWorkerIdThenReturnTheTargetsWithSpecifiedWorkerId() {
                List<Target> expected = Arrays.asList(Target.custom().workerId("workerId").host("h1").build(),
                        Target.custom().workerId("w2").host("h2").build());
                List<Target> actual = WorkerFilterTest.this.filter.filter(WorkerFilterTest.this.fitable,
                        this.workerId,
                        expected,
                        new HashMap<>());
                assertThat(actual).isNotNull().hasSize(1);
                assertThat(actual.get(0).workerId()).isEqualTo("workerId");
                assertThat(actual.get(0).host()).isEqualTo("h1");
            }
        }
    }
}
