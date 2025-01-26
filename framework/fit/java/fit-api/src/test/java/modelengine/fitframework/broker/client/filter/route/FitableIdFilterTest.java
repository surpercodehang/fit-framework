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
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.util.ReflectionUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * {@link FitableIdFilter} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-03-05
 */
@DisplayName("验证指定泛服务实现唯一标识的路由的过滤器")
public class FitableIdFilterTest {
    @Nested
    @DisplayName("验证构造函数")
    class TestConstructor {
        @Nested
        @DisplayName("验证不定长参数的构造函数")
        class TestVarArgsConstructor {
            @Test
            @DisplayName("当参数为 Null 时，抛出参数异常")
            void givenNullThenThrowIllegalArgumentException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> new FitableIdFilter((String[]) null),
                        IllegalArgumentException.class);
                assertThat(exception).isNotNull().hasMessage("No valid fitable id to instantiate FitableIdFilter.");
            }

            @Test
            @DisplayName("当参数不为 Null 时，构造出的过滤器中的泛服务实现唯一标识列表不为空")
            void givenNotNullThenGetCorrectFitableIds() {
                FitableIdFilter filter = new FitableIdFilter("fid1", "fid2");
                List<String> actual = TestConstructor.this.getFitableIds(filter);
                assertThat(actual).isNotEmpty().hasSize(2).contains("fid1", "fid2");
            }
        }

        @Nested
        @DisplayName("验证列表参数的构造函数")
        class TestListArgsConstructor {
            @Test
            @DisplayName("当参数为 Null 时，抛出参数异常")
            void givenNullThenThrowIllegalArgumentException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> new FitableIdFilter((List<String>) null),
                                IllegalArgumentException.class);
                assertThat(exception).isNotNull().hasMessage("No valid fitable id to instantiate FitableIdFilter.");
            }

            @Test
            @DisplayName("当参数不为 Null 时，构造出的过滤器中的泛服务实现唯一标识列表不为空")
            void givenNotNullThenGetCorrectFitableIds() {
                FitableIdFilter filter = new FitableIdFilter(Arrays.asList("fid1", "fid2"));
                List<String> actual = TestConstructor.this.getFitableIds(filter);
                assertThat(actual).isNotEmpty().hasSize(2).contains("fid1", "fid2");
            }

            @Test
            @DisplayName("当参数中包含 Null 时，构造出的过滤器中的泛服务实现唯一标识列表中不包含 Null")
            void givenListWithNullThenGetFitableIdsWithoutNull() {
                FitableIdFilter filter = new FitableIdFilter(Arrays.asList("fid1", "fid2", null));
                List<String> actual = TestConstructor.this.getFitableIds(filter);
                assertThat(actual).isNotEmpty().hasSize(2).contains("fid1", "fid2");
            }
        }

        @SuppressWarnings("unchecked")
        private List<String> getFitableIds(FitableIdFilter filter) {
            return (List<String>) ReflectionUtils.getField(filter, "fitableIds");
        }
    }

    @Nested
    @DisplayName("当输入参数为 Null 时")
    class GivenArgsIsNull {
        private Router.Filter filter;
        private GenericableMetadata genericable;

        @BeforeEach
        void setup() {
            this.filter = new FitableIdFilter("fid");
            this.genericable = mock(GenericableMetadata.class);
        }

        @AfterEach
        void teardown() {
            this.filter = null;
            this.genericable = null;
        }

        @Test
        @DisplayName("当待过滤的泛服务实现列表中不包含指定泛服务实现唯一标识时，过滤结果为空数组")
        void givenToFilterFitablesNotContainsSpecifiedFitableIdThenReturnEmpty() {
            FitableMetadata fitableMetadata1 = this.mockFitableMetadata("fid1");
            FitableMetadata fitableMetadata2 = this.mockFitableMetadata("fid2");
            List<? extends FitableMetadata> toFilter = Arrays.asList(fitableMetadata1, fitableMetadata2);
            List<? extends FitableMetadata> actual =
                    this.filter.filter(this.genericable, toFilter, null, new HashMap<>());
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("当待过滤的泛服务实现列表包含 2 个不同泛服务实现时，过滤结果仅包含指定泛服务实现")
        void givenToFilterFitablesContainsDifferentFitablesThenReturnSpecifiedFitables() {
            FitableMetadata fitableMetadata1 = this.mockFitableMetadata("fid");
            FitableMetadata fitableMetadata2 = this.mockFitableMetadata("another");
            List<? extends FitableMetadata> toFilter = Arrays.asList(fitableMetadata1, fitableMetadata2);
            List<? extends FitableMetadata> actual =
                    this.filter.filter(this.genericable, toFilter, null, new HashMap<>());
            assertThat(actual).hasSize(1).element(0).isEqualTo(fitableMetadata1);
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
