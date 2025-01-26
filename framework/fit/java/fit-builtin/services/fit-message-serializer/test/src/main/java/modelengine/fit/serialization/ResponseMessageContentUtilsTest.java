/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.serialization.http.websocket.ResponseMessageContentUtils;
import modelengine.fitframework.serialization.TagLengthValues;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * {@link ResponseMessageContentUtils} 的基本测试用例。
 *
 * @author 何天放
 * @since 2024-04-16
 */
@DisplayName("测试 ResponseMessageContentUtils")
public class ResponseMessageContentUtilsTest {
    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置序列化方式并获取到所设定值")
    void shouldReturnCorrectDataFormatFromTagLengthValues() {
        TagLengthValues tlvs = TagLengthValues.create();
        ResponseMessageContentUtils.setDataFormat(tlvs, 1);
        assertThat(ResponseMessageContentUtils.getDataFormat(tlvs)).isEqualTo(1);
    }

    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置错误码并获取到所设定值")
    void shouldReturnCorrectCodeFromTagLengthValues() {
        TagLengthValues tlvs = TagLengthValues.create();
        ResponseMessageContentUtils.setCode(tlvs, 123);
        assertThat(ResponseMessageContentUtils.getCode(tlvs)).isEqualTo(123);
    }

    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置异常消息并获取到所设定值")
    void shouldReturnCorrectMessageFromTagLengthValues() {
        TagLengthValues tlvs = TagLengthValues.create();
        ResponseMessageContentUtils.setMessage(tlvs, "test message");
        assertThat(ResponseMessageContentUtils.getMessage(tlvs)).isEqualTo("test message");
    }

    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置扩展信息并获取到所设定值")
    void shouldReturnCorrectTagLengthValuesFromTagLengthValues() {
        TagLengthValues tlvsToPut = TagLengthValues.create();
        tlvsToPut.putTag(1, "bcd".getBytes(StandardCharsets.UTF_8));
        TagLengthValues tlvs = TagLengthValues.create();
        ResponseMessageContentUtils.setExtensions(tlvs, tlvsToPut);
        assertThat(ResponseMessageContentUtils.getExtensions(tlvs).getValue(1)).isEqualTo("bcd".getBytes(
                StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置数据实体并获取到所设定值")
    void shouldReturnCorrectEntityFromTagLengthValues() {
        TagLengthValues tlvs = TagLengthValues.create();
        ResponseMessageContentUtils.setEntity(tlvs, "entity".getBytes(StandardCharsets.UTF_8));
        assertThat(ResponseMessageContentUtils.getEntity(tlvs)).isEqualTo("entity".getBytes(StandardCharsets.UTF_8));
    }
}
