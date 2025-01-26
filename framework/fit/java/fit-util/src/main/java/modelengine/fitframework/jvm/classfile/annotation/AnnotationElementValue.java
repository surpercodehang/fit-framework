/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.annotation;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.exception.MethodNotFoundException;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.jvm.classfile.AttributeInfo;
import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.jvm.classfile.constant.ClassInfo;
import modelengine.fitframework.jvm.classfile.constant.DoubleInfo;
import modelengine.fitframework.jvm.classfile.constant.FloatInfo;
import modelengine.fitframework.jvm.classfile.constant.IntegerInfo;
import modelengine.fitframework.jvm.classfile.constant.LongInfo;
import modelengine.fitframework.jvm.classfile.constant.Utf8Info;
import modelengine.fitframework.jvm.classfile.lang.U1;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为注解中的元素提供值。
 *
 * @author 梁济时
 * @since 2022-10-29
 */
public abstract class AnnotationElementValue {
    private static volatile Map<U1, ElementValueReader> readers = null;
    private static final Object MONITOR = LockUtils.newSynchronizedLock();

    private final AnnotationElementValuePair pair;
    private final U1 tag;

    /**
     * 使用属性值的标签初始化 {@link AnnotationElementValue} 类的新实例。
     *
     * @param pair 表示元素值所属的元素键值对的 {@link AnnotationElementValuePair}。
     * @param tag 表示元素值的标签的 {@link U1}。
     * @throws IllegalArgumentException {@code pair} 或 {@code tag} 为 {@code null}。
     */
    private AnnotationElementValue(AnnotationElementValuePair pair, U1 tag) {
        this.pair = notNull(pair, "The owning pair of an annotation element value cannot be null.");
        this.tag = notNull(tag, "The tag of an annotation element value cannot be null.");
    }

    /**
     * 获取所属元素属性的键值对。
     *
     * @return 表示所属键值对的 {@link AnnotationElementValuePair}。
     */
    public final AnnotationElementValuePair pair() {
        return this.pair;
    }

    /**
     * 获取键值对所属的注解。
     *
     * @return 表示所属注解的 {@link AnnotationInfo}。
     */
    public AnnotationInfo annotation() {
        return this.pair().annotation();
    }

    /**
     * 获取键值对所属的属性。
     *
     * @return 表示所属属性的 {@link AttributeInfo}。
     */
    public AttributeInfo attribute() {
        return this.pair().attribute();
    }

    /**
     * 获取键值对所属的类文件。
     *
     * @return 表示所属类文件的 {@link ClassFile}。
     */
    public ClassFile file() {
        return this.pair().file();
    }

    /**
     * 获取注解元素值的标签。
     *
     * @return 表示标签的 {@link U1}。
     */
    public final U1 tag() {
        return this.tag;
    }

    /**
     * 根据所属元素属性的键值对，从指定的输入流中读取注解元素的值。
     *
     * @param pair 表示所属元素属性的键值对的 {@link AnnotationElementValuePair}。
     * @param in 表示指定的输入流的 {@link InputStream}。
     * @return 表示读取到的注解元素值的 {@link AnnotationElementValue}。
     * @throws IOException 当读取过程中发生输入输出异常时。
     */
    public static AnnotationElementValue read(AnnotationElementValuePair pair, InputStream in) throws IOException {
        notNull(pair, "The owning pair of annotation element value cannot be null.");
        U1 readTag = U1.read(validate(in));
        ElementValueReader reader = readerOfTag(readTag);
        return reader.read(pair, in);
    }

    private static InputStream validate(InputStream in) {
        return notNull(in, "The input stream that contains annotation element value cannot be null.");
    }

    private static ElementValueReader readerOfTag(U1 tag) {
        if (readers == null) {
            synchronized (MONITOR) {
                if (readers == null) {
                    readers = loadReaders();
                }
            }
        }
        ElementValueReader reader = readers.get(tag);
        if (reader == null) {
            throw new IllegalStateException(StringUtils.format("Unknown tag of annotation element value. [tag={0}]",
                    tag));
        } else {
            return reader;
        }
    }

