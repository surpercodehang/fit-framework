/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fit.http.header.HeaderValue;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 为 {@link HttpUtils} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-02-22
 */
@DisplayName("测试 HttpUtils 工具类")
public class HttpUtilsTest {
    @Test
    @DisplayName("给定空的值，解析消息头的值返回为空")
    void givenEmptyValueThenReturnHeaderValueISEmpty() {
        String rawValue = "";
        HeaderValue headerValue = HttpUtils.parseHeaderValue(rawValue);
        assertThat(headerValue.value()).isEqualTo(StringUtils.EMPTY);
    }

    @Test
    @DisplayName("给定不是消息头的数据，返回值为空")
    void givenParameterNotIsValueOfHeaderThenReturnIsEmpty() {
        String rawValue = "testKey=testValue;notContainsSeparator";
        HeaderValue headerValue = HttpUtils.parseHeaderValue(rawValue);
        assertThat(headerValue.value()).isEqualTo(StringUtils.EMPTY);
    }

    @Test
    @DisplayName("调用 toUrl() 方法，给定一个错误的 Url 值，抛出异常")
    void givenIncorrectUrlWhenInvokeToUrlThenThrowException() {
        String incorrectUrl = "errorUrl";
        IllegalStateException illegalStateException =
                catchThrowableOfType(() -> HttpUtils.toUrl(incorrectUrl), IllegalStateException.class);
        assertThat(illegalStateException).hasMessage("The request URL is incorrect.");
    }

    @Test
    @DisplayName("调用 toUri() 方法，给定一个错误的 Url 值，抛出异常")
    void givenIncorrectUrlWhenInvokeToUriThenThrowException() throws MalformedURLException {
        URL url = new URL("jar", null, -1, "");
        IllegalStateException illegalStateException =
                catchThrowableOfType(() -> HttpUtils.toUri(url), IllegalStateException.class);
        assertThat(illegalStateException).hasMessage("The request URL is incorrect.");
    }
}
