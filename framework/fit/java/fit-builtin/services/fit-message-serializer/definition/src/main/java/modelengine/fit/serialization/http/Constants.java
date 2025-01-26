/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.http;

/**
 * 表示 FIT Http 通信的地址信息集合。
 *
 * @author 季聿阶
 * @since 2024-02-17
 */
public class Constants {
    /** 表示 FIT 通信中同步获取结果的路径。 */
    public static final String FIT_PATH_PATTERN = "/fit/{genericableId}/{fitableId}";

    /** 表示 FIT 通信中等待异步结果的路径。 */
    public static final String FIT_ASYNC_TASK_PATH_PATTERN = "/fit/async/await-response";

    /** 表示 FIT 通信中异步长轮询的最长等待时间。 */
    public static final long FIT_ASYNC_LONG_POLLING_DURATION_MILLIS = 60_000L;
}
