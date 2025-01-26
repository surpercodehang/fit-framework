/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ListNodeListAdapter} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-02-06
 */
public class ListNodeListAdapterTest {
    private ListNodeListAdapter nodes;
    private Node node;

    @BeforeEach
    void setup() {
        List<Node> list = new ArrayList<>();
        this.node = mock(Node.class);
        when(this.node.toString()).thenReturn("node1");
        list.add(this.node);
        this.nodes = new ListNodeListAdapter(list);
    }

    @AfterEach
    void teardown() {
        this.nodes = null;
        this.node = null;
    }

    @Nested
    @DisplayName("Test method: item(int index)")
    class TestItem {
        @Test
        @DisplayName("Given 1 item then get correct item")
        void given1ItemThenGetCorrectItem() {
            Node actual = ListNodeListAdapterTest.this.nodes.item(0);
            assertThat(actual).isNotNull().isEqualTo(ListNodeListAdapterTest.this.node);
        }
    }

    @Nested
    @DisplayName("Test method: getLength()")
    class TestGetLength {
        @Test
        @DisplayName("Given 1 item then get correct length")
        void given1ItemThenGetCorrectItem() {
            int actual = ListNodeListAdapterTest.this.nodes.getLength();
            assertThat(actual).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Test method: toString()")
    class TestToString {
        @Test
        @DisplayName("Given 1 item then get correct string")
        void given1ItemThenGetCorrectString() {
            String actual = ListNodeListAdapterTest.this.nodes.toString();
            assertThat(actual).isEqualTo("[node1]");
        }
    }
}
