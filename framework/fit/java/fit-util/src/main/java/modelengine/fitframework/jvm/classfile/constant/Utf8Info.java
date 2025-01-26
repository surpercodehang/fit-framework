/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.constant;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.Constant;
import modelengine.fitframework.jvm.classfile.ConstantPool;
import modelengine.fitframework.jvm.classfile.lang.U1;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.util.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * 表示UTF-8字符串常量。
 * <ul>
 *     <li><b>Tag: </b>1</li>
 *     <li><b>class file format: </b>45.3</li>
 *     <li><b>Java SE: </b>1.0.2</li>
 * </ul>
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class Utf8Info extends Constant {
    /**
     * 表示常量的标签。
     */
    public static final U1 TAG = U1.of(1);

    private final String value;

    /**
     * 使用所属的常量池及包含常量数据的输入流初始化 {@link Utf8Info} 类的新实例。
     *
     * @param pool 表示所属的常量池的 {@link ConstantPool}。
     * @param in 表示包含常量数据的输入流的 {@link InputStream}。
     * @throws IllegalArgumentException {@code pool} 或 {@code in} 为 {@code null}。
     * @throws IOException 读取过程发生输入输出异常。
     */
    public Utf8Info(ConstantPool pool, InputStream in) throws IOException {
        super(pool, TAG);
        Validation.notNull(in, "The input stream to read constant data cannot be null.");
        int length = U2.read(in).intValue();
        byte[] buffer = IoUtils.read(in, length);
        this.value = new String(buffer, StandardCharsets.UTF_8);
    }

    /**
     * 获取常量的字符串数据。
     *
     * @return 表示字符串数据的 {@link String}。
     */
    public String stringValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Utf8Info) {
            Utf8Info another = (Utf8Info) obj;
            return this.pool() == another.pool() && Objects.equals(another.value, this.value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.pool(), Utf8Info.class, this.value});
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        super.write(out);
        byte[] buffer = this.value.getBytes(StandardCharsets.UTF_8);
        U2.of(buffer.length).write(out);
        out.write(buffer);
    }
}
