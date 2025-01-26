/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.parameter;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.merge.ConflictResolver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link MergeConflictResolver} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-27
 */
@DisplayName("测试 MergeConflictResolver 类")
class MergeConflictResolverTest {
    @Test
    @DisplayName("返回冲突解决后的字符串")
    void shouldReturnMergedString() {
        final MergeConflictResolver conflictResolver = new MergeConflictResolver();
        final ConflictResolver.Result<String> result = conflictResolver.resolve("v1", "v2", null);
        assertThat(result.result()).isEqualTo("v1,v2");
    }
}
