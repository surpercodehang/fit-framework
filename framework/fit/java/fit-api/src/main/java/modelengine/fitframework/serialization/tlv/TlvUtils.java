/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.tlv;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.tlv.support.ExceptionPropertiesValueSerializer;
import modelengine.fitframework.util.MapUtils;

import java.util.Map;

/**
 * 表示 {@link TagLengthValues} 的工具类。
 *
 * @author 季聿阶
 * @since 2024-05-09
 */
public class TlvUtils {
    /**
     * 从 TLV 中获取进程的唯一标识。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示进程唯一标识的 {@link String}。
     */
    public static String getWorkerId(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return new String(tagValues.getValue(Tags.getWorkerIdTag()), UTF_8);
    }

    /**
     * 向 TLV 中设置进程的唯一标识。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param workerId 表示进程唯一标识的 {@link String}。
     */
    public static void setWorkerId(TagLengthValues tagValues, String workerId) {
        notNull(tagValues, "The TLV cannot be null.");
        notBlank(workerId, "The worker id cannot be blank.");
        tagValues.putTag(Tags.getWorkerIdTag(), workerId.getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取进程实例的唯一标识。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示进程实例唯一标识的 {@link String}。
     */
    public static String getWorkerInstanceId(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return new String(tagValues.getValue(Tags.getWorkerInstanceIdTag()), UTF_8);
    }

    /**
     * 向 TLV 中设置进程实例的唯一标识。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param workerInstanceId 表示进程实例唯一标识的 {@link String}。
     */
    public static void setWorkerInstanceId(TagLengthValues tagValues, String workerInstanceId) {
        notNull(tagValues, "The TLV cannot be null.");
        notBlank(workerInstanceId, "The worker instance id cannot be blank.");
        tagValues.putTag(Tags.getWorkerInstanceIdTag(), workerInstanceId.getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取异常属性集。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示异常属性集的 {@link Map}{@code <}{@link String}{@code ,}{@link String}{@code >}。
     */
    public static Map<String, String> getExceptionProperties(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        int tag = Tags.getExceptionPropertiesTag();
        return ExceptionPropertiesValueSerializer.INSTANCE.deserialize(tagValues.getValue(tag));
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
        int tag = Tags.getExceptionPropertiesTag();
        tagValues.putTag(tag, ExceptionPropertiesValueSerializer.INSTANCE.serialize(properties));
    }
}
