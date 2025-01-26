/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.http;

import modelengine.fitframework.serialization.tlv.Tags;

/**
 * 表示 Http 通道中 {@link modelengine.fitframework.serialization.TagLengthValues} 的标签常量值。
 *
 * @author 季聿阶
 * @since 2024-05-09
 */
public class HttpTags extends Tags {
    /** 表示异步任务唯一标识的标签值。 */
    private static final int ASYNC_TASK_ID_TAG = 0x40;

    static {
        // 校验标签值，确保所有标签值不冲突。
        validate(HttpTags.class);
    }

    /**
     * 获取异步任务唯一标识的标签值。
     *
     * @return 表示异步任务唯一标识的标签值的 {@code int}。
     */
    public static int getAsyncTaskIdTag() {
        return ASYNC_TASK_ID_TAG;
    }
}
