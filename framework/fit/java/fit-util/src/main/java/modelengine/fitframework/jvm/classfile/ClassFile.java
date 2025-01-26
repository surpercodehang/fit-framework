/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import modelengine.fitframework.jvm.classfile.constant.ClassInfo;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.jvm.classfile.lang.U4;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 为类文件提供定义。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class ClassFile {
    /**
     * 表示类文件开始位置的幻数。
     */
    public static final U4 MAGIC = U4.of(0xCAFEBABE);

    /**
     * 表示类文件的扩展名。
     */
    public static final String FILE_EXTENSION = ".class";

    private final ClassFileVersion version;
    private final ConstantPool constants;
    private final U2 accessFlags;
    private final U2 thisClass;
    private final U2 superClass;
    private final InterfaceList interfaces;
    private final FieldList fields;
    private final MethodList methods;
    private final AttributeList attributes;

    /**
     * 使用包含类数据的输入流初始化 {@link ClassFile} 类的新实例。
     *
     * @param in 表示包含类数据的输入流的 {@link InputStream}。
     * @throws IOException 读取类文件数据过程发生输入输出异常。
     */
    public ClassFile(InputStream in) throws IOException {
        magic(in);
        this.version = ClassFileVersion.read(in);
        this.constants = new ConstantPool(this, in);
        this.accessFlags = U2.read(in);
        this.thisClass = U2.read(in);
        this.superClass = U2.read(in);
        this.interfaces = new InterfaceList(this, in);
        this.fields = new FieldList(this, in);
        this.methods = new MethodList(this, in);
        this.attributes = new AttributeList(this, in);
    }

    private static void magic(InputStream in) throws IOException {
        U4 magic = U4.read(in);
        if (!Objects.equals(magic, MAGIC)) {
            throw new IllegalClassFormatException(StringUtils.format(
                    "The data does not start with specific magic number. [actual={0}, expected={1}]",
                    magic.toHexString(), MAGIC.toHexString()));
        }
    }

    /**
     * 获取类文件的版本信息。
     *
     * @return 表示类文件版本信息的 {@link ClassFileVersion}。
     */
    public ClassFileVersion version() {
        return this.version;
    }

    /**
     * 获取类文件中定义的常量池。
     *
     * @return 表示常量池的 {@link ConstantPool}。
     */
    public ConstantPool constants() {
        return this.constants;
    }

    /**
     * 获取类型的访问标记。
     *
     * @return 表示访问标记的 {@link U2}。
     */
    public U2 accessFlags() {
        return this.accessFlags;
    }

    /**
     * 获取当前类型在常量池中的索引。
     * <p>常量池中该位置的索引必然是一个 {@link ClassInfo CONSTANT_Class_info}。</p>
     *
     * @return 表示常量池中的索引的 {@link U2}。
     */
    public U2 thisClass() {
        return this.thisClass;
    }

    /**
     * 获取父类型在常量池中的索引。
     * <ul>
     *     <li>当类型为 {@link Object}，值为 {@code 0}</li>
     *     <li>当类型为接口时，值为常量池中表示 {@link Object} 的 {@link ClassInfo CONSTANT_Class_info} 类型常量的索引</li>
     *     <li>其他情况下，值必然为常量池中 {@link ClassInfo CONSTANT_Class_info} 类型的常量的有效索引</li>
     * </ul>
     *
     * @return 表示常量池中的索引的 {@link U2}。
     */
    public U2 superClass() {
        return this.superClass;
    }

    /**
     * 获取类型所实现的接口的列表。
     *
     * @return 表示接口列表的 {@link InterfaceList}。
     */
    public InterfaceList interfaces() {
        return this.interfaces;
    }

    /**
     * 获取类型中包含字段的列表。
     *
     * @return 表示字段列表的 {@link FieldList}。
     */
    public FieldList fields() {
        return this.fields;
    }

    /**
     * 获取类型中包含的方法的列表。
     *
     * @return 表示方法列表的 {@link MethodList}。
     */
    public MethodList methods() {
        return this.methods;
    }

    /**
     * 获取类型相关的属性的列表。
     *
     * @return 表示属性列表的 {@link AttributeList}。
     */
    public AttributeList attributes() {
        return this.attributes;
    }
}
