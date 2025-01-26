/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.descriptor;

import static modelengine.fitframework.inspection.Validation.between;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 为类型提供描述符。
 *
 * @author 梁济时
 * @since 2022-10-20
 */
public abstract class ClassDescriptor {
    /**
     * 隐藏默认构造方法，类型只能在内部类中被继承。
     */
    private ClassDescriptor() {}

    /**
     * 表示当前类型的名词。
     *
     * @return 表示名称名词的 {@link String}。
     */
    public abstract String term();

    /**
     * 检查当前描述符是否是指定类型的实例。
     *
     * @param clazz 表示待检查的类型的 {@link Class}。
     * @return 若是指定类型的实例，则为 {@code true}，否则为 {@code false}。
     */
    public boolean is(Class<? extends ClassDescriptor> clazz) {
        return clazz != null && clazz.isInstance(this);
    }

    @Override
    public String toString() {
        return this.term();
    }

    /**
     * 表示基本类型。
     *
     * @author 梁济时
     * @since 2022-10-20
     */
    public static final class Primitive extends ClassDescriptor {
        /**
         * 表示 {@code byte} 基本类型。
         */
        public static final Primitive BYTE = new Primitive('B', byte.class);

        /**
         * 表示 {@code char} 基本类型。
         */
        public static final Primitive CHAR = new Primitive('C', char.class);

        /**
         * 表示 {@code double} 基本类型。
         */
        public static final Primitive DOUBLE = new Primitive('D', double.class);

        /**
         * 表示 {@code float} 基本类型。
         */
        public static final Primitive FLOAT = new Primitive('F', float.class);

        /**
         * 表示 {@code int} 基本类型。
         */
        public static final Primitive INT = new Primitive('I', int.class);

        /**
         * 表示 {@code long} 基本类型。
         */
        public static final Primitive LONG = new Primitive('J', long.class);

        /**
         * 表示 {@code short} 基本类型。
         */
        public static final Primitive SHORT = new Primitive('S', short.class);

        /**
         * 表示 {@code boolean} 基本类型。
         */
        public static final Primitive BOOLEAN = new Primitive('Z', boolean.class);

        private final char term;
        private final Class<?> type;

        private Primitive(char term, Class<?> type) {
            this.term = term;
            this.type = type;
        }

        @Override
        public String term() {
            return Character.toString(this.term);
        }

        /**
         * 获取表示的基本数据类型。
         *
         * @return 表示基本数据类型的 {@link Class}。
         */
        public Class<?> type() {
            return this.type;
        }

        /**
         * 获取基本数据类型。
         *
         * @param term 表示输入字符的 {@code char}。
         * @return 表示基本数据类型的 {@link Primitive}。
         */
        public static Primitive of(char term) {
            switch (term) {
                case 'B':
                    return BYTE;
                case 'C':
                    return CHAR;
                case 'D':
                    return DOUBLE;
                case 'F':
                    return FLOAT;
                case 'I':
                    return INT;
                case 'J':
                    return LONG;
                case 'S':
                    return SHORT;
                case 'Z':
                    return BOOLEAN;
                default:
                    return null;
            }
        }

        @Override
        public String toString() {
            return this.type.toString();
        }
    }

    /**
     * 表示引用类型。
     *
     * @author 梁济时
     * @since 2022-10-20
     */
    public static final class Reference extends ClassDescriptor {
        private final String name;

        private Reference(String name) {
            this.name = name;
        }

        @Override
        public String term() {
            StringBuilder builder = new StringBuilder(this.name.length() + 2);
            builder.append('L');
            for (int i = 0; i < this.name.length(); i++) {
                char ch = this.name.charAt(i);
                if (ch == '.') {
                    builder.append('/');
                } else {
                    builder.append(ch);
                }
            }
            builder.append(';');
            return builder.toString();
        }

