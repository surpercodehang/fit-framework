/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.build.service.ErrorManifest;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.XmlUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 表示 {@link ErrorManifest} 的单元测试。
 *
 * @author 季聿阶
 * @since 2023-06-18
 */
@DisplayName("测试 ErrorMetadata 类")
class ErrorManifestTest {
    @Test
    @DisplayName("将正确的元数据信息写入到输出流中")
    void shouldWriteErrorMetadata() throws IOException, MojoExecutionException {
        Document actualDocument;
        Document expectedDocument;
        ErrorManifest file =
                new ErrorManifest(MapBuilder.<String, Integer>get().put("modelengine.SampleException", 1).build());
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            file.write(out);
            try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())) {
                actualDocument = XmlUtils.load(in);
            }
        }
        try (InputStream in = ErrorManifestTest.class.getResourceAsStream("/errors.xml")) {
            expectedDocument = XmlUtils.load(in);
        }
        Map<String, Object> actual = XmlUtils.toMap(actualDocument);
        Map<String, Object> expected = XmlUtils.toMap(expectedDocument);
        assertThat(actual).isEqualTo(expected);
    }
}
