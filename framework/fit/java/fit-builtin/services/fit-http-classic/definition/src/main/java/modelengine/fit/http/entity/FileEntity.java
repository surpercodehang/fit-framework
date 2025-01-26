/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.support.DefaultFileEntity;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;

/**
 * 表示文件类型的消息体数据。
 *
 * @author 季聿阶
 * @since 2022-10-12
 */
public interface FileEntity extends ReadableBinaryEntity {
    /**
     * 获取文件的名字。
     *
     * @return 表示文件名字的 {@link String}。
     */
    String filename();

    /**
     * 获取文件的长度。
     *
     * @return 表示文件长度的 {@code long}。
     */
    long length();

    /**
     * 获取文件是否是附件类型的标志。
     *
     * @return 表示文件是否是附件类型标志的 {@code boolean}。
     */
    boolean isAttachment();

    /**
     * 获取文件是否是内联的标志。
     *
     * @return 表示文件是否是内联标志的 {@code boolean}。
     */
    boolean isInline();

    /**
     * 创建文件类型的消息体数据。创建出来的消息体数据是附件类型，适用于文件下载。</p>
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * <p>{@link HttpMessage} 有很多子类，请仔细确认区分所属的消息是请求还是响应。</p>
     * @param filename 表示消息体内容所属文件的名字的 {@link String}。
     * @param in 表示消息体内容所属文件的输入流的 {@link InputStream}。
     * @param length 表示消息体内容所属文件的大小的 {@code long}。
     * @return 表示创建出来的文件类型的消息体数据的 {@link FileEntity}。
     */
    static FileEntity createAttachment(HttpMessage httpMessage, String filename, InputStream in, long length) {
        return create(httpMessage, filename, in, length, Position.ATTACHMENT, null);
    }

    /**
     * 创建文件类型的消息体数据。创建出来的消息体数据是内联类型，适用于页面预览。</p>
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * <p>{@link HttpMessage} 有很多子类，请仔细确认区分所属的消息是请求还是响应。</p>
     * @param filename 表示消息体内容所属文件的名字的 {@link String}。
     * @param in 表示消息体内容所属文件的输入流的 {@link InputStream}。
     * @param length 表示消息体内容所属文件的大小的 {@code long}。
     * @return 表示创建出来的文件类型的消息体数据的 {@link FileEntity}。
     */
    static FileEntity createInline(HttpMessage httpMessage, String filename, InputStream in, long length) {
        return create(httpMessage, filename, in, length, Position.INLINE, null);
    }

    /**
     * 创建文件类型的消息体数据。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * <p>{@link HttpMessage} 有很多子类，请仔细确认区分所属的消息是请求还是响应。</p>
     * @param filename 表示消息体内容所属文件的名字的 {@link String}。
     * @param in 表示消息体内容所属文件的输入流的 {@link InputStream}。
     * @param length 表示消息体内容所属文件的大小的 {@code long}。
     * @param position 表示文件显示位置的 {@link Position}。
     * @param actualFile 表示真实文件的 {@link File}。可以为 {@code null}，<b>注意：该文件在资源释放时会被删除。</b>
     * @return 表示创建出来的文件类型的消息体数据的 {@link FileEntity}。
     */
    static FileEntity create(HttpMessage httpMessage, String filename, InputStream in, long length, Position position,
            File actualFile) {
        return new DefaultFileEntity(httpMessage, filename, in, length, position, actualFile);
    }

    /**
     * 表示文件消息体数据的显示位置。
     */
    enum Position {
        /** 表示文件消息体数据需要内联显示。 */
        INLINE,

        /** 表示文件消息体数据需要作为附件下载。 */
        ATTACHMENT;

        /**
         * 将文件消息体数据的显示位置名转换为对应的显示位置。
         * <p>当显示位置名不匹配时，统一返回 {@link Position#INLINE} 作为默认的显示位置。</p>
         *
         * @param name 表示文件消息体数据的显示位置名的 {@link String}。
         * @return 表示转换后的显示位置的 {@link Position}。
         */
        public static Position from(String name) {
            for (Position position : values()) {
                if (StringUtils.equalsIgnoreCase(name, position.name())) {
                    return position;
                }
            }
            return Position.INLINE;
        }
    }
}
