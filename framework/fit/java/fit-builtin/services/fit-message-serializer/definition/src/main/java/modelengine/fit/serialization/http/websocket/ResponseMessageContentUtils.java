/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.http.websocket;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.tlv.TagValuesChecker;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示 WebSocket 通信过程中构造调用结果的工具类。
 * <p>调用结果消息中各字段的标识与 http 调用中请求头以及流式调用消息位于不同通道，与前两者不会产生标识的冲突。</p>
 *
 * @author 何天放
 * @since 2024-04-17
 */
public class ResponseMessageContentUtils extends TagValuesChecker {
    private static final int DATA_FORMAT_TAG = 0x00;
    private static final int CODE_TAG = 0x01;
    private static final int MESSAGE_TAG = 0x02;
    private static final int TLV_TAG = 0x03;
    private static final int ENTITY_TAG = 0x04;

    static {
        // 校验标签值，确保所有标签值不冲突。
        validate(RequestMessageContentUtils.class);
    }

    /**
     * 从 TLV 中获取序列化方式。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示序列化方式的 {@code int}。
     */
    public static int getDataFormat(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return Integer.parseInt(new String(tagValues.getValue(DATA_FORMAT_TAG), UTF_8));
    }

    /**
     * 向 TLV 中设置序列化方式。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param dataFormat 表示序列化方式的 {@code int}。
     */
    public static void setDataFormat(TagLengthValues tagValues, int dataFormat) {
        notNull(tagValues, "The TLV cannot be null.");
        tagValues.putTag(DATA_FORMAT_TAG, Integer.toString(dataFormat).getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取错误码。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示错误码的 {@code int}。
     */
    public static int getCode(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return Integer.parseInt(new String(tagValues.getValue(CODE_TAG), UTF_8));
    }

    /**
     * 向 TLV 中设置错误码。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param code 表示错误码的 {@code int}。
     */
    public static void setCode(TagLengthValues tagValues, int code) {
        notNull(tagValues, "The TLV cannot be null.");
        tagValues.putTag(CODE_TAG, Integer.toString(code).getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取异常消息。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示异常消息的 {@link String}。
     */
    public static String getMessage(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return new String(tagValues.getValue(MESSAGE_TAG), UTF_8);
    }

    /**
     * 向 TLV 中设置异常消息。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param message 表示异常消息的 {@link String}。
     */
    public static void setMessage(TagLengthValues tagValues, String message) {
        if (StringUtils.isBlank(message)) {
            return;
        }
        notNull(tagValues, "The TLV cannot be null.");
        tagValues.putTag(MESSAGE_TAG, message.getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取扩展字段。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示扩展字段的 {@link TagLengthValues}。
     */
    public static TagLengthValues getExtensions(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return TagLengthValues.deserialize(tagValues.getValue(TLV_TAG));
    }

    /**
     * 向 TLV 中设置扩展字段。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param extensions 表示扩展字段的 {@link TagLengthValues}。
     */
    public static void setExtensions(TagLengthValues tagValues, TagLengthValues extensions) {
        notNull(tagValues, "The TLV cannot be null.");
        notNull(extensions, "The extensions cannot be null.");
        tagValues.putTag(TLV_TAG, extensions.serialize());
    }

    /**
     * 从 TLV 中获取数据实体。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示数据实体的 {@code byte[]}。
     */
    public static byte[] getEntity(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return tagValues.getValue(ENTITY_TAG);
    }

    /**
     * 向 TLV 中设置数据实体。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param entity 表示数据实体的 {@code byte[]}。
     */
    public static void setEntity(TagLengthValues tagValues, byte[] entity) {
        notNull(tagValues, "The TLV cannot be null.");
        notNull(entity, "The entity cannot be null.");
        tagValues.putTag(ENTITY_TAG, entity);
    }
}
