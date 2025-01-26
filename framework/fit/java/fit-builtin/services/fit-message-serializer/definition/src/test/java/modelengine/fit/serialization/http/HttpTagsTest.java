/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link HttpTags} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-05-09
 */
@DisplayName("测试 HttpTags")
public class HttpTagsTest {
    @DisplayName("当获取常量类时，校验通过")
    @Test
    void shouldNotThrowException() {
        // noinspection ResultOfMethodCallIgnored
        Assertions.assertThatNoException().isThrownBy(HttpTags::getAsyncTaskIdTag);
    }
}
