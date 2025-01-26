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

import modelengine.fitframework.broker.Aliases;
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
import java.util.HashSet;
import java.util.List;

/**
 * {@link AliasFilter} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-03-05
 */
@DisplayName("验证指定别名的路由的过滤器")
public class AliasFilterTest {
    @Nested
    @DisplayName("验证构造函数")
    class TestConstructor {
        @Nested
        @DisplayName("验证不定长参数的构造函数")
        class TestVarArgsConstructor {
            @Test
            @DisplayName("当参数为 Null 时，抛出参数异常")
            void givenNullThenThrowIllegalArgumentException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> new AliasFilter((String[]) null), IllegalArgumentException.class);
                assertThat(exception).isNotNull().hasMessage("No valid alias to instantiate AliasFilter.");
            }

            @Test
            @DisplayName("当参数不为 Null 时，构造出的过滤器中的别名列表不为空")
            void givenNotNullThenGetCorrectAliases() {
                AliasFilter filter = new AliasFilter("a1", "a2");
                List<String> actual = TestConstructor.this.getAliases(filter);
                assertThat(actual).isNotEmpty().hasSize(2).contains("a1", "a2");
            }
        }

        @Nested
        @DisplayName("验证列表参数的构造函数")
        class TestListArgsConstructor {
            @Test
            @DisplayName("当参数为 Null 时，抛出参数异常")
            void givenNullThenThrowIllegalArgumentException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> new AliasFilter((List<String>) null),
                        IllegalArgumentException.class);
                assertThat(exception).isNotNull().hasMessage("No valid alias to instantiate AliasFilter.");
            }

            @Test
            @DisplayName("当参数不为 Null 时，构造出的过滤器中的别名列表不为空")
            void givenNotNullThenGetCorrectAliases() {
                AliasFilter filter = new AliasFilter(Arrays.asList("a1", "a2"));
                List<String> actual = TestConstructor.this.getAliases(filter);
                assertThat(actual).isNotEmpty().hasSize(2).contains("a1", "a2");
            }

            @Test
            @DisplayName("当参数中包含 Null 时，构造出的过滤器中的别名列表中不包含 Null")
            void givenListWithNullThenGetAliasesWithoutNull() {
                AliasFilter filter = new AliasFilter(Arrays.asList("a1", "a2", null));
                List<String> actual = TestConstructor.this.getAliases(filter);
                assertThat(actual).isNotEmpty().hasSize(2).contains("a1", "a2");
            }
        }

        @SuppressWarnings("unchecked")
        private List<String> getAliases(AliasFilter filter) {
            return (List<String>) ReflectionUtils.getField(filter, "aliases");
        }
    }

    @Nested
    @DisplayName("当输入参数为 Null 时")
    class GivenArgsIsNull {
        private Router.Filter filter;
        private GenericableMetadata genericable;

        @BeforeEach
        void setup() {
            this.filter = new AliasFilter("a");
            this.genericable = mock(GenericableMetadata.class);
        }

        @AfterEach
        void teardown() {
            this.filter = null;
            this.genericable = null;
        }

        @Test
        @DisplayName("当待过滤的泛服务实现列表中不包含指定别名时，过滤结果为空数组")
        void givenToFilterFitablesNotContainsSpecifiedAliasThenReturnEmpty() {
            FitableMetadata fitableMetadata1 = this.mockFitableMetadata("fid1", Arrays.asList("a1", "a2"));
            FitableMetadata fitableMetadata2 = this.mockFitableMetadata("fid2", Arrays.asList("a1", "a2"));
            List<? extends FitableMetadata> toFilter = Arrays.asList(fitableMetadata1, fitableMetadata2);
            List<? extends FitableMetadata> actual =
                    this.filter.filter(this.genericable, toFilter, null, new HashMap<>());
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("当待过滤的泛服务实现列表包含不同的别名时，过滤结果仅包含指定别名的泛服务实现")
        void givenToFilterFitablesContainsDifferentAliasesThenReturnFitablesWithSpecifiedAlias() {
            FitableMetadata fitableMetadata1 = this.mockFitableMetadata("fid1", Arrays.asList("a", "a2"));
            FitableMetadata fitableMetadata2 = this.mockFitableMetadata("fid2", Arrays.asList("a1", "a2"));
            List<? extends FitableMetadata> toFilter = Arrays.asList(fitableMetadata1, fitableMetadata2);
            List<? extends FitableMetadata> actual =
                    this.filter.filter(this.genericable, toFilter, null, new HashMap<>());
            assertThat(actual).hasSize(1).element(0).isEqualTo(fitableMetadata1);
        }

        private FitableMetadata mockFitableMetadata(String fitableId, List<String> aliases) {
            FitableMetadata fitable = mock(FitableMetadata.class);
            when(fitable.genericable()).thenReturn(this.genericable);
            when(fitable.id()).thenReturn(fitableId);
            Aliases aliasCollection = mock(Aliases.class);
            when(aliasCollection.all()).thenReturn(new HashSet<>(aliases));
            when(fitable.aliases()).thenReturn(aliasCollection);
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
