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
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * 表示包信息。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class PackageInfo extends Constant {
    /**
     * 表示常量的标签。
     */
    public static final U1 TAG = U1.of(20);

    private final U2 nameIndex;

    /**
     * 构造一个新的包信息。
     *
     * @param pool 表示常量池的 {@link ConstantPool}。
     * @param in 表示输入流的 {@link InputStream}。
     * @throws IOException 如果发生 I/O 错误。
     */
    public PackageInfo(ConstantPool pool, InputStream in) throws IOException {
        super(pool, TAG);
        Validation.notNull(in, "The input stream to read constant data cannot be null.");
        this.nameIndex = U2.read(in);
    }

    /**
     * 获取包名称在常量池中的索引。
     *
     * @return 表示包名称在常量池中的索引的 {@link U2}。
     */
    public U2 nameIndex() {
        return this.nameIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof PackageInfo) {
            PackageInfo another = (PackageInfo) obj;
            return another.pool() == this.pool() && Objects.equals(this.nameIndex, another.nameIndex);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.pool(), PackageInfo.class, this.nameIndex});
    }

    @Override
    public String toString() {
        return StringUtils.format("[tag={0}, name_index={1}]", this.tag(), this.nameIndex());
    }

    @Override
    public void write(OutputStream out) throws IOException {
        super.write(out);
        this.nameIndex().write(out);
    }
}
