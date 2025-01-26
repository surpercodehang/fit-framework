/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

/**
 * {@link Entity} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-07-14
 */
public class DefaultTextEntity extends AbstractEntity implements TextEntity {
    private final String content;

    /**
     * 创建文本类型的消息体数据对象。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param content 表示文本数据的 {@link String}。
     */
    public DefaultTextEntity(HttpMessage httpMessage, String content) {
        super(httpMessage);
        this.content = nullIf(content, StringUtils.EMPTY);
    }

    @Override
    public String content() {
        return this.content;
    }

    @Nonnull
    @Override
    public MimeType resolvedMimeType() {
        return MimeType.TEXT_PLAIN;
    }
}
