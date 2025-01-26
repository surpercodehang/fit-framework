/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.support.ArrayIterator;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * 为数组提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class ArrayUtils {
    /** 表示空的对象数组。 */
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private ArrayUtils() {}

    /**
     * 在指定数组中使用二分查找检索指定元素所在的位置。
     * <p><b>注意：在调用该方法前，必须对 {@code array} 进行排序，如果没有进行排序，则结果是未定义的。如果 {@code array}
     * 中包含了多个指定元素，并不保证返回其中的某一个确定的元素。</b></p>
     * <p><b>该方法和 {@link Arrays#binarySearch(Object[], Object) JDK 默认二分搜索} 相比，对于 {@code null}
     * 的处理更加友好。</b></p>
     *
     * @param array 表示待查找的数组的 {@link E}{@code []}。
     * @param key 表示待查找的目标值的 {@link E}。
     * @param <E> 表示元素的类型的 {@link E}。
     * @return 若数组中存在指定元素，则返回该元素所在位置的索引的 {@code int}；否则为 {@code (-1 - (insertion point))}。
     * <p>{@code insertion point} 表示若将目标元素插入数组后其所在的索引：即 {@code array} 中第一个比目标元素大的元素的索引，如果
     * {@code array} 中的所有元素都比目标元素小，则返回 {@code array.length}。<b>注意：返回值大于或等于 {@code
     * 0}，当且仅当目标元素被找到。</b></p>
     * @throws IllegalArgumentException 当 {@code array} 为 {@code null} 时。
     * @see Arrays#binarySearch(Object[], Object)
     * @see #binarySearch(Object[], Comparable, Function, Comparator)
     */
    public static <E extends Comparable<E>> int binarySearch(E[] array, E key) {
        return binarySearch(array, key, Function.identity(), ObjectUtils::compare);
    }

    /**
     * 在指定数组中使用二分查找检索指定元素所在的位置。
     * <p><b>注意：在调用该方法前，必须对 {@code array} 进行排序，如果没有进行排序，则结果是未定义的。如果 {@code array}
     * 中包含了多个指定元素，并不保证返回其中的某一个确定的元素。</b></p>
     * <p><b>该方法和 {@link Arrays#binarySearch(Object[], Object) JDK 默认二分搜索} 相比，对于 {@code null}
     * 的处理更加友好。</b></p>
     *
     * @param array 表示待查找的数组的 {@link E}{@code []}。
     * @param key 表示待查找的目标值的 {@link K}。
     * @param mapper 表示将数组元素转换为待查找元素的转换方法的 {@link Function}{@code <}{@link E}{@code , }{@link K}{@code >}。
     * @param <E> 表示元素的类型的 {@link E}。
     * @param <K> 表示待查找的目标元素类型的 {@link K}。
     * @return 若数组中存在指定元素，则返回该元素所在位置的索引的 {@code int}；否则为 {@code (-1 - (insertion point))}。
     * <p>{@code insertion point}
     * 表示若将目标元素插入数组后其所在的索引：即 {@code array} 中第一个比目标元素大的元素的索引，如果 {@code array}
     * 中的所有元素都比目标元素小，则返回 {@code array.length}。<b>注意：返回值大于或等于 {@code 0}，当且仅当目标元素被找到。</b></p>
     * @throws IllegalArgumentException 当 {@code array} 或者 {@code mapper} 为 {@code null} 时。
     * @see Arrays#binarySearch(Object[], Object)
     * @see #binarySearch(Object[], Comparable, Function, Comparator)
     */
    public static <E, K extends Comparable<K>> int binarySearch(E[] array, K key, Function<E, K> mapper) {
        return binarySearch(array, key, mapper, ObjectUtils::compare);
    }

    /**
     * 在指定数组中使用二分查找检索指定元素所在的位置。
     * <p><b>注意：在调用该方法前，必须对 {@code array} 进行排序，如果没有进行排序，则结果是未定义的。如果 {@code array}
     * 中包含了多个指定元素，并不保证返回其中的某一个确定的元素。</b></p>
     * <p><b>该方法和 {@link Arrays#binarySearch(Object[], Object) JDK 默认二分搜索} 相比，对于 {@code null}
     * 的处理更加友好。</b></p>
     *
     * @param array 表示待查找的数组的 {@link E}{@code []}。
     * @param key 表示待查找的目标值的 {@link K}。
     * @param mapper 表示将数组元素转换为待查找元素的转换方法的 {@link Function}{@code <}{@link E}{@code , }{@link K}{@code >}。
     * @param comparator 表示对 {@link K} 进行比较的比较器的 {@link Comparator}{@code <}{@link K}{@code >}。
     * @param <E> 表示元素的类型的 {@link E}。
     * @param <K> 表示待查找的目标元素类型的 {@link K}。
     * @return 若数组中存在指定元素，则返回该元素所在位置的索引的 {@code int}；否则为 {@code (-1 - (insertion point))}。
     * <p>{@code insertion point} 表示若将目标元素插入数组后其所在的索引：即 {@code array} 中第一个比目标元素大的元素的索引，如果
     * {@code array} 中的所有元素都比目标元素小，则返回 {@code array.length}。<b>注意：返回值大于或等于 {@code
     * 0}，当且仅当目标元素被找到。</b></p>
     * @throws IllegalArgumentException 当 {@code array}，{@code mapper} 或者 {@code comparator} 为 {@code null} 时。
     * @see Arrays#binarySearch(Object[], Object)
     * @see Arrays#binarySearch(Object[], Object, Comparator)
     */
    public static <E, K extends Comparable<K>> int binarySearch(E[] array, K key, Function<E, K> mapper,
            Comparator<K> comparator) {
        Validation.notNull(array, "The array to binary search cannot be null.");
        Validation.notNull(mapper, "The mapper to map element cannot be null.");
        Validation.notNull(comparator, "The comparator to compare elements cannot be null.");
        if (array.length < 1) {
            return -1;
        }
        int compareResult = comparator.compare(mapper.apply(array[0]), key);
        if (compareResult > 0) {
            return -1;
        } else if (compareResult == 0) {
            return 0;
        } else {
            compareResult = comparator.compare(mapper.apply(array[array.length - 1]), key);
            if (compareResult < 0) {
                return -1 - array.length;
            } else if (compareResult == 0) {
                return array.length - 1;
            } else {
                return binarySearch0(array, key, mapper, comparator);
            }
        }
    }

    private static <E, K> int binarySearch0(E[] array, K key, Function<E, K> mapper, Comparator<K> comparator) {
        int left = 0;
        int right = array.length - 1;
        while (left <= right) {
            int mid = (left + right) >>> 1;
            int compareResult = comparator.compare(mapper.apply(array[mid]), key);
            if (compareResult < 0) {
                left = mid + 1;
            } else if (compareResult > 0) {
                right = mid - 1;
            } else {
                return mid;
            }
        }
        return -(left + 1);
    }

    /**
     * 将一个数组对象的 {@link Object} 转换成具体的数组对象的 {@link Object}{@code []}。
     *
     * @param array 表示待转换的数组对象的 {@link Object}。
     * @return 表示转换后的数组对象的 {@link Object}{@code []}。
     * @throws IllegalArgumentException 当 {@code array} 不是数组时。
     */
    public static Object[] concrete(Object array) {
        Validation.notNull(array, "Array cannot be null.");
        Object[] actualArray = new Object[Array.getLength(array)];
        for (int i = 0; i < actualArray.length; i++) {
            actualArray[i] = Array.get(array, i);
        }
        return actualArray;
    }

    /**
     * 将一个变长参数列表转换成一个数组。
     *
     * @param elements 表示变长参数列表的 {@link Object}{@code []}。
     * @return 表示转换后的数组的 {@link Object}{@code []}。
     */
    @Nonnull
    public static Object[] flat(Object... elements) {
        if (elements == null) {
            return new Object[0];
        }
        List<Object> list = new LinkedList<>();
        for (Object element : elements) {
            append(list, element);
        }
        return list.toArray();
    }

    private static void append(List<Object> list, Object element) {
        if (element == null) {
            list.add(null);
            return;
        }
        if (element instanceof Iterable) {
            for (Object obj : (Iterable<?>) element) {
                append(list, obj);
            }
        } else if (element instanceof Iterator) {
            Iterator<?> iterator = (Iterator<?>) element;
            while (iterator.hasNext()) {
                append(list, iterator.next());
            }
        } else if (element instanceof Enumeration) {
            Enumeration<?> enumeration = (Enumeration<?>) element;
            while (enumeration.hasMoreElements()) {
                append(list, enumeration.nextElement());
            }
        } else if (element.getClass().isArray()) {
            int length = Array.getLength(element);
            for (int i = 0; i < length; i++) {
                append(list, Array.get(element, i));
            }
        } else {
            list.add(element);
        }
    }

    /**
     * 检查指定的数组是否为 {@code null} 或是一个空数组。
     *
     * @param array 表示待检查的数组的 {@link T}{@code []}。
     * @param <T> 表示数组中包含元素类型的 {@link T}。
     * @return 若数组为 {@code null} 或是一个空数组，则为 {@code true}；否则为 {@code false}。
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length < 1;
    }

    /**
     * 检查指定的数组是否为 {@code null} 或是一个空数组。
     *
     * @param array 表示待检查的数组的 {@code byte[]}。
     * @return 若数组为 {@code null} 或是一个空数组，则为 {@code true}；否则为 {@code false}。
     */
    public static boolean isEmpty(byte[] array) {
        return array == null || array.length < 1;
    }

    /**
     * 检查指定数组是否不为 {@code null} 并且不是一个空数组。
     *
     * @param array 表示待检查的数组的 {@link T}{@code []}。
     * @param <T> 表示数组中包含元素类型的 {@link T}。
     * @return 若数组不为 {@code null} 并且不是一个空数组，则为 {@code true}；否则为 {@code false}。
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    /**
     * 生成一个迭代器，用以迭代数组中的所有元素。
     *
     * @param items 表示待生成迭代器的数组的 {@link T}{@code []}。
     * @param <T> 表示数组中元素类型的 {@link T}。
     * @return 表示用以迭代数组中所有元素的迭代器的 {@link Iterator}{@code <}{@link T}{@code >}。
     */
    @SafeVarargs
    public static <T> Iterator<T> iterator(T... items) {
        return new ArrayIterator<>(items);
    }

    /**
     * 返回一个字符串，用以表示指定数组的信息。
     *
     * @param array 表示待转为字符串表现形式的数组的 {@link T}{@code []}。
     * @param <T> 表示数组中元素类型的 {@link T}。
     * @return 表示数组信息的字符串的 {@link String}。
     */
    public static <T> String toString(T[] array) {
        return toString(array, null);
    }

    /**
     * 将数组对象转换为字符串表现形式。
     * <p>以方括号包裹的，以半角逗号和一个空格分隔的每个元素通过 {@code toStringMapper} 方法转换为字符串。</p>
     *
     * @param array 表示待转为字符串表现形式的数组的 {@link T}{@code []}。
     * @param toStringMapper 表示用以将数组中元素转为字符串的方法的 {@link Function}{@code <}{@link T}{@code ,
     * }{@link String}{@code >}。当 {@code toStringMapper} 为 {@code null} 时，默认使用 {@link
     * ObjectUtils#toString(Object)} 方法。
     * @param <T> 表示数组中元素类型的 {@link T}。
     * @return 表示数组信息的字符串的 {@link String}。
     */
    public static <T> String toString(T[] array, Function<T, String> toStringMapper) {
        if (array == null) {
            return StringUtils.EMPTY;
        }
        Function<T, String> actualToString = ObjectUtils.nullIf(toStringMapper, ObjectUtils::toString);
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        if (array.length > 0) {
            builder.append(actualToString.apply(array[0]));
            for (int i = 1; i < array.length; i++) {
                builder.append(", ").append(actualToString.apply(array[i]));
            }
        }
        builder.append(']');
        return builder.toString();
    }
}
