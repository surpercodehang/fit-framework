/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import java.io.Closeable;
import java.io.IOException;

/**
 * 为数据提供随机访问能力。
 *
 * @author 梁济时
 * @since 2023-02-21
 */
public interface DataRandomReader extends Closeable {
    /**
     * 表示可读取数据的长度。
     *
     * @return 表示数据长度的 64 位整数。
     */
    long length();

    /**
     * 从指定位置读取指定长度的数据。
     *
     * @param position 表示待读取的数据所在位置的 64 位整数。
     * @param length 表示待读取的数据的长度的 32 位整数。
     * @return 表示读取到数据的字节数组。
     * @throws IllegalArgumentException {@code position} 或 {@code length} 超出限制。
     * @throws IOException 读取过程发生输入输出异常。
     */
    byte[] read(long position, int length) throws IOException;

    /**
     * 根据指定偏移量和长度获取一个新的数据随机读取器。
     *
     * @param offset 表示指定偏移量的 {@code long}。
     * @param length 表示指定长度的 {@code long}。
     * @return 表示新的数据随机读取器的 {@link DataRandomReader}。
     * @throws IOException 当创建数据随机读取器过程中发生输入输出异常时。
     */
    DataRandomReader sub(long offset, long length) throws IOException;

    /**
     * 从指定数据定位器处获得一个数据随机读取器。
     *
     * @param locator 表示指定数据定位器的 {@link DataLocator}。
     * @return 表示数据随机读取器的 {@link DataRandomReader}。
     * @throws IOException 当创建数据随机读取器过程中发生输入输出异常时。
     */
    static DataRandomReader from(DataLocator locator) throws IOException {
        return new DataRandomReaders.Default(locator);
    }
}
