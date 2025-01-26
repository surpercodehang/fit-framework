/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.http;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.serialization.TagLengthValues;

import java.util.Base64;

/**
 * 表示 Http 通信过程中通过 TLV 来传输扩展信息的工具类。
 *
 * @author 季聿阶
 * @since 2024-02-17
 */
public class HttpUtils {
    /**
     * 从 TLV 中获取异步任务的唯一标识。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示异步任务唯一标识的 {@link String}。
     */
    public static String getAsyncTaskId(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return new String(tagValues.getValue(HttpTags.getAsyncTaskIdTag()), UTF_8);
    }

    /**
     * 向 TLV 中设置异步任务的唯一标识。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param asyncTaskId 表示异步任务唯一标识的 {@link String}。
     */
    public static void setAsyncTaskId(TagLengthValues tagValues, String asyncTaskId) {
        notNull(tagValues, "The TLV cannot be null.");
        notBlank(asyncTaskId, "The async task id cannot be blank.");
        tagValues.putTag(HttpTags.getAsyncTaskIdTag(), asyncTaskId.getBytes(UTF_8));
    }

    /**
     * 将数据进行编码。
     *
     * @param data 表示待编码的数据的 {@code byte[]}。
     * @return 表示编码后的数据内容的 {@code String}。
     */
    public static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * 将数据进行解码。
     *
     * @param data 表示待解码的数据的 {@link String}。
     * @return 表示解码后的数据内容的 {@code byte[]}。
     */
    public static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
