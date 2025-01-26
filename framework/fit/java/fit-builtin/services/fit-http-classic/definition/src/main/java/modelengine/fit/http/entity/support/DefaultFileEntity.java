/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity.support;

import static modelengine.fitframework.inspection.Validation.greaterThanOrEquals;
import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link FileEntity} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-10-12
 */
public class DefaultFileEntity extends DefaultReadableBinaryEntity implements FileEntity {
    private static final Map<String, MimeType> MIME_TYPE_MAP = MapBuilder.<String, MimeType>get()
            .put(".js", MimeType.APPLICATION_JAVASCRIPT)
            .put(".css", MimeType.TEXT_CSS)
            .build();

    private final String filename;
    private final long length;
    private final Position position;
    private final File actualFile;

    /**
     * 创建文件类型的消息体数据对象。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param filename 表示消息体内容所属文件的名字的  {@link String}。
     * @param in 表示消息体内容所属文件的输入流的 {@link InputStream}。
     * @param length 表示消息体内容所属文件的大小的 {@code long}。
     * @param position 表示文件显示位置的 {@link Position}。
     * @param actualFile 表示真实文件的 {@link File}。可以为 {@code null}，<b>注意：该文件在资源释放时会被删除。</b>
     */
    public DefaultFileEntity(HttpMessage httpMessage, String filename, InputStream in, long length, Position position,
            File actualFile) {
        super(httpMessage, in);
        this.filename = notBlank(filename, "The filename cannot be blank.");
        this.length = greaterThanOrEquals(length, 0, "The file length must not be negative. [length={0}]", length);
        this.position = ObjectUtils.nullIf(position, Position.INLINE);
        this.actualFile = actualFile;
    }

    @Override
    public String filename() {
        return this.filename;
    }

    @Override
    public long length() {
        return this.length;
    }

    @Override
    public boolean isAttachment() {
        return this.position == Position.ATTACHMENT;
    }

    @Override
    public boolean isInline() {
        return this.position == Position.INLINE;
    }

    @Nonnull
    @Override
    public MimeType resolvedMimeType() {
        return Optional.ofNullable(MimeType.from(URLConnection.getFileNameMap().getContentTypeFor(this.filename)))
                .orElseGet(() -> {
                    String fileExtension = this.getFileExtension();
                    return Optional.ofNullable(MIME_TYPE_MAP.get(fileExtension)).orElseGet(super::resolvedMimeType);
                });
    }

    private String getFileExtension() {
        return StringUtils.toLowerCase(FileUtils.extension(this.filename));
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (this.actualFile != null) {
            FileUtils.delete(this.actualFile);
        }
    }
}
