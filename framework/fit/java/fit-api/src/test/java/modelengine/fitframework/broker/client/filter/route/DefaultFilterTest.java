/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.filter.route;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.GenericableMetadata;
import modelengine.fitframework.broker.Route;
import modelengine.fitframework.broker.client.Router;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * {@link DefaultFilter} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-03-05
 */
@DisplayName("验证使用泛服务默认实现的路由的过滤器")
public class DefaultFilterTest {
    @Nested
    @DisplayName("当输入参数为 Null 时")
    class GivenArgsIsNull {
        private Router.Filter filter;
        private GenericableMetadata genericable;

        @BeforeEach
        void setup() {
            this.filter = Router.Filter.defaultFilter();
            this.genericable = mock(GenericableMetadata.class);
        }

        @AfterEach
        void teardown() {
            this.filter = null;
            this.genericable = null;
        }

        @Test
        @DisplayName("当待过滤的泛服务实现列表中不包含默认实现时，过滤结果为空数组")
        void givenToFilterFitablesNotContainsDefaultThenReturnEmpty() {
            this.mockGenericableMetadata("fid");
            FitableMetadata fitableMetadata1 = this.mockFitableMetadata("fid1");
            FitableMetadata fitableMetadata2 = this.mockFitableMetadata("fid2");
            List<? extends FitableMetadata> toFilter = Arrays.asList(fitableMetadata1, fitableMetadata2);
            List<? extends FitableMetadata> actual =
                    this.filter.filter(this.genericable, toFilter, null, new HashMap<>());
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("当待过滤的泛服务实现列表包含默认实现时，过滤结果为默认实现")
        void givenToFilterFitablesContainsDefaultThenReturnTheDefaultOne() {
            this.mockGenericableMetadata("fid1");
            FitableMetadata fitableMetadata1 = this.mockFitableMetadata("fid1");
            FitableMetadata fitableMetadata2 = this.mockFitableMetadata("fid2");
            List<? extends FitableMetadata> toFilter = Arrays.asList(fitableMetadata1, fitableMetadata2);
            List<? extends FitableMetadata> actual =
                    this.filter.filter(this.genericable, toFilter, null, new HashMap<>());
            assertThat(actual).hasSize(1).element(0).isEqualTo(fitableMetadata1);
        }

        private void mockGenericableMetadata(String fitableId) {
            Route route = mock(Route.class);
            when(route.defaultFitable()).thenReturn(fitableId);
            when(this.genericable.route()).thenReturn(route);
        }

        private FitableMetadata mockFitableMetadata(String fitableId) {
            FitableMetadata fitable = mock(FitableMetadata.class);
            when(fitable.genericable()).thenReturn(this.genericable);
            when(fitable.id()).thenReturn(fitableId);
            return fitable;
        }

        @Test
        @DisplayName("当待过滤的泛服务实现列表为 Null 时，抛出参数异常")
        void givenToFilterFitablesNullThenThrowIllegalArgumentException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> this.filter.filter(this.genericable, null, null, new HashMap<>()),
                            IllegalArgumentException.class);
            assertThat(exception).isNotNull().hasMessage("The metadata of fitables to filter cannot be null.");
        }
    }
}
