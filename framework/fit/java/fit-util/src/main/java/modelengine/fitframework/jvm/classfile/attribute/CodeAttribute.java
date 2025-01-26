/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.attribute;

import modelengine.fitframework.jvm.classfile.AttributeInfo;
import modelengine.fitframework.jvm.classfile.AttributeList;
import modelengine.fitframework.jvm.classfile.lang.U1;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.jvm.classfile.lang.U4;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 为方法提供JVM指令及辅助信息的属性。
 *
 * @author 梁济时
 * @since 2022-06-10
 */
public final class CodeAttribute extends AbstractAttribute {
    /**
     * 表示属性名称。
     */
    public static final String NAME = "Code";

    private final U2 maxStack;
    private final U2 maxLocals;
    private final Content content;
    private final ExceptionTableList exceptionTables;
    private final AttributeList attributes;

    /**
     * 使用所属的属性和包含属性数据的输入流初始化 {@link CodeAttribute} 类的新实例。
     *
     * @param attribute 表示所属的属性的 {@link AttributeInfo}。
     * @param in 表示包含属性数据的输入流的 {@link InputStream}。
     * @throws IOException 读取数据过程发生输入输出异常。
     */
    public CodeAttribute(AttributeInfo attribute, InputStream in) throws IOException {
        super(attribute);
        this.maxStack = U2.read(in);
        this.maxLocals = U2.read(in);
        this.content = new Content(this, in);
        this.exceptionTables = new ExceptionTableList(this, in);
        this.attributes = new AttributeList(attribute.list().file(), in);
    }

    /**
     * 获取方法操作符的最大深度。
     *
     * @return 表示操作符最大深度的 {@link U2}。
     */
    public U2 maxStack() {
        return this.maxStack;
    }

    /**
     * 获取方法本地变量的最大数量。
     *
     * @return 表示本地变量最大数量的 {@link U2}。
     */
    public U2 maxLocals() {
        return this.maxLocals;
    }

    /**
     * 获取代码的内容。
     * <p>内容的最大长度为65536。</p>
     *
     * @return 表示代码内容的 {@link Content}。
     */
    public Content content() {
        return this.content;
    }

    /**
     * 表示异常表格的列表。
     * <p>每个条目表示一个异常处理程序。</p>
     *
     * @return 表示异常表格的列表的 {@link ExceptionTableList}。
     */
    public ExceptionTableList exceptionTables() {
        return this.exceptionTables;
    }

    /**
     * 表示代码的属性列表。
     *
     * @return 表示属性列表的 {@link AttributeList}。
     */
    public AttributeList attributes() {
        return this.attributes;
    }

    /**
     * 为代码提供异常表格定义。
     *
     * @author 梁济时
     * @since 2022-06-10
     */
    public static final class ExceptionTable {
        private final ExceptionTableList list;
        private final U2 startPc;
        private final U2 endPc;
        private final U2 handlerPc;
        private final U2 catchType;

        private ExceptionTable(ExceptionTableList list, InputStream in) throws IOException {
            this.list = list;
            this.startPc = U2.read(in);
            this.endPc = U2.read(in);
            this.handlerPc = U2.read(in);
            this.catchType = U2.read(in);
            if (this.startPc.compareTo(this.endPc) >= 0) {
                throw new IllegalArgumentException(StringUtils.format(
                        "The start_pc must be less than the end_pc in an exception table. [start_pc={0}, end_pc={1}]",
                        this.startPc, this.endPc));
            }
        }

        /**
         * 获取所属的列表。
         *
         * @return 表示异常表格列表的 {@link ExceptionTableList}。
         */
        public ExceptionTableList list() {
            return this.list;
        }

        /**
         * 表示异常处理程序的开始位置。
         *
         * @return 表示异常处理程序开始位置的 {@link U2}。
         */
        public U2 startPc() {
            return this.startPc;
        }

        /**
         * 表示异常处理程序的结束位置。
         *
         * @return 表示异常处理程序的结束位置的 {@link U2}。
         */
        public U2 endPc() {
            return this.endPc;
        }

