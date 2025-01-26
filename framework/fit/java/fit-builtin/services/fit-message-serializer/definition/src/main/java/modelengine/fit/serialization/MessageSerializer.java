/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 调用泛服务的输入输出提供序列化程序。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-11-05
 */
public interface MessageSerializer {
    /**
     * 将调用的请求信息序列化成二进制序列。
     *
     * @param argumentTypes 表示请求参数类型列表的 {@link Type}{@code []}。
     * @param arguments 表示请求参数列表的 {@link Object}{@code []}。
     * @return 表示包含请求参数信息的二进制序列的 {@code byte[]}。
     */
    byte[] serializeRequest(Type[] argumentTypes, Object[] arguments);

    /**
     * 从二进制序列中反序列化出调用的请求参数列表。
     *
     * @param argumentTypes 表示请求参数类型列表的 {@link Type}{@code []}。
     * @param serialized 表示包含请求参数信息的二进制序列的 {@code byte[]}。
     * @return 表示请求参数列表的 {@link Object}{@code []}。
     */
    Object[] deserializeRequest(Type[] argumentTypes, byte[] serialized);

    /**
     * 将调用的响应信息序列化成二进制序列。
     *
     * @param returnType 表示响应数据类型的 {@link Type}。
     * @param returnData 表示响应数据的 {@link T}。
     * @param <T> 表示响应数据类型的 {@link T}。
     * @return 表示包含响应消息信息的二进制序列的 {@code byte[]}。
     */
    <T> byte[] serializeResponse(Type returnType, T returnData);

    /**
     * 从二进制序列中反序列化出调用的响应信息。
     *
     * @param returnType 表示响应数据类型的 {@link Type}。
     * @param serialized 表示包含响应消息信息的二进制序列的 {@code byte[]}。
     * @param <T> 表示响应数据类型的 {@link T}。
     * @return 表示响应消息体的 {@link T}。
     */
    <T> T deserializeResponse(Type returnType, byte[] serialized);

    /**
     * 根据指定的方法，判断当前的序列化方式是否支持。
     *
     * @param method 表示指定的方法的 {@link Method}。
     * @return 表示是否支持的结果的 {@code boolean}。
     */
    boolean isSupported(Method method);

    /**
     * 获取当前序列化器的序列化格式代号。
     * <p>支持的序列化格式代号如下：
     * <ul>
     *     <li>ProtoBuf：0</li>
     *     <li>Json：1</li>
     *     <li>CBOR：2</li>
     * </ul>
     * </p>
     *
     * @return 表示当前序列化器的序列化格式代号的 {@code int}。
     */
    int getFormat();
}
