/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@DisplayName("测试字符序适配器")
class SymbolSequenceListAdapterTest {
    private SymbolSequence<Integer> sequence;

    @BeforeEach
    void setup() {
        this.sequence = SymbolSequence.fromList(Arrays.asList(1, 2, 3));
    }

    @Test
    @DisplayName("返回的长度正确")
    void should_return_correct_length() {
        assertEquals(3, this.sequence.length());
    }

    @Test
    @DisplayName("返回正确的数据")
    void should_return_correct_data() {
        assertEquals(1, this.sequence.at(0));
        assertEquals(2, this.sequence.at(1));
        assertEquals(3, this.sequence.at(2));
    }

    @Test
    @DisplayName("返回友好的字符串表现形式")
    void should_return_friendly_string() {
        assertEquals("[1, 2, 3]", this.sequence.toString());
    }
}
