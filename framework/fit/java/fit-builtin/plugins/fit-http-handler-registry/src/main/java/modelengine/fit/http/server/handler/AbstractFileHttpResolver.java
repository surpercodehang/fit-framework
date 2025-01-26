/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * 表示文件资源的 http 请求解析器基类。
 *
 * @author 邬涨财
 * @since 2024-01-18
 */
public abstract class AbstractFileHttpResolver<T> {
    /**
     * 获取文件类型的消息体数据。
     *
     * @param path 表示 HTTP 请求的资源路径的 {@link String}。
     * @param response 表示 Http 响应的 {@link HttpClassicServerResponse}。
     * @param position 表示文件消息体数据的显示位置的 {@link FileEntity.Position}。
     * @param locationValues 表示需要搜索的目录的 {@link List}{@code <}{@link String}{@code >}。
     * @param classLoader 表示获取资源使用到的类加载器的 {@link ClassLoader}。
     * @return 获取到的文件类型的消息体数据的 {@link Optional}{@code <}{@link FileEntity}{@code >}。
     */
    public Optional<FileEntity> getFileEntity(String path, HttpClassicServerResponse response,
            FileEntity.Position position, List<String> locationValues, ClassLoader classLoader) {
        for (String location : locationValues) {
            String actualPath = location + path;
            T file = this.getFile(actualPath, classLoader);
            if (!this.isFileValid(file)) {
                continue;
            }
            try {
                // 该输入流在当前时刻不能关闭，必须得在 Http 响应结束后统一关闭。
                InputStream inputStream = this.getInputStream(file);
                long length = this.getLength(file, actualPath, inputStream);
                return Optional.of(FileEntity.create(response,
                        this.getFileName(file),
                        inputStream,
                        length,
                        position,
                        null));
            } catch (IOException e) {
                throw new IllegalStateException(StringUtils.format("Failed to read {0}. [name={1}]",
                        this.getType(),
                        this.getFileName(file)));
            }
        }
        return Optional.empty();
    }

    /**
     * 获取文件资源。
     *
     * @param actualPath 表示文件路径的 {@link String}。
     * @param classLoader 表示获取资源使用到的类加载器的 {@link ClassLoader}。
     * @return 表示获取到的文件资源对象。
     */
    protected abstract T getFile(String actualPath, ClassLoader classLoader);

    /**
     * 判断文件资源是否合法。
     *
     * @param file 表示文件资源的 {@code T}。
     * @return 表示文件资源是否合法的 {@code T}。
     */
    protected abstract boolean isFileValid(T file);

    /**
     * 获取文件资源的输入流。
     *
     * @param file 表示文件资源的 {@code T}。
     * @return 表示文件资源的输入流的 {@link InputStream}。
     * @throws IOException 表示获取文件资源输入流发生的 I/O 异常。
     */
    protected abstract InputStream getInputStream(T file) throws IOException;

    /**
     * 获取文件资源的长度
     *
     * @param file 表示文件资源的 {@code T}。
     * @param actualPath 表示文件路径的 {@link String}。
     * @param inputStream 表示文件资源的输入流的 {@link InputStream}。
     * @return 表示长度的 {@code long}。
     * @throws IOException 表示获取文件资源输入流发生的 I/O 异常。
     */
    protected abstract long getLength(T file, String actualPath, InputStream inputStream) throws IOException;

    /**
     * 获取文件名。
     *
     * @param file 表示文件资源的 {@code T}。
     * @return 表示文件名的 {@link String}。
     */
    protected abstract String getFileName(T file);

    /**
     * 获取文件资源的类型。
     *
     * @return 表示文件资源的类型的 {@link String}。
     */
    protected abstract String getType();
}
