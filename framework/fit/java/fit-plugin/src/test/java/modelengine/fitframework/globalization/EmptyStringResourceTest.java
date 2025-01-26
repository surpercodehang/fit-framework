/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.globalization;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

/**
 * 为 {@link EmptyStringResource} 提供单元测试。
 *
 * @author 季聿阶
 * @since 2024-12-06
 */
@DisplayName("测试 EmptyStringResource")
public class EmptyStringResourceTest {
    @Test
    @DisplayName("当格式化参数为数组时，返回 null")
    void shouldReturnNullWhenArgsIsArray() {
        String message = EmptyStringResource.INSTANCE.getMessage(Locale.ENGLISH, "key", (Object[]) null);
        assertThat(message).isNull();
    }

    @Test
    @DisplayName("当格式化参数为映射时，返回 null")
    void shouldReturnNullWhenArgsIsMap() {
        String message = EmptyStringResource.INSTANCE.getMessage(Locale.ENGLISH, "key", (Map<String, Object>) null);
        assertThat(message).isNull();
    }
}
