/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.location;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link Locations} 的单元测试。
 *
 * @author 高三海
 * @author 季聿阶
 * @since 2025-02-18
 */
@DisplayName("测试 Locations")
public class LocationsTest {
    @Test
    @DisplayName("当文件为 null 时，其路径也是 null")
    void shouldReturnNullWhenFileIsNull() {
        // noinspection ConstantValue
        String actual = Locations.path(null);
        // noinspection ConstantValue
        assertThat(actual).isNull();
    }
}
