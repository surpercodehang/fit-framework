/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import java.io.Closeable;

/**
 * 表示 Http 消息。
 *
 * @author 季聿阶
 * @since 2022-07-11
 */
public interface Message<S extends StartLine, H extends MessageHeaders, B extends MessageBody> extends Closeable {
    /**
     * 获取 Http 消息的起始行。
     *
     * @return 表示 Http 消息的起始行的 {@link S}。
     */
    S startLine();

    /**
     * 获取 Http 消息的消息头集合。
     *
     * @return 表示 Http 消息的消息头集合的 {@link H}。
     */
    H headers();

    /**
     * 获取 Http 消息的消息体。
     *
     * @return 表示 Http 消息的消息体的 {@link B}。
     */
    B body();
}
