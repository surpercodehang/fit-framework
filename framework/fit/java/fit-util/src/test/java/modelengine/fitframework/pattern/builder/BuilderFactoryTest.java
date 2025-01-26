/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 为 {@link BuilderFactory} 提供单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-06-22
 */
@DisplayName("测试 BuilderFactory")
public class BuilderFactoryTest {
    @Nested
    @DisplayName("测试构建器的设值")
    class TestSet {
        @Test
        @DisplayName("当待设置的值为包装类型时，设置值成功")
        void givenFieldIsBoxedThenFieldSetSuccessfully() {
            String host = "localhost";

            Address address = Address.builder().host(host).build();

            assertThat(address).isNotNull();
            assertThat(address.host()).isEqualTo(host);
            assertThat(address.port()).isEqualTo(0);
        }

        @Test
        @DisplayName("当待设置的值为非包装类型时，设置值成功")
        void givenFieldIsUnBoxedThenFieldSetSuccessfully() {
            int port = 8080;

            Address address = Address.builder().port(port).build();

            assertThat(address).isNotNull();
            assertThat(address.host()).isEqualTo(null);
            assertThat(address.port()).isEqualTo(port);
        }
    }

    @Nested
    @DisplayName("测试构建器的构建")
    class TestBuild {
        @Test
        @DisplayName("当构建器为直接获取时，构建结果符合预期")
        void givenDefaultBuilderThenResultIsExpected() {
            String host = "localhost";
            int port = 8080;

            Address address = Address.builder().host(host).port(port).build();

            assertThat(address).isNotNull();
            assertThat(address.host()).isEqualTo(host);
            assertThat(address.port()).isEqualTo(port);
        }

        @Test
        @DisplayName("当构建器为 default 方法拷贝获取时，抛出不支持的异常")
        void givenCopiedBuilderThenThrowUnsupportedException() {
            String host = "localhost";
            int port = 8080;

            Address address = Address.builder().host(host).port(port).build();
            UnsupportedOperationException unsupportedOperationException = catchThrowableOfType(address::copy,
                    UnsupportedOperationException.class);
            assertThat(unsupportedOperationException).isNotNull().hasMessage("Not supported default method to invoke.");
        }
    }
}
