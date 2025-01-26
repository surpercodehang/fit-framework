/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * 为类文件提供版本定义。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class ClassFileVersion implements Comparable<ClassFileVersion> {
    private final U2 major;
    private final U2 minor;

    private ClassFileVersion(U2 major, U2 minor) {
        this.major = major;
        this.minor = minor;
    }

    /**
     * 获取主版本号。
     *
     * @return 表示主版本号的 {@link U2}。
     */
    public U2 major() {
        return this.major;
    }

    /**
     * 获取次版本号。
     *
     * @return 表示次版本号的 {@link U2}。
     */
    public U2 minor() {
        return this.minor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ClassFileVersion) {
            ClassFileVersion another = (ClassFileVersion) obj;
            return Objects.equals(another.major, this.major) && Objects.equals(another.minor, this.minor);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {ClassFileVersion.class, this.major, this.minor});
    }

    @Override
    public String toString() {
        return StringUtils.format("{0}.{1}", this.major, this.minor);
    }

    @Override
    public int compareTo(ClassFileVersion another) {
        int ret = this.major().compareTo(another.major());
        if (ret == 0) {
            ret = this.minor().compareTo(another.minor());
        }
        return ret;
    }

    /**
     * 使用主版本号和次版本号创建类文件版本的新实例。
     *
     * @param major 表示主版本号的32位整数。
     * @param minor 表示次版本号的32位整数。
     * @return 表示新创建的版本信息的 {@link ClassFileVersion}。
     */
    public static ClassFileVersion of(int major, int minor) {
        return new ClassFileVersion(U2.of(major), U2.of(minor));
    }

    /**
     * 从输入流中读取版本信息。
     *
     * @param in 表示包含版本信息的输入流的 {@link InputStream}。
     * @return 表示从输入流中读取到的版本信息的 {@link ClassFileVersion}。
     * @throws IOException 读取过程发生输入输出异常。
     */
    public static ClassFileVersion read(InputStream in) throws IOException {
        U2 readMinor = U2.read(in);
        U2 readMajor = U2.read(in);
        return new ClassFileVersion(readMajor, readMinor);
    }

    /**
     * 将版本信息写入到输出流中。
     *
     * @param out 表示待将版本信息写入到的输出流的 {@link OutputStream}。
     * @throws IOException 写入过程发生输入输出异常。
     */
    public void write(OutputStream out) throws IOException {
        this.minor.write(out);
        this.major.write(out);
    }
}
