/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization;

import modelengine.fitframework.serialization.support.DefaultResponseMetadata;

/**
 * 为远端服务调用提供返回值元数据。
 *
 * @author 季聿阶
 * @since 2023-11-27
 */
public interface ResponseMetadata {
    /** 表示返回值没有错误的编码。 */
    int CODE_OK = 0;

    /**
     * 获取消息体的格式。
     *
     * @return 表示消息体格式的字节的 {@code byte}。
     */
    byte dataFormatByte();

    /**
     * 获取消息体的格式。
     *
     * @return 表示消息体格式的 {@code int}。
     */
    int dataFormat();

    /**
     * 获取消息体的状态码。
     *
     * @return 表示状态码的 {@code int}。
     */
    int code();

    /**
     * 判断如果存在异常，是否为可降级异常。
     *
     * @return 如果存在异常且是可降级异常，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isDegradable();

    /**
     * 判断如果存在异常，是否为可重试异常。
     *
     * @return 如果存在异常且是可重试异常，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isRetryable();

    /**
     * 获取消息体的消息。
     *
     * @return 表示消息的字符串的 {@link String}。
     */
    String message();

    /**
     * 获取消息体中的扩展字段。
     *
     * @return 表示扩展字段的 {@link TagLengthValues}。
     */
    TagLengthValues tagValues();

    /**
     * 为 {@link ResponseMetadata} 提供构建器。
     *
     * @author 季聿阶
     * @since 2021-05-14
     */
    interface Builder {
        /**
         * 向当前构建器中设置消息体的格式。
         *
         * @param dataFormat 表示消息体格式的字节的 {@code int}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder dataFormat(int dataFormat);

        /**
         * 向当前构建器中设置消息体的状态码。
         *
         * @param code 表示消息体的状态码的 {@code int}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder code(int code);

        /**
         * 向当前构建器中设置可降级异常的标记。
         *
         * @param isDegradable 表示可降级异常标记的 {@code boolean}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder isDegradable(boolean isDegradable);

        /**
         * 向当前构建器中设置可重试异常的标记。
         *
         * @param isRetryable 表示可重试异常标记的 {@code boolean}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder isRetryable(boolean isRetryable);

        /**
         * 向当前构建器中设置消息体的消息。
         *
         * @param message 表示消息体的消息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder message(String message);

        /**
         * 向当前构建器中设置消息体的扩展字段。
         *
         * @param tagValues 表示待设置的扩展字段的 {@link TagLengthValues}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder tagValues(TagLengthValues tagValues);

        /**
         * 构建一个远端服务调用的元数据信息。
         *
         * @return 表示包含构建器中包含的信息的远端服务调用的元数据信息的 {@link ResponseMetadata}。
         */
        ResponseMetadata build();
    }

    /**
     * 返回一个构建器，用以生成远端服务调用的元数据信息默认实现的新实例。
     *
     * @return 表示用以生成远端服务调用的元数据信息默认实现的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return custom(null);
    }

    /**
     * 返回一个构建器，用以生成远端服务调用的元数据信息默认实现的新实例。
     *
     * @param metadata 表示构建器初始状态包含信息的 {@link ResponseMetadata}。
     * @return 表示用以生成远端服务调用的元数据信息默认实现的构建器的 {@link Builder}。
     */
    static Builder custom(ResponseMetadata metadata) {
        return new DefaultResponseMetadata.Builder(metadata);
    }

    /**
     * 返回一个构建器，包含当前远端服务调用的元数据信息的数据。
     *
     * @return 表示包含当前远端服务调用的元数据信息的构建器的 {@link Builder}。
     */
    default Builder copy() {
        return custom(this);
    }

    /**
     * 返回一个序列化组件，用以对远端服务调用的元数据信息进行序列化与反序列化。
     *
     * @return 表示用以对远端服务调用的元数据信息进行序列化和反序列化的 {@link ByteSerializer}。
     */
    static ByteSerializer<ResponseMetadata> serializer() {
        return DefaultResponseMetadata.Serializer.INSTANCE;
    }

    /**
     * 将当前对象序列化为二进制序列。
     *
     * @return 表示包含当前当前对象数据的二进制序列的 {@code byte[]}。
     */
    default byte[] serialize() {
        return ByteSerializer.serialize2Bytes(serializer(), this);
    }

    /**
     * 将二进制序列反序列化为响应元数据。
     *
     * @param bytes 表示待反序列化的二进制序列的 {@code byte[]}。
     * @return 表示反序列化后的响应元数据的 {@link ResponseMetadata}。
     */
    static ResponseMetadata deserialize(byte[] bytes) {
        return ByteSerializer.deserialize(serializer(), bytes);
    }
}
