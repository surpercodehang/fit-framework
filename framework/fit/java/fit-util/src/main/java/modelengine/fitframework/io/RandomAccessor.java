/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.io;

import modelengine.fitframework.io.support.RandomAccessorSubsection;

import java.io.IOException;

/**
 * 为数据提供随机访问程序。
 *
 * @author 梁济时
 * @since 2022-07-25
 */
public interface RandomAccessor extends AutoCloseable {
    /**
     * 读取指定偏移量位置指定长度的数据。
     *
     * @param offset 表示数据偏移量的64位整数。
     * @param length 表示待读取的数据长度的32位整数。
     * @return 表示读取到的数据的字节数组。
     * @throws IllegalArgumentException {@code offset} 或 {@code length} 超出限制。
     * @throws IOException 读取数据过程发生输入输出异常。
     */
    byte[] read(long offset, int length) throws IOException;

    /**
     * 获取所包含数据的字节数。
     *
     * @return 表示字节数的64位整数。
     */
    long size();

    @Override
    default void close() throws IOException {}

    /**
     * 获取随机访问数据的子片段。
     *
     * @param offset 表示子片段所在的偏移量的64位整数。
     * @param length 表示子片段的数据长度的64位整数。
     * @return 表示用以访问子片段的随机访问程序的 {@link RandomAccessor}。
     * @throws IllegalArgumentException {@code offset} 或 {@code length} 超出限制。
     */
    default RandomAccessor sub(long offset, long length) {
        return new RandomAccessorSubsection(this, offset, length);
    }
}
