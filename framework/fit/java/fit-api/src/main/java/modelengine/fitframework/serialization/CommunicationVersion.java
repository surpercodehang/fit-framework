/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization;

import modelengine.fitframework.serialization.support.CommunicationVersion3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

/**
 * 表示 FIT 通讯协议的版本。
 *
 * @author 季聿阶
 * @since 2024-02-18
 */
public interface CommunicationVersion {
    /**
     * 获取支持的通讯协议的版本号。
     *
     * @return 表示支持的通讯协议的版本号的 {@code short}。
     */
    short supported();

    /**
     * 将请求的元数据序列化成二进制数组。
     *
     * @param metadata 表示请求的元数据的 {@link RequestMetadata}。
     * @param out 表示序列化后的二进制输出流的 {@link OutputStream}。
     * @throws IOException 当序列化过程中发生输入输出异常时。
     */
    void serializeRequestMetadata(RequestMetadata metadata, OutputStream out) throws IOException;

    /**
     * 将二进制数组反序列化为请求的元数据。
     *
     * @param in 表示请求的输入流的 {@link InputStream}。
     * @return 表示反序列化后的请求元数据的 {@link RequestMetadata}。
     * @throws IOException 当反序列化过程中发生输入输出异常时。
     */
    RequestMetadata deserializeRequestMetadata(InputStream in) throws IOException;

    /**
     * 将响应的元数据序列化成二进制数组。
     *
     * @param metadata 表示响应的元数据的 {@link ResponseMetadata}。
     * @param out 表示序列化后的二进制输出流的 {@link OutputStream}。
     * @throws IOException 当序列化过程中发生输入输出异常时。
     */
    void serializeResponseMetadata(ResponseMetadata metadata, OutputStream out) throws IOException;

    /**
     * 将二进制数组反序列化为响应的元数据。
     *
     * @param in 表示请求的输入流的 {@link InputStream}。
     * @return 表示反序列化后的响应元数据的 {@link ResponseMetadata}。
     * @throws IOException 当反序列化过程中发生输入输出异常时。
     */
    ResponseMetadata deserializeResponseMetadata(InputStream in) throws IOException;

    /**
     * 获取支持的最新的协议列表。
     * <p>该协议列表已经经过排序，从最高版本到最低版本。</p>
     *
     * @return 表示支持的最新的协议列表的 {@link Queue}{@code <}{@link CommunicationVersion}{@code >}。
     */
    static Queue<CommunicationVersion> latest() {
        LinkedList<CommunicationVersion> versions = new LinkedList<>();
        versions.add(CommunicationVersion3.INSTANCE);
        return versions;
    }

    /**
     * 从支持的协议列表中选出指定的协议。
     *
     * @param versionNum 表示指定协议的版本号的 {@code short}。
     * @return 表示选出的协议的 {@link Optional}{@code <}{@link CommunicationVersion}{@code >}。
     */
    static Optional<CommunicationVersion> choose(short versionNum) {
        for (CommunicationVersion version : latest()) {
            if (versionNum == version.supported()) {
                return Optional.of(version);
            }
        }
        return Optional.empty();
    }
}
