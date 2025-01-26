/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.support;

import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.XmlUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 表示清单文件的抽象类。
 *
 * @author 季聿阶
 * @since 2023-06-20
 */
public abstract class AbstractManifest {
    private static final String CRLF = "\r\n";
    private static final String CR = "\r";
    private static final String LF = "\n";

    /**
     * 将换行符替换为 Unix 风格。
     *
     * @param value 表示待替换的文本内容的 {@link String}。
     * @return 表示替换后的文本内容的 {@link String}。
     */
    protected static String unix(String value) {
        return value.replace(CRLF, LF).replace(CR, LF);
    }

    /**
     * 将给定的 XML 文档输出到指定的输出流。
     *
     * @param out 表示要写入 XML 文档的输出流的 {@link OutputStream}。
     * @param document 表示要写入输出流的 XML 文档的 {@link Document}。
     * @throws MojoExecutionException 在写入文档时发生 I/O 错误时抛出的异常。
     */
    protected static void outputDocument(OutputStream out, Document document) throws MojoExecutionException {
        String content;
        try (ByteArrayOutputStream temporary = new ByteArrayOutputStream()) {
            XmlUtils.writer(temporary).enableIndent().indentWidth(4).write(document);
            content = new String(temporary.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            String message =
                    StringUtils.format("Failed to write manifest to memory stream. [error={0}]", e.getMessage());
            throw new MojoExecutionException(message, e);
        }
        content = unix(content);
        try {
            out.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to write content to output stream. [error={0}]",
                    e.getMessage()), e);
        }
    }
}