        /**
         * 获取异常处理程序的位置。
         *
         * @return 表示异常处理程序开始位置的 {@link U2}。
         */
        public U2 handlerPc() {
            return this.handlerPc;
        }

        /**
         * 获取所捕获异常的类型在常量池中的索引。
         * <p>该索引位置处的常量条目的类型必然是 {@code CONSTANT_Class_info}。</p>
         *
         * @return 表示常量池中索引的 {@link U2}。
         */
        public U2 catchType() {
            return this.catchType;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof ExceptionTable) {
                ExceptionTable another = (ExceptionTable) obj;
                return Objects.equals(this.startPc(), another.startPc())
                        && Objects.equals(this.endPc(), another.endPc())
                        && Objects.equals(this.handlerPc(), another.handlerPc())
                        && Objects.equals(this.catchType(), another.catchType());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {ExceptionTable.class, this.startPc(), this.endPc(),
                    this.handlerPc(), this.catchType()});
        }

        @Override
        public String toString() {
            return StringUtils.format("[start_pc={0}, end_pc={1}, handler_pc={2}, catch_type={3}]",
                    this.startPc(), this.endPc(), this.handlerPc(), this.catchType());
        }
    }

    /**
     * 为异常表格提供列表。
     *
     * @author 梁济时
     * @since 2022-06-10
     */
    public static final class ExceptionTableList implements Iterable<ExceptionTable> {
        private final CodeAttribute code;
        private final List<ExceptionTable> exceptionTables;

        private ExceptionTableList(CodeAttribute code, InputStream in) throws IOException {
            this.code = code;
            U2 count = U2.read(in);
            this.exceptionTables = new ArrayList<>(count.intValue());
            for (U2 i = U2.ZERO; i.compareTo(count) < 0; i = i.add(U2.ONE)) {
                this.exceptionTables.add(new ExceptionTable(this, in));
            }
        }

        /**
         * 获取异常处理程序所属的代码。
         *
         * @return 表示代码的 {@link CodeAttribute}。
         */
        public CodeAttribute code() {
            return this.code;
        }

        /**
         * 表示异常处理程序的数量。
         *
         * @return 表示异常处理程序数量的 {@link U2}。
         */
        public U2 count() {
            return U2.of(this.exceptionTables.size());
        }

        /**
         * 获取指定索引处的异常处理程序。
         *
         * @param index 表示待获取的异常处理程序的索引的 {@link U2}。
         * @return 表示该索引位置处的异常处理程序的 {@link ExceptionTable}。
         */
        public ExceptionTable get(U2 index) {
            return this.exceptionTables.get(index.intValue());
        }

        @Override
        public Iterator<ExceptionTable> iterator() {
            return Collections.unmodifiableList(this.exceptionTables).iterator();
        }
    }

    /**
     * 获取代码的内容。
     *
     * @author 梁济时
     * @since 2022-06-10
     */
    public static final class Content implements Iterable<U1> {
        private final CodeAttribute code;
        private final List<U1> data;

        private Content(CodeAttribute code, InputStream in) throws IOException {
            this.code = code;
            U4 count = U4.read(in);
            this.data = new ArrayList<>(count.intValue());
            for (U4 i = U4.ZERO; i.compareTo(count) < 0; i = i.add(U4.ONE)) {
                this.data.add(U1.read(in));
            }
        }

        /**
         * 获取异常处理程序所属的代码。
         *
         * @return 表示代码的 {@link CodeAttribute}。
         */
        public CodeAttribute code() {
            return this.code;
        }

        /**
         * 获取代码的长度。
         *
         * @return 表示代码长度的 {@link U4}。
         */
        public U4 count() {
            return U4.of(this.data.size());
        }

        /**
         * 获取指定索引处的数据。
         *
         * @param index 表示数据所在索引的 {@link U4}。
         * @return 表示该索引处的数据的 {@link U1}。
         */
        public U1 get(U4 index) {
            return this.data.get(index.intValue());
        }

        @Override
        public Iterator<U1> iterator() {
            return Collections.unmodifiableList(this.data).iterator();
        }
    }
}
