/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.broker.DynamicRouter;
import modelengine.fitframework.broker.Fitable;
import modelengine.fitframework.broker.UniqueFitableId;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

/**
 * 表示 {@link DefaultGenericable} 的单元测试。
 *
 * @author 季聿阶
 * @since 2023-08-17
 */
@DisplayName("测试 DefaultGenericable")
public class DefaultGenericableTest {
    private DynamicRouter router;

    @BeforeEach
    void setup() {
        this.router = Mockito.mock(DynamicRouter.class);
    }

    @AfterEach
    void teardown() {
        this.router = null;
    }

    @Test
    @DisplayName("当设置的服务实现列表中存在重复的服务实现时，能够正确的消除冗余服务实现")
    void givenDuplicatedFitablesThenRemoveDuplicated() {
        DefaultGenericable genericable = new DefaultGenericable(this.router, "g", "1.0.0");
        Fitable fitable = Mockito.mock(Fitable.class);
        Mockito.when(fitable.toUniqueId()).thenReturn(UniqueFitableId.create("g", "f"));
        List<Fitable> fitables = Arrays.asList(fitable, fitable);
        genericable.fitables(fitables);
        assertThat(genericable.fitables()).hasSize(1).containsExactly(fitable);
    }

    @Test
    @DisplayName("当设置的服务实现已经存在于服务中时，能够正确的消除冗余服务实现")
    void givenExistFitableThenRemoveDuplicated() {
        DefaultGenericable genericable = new DefaultGenericable(this.router, "g", "1.0.0");
        Fitable fitable = Mockito.mock(Fitable.class);
        Mockito.when(fitable.toUniqueId()).thenReturn(UniqueFitableId.create("g", "f"));
        genericable.appendFitable(fitable);
        assertThat(genericable.fitables()).hasSize(1).containsExactly(fitable);
        genericable.appendFitable(fitable);
        assertThat(genericable.fitables()).hasSize(1).containsExactly(fitable);
    }
}
