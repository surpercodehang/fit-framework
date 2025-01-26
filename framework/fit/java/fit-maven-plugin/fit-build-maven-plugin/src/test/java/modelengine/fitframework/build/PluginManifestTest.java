/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.build.plugin.PluginManifest;
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

@DisplayName("测试 PluginMetadataFile 类")
class PluginManifestTest {
    @Test
    @DisplayName("将正确的元数据信息写入到输出流中")
    void shouldWritePluginMetadata() throws IOException, MojoExecutionException {
        Document actualDocument;
        Document expectedDocument;
        PluginManifest file = PluginManifest.custom()
                .group("group")
                .name("part1-part2")
                .version("1.0.0")
                .category("user")
                .level("5")
                .build();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            file.write(out);
            try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())) {
                actualDocument = XmlUtils.load(in);
            }
        }
        try (InputStream in = PluginManifestTest.class.getResourceAsStream("/plugin.xml")) {
            expectedDocument = XmlUtils.load(in);
        }
        Map<String, Object> actual = XmlUtils.toMap(actualDocument);
        Map<String, Object> expected = XmlUtils.toMap(expectedDocument);
        assertThat(actual).isEqualTo(expected);
    }
}