    private static Map<U1, ElementValueReader> loadReaders() {
        Map<U1, ElementValueReader> cache = new HashMap<>();
        Class<?>[] innerClasses = AnnotationElementValue.class.getDeclaredClasses();
        for (Class<?> innerClass : innerClasses) {
            if (innerClass.isInterface() || innerClass.getSuperclass() != AnnotationElementValue.class
                    || isNotPublicStaticFinal(innerClass.getModifiers())) {
                continue;
            }
            U1 classTag = tagOfClass(innerClass);
            if (classTag == null) {
                continue;
            }
            Constructor<?> constructor;
            try {
                constructor = ReflectionUtils.getDeclaredConstructor(innerClass,
                        AnnotationElementValuePair.class,
                        InputStream.class);
            } catch (MethodNotFoundException ex) {
                throw new IllegalStateException(StringUtils.format(
                        "No matched constructor found in concrete class of AnnotationElementValue. [class={0}]",
                        ex.getClass()), ex);
            }
            ElementValueReader reader = createElementValueReader(constructor);
            cache.put(classTag, reader);
        }
        return cache;
    }

    private static ElementValueReader createElementValueReader(Constructor<?> constructor) {
        List<Class<?>> unexpectedCheckedExceptionTypes = Stream.of(constructor.getExceptionTypes())
                .filter(ReflectionUtils::isCheckedException)
                .filter(exceptionType -> !IOException.class.isAssignableFrom(exceptionType))
                .collect(Collectors.toList());
        if (!unexpectedCheckedExceptionTypes.isEmpty()) {
            // AnnotationElementValue 的具体实现类的构造方法，不允许抛出除 IOException 之外的其他受检异常。
            String exceptionTypes = unexpectedCheckedExceptionTypes.stream()
                    .map(Class::getName)
                    .collect(Collectors.joining(", ", "[", "]"));
            throw new IllegalStateException(StringUtils.format("The constructor does not allow throwing checked "
                            + "exceptions outside of IOException. [class={0}, exceptions={1}]",
                    constructor.getDeclaringClass().getName(),
                    exceptionTypes));
        }
        constructor.setAccessible(true);
        return (pair, in) -> {
            Object obj;
            try {
                obj = constructor.newInstance(pair, in);
            } catch (InstantiationException ignored) {
                // 当 constructor 所属的类型（innerClass）为 interface 或 abstract 时抛出的异常。
                // 在前置流程中已经避免了该场景，因此该异常不会发生。
                throw new Error();
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(StringUtils.format(
                        "Failed to access constructor of AnnotationElementValue. [class={0}]",
                        constructor.getDeclaringClass().getName()), e);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof IOException) {
                    throw (IOException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    throw (RuntimeException) cause;
                }
            }
            return ObjectUtils.cast(obj);
        };
    }

    private static U1 tagOfClass(Class<?> clazz) {
        Field field;
        try {
            field = clazz.getDeclaredField("TAG");
        } catch (NoSuchFieldException ignored) {
            return null;
        }
        if (isNotPublicStaticFinal(field.getModifiers()) || field.getType() != U1.class) {
            return null;
        }
        return ObjectUtils.cast(ReflectionUtils.getField(null, field));
    }

