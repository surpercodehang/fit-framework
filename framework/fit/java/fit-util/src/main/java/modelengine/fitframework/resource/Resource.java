/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource;

import modelengine.fitframework.resource.support.FileResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 为资源提供定义。
 *
 * @author 梁济时
 * @since 2022-06-08
 */
public interface Resource {
    /**
     * 获取空的资源数组。
     */
    Resource[] EMPTY_ARRAY = new Resource[0];

    /**
     * 获取资源的名称。
     *
     * @return 表示资源的名称的 {@link String}。
     */
    String filename();

    /**
     * 获取资源的唯一资源定位符。
     *
     * @return 表示定位符的 {@link URL}。
     * @throws MalformedURLException URL 的格式不正确。
     */
    URL url() throws MalformedURLException;

    /**
     * 打开资源以读取内容。
     *
     * @return 表示用以读取资源内容的输入流的 {@link InputStream}。
     * @throws IOException 打开输入流过程发生输入输出异常。
     */
    InputStream read() throws IOException;

    /**
     * 为指定的文件创建资源。
     *
     * @param file 表示资源文件的 {@link String}。
     * @return 表示该文件的资源的 {@link Resource}。
     * @throws IllegalArgumentException {@code file} 为 {@code null}。
     */
    static Resource fromFile(File file) {
        return new FileResource(file);
    }
}
