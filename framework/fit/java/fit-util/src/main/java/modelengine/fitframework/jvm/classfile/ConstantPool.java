/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.constant.ClassInfo;
import modelengine.fitframework.jvm.classfile.constant.DoubleInfo;
import modelengine.fitframework.jvm.classfile.constant.DynamicInfo;
import modelengine.fitframework.jvm.classfile.constant.FieldRefInfo;
import modelengine.fitframework.jvm.classfile.constant.FloatInfo;
import modelengine.fitframework.jvm.classfile.constant.IntegerInfo;
import modelengine.fitframework.jvm.classfile.constant.InterfaceMethodRefInfo;
import modelengine.fitframework.jvm.classfile.constant.InvokeDynamicInfo;
import modelengine.fitframework.jvm.classfile.constant.LongInfo;
import modelengine.fitframework.jvm.classfile.constant.MethodHandleInfo;
import modelengine.fitframework.jvm.classfile.constant.MethodRefInfo;
import modelengine.fitframework.jvm.classfile.constant.MethodTypeInfo;
import modelengine.fitframework.jvm.classfile.constant.ModuleInfo;
import modelengine.fitframework.jvm.classfile.constant.NameAndTypeInfo;
import modelengine.fitframework.jvm.classfile.constant.PackageInfo;
import modelengine.fitframework.jvm.classfile.constant.StringInfo;
import modelengine.fitframework.jvm.classfile.constant.Utf8Info;
import modelengine.fitframework.jvm.classfile.lang.U1;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 表示类文件中的常量池。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class ConstantPool implements Iterable<Constant> {
    private static final Map<U1, Constant.Reader> READERS = MapBuilder.<U1, Constant.Reader>get()
            .put(Utf8Info.TAG, Utf8Info::new)
            .put(IntegerInfo.TAG, IntegerInfo::new)
            .put(FloatInfo.TAG, FloatInfo::new)
            .put(LongInfo.TAG, LongInfo::new)
            .put(DoubleInfo.TAG, DoubleInfo::new)
            .put(ClassInfo.TAG, ClassInfo::new)
            .put(StringInfo.TAG, StringInfo::new)
            .put(FieldRefInfo.TAG, FieldRefInfo::new)
            .put(MethodRefInfo.TAG, MethodRefInfo::new)
            .put(InterfaceMethodRefInfo.TAG, InterfaceMethodRefInfo::new)
            .put(NameAndTypeInfo.TAG, NameAndTypeInfo::new)
            .put(MethodHandleInfo.TAG, MethodHandleInfo::new)
            .put(MethodTypeInfo.TAG, MethodTypeInfo::new)
            .put(DynamicInfo.TAG, DynamicInfo::new)
            .put(InvokeDynamicInfo.TAG, InvokeDynamicInfo::new)
            .put(ModuleInfo.TAG, ModuleInfo::new)
            .put(PackageInfo.TAG, PackageInfo::new)
            .build();

    private final ClassFile file;
    private final List<Constant> constants;

    ConstantPool(ClassFile file, InputStream in) throws IOException {
        this.file = Validation.notNull(file, "The owning class file of constant pool cannot be null.");
        this.constants = new ArrayList<>();
        this.constants.add(null);
        U2 length = U2.read(in);
        for (U2 i = U2.ONE; i.compareTo(length) < 0; ) {
            U1 tag = U1.read(in);
            Constant.Reader reader = READERS.get(tag);
            if (reader == null) {
                throw new IllegalClassFormatException(StringUtils.format("Unknown constant tag. [tag={0}]", tag));
            }
            Constant constant = reader.read(this, in);
            this.constants.add(constant);
            i = i.add(U2.of(constant.slots()));
            IntStream.range(1, constant.slots()).forEach(index -> this.constants.add(null));
        }
    }

    /**
     * 获取常量池所属的类文件。
     *
     * @return 表示类文件的 {@link ClassFile}。
     */
    public ClassFile file() {
        return this.file;
    }

    /**
     * 获取指定索引处的常量。
     * <p>表示从1开始的索引。</p>
     *
     * @param index 表示常量的索引的 {@link U2}。
     * @param <T> 表示常量的类型。
     * @return 表示该索引处的常量的实例 {@link Constant}。
     * @throws IndexOutOfBoundsException {@code index} 小于1或大于 {@link #count()}。
     */
    public <T extends Constant> T get(U2 index) {
        return ObjectUtils.cast(this.constants.get(index.intValue()));
    }

    /**
     * 获取常量池中常量的数量。
     *
     * @return 表示常量数量的 {@link U2}。
     */
    public U2 count() {
        return U2.of(this.constants.size() - 1);
    }

    /**
     * 返回一个流，用以处理常量池中的所有常量。
     *
     * @return 表示用以处理常量池中所有常量的流的 {@link Stream}{@code <}{@link Constant}{@code >}。
     */
    public Stream<Constant> stream() {
        return this.constants.stream().filter(Objects::nonNull);
    }

    @Override
    public Iterator<Constant> iterator() {
        return new ConstantIterator(this.constants.iterator());
    }

    private static class ConstantIterator implements java.util.Iterator<Constant> {
        private final Iterator<Constant> iterator;
        private boolean hasNext;
        private Constant next;

        private ConstantIterator(Iterator<Constant> iterator) {
            this.iterator = iterator;
            this.moveNext();
        }

        private void moveNext() {
            while (this.iterator.hasNext()) {
                this.next = this.iterator.next();
                if (this.next != null) {
                    this.hasNext = true;
                    return;
                }
            }
            this.next = null;
            this.hasNext = false;
        }

        @Override
        public boolean hasNext() {
            return this.hasNext;
        }

        @Override
        public Constant next() {
            if (this.next == null) {
                throw new NoSuchElementException("No more constant.");
            } else {
                Constant constant = this.next;
                this.moveNext();
                return constant;
            }
        }
    }
}
