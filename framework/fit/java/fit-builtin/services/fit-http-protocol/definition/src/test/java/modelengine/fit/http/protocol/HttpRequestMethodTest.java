/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link HttpRequestMethod} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-07-10
 */
@DisplayName("测试 RequestMethod")
public class HttpRequestMethodTest {
    @Nested
    @DisplayName("当调用 from(String methodName) 方法时")
    class WhenFrom {
        @Test
        @DisplayName("当方法名为标准小写时，返回标准的方法枚举")
        void givenStandardLowerCaseMethodNameThenReturnStandardMethod() {
            HttpRequestMethod actual = HttpRequestMethod.from("head");
            assertThat(actual).isEqualTo(HttpRequestMethod.HEAD);
        }

        @Test
        @DisplayName("当方法名为标准大写时，返回标准的方法枚举")
        void givenStandardUpperCaseMethodNameThenReturnStandardMethod() {
            HttpRequestMethod actual = HttpRequestMethod.from("HEAD");
            assertThat(actual).isEqualTo(HttpRequestMethod.HEAD);
        }

        @Test
        @DisplayName("当方法名为未知时，返回 null")
        void givenUnknownMethodNameThenReturnNull() {
            HttpRequestMethod actual = HttpRequestMethod.from("unknown");
            assertThat(actual).isNull();
        }
    }
}