        /**
         * 获取类型名称。
         *
         * @return 表示类型名称的 {@link String}。
         */
        public String name() {
            return this.name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj != null && obj.getClass() == this.getClass()) {
                Reference another = (Reference) obj;
                return Objects.equals(this.name, another.name);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {this.getClass(), this.name});
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    /**
     * 表示数组类型。
     *
     * @author 梁济时
     * @since 2022-10-20
     */
    public static final class Array extends ClassDescriptor {
        private final ClassDescriptor element;

        private Array(ClassDescriptor element) {
            this.element = element;
        }

        @Override
        public String term() {
            return "[" + this.element.term();
        }

        /**
         * 获取数组中元素的类型。
         *
         * @return 表示元素类型的 {@link ClassDescriptor}。
         */
        public ClassDescriptor element() {
            return this.element;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj != null && obj.getClass() == this.getClass()) {
                Array another = (Array) obj;
                return Objects.equals(this.element, another.element);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {this.getClass(), this.element});
        }

        @Override
        public String toString() {
            return this.element().toString() + "[]";
        }
    }

    /**
     * 表示 {@code void} 类型。
     *
     * @author 梁济时
     * @since 2022-10-20
     */
    public static final class Void extends ClassDescriptor {
        private static final Void INSTANCE = new Void();

        private Void() {}

        /**
         * 获取类型的唯一实例。
         *
         * @return 表示唯一实例的 {@link Void}。
         */
        public static Void instance() {
            return INSTANCE;
        }

        @Override
        public String term() {
            return "V";
        }

        @Override
        public String toString() {
            return "void";
        }
    }

    /**
     * 为 {@link ClassDescriptor} 提供解析程序。
     *
     * @author 梁济时
     * @since 2022-10-20
     */
    public static final class Parser {
        private final CharSequence chars;
        private final int start;
        private final int end;

        private int position;

        /**
         * 使用待读取的字符序初始化 {@link Parser} 类的新实例。
         *
         * @param chars 表示待读取的字符序的 {@link CharSequence}。
         * @throws IllegalArgumentException {@code text} 为 {@code null}。
         */
        public Parser(CharSequence chars) {
            this(chars, 0, chars.length());
        }

        /**
         * 使用待读取的字符序及字段信息在字符序中的开始位置初始化 {@link Parser} 类的新实例。
         *
         * @param chars 表示待读取的字符序的 {@link CharSequence}。
         * @param start 表示待读取的信息在字符序中的位置的 32 位整数。
         * @throws IllegalArgumentException {@code text} 为 {@code null} 或 {@code start} 超出字符串长度限制。
         */
        public Parser(CharSequence chars, int start) {
            this(chars, start, chars.length());
        }

        /**
         * 使用待读取的字符序及字段信息在字符序中的范围初始化 {@link Parser} 类的新实例。
         *
         * @param chars 表示待读取的字符序的 {@link CharSequence}。
         * @param start 表示待读取的信息在字符序中的位置的 32 位整数。
         * @param end 表示待读取的信息在字符序中的结束位置的 32 位整数。
         * @throws IllegalArgumentException {@code text} 为 {@code null} 或 {@code start}、{@code end} 超出字符序长度限制。
         */
        public Parser(CharSequence chars, int start, int end) {
            this.chars = notNull(chars, "The text to read field type cannot be null.");
            this.start = between(start,
                    0,
                    chars.length(),
                    "The start of text to read field type is out of bounds. [text={0}, start={1}]",
                    chars,
                    start);
            this.end = between(end,
                    start,
                    chars.length(),
                    "The end of text to read field type is out of bounds. [text={0}, start={1}, end={2}]",
                    chars,
                    start,
                    end);

            this.position = this.start;
        }

        /**
         * 获取当前待读取的位置。
         *
         * @return 表示当前位置的 32 位整数。
         */
        public int position() {
            return this.position;
        }

