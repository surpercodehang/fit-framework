/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization;

import modelengine.fitframework.serialization.support.DefaultTagLengthValues;

import java.util.Map;
import java.util.Set;

/**
 * 为元数据提供扩展字段(Tag-Length-Value)。
 *
 * @author 季聿阶
 * @since 2021-05-14
 */
public interface TagLengthValues {
    /**
     * 获取扩展字段中的所有标识集合。
     *
     * @return 表示扩展字段中的所有标识的 {@link Set}{@code <}{@link Integer}{@code >}。
     */
    Set<Integer> getTags();

    /**
     * 获取扩展字段中的指定标识的值。
     *
     * @param tag 表示所指定标识的 {@code int}。
     * @return 表示扩展字段中指定标识的值的 {@code byte[]}。
     */
    byte[] getValue(int tag);

    /**
     * 设置标识和值。
     *
     * @param tag 表示待设置的标识的 {@code int}。
     * @param value 表示待设置的标识的值的 {@code byte[]}。
     */
    void putTag(int tag, byte[] value);

    /**
     * 设置一组标识和值。
     *
     * @param tagValues 表示待设置的一组标识和值的键值对的 {@link Map}{@code <}{@link Integer}{@code , byte[]>}。
     */
    void putTags(Map<Integer, byte[]> tagValues);

    /**
     * 清除指定的标识。
     *
     * @param tag 表示待清除的标识的 {@code int}。
     */
    void remove(int tag);

    /**
     * 创建一个空的扩展字段集合。
     *
     * @return 表示一个空的扩展字段集合的 {@link TagLengthValues}。
     */
    static TagLengthValues create() {
        return new DefaultTagLengthValues();
    }

    /**
     * 返回一个序列化组件，用以对扩展字段进行序列化与反序列化。
     *
     * @return 表示用以对扩展字段进行序列化和反序列化的 {@link ByteSerializer}{@code <}{@link TagLengthValues}{@code >}。
     */
    static ByteSerializer<TagLengthValues> serializer() {
        return DefaultTagLengthValues.Serializer.INSTANCE;
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
     * 将二进制序列反序列化为扩展字段集合。
     *
     * @param bytes 表示待反序列化的二进制序列的 {@code byte[]}。
     * @return 表示反序列化后的扩展字段集合的 {@link TagLengthValues}。
     */
    static TagLengthValues deserialize(byte[] bytes) {
        return ByteSerializer.deserialize(serializer(), bytes);
    }
}
