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
import modelengine.fitframework.serialization.tlv.TlvUtils;
import modelengine.fitframework.serialization.tlv.support.ExceptionPropertiesValueSerializer;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 WebSocket 通信过程中构造响应式调用异常终结信号内容的工具类。
 * <p>异常终结消息中各字段的标识与 http 调用中请求头以及流式调用消息位于不同通道，与前两者不会产生标识的冲突。</p>
 * <p>异常终结消息中需要包含异常属性集，但此处复用 {@link TlvUtils} 中的异常属性集相关方法，因此此处需要进行标识是否重复的校验。</p>
 *
 * @author 何天放
 * @since 2024-04-17
 */
public class FailMessageContentUtils extends TagValuesChecker {
    private static final int CODE_TAG = 0x00;
    private static final int MESSAGE_TAG = 0x01;
    private static final int PROPERTIES_TAG = 0x02;

    static {
        // 校验标签值，确保所有标签值不冲突。
        validate(FailMessageContentUtils.class);
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
     * @param message 表示错误码的 {@link String}。
     */
    public static void setMessage(TagLengthValues tagValues, String message) {
        if (StringUtils.isBlank(message)) {
            return;
        }
        notNull(tagValues, "The TLV cannot be null.");
        tagValues.putTag(MESSAGE_TAG, message.getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取异常属性集。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示异常属性集的 {@link Map}{@code <}{@link String}{@code ,}{@link String}{@code >}。
     */
    public static Map<String, String> getExceptionProperties(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return ExceptionPropertiesValueSerializer.INSTANCE.deserialize(tagValues.getValue(PROPERTIES_TAG));
    }

    /**
     * 向 TLV 中设置异常属性集。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param properties 表示异常属性集的 {@link Map}{@code <}{@link String}{@code ,}{@link String}{@code >}。
     */
    public static void setExceptionProperties(TagLengthValues tagValues, Map<String, String> properties) {
        if (MapUtils.isEmpty(properties)) {
            return;
        }
        notNull(tagValues, "The TLV cannot be null.");
        tagValues.putTag(PROPERTIES_TAG, ExceptionPropertiesValueSerializer.INSTANCE.serialize(properties));
    }
}