    private static boolean isNotPublicStaticFinal(int modifiers) {
        return !Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers);
    }

    /**
     * 为注解的元素值提供读取程序。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    @FunctionalInterface
    private interface ElementValueReader {
        /**
         * 读取注解的属性值。
         *
         * @param pair 表示包含属性值信息的键值对的 {@link AnnotationElementValue}。
         * @param in 表示包含属性值信息的输入流的 {@link InputStream}。
         * @return 表示读取到的注解的属性值信息的 {@link AnnotationElementValue}。
         * @throws IOException 读取过程发生输入输出异常。
         */
        AnnotationElementValue read(AnnotationElementValuePair pair, InputStream in) throws IOException;
    }

    /**
     * 表示字节类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class ByteValue extends AnnotationElementValue {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) 'B');

        private final U2 constValueIndex;

        private ByteValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.constValueIndex = U2.read(validate(in));
        }

        /**
         * 获取值在常量池中的索引。常量池中该索引处的元素必然是一个 {@link IntegerInfo}。
         *
         * @return 表示值在常量池中索引的 {@link U2}。
         */
        public U2 constValueIndex() {
            return this.constValueIndex;
        }
    }

    /**
     * 表示字符类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class CharValue extends AnnotationElementValue {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) 'C');

        private final U2 constValueIndex;

        private CharValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.constValueIndex = U2.read(validate(in));
        }

        /**
         * 获取值在常量池中的索引。常量池中该索引处的元素必然是一个 {@link IntegerInfo}。
         *
         * @return 表示值在常量池中索引的 {@link U2}。
         */
        public U2 constValueIndex() {
            return this.constValueIndex;
        }
    }

    /**
     * 表示双精度浮点数类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class DoubleValue extends AnnotationElementValue {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) 'D');

        private final U2 constValueIndex;

        private DoubleValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.constValueIndex = U2.read(validate(in));
        }

        /**
         * 获取值在常量池中的索引。常量池中该索引处的元素必然是一个 {@link DoubleInfo}。
         *
         * @return 表示值在常量池中索引的 {@link U2}。
         */
        public U2 constValueIndex() {
            return this.constValueIndex;
        }
    }

    /**
     * 表示单精度浮点数类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class FloatValue extends AnnotationElementValue {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) 'F');

        private final U2 constValueIndex;

        private FloatValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.constValueIndex = U2.read(validate(in));
        }

        /**
         * 获取值在常量池中的索引。常量池中该索引处的元素必然是一个 {@link FloatInfo}。
         *
         * @return 表示值在常量池中索引的 {@link U2}。
         */
        public U2 constValueIndex() {
            return this.constValueIndex;
        }
    }

    /**
     * 表示 32 位整数类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class IntegerValue extends AnnotationElementValue {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) 'I');

        private final U2 constValueIndex;

        private IntegerValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.constValueIndex = U2.read(validate(in));
        }

        /**
         * 获取值在常量池中的索引。常量池中该索引处的元素必然是一个 {@link IntegerInfo}。
         *
         * @return 表示值在常量池中索引的 {@link U2}。
         */
        public U2 constValueIndex() {
            return this.constValueIndex;
        }
    }

    /**
     * 表示 64 位整数类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class LongValue extends AnnotationElementValue {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) 'J');

        private final U2 constValueIndex;

        private LongValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.constValueIndex = U2.read(validate(in));
        }

        /**
         * 获取值在常量池中的索引。常量池中该索引处的元素必然是一个 {@link LongInfo}。
         *
         * @return 表示值在常量池中索引的 {@link U2}。
         */
        public U2 constValueIndex() {
            return this.constValueIndex;
        }
    }

    /**
     * 表示 16 位整数类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class ShortValue extends AnnotationElementValue {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) 'S');

        private final U2 constValueIndex;

        private ShortValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.constValueIndex = U2.read(validate(in));
        }

        /**
         * 获取值在常量池中的索引。常量池中该索引处的元素必然是一个 {@link IntegerInfo}。
         *
         * @return 表示值在常量池中索引的 {@link U2}。
         */
        public U2 constValueIndex() {
            return this.constValueIndex;
        }
    }

    /**
     * 表示布尔类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class BooleanValue extends AnnotationElementValue {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) 'Z');

        private final U2 constValueIndex;

        private BooleanValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.constValueIndex = U2.read(validate(in));
        }

        /**
         * 获取值在常量池中的索引。常量池中该索引处的元素必然是一个 {@link IntegerInfo}。
         *
         * @return 表示值在常量池中索引的 {@link U2}。
         */
        public U2 constValueIndex() {
            return this.constValueIndex;
        }
    }

    /**
     * 表示字符串类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class StringValue extends AnnotationElementValue {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) 's');

        private final U2 constValueIndex;

        private StringValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.constValueIndex = U2.read(validate(in));
        }

        /**
         * 获取值在常量池中的索引。常量池中该索引处的元素必然是一个 {@link Utf8Info}。
         *
         * @return 表示值在常量池中索引的 {@link U2}。
         */
        public U2 constValueIndex() {
            return this.constValueIndex;
        }
    }

    /**
     * 表示枚举类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class EnumValue extends AnnotationElementValue {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) 'e');

        private final U2 typeNameIndex;
        private final U2 constNameIndex;

        private EnumValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.typeNameIndex = U2.read(in);
            this.constNameIndex = U2.read(in);
        }

        /**
         * 获取枚举类型在常量池中的索引。常量池中该索引处的元素必然是一个 {@link Utf8Info}。
         *
         * @return 表示类型名称在常量池中的索引的 {@link U2}。
         */
        public U2 typeNameIndex() {
            return this.typeNameIndex;
        }

        /**
         * 获取枚举值在枚举定义中的名称在常量池中的索引。常量池中该索引处的元素必然是一个 {@link Utf8Info}。
         *
         * @return 表示枚举值的名称在常量池中索引的 {@link U2}。
         */
        public U2 constNameIndex() {
            return this.constNameIndex;
        }
    }

    /**
     * 表示 Java 类类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class ClassValue extends AnnotationElementValue {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) 'c');

        private final U2 classInfoIndex;

        private ClassValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.classInfoIndex = U2.read(in);
        }

        /**
         * 获取类型在常量池中的索引。常量池中该索引处的元素必然是一个 {@link ClassInfo}。
         *
         * @return 表示类型在常量池中索引的 {@link U2}。
         */
        public U2 classInfoIndex() {
            return this.classInfoIndex;
        }
    }

    /**
     * 表示注解类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class AnnotationValue extends AnnotationElementValue {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) '@');

        private final AnnotationInfo annotationValue;

        private AnnotationValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.annotationValue = new AnnotationInfo(pair.attribute(), in);
        }

        /**
         * 获取注解类型的值。
         *
         * @return 表示注解值的 {@link AnnotationInfo}。
         */
        public AnnotationInfo annotationValue() {
            return this.annotationValue;
        }
    }

    /**
     * 表示数组类型的值。
     *
     * @author 梁济时
     * @since 2022-10-29
     */
    public static final class ArrayValue extends AnnotationElementValue implements Iterable<AnnotationElementValue> {
        /**
         * 表示元素值的类型标签。
         */
        public static final U1 TAG = U1.of((byte) '[');

        private final U2 count;
        private final List<AnnotationElementValue> values;

        private ArrayValue(AnnotationElementValuePair pair, InputStream in) throws IOException {
            super(pair, TAG);
            this.count = U2.read(in);
            this.values = new ArrayList<>(this.count.intValue());
            for (U2 i = U2.ZERO; i.compareTo(this.count) < 0; i = i.add(U2.ONE)) {
                this.values.add(read(pair, in));
            }
        }

        /**
         * 获取包含元素值的数量。
         *
         * @return 表示包含元素值数量的 {@link U2}。
         */
        public U2 count() {
            return this.count;
        }

        /**
         * 获取指定索引处的元素值。
         *
         * @param index 表示元素值的索引的 {@link U2}。
         * @return 表示该索引处的元素值的 {@link AnnotationElementValue}。
         * @throws IllegalArgumentException {@code index} 为 {@code null}。
         * @throws IndexOutOfBoundsException {@code index} 超出索引限制。
         */
        public AnnotationElementValue get(U2 index) {
            notNull(index, "The index of value in annotation cannot be null.");
            return this.values.get(index.intValue());
        }

        @Nonnull
        @Override
        public Iterator<AnnotationElementValue> iterator() {
            return this.values.iterator();
        }
    }
}
