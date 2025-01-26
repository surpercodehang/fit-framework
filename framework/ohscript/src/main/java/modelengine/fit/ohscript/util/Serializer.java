/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 序列化器
 *
 * @param <I> 待序列化的数据类型
 * @since 1.0
 */
public class Serializer<I> {
    private final byte[] bytes;

    /**
     * 构造方法
     *
     * @param bytes 字节数组
     */
    public Serializer(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * 序列化方法
     *
     * @param obj 待序列化的对象
     * @return 序列化后的对象
     * @throws IOException 如果发生I/O错误
     */
    public static <T> Serializer<T> serialize(T obj) throws IOException {
        try (ByteArrayOutputStream outputBytes = new ByteArrayOutputStream()) {
            try (ObjectOutputStream out = new ObjectOutputStream(outputBytes)) {
                out.writeObject(obj);
            }
            return new Serializer<T>(outputBytes.toByteArray());
        }
    }

    /**
     * 反序列化方法
     *
     * @return 反序列化后的对象
     * @throws IOException 如果发生I/O错误
     * @throws ClassNotFoundException 如果找不到类
     */
    public I deSerialize() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(this.bytes))) {
            return (I) in.readObject();
        }
    }
}