        /**
         * 解析下一个字段类型。
         *
         * @return 若存在下一个字段类型，则为表示解析到的字段类型的 {@link ClassDescriptor}，否则为 {@code null}。
         * @throws IllegalStateException 未能解析到有效的字段类型。
         */
        public ClassDescriptor parseNext() {
            if (this.position >= this.end) {
                return null;
            }
            char ch = this.chars.charAt(this.position++);
            ClassDescriptor type = Primitive.of(ch);
            if (type != null) {
                return type;
            }
            if (ch == '[') {
                return new Array(this.parseNext());
            }
            if (ch == 'L') {
                return this.readReferenceType();
            }
            if (ch == 'V') {
                return Void.instance();
            }
            throw new IllegalStateException(StringUtils.format(
                    "Unexpected character occurs. [text={0}, position={1}, character={2}]",
                    this.chars,
                    this.position - 1,
                    ch));
        }

        /**
         * 解析所有的类型描述符。
         *
         * @return 表示解析到的类型描述符的列表的 {@link List}{@code <}{@link ClassDescriptor}{@code >}。
         */
        public List<ClassDescriptor> parseAll() {
            List<ClassDescriptor> descriptors = new LinkedList<>();
            ClassDescriptor descriptor;
            while ((descriptor = this.parseNext()) != null) {
                descriptors.add(descriptor);
            }
            return new ArrayList<>(descriptors);
        }

        private ClassDescriptor readReferenceType() {
            StringBuilder builder = new StringBuilder();
            while (this.position < this.end) {
                char ch = this.chars.charAt(position++);
                if (ch == ';') {
                    if (builder.length() < 1) {
                        throw new IllegalStateException(StringUtils.format(
                                "Empty name of reference type. [text={0}, position={1}]",
                                this.chars,
                                this.position - 2));
                    }
                    return new Reference(builder.toString());
                } else if (ch == '/') {
                    builder.append('.');
                } else {
                    builder.append(ch);
                }
            }
            throw new IllegalStateException(StringUtils.format("Incomplete reference type. [text={0}]", this.chars));
        }

        @Override
        public String toString() {
            return StringUtils.format("[text={0}, position={1}]",
                    this.chars.subSequence(this.start, this.end),
                    this.position);
        }
    }

    /**
     * 获取指定类型的描述符。
     *
     * @param clazz 表示待描述的类型的 {@link Class}。
     * @return 表示类型描述符的 {@link ClassDescriptor}。
     */
    public static ClassDescriptor of(Class<?> clazz) {
        if (clazz == void.class) {
            return Void.instance();
        } else if (clazz == byte.class) {
            return Primitive.BYTE;
        } else if (clazz == char.class) {
            return Primitive.CHAR;
        } else if (clazz == double.class) {
            return Primitive.DOUBLE;
        } else if (clazz == float.class) {
            return Primitive.FLOAT;
        } else if (clazz == int.class) {
            return Primitive.INT;
        } else if (clazz == long.class) {
            return Primitive.LONG;
        } else if (clazz == short.class) {
            return Primitive.SHORT;
        } else if (clazz == boolean.class) {
            return Primitive.BOOLEAN;
        } else if (clazz.isArray()) {
            return new Array(of(clazz.getComponentType()));
        } else {
            return new Reference(clazz.getName());
        }
    }

    /**
     * 解析字符串为类型描述符。
     *
     * @param text 表示待解析的字符串的 {@link String}。
     * @return 表示类型描述符的 {@link ClassDescriptor}。
     */
    public static ClassDescriptor parse(String text) {
        Parser parser = new Parser(text);
        List<ClassDescriptor> descriptors = parser.parseAll();
        if (descriptors.isEmpty()) {
            throw new IllegalStateException(StringUtils.format(
                    "The string to parse does not contain any field descriptor. [text={0}]",
                    text));
        } else if (descriptors.size() > 1) {
            throw new IllegalStateException(StringUtils.format(
                    "More than 1 field descriptor found in the string to parse. [text={0}]",
                    text));
        } else {
            return descriptors.get(0);
        }
    }
}
