/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.lang.U1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 表示常量池中的常量。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public abstract class Constant {
    private final ConstantPool pool;
    private final U1 tag;

    /**
     * 使用常量池和标签初始化 {@link Constant} 类的新实例。
     *
     * @param pool 表示常量池的 {@link ConstantPool}。
     * @param tag 表示常量的标签的 {@link U1}。
     * @throws IllegalArgumentException 当 {@code pool} 或 {@code tag} 为 {@code null} 时。
     */
    public Constant(ConstantPool pool, U1 tag) {
        this.pool = Validation.notNull(pool, "The owning pool of constant cannot be null.");
        this.tag = Validation.notNull(tag, "The tag of a constant cannot be null.");
    }

    /**
     * 获取常量所属的常量池。
     *
     * @return 表示所属常量池的 {@link ConstantPool}。
     */
    public ConstantPool pool() {
        return this.pool;
    }

    /**
     * 获取常量所属的类文件。
     *
     * @return 表示类文件的 {@link ClassFile}。
     */
    public ClassFile file() {
        return this.pool.file();
    }

    /**
     * 获取常量的标签。
     *
     * @return 表示常量标签的 {@link U1}。
     */
    public U1 tag() {
        return this.tag;
    }

    /**
     * 获取常量所占用的槽位数。
     *
     * @return 表示常量所占用的槽位数的32位整数。
     */
    public int slots() {
        return 1;
    }

    /**
     * 将常量数据写入到输出流中。
     *
     * @param out 表示待将常量写入到的输出流的 {@link OutputStream}。
     * @throws IOException 写入数据过程发生输入输出异常。
     */
    public void write(OutputStream out) throws IOException {
        this.tag().write(out);
    }

    /**
     * 表示常量池的读取器。
     */
    @FunctionalInterface
    public interface Reader {
        /**
         * 从所属常量池和包含常量数据的输入流中读取常量。
         *
         * @param pool 表示所属常量池的 {@link ConstantPool}。
         * @param in 表示包含常量数据的输入流的 {@link InputStream}。
         * @return 表示读取的常量的 {@link Constant}。
         * @throws IOException 当读取过程中发生异常时。
         */
        Constant read(ConstantPool pool, InputStream in) throws IOException;
    }
}
