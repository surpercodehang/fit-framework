/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.serialization.http.websocket.RequestMessageContentUtils;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.Version;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * {@link RequestMessageContentUtils} 的基本测试用例。
 *
 * @author 何天放
 * @since 2024-04-16
 */
@DisplayName("测试 RequestMessageContentUtils")
public class RequestMessageContentUtilsTest {
    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置序列化方式并获取到所设定值")
    void shouldReturnCorrectDataFormatFromTagLengthValues() {
        TagLengthValues tlvs = TagLengthValues.create();
        RequestMessageContentUtils.setDataFormat(tlvs, 1);
        assertThat(RequestMessageContentUtils.getDataFormat(tlvs)).isEqualTo(1);
    }

    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置泛服务版本并获取到所设定值")
    void shouldReturnCorrectGenericableVersionFromTagLengthValues() {
        TagLengthValues tlvs = TagLengthValues.create();
        RequestMessageContentUtils.setGenericableVersion(tlvs, Version.builder("1.0.0").build());
        assertThat(RequestMessageContentUtils.getGenericableVersion(tlvs).toString()).isEqualTo("1.0.0");
    }

    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置扩展信息并获取到所设定值")
    void shouldReturnCorrectTagLengthValuesFromTagLengthValues() {
        TagLengthValues tlvsToPut = TagLengthValues.create();
        tlvsToPut.putTag(1, "abc".getBytes(StandardCharsets.UTF_8));
        TagLengthValues tlvs = TagLengthValues.create();
        RequestMessageContentUtils.setExtensions(tlvs, tlvsToPut);
        assertThat(RequestMessageContentUtils.getExtensions(tlvs).getValue(1)).isEqualTo("abc".getBytes(
                StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置数据实体并获取到所设定值")
    void shouldReturnCorrectEntityFromTagLengthValues() {
        TagLengthValues tlvs = TagLengthValues.create();
        RequestMessageContentUtils.setEntity(tlvs, "abc".getBytes(StandardCharsets.UTF_8));
        assertThat(RequestMessageContentUtils.getEntity(tlvs)).isEqualTo("abc".getBytes(StandardCharsets.UTF_8));
    }
}
