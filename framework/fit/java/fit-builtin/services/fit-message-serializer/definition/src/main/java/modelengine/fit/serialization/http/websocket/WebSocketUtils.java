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

/**
 * 表示 WebSocket 通信过程中通过 TLV 来传输流式调用信息的工具类。
 * <p>流式调用消息中各字段的标识与 http 调用中请求头位于不同通道，与其不会产生标识的冲突。</p>
 *
 * @author 何天放
 * @since 2024-04-15
 */
public class WebSocketUtils extends TagValuesChecker {
    private static final int STREAM_MESSAGE_TYPE_TAG = 0x80;
    private static final int STREAM_MESSAGE_INDEX_TAG = 0x81;
    private static final int STREAM_MESSAGE_CONTENT_TAG = 0x82;

    static {
        // 校验标签值，确保所有标签值不冲突。
        validate(WebSocketUtils.class);
    }

    /**
     * 从 TLV 中获取类型信息。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示类型信息的 {@code int}。
     */
    public static int getType(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        try {
            return Integer.parseInt(new String(tagValues.getValue(STREAM_MESSAGE_TYPE_TAG), UTF_8));
        } catch (NumberFormatException ignored) {
            return StreamMessageType.UNKNOWN.code();
        }
    }

    /**
     * 向 TLV 中设置类型信息。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param type 表示类型信息的 {@code int}。
     */
    public static void setType(TagLengthValues tagValues, int type) {
        notNull(tagValues, "The TLV cannot be null.");
        tagValues.putTag(STREAM_MESSAGE_TYPE_TAG, Integer.toString(type).getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取索引。
     * <p>对于请求发起和调用结果类型的消息，不需要解析本字段。</p>
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示索引的 {@code int}。
     */
    public static int getIndex(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return Integer.parseInt(new String(tagValues.getValue(STREAM_MESSAGE_INDEX_TAG), UTF_8));
    }

    /**
     * 向 TLV 中设置索引。
     * <p>对于请求发起和调用结果类型的消息，不需要设置本字段。</p>
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param index 表示索引的 {@code int}。
     */
    public static void setIndex(TagLengthValues tagValues, int index) {
        notNull(tagValues, "The TLV cannot be null.");
        tagValues.putTag(STREAM_MESSAGE_INDEX_TAG, Integer.toString(index).getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取内容。
     * <p>对于取消订阅、正常终结类型的消息不需要解析本字段。</p>
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示内容的 {@code byte[]}。
     */
    public static byte[] getContent(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return tagValues.getValue(STREAM_MESSAGE_CONTENT_TAG);
    }

    /**
     * 向 TLV 中设置内容。
     * <p>对于取消订阅、正常终结类型的消息不需要设置本字段。</p>
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param type 表示内容的 {@code byte[]}。
     */
    public static void setContent(TagLengthValues tagValues, byte[] type) {
        notNull(tagValues, "The TLV cannot be null.");
        notNull(type, "The content cannot be null.");
        tagValues.putTag(STREAM_MESSAGE_CONTENT_TAG, type);
    }
}
