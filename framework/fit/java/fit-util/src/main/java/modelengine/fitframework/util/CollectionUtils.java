/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.merge.Conflict;
import modelengine.fitframework.merge.ConflictResolutionPolicy;
import modelengine.fitframework.merge.ConflictResolver;
import modelengine.fitframework.merge.ConflictResolverCollection;
import modelengine.fitframework.merge.list.ListAppendConflictResolver;
import modelengine.fitframework.merge.list.ListMerger;
import modelengine.fitframework.merge.list.support.DefaultListMerger;
import modelengine.fitframework.util.support.FilteredIterator;
import modelengine.fitframework.util.support.IteratorEnumerationAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 为集合提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class CollectionUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private CollectionUtils() {}

    /**
     * 在指定列表中使用二分查找检索指定元素所在的位置。
     * <p><b>注意：在调用该方法前，必须对 {@code list} 进行排序，如果没有进行排序，则结果是未定义的。如果 {@code list}
     * 中包含了多个指定元素，并不保证返回其中的某一个确定的元素。</b></p>
     * <p><b>该方法和 {@link Arrays#binarySearch(Object[], Object) JDK 默认二分搜索} 相比，对于 {@code null}
     * 的处理更加友好。</b></p>
     *
     * @param list 表示待查找的列表的 {@link List}{@code <}{@link E}{@code >}。
     * @param key 表示待查找的目标值的 {@link E}。
     * @param <E> 表示元素的类型的 {@link E}。
     * @return 若列表中存在指定元素，则返回该元素所在位置的索引的 {@code int}；否则为 {@code (-1 -
     * (insertion point))}。{@code insertion point} 表示若将目标元素插入列表后其所在的索引：即 {@code list}
     * 中第一个比目标元素大的元素的索引，如果 {@code list} 中的所有元素都比目标元素小，则返回 {@link
     * List#size()}。<b>注意：返回值大于或等于 {@code 0}，当且仅当目标元素被找到。</b>
     * @throws IllegalArgumentException 当 {@code list} 为 {@code null} 时。
     * @see Arrays#binarySearch(Object[], Object)
     * @see #binarySearch(List, Comparable, Function, Comparator)
     */
    public static <E extends Comparable<E>> int binarySearch(List<E> list, E key) {
        return binarySearch(list, key, Function.identity(), ObjectUtils::compare);
    }

    /**
     * 在指定列表中使用二分查找检索指定元素所在的位置。
     * <p><b>注意：在调用该方法前，必须对 {@code list} 进行排序，如果没有进行排序，则结果是未定义的。如果 {@code list}
     * 中包含了多个指定元素，并不保证返回其中的某一个确定的元素。</b></p>
     * <p><b>该方法和 {@link Arrays#binarySearch(Object[], Object) JDK 默认二分搜索} 相比，对于 {@code null}
     * 的处理更加友好。</b></p>
     *
     * @param list 表示待查找的列表的 {@link List}{@code <}{@link E}{@code >}。
     * @param key 表示待查找的目标值的 {@link K}。
     * @param mapper 表示将列表元素转换为待查找元素的转换方法的 {@link Function}{@code <}{@link E}{@code ,
     * }{@link K}{@code >}。
     * @param <E> 表示元素的类型的 {@link E}。
     * @param <K> 表示待查找的目标元素类型的 {@link K}。
     * @return 若列表中存在指定元素，则返回该元素所在位置的索引的 {@code int}；否则为 {@code (-1 -
     * (insertion point))}。{@code insertion point} 表示若将目标元素插入列表后其所在的索引：即 {@code list}
     * 中第一个比目标元素大的元素的索引，如果 {@code list} 中的所有元素都比目标元素小，则返回 {@link
     * List#size()}。<b>注意：返回值大于或等于 {@code 0}，当且仅当目标元素被找到。</b>
     * @throws IllegalArgumentException 当 {@code list} 或者 {@code mapper} 为 {@code null} 时。
     * @see Arrays#binarySearch(Object[], Object)
     * @see #binarySearch(List, Comparable, Function, Comparator)
     */
    public static <E, K extends Comparable<K>> int binarySearch(List<E> list, K key, Function<E, K> mapper) {
        return binarySearch(list, key, mapper, ObjectUtils::compare);
    }

    /**
     * 在指定列表中使用二分查找检索指定元素所在的位置。
     * <p><b>注意：在调用该方法前，必须对 {@code list} 进行排序，如果没有进行排序，则结果是未定义的。如果 {@code list}
     * 中包含了多个指定元素，并不保证返回其中的某一个确定的元素。</b></p>
     * <p><b>该方法和 {@link Arrays#binarySearch(Object[], Object) JDK 默认二分搜索} 相比，对于 {@code null}
     * 的处理更加友好。</b></p>
     *
     * @param list 表示待查找的列表的 {@link List}{@code <}{@link E}{@code >}。
     * @param key 表示待查找的目标值的 {@link K}。
     * @param mapper 表示将列表元素转换为待查找元素的转换方法的 {@link Function}{@code <}{@link E}{@code ,
     * }{@link K}{@code >}。
     * @param comparator 表示对 {@link K} 进行比较的比较器的 {@link Comparator}{@code <}{@link K}{@code >}。
     * @param <E> 表示元素的类型的 {@link E}。
     * @param <K> 表示待查找的目标元素类型的 {@link K}。
     * @return 若列表中存在指定元素，则返回该元素所在位置的索引的 {@code int}；否则为 {@code (-1 -
     * (insertion point))}。{@code insertion point} 表示若将目标元素插入列表后其所在的索引：即 {@code list}
     * 中第一个比目标元素大的元素的索引，如果 {@code list} 中的所有元素都比目标元素小，则返回 {@link
     * List#size()}。<b>注意：返回值大于或等于 {@code 0}，当且仅当目标元素被找到。</b>
     * @throws IllegalArgumentException 当 {@code list}，{@code mapper} 或者 {@code comparator} 为 {@code null} 时。
     * @see Arrays#binarySearch(Object[], Object)
     * @see Arrays#binarySearch(Object[], Object, Comparator)
     */
    public static <E, K extends Comparable<K>> int binarySearch(List<E> list, K key, Function<E, K> mapper,
            Comparator<K> comparator) {
        notNull(list, "The list to binary search cannot be null.");
        notNull(mapper, "The mapper to map element cannot be null.");
        notNull(comparator, "The comparator to compare elements cannot be null.");
        if (list.isEmpty()) {
            return -1;
        }
        int compareResult = comparator.compare(mapper.apply(list.get(0)), key);
        if (compareResult > 0) {
            return -1;
        } else if (compareResult == 0) {
            return 0;
        } else {
            compareResult = comparator.compare(mapper.apply(list.get(list.size() - 1)), key);
            if (compareResult < 0) {
                return -1 - list.size();
            } else if (compareResult == 0) {
                return list.size() - 1;
            } else {
                return binarySearch0(list, key, mapper, comparator);
            }
        }
    }

    private static <E, K> int binarySearch0(List<E> list, K key, Function<E, K> mapper, Comparator<K> comparator) {
        int left = 0;
        int right = list.size() - 1;
        while (left <= right) {
            int mid = (left + right) >>> 1;
            int compareResult = comparator.compare(mapper.apply(list.get(mid)), key);
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
     * 将指定映射中的所有键值对的键和值分别进行转换，以得到新的映射。
     *
     * @param map 表示待转换的映射的 {@link Map}{@code <}{@link K1}{@code , }{@link V1}{@code >}。
     * @param keyMapper 表示用以转换键的方法的 {@link Function}{@code <}{@link K1}{@code , }{@link K2}{@code >}。
     * @param valueMapper 表示用以转换值的方法的 {@link Function}{@code <}{@link V1}{@code , }{@link V2}{@code >}。
     * @param <K1> 表示待转换映射的键的类型的 {@link K1}。
     * @param <V1> 表示待转换映射的值的类型的 {@link V1}。
     * @param <K2> 表示转换后映射的键的类型的 {@link K2}。
     * @param <V2> 表示转换后映射的值的类型的 {@link V2}。
     * @return 表示转换后得到的新的映射的 {@link Map}{@code <}{@link K2}{@code , }{@link V2}{@code >}。
     * @throws IllegalArgumentException 当 {@code keyMapper} 或 {@code valueMapper} 为 {@code null} 时。
     * @throws IllegalStateException 当通过 {@code keyMapper} 产生了重复的键时。
     */
    public static <K1, V1, K2, V2> Map<K2, V2> cast(Map<K1, V1> map, Function<K1, K2> keyMapper,
            Function<V1, V2> valueMapper) {
        notNull(keyMapper, "The mapper to cast keys of map cannot be null.");
        notNull(valueMapper, "The mapper to cast values of map cannot be null.");
        if (map == null) {
            return null;
        }
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> keyMapper.apply(entry.getKey()),
                        entry -> valueMapper.apply(entry.getValue())));
    }

    /**
     * 将多个列表拼接成一个列表。
     *
     * @param lists 表示待拼接的列表的 {@link List}{@code <}{@link T}{@code >[]}。
     * @param <T> 表示列表中元素类型的 {@link T}。
     * @return 表示拼接后的列表的 {@link List}{@code <}{@link T}{@code >}。
     */
    @SafeVarargs
    public static <T> List<T> connect(List<T>... lists) {
        if (ArrayUtils.isEmpty(lists)) {
            return Collections.emptyList();
        }
        return Arrays.stream(lists).filter(Objects::nonNull).flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * 求两个集合的差集。
     * <p>定义集合 {@code C} 为集合 {@code A} 与集合 {@code B} 的差集，集合 {@code C} 中的元素满足存在于集合 {@code
     * A}，但不存在于集合 {@code B}。</p>
     *
     * @param include 表示待求差集的源集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param exclude 表示待与源集合计算差集的集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param <E> 表示集合中的元素类型的 {@link E}。
     * @return 表示两个集合的差集的 {@link Set}{@code <}{@link E}{@code >}。
     * @throws IllegalArgumentException 当 {@code include} 为 {@code null} 时。
     */
    public static <E> Set<E> difference(Collection<E> include, Collection<E> exclude) {
        notNull(include, "The collection to include cannot be null.");
        Set<E> set = new HashSet<>(include);
        if (exclude != null) {
            set.removeAll(exclude);
        }
        return set;
    }

    /**
     * 检查两个集合是否包含完全一致的数据。
     *
     * @param first 表示待比较的第一个集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param second 表示待比较的第二个集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param <E> 表示集合中元素类型的 {@link E}。
     * @return 若两个集合包含完全一致的数据，则为 {@code true}；否则为 {@code false}。
     */
    public static <E> boolean equals(Collection<? extends E> first, Collection<? extends E> second) {
        return equals(first, second, null);
    }

    /**
     * 通过指定的比较器，检查两个集合是否包含完全一致的数据。
     *
     * @param first 表示待比较的第一个集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param second 表示待比较的第二个集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param equalizer 表示用以比较两个集合中的元素的比较器的 {@link Equalizer}{@code <}{@link E}{@code >}。
     * @param <E> 表示集合中元素类型的 {@link E}。
     * @return 若两个集合包含完全一致的数据，则为 {@code true}；否则为 {@code false}。
     */
    public static <E> boolean equals(Collection<? extends E> first, Collection<? extends E> second,
            Equalizer<E> equalizer) {
        if (first == null) {
            return second == null;
        }
        if (second == null) {
            return false;
        }
        if (first.size() != second.size()) {
            return false;
        }
        return equals(first, (Iterable<? extends E>) second, equalizer);
    }

    /**
     * 检查两个迭代器是否包含完全一致的数据。
     *
     * @param first 表示待比较的第一个迭代器的 {@link Iterable}{@code <}{@link E}{@code >}。
     * @param second 表示待比较的第二个迭代器的 {@link Iterable}{@code <}{@link E}{@code >}。
     * @param <E> 表示迭代器中元素类型的 {@link E}。
     * @return 若两个迭代器包含完全一致的数据，则为 {@code true}；否则为 {@code false}。
     */
    public static <E> boolean equals(Iterable<? extends E> first, Iterable<? extends E> second) {
        return equals(first, second, null);
    }

    /**
     * 通过指定的比较器，检查两个迭代器是否包含完全一致的数据。
     *
     * @param first 表示待比较的第一个迭代器的 {@link Iterable}{@code <}{@link E}{@code >}。
     * @param second 表示待比较的第二个迭代器的 {@link Iterable}{@code <}{@link E}{@code >}。
     * @param equalizer 表示用以比较两个迭代器中的元素的比较器的 {@link Equalizer}{@code <}{@link E}{@code >}。
     * @param <E> 表示迭代器中元素类型的 {@link E}。
     * @return 若两个迭代器包含完全一致的数据，则为 {@code true}；否则为 {@code false}。
     */
    public static <E> boolean equals(Iterable<? extends E> first, Iterable<? extends E> second,
            Equalizer<E> equalizer) {
        if (first == null) {
            return second == null;
        }
        if (second == null) {
            return false;
        }
        Equalizer<E> actualEqualizer = ObjectUtils.nullIf(equalizer, Objects::equals);
        Iterator<? extends E> firstIterator = first.iterator();
        Iterator<? extends E> secondIterator = second.iterator();
        while (firstIterator.hasNext()) {
            if (!secondIterator.hasNext()) {
                return false;
            }
            if (!actualEqualizer.equals(firstIterator.next(), secondIterator.next())) {
                return false;
            }
        }
        return !secondIterator.hasNext();
    }

    /**
     * 通过指定的过滤条件来忽略集合中的元素。
     *
     * @param collection 表示待忽略元素的指定集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param ignorePredicate 表示指定的过滤条件的 {@link Predicate}{@code <}{@link E}{@code >}。
     * @param creator 表示忽略后集合的创建器的 {@link Supplier}{@code <}{@link C}{@code >}。
     * @param <C> 表示 {@link Collection}{@code <}{@link E}{@code >} 的子类的 {@link C}。
     * @param <E> 表示集合中元素类型的 {@link E}。
     * @return 表示过滤元素后的集合的 {@link C}。
     */
    public static <C extends Collection<E>, E> C ignoreElements(Collection<E> collection, Predicate<E> ignorePredicate,
            Supplier<C> creator) {
        Supplier<C> actualCreator = ObjectUtils.cast(ObjectUtils.getIfNull(creator, ArrayList::new));
        Predicate<E> actualIgnorePredicate = ObjectUtils.cast(ObjectUtils.getIfNull(ignorePredicate, () -> true));
        C result = actualCreator.get();
        if (CollectionUtils.isEmpty(collection)) {
            return result;
        }
        collection.stream().filter(actualIgnorePredicate.negate()).forEach(result::add);
        return result;
    }

    /**
     * 忽略集合中的 {@code null} 元素，并形成一个新的列表。
     *
     * @param collection 表示待忽略元素的指定集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param <E> 表示集合中元素类型的 {@link E}。
     * @return 表示过滤 {@code null} 元素后的列表的 {@link List}{@code <}{@link E}{@code >}。
     */
    public static <E> List<E> ignoreNullToList(Collection<E> collection) {
        return CollectionUtils.ignoreElements(collection, Objects::isNull, ArrayList::new);
    }

    /**
     * 求两个集合的交集。
     * <p>定义集合 {@code C} 为集合 {@code A} 与集合 {@code B} 的交集，集合 {@code C} 中的元素满足存在于集合 {@code
     * A}，同时存在于集合 {@code B}。</p>
     *
     * @param first 表示待求交集的源集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param second 表示待与源集合计算交集的集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param <E> 表示集合中的元素类型的 {@link E}。
     * @return 表示两个集合的交集的 {@link Set}{@code <}{@link E}{@code >}。
     * @throws IllegalArgumentException 当 {@code first} 为 {@code null} 时。
     */
    public static <E> Set<E> intersect(Collection<E> first, Collection<E> second) {
        notNull(first, "The collection to intersect cannot be null.");
        Set<E> set = new HashSet<>(first);
        if (second != null) {
            set.retainAll(second);
        }
        return set;
    }

    /**
     * 按照第一个集合的顺序求两个集合的交集。
     * <p>定义集合 {@code C} 为集合 {@code A} 与集合 {@code B} 的交集，集合 {@code C} 中的元素满足存在于集合 {@code
     * A}，同时存在于集合 {@code B}。</p>
     *
     * @param first 表示待求交集的源集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param second 表示待与源集合计算交集的集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param <E> 表示集合中的元素类型的 {@link E}。
     * @return 表示两个集合的交集的 {@link Set}{@code <}{@link E}{@code >}。
     * @throws IllegalArgumentException 当 {@code first} 为 {@code null} 时。
     */
    public static <E> Set<E> intersectOrdered(Collection<E> first, Collection<E> second) {
        notNull(first, "The collection to intersect cannot be null.");
        Set<E> set = new LinkedHashSet<>();
        for (E item : first) {
            if (second != null && second.contains(item)) {
                set.add(item);
            }
        }
        return set;
    }

    /**
     * 检查指定集合是否为 {@code null} 或是一个空集合。
     *
     * @param collection 表示待检查的集合的 {@link Collection}{@code <}{@link T}{@code >}。
     * @param <T> 表示集合中元素类型的 {@link T}。
     * @return 若集合为 {@code null} 或是一个空集合，则为 {@code true}；否则为 {@code false}。
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 检查指定集合是否不为 {@code null} 并且不是一个空集合。
     *
     * @param collection 表示待检查的集合的 {@link Collection}{@code <}{@link T}{@code >}。
     * @param <T> 表示集合中元素类型的 {@link T}。
     * @return 若集合不为 {@code null} 并且不是一个空集合，则为 {@code true}；否则为 {@code false}。
     */
    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    /**
     * 返回一个迭代器，用以遍历指定可迭代对象。
     *
     * @param iterable 表示待遍历的可迭代对象的 {@link Iterable}{@code <}{@link T}{@code >}。
     * @param <T> 表示可迭代对象中元素类型的 {@link T}。
     * @return 若 {@code iterable} 为 {@code null}，则为 {@code null}，否则为其迭代器的
     * {@link Iterator}{@code <}{@link T}{@code >}。
     */
    public static <T> Iterator<T> iterator(Iterable<T> iterable) {
        return ObjectUtils.mapIfNotNull(iterable, Iterable::iterator);
    }

    /**
     * 获取可迭代对象中第一个按指定映射方法转换后得到的非 {@code null} 对象。
     *
     * @param iterable 表示作为数据源的可迭代对象的 {@link Iterable}{@code <}{@link E}{@code >}。
     * @param mapper 表示用以将源数据转换为目标类型数据的映射方法的 {@link Function}{@code <}{@link E}{@code ,
     * }{@link R}{@code >}。
     * @param <E> 表示可迭代对象中元素类型的 {@link E}。
     * @param <R> 表示目标数据类型的 {@link R}。
     * @return 若可迭代对象中存在元素，为其第一个转换得到的目标对象的非 {@code null} 的值；否则为 {@code null}。
     * @throws IllegalArgumentException {@code mapper} 为 {@code null}。
     */
    public static <E, R> R mapFirst(Iterable<E> iterable, Function<E, R> mapper) {
        return mapFirst(iterable, mapper, null);
    }

    /**
     * 获取可迭代对象中第一个按指定映射方法转换后得到的非 {@code null} 对象。
     *
     * @param iterable 表示作为数据源的可迭代对象的 {@link Iterable}{@code <}{@link E}{@code >}。
     * @param mapper 表示用以将源数据转换为目标类型数据的映射方法的 {@link Function}{@code <}{@link E}{@code ,
     * }{@link R}{@code >}。
     * @param defaultValue 表示当未能获取到非 {@code null} 对象时使用的默认值的 {@link R}。
     * @param <E> 表示可迭代对象中元素类型的 {@link E}。
     * @param <R> 表示目标数据类型的 {@link R}。
     * @return 若可迭代对象中存在元素，为其第一个转换得到的目标对象的非 {@code null} 的值；否则为 {@code null}。
     * @throws IllegalArgumentException {@code mapper} 为 {@code null}。
     */
    public static <E, R> R mapFirst(Iterable<E> iterable, Function<E, R> mapper, R defaultValue) {
        notNull(mapper, "The mapper to map element to result cannot be null.");
        if (iterable != null) {
            for (E element : iterable) {
                R result = mapper.apply(element);
                if (result != null) {
                    return result;
                }
            }
        }
        return defaultValue;
    }

    /**
     * 将两个列表合并成一个新的列表。
     * <p>冲突处理器为 {@link ListAppendConflictResolver}。</p>
     *
     * @param first 表示待合并的第一个列表的 {@link List}{@code <}{@link E}{@code >}。
     * @param second 表示待合并的第二个列表的 {@link List}{@code <}{@link E}{@code >}。
     * @param <E> 表示列表中元素类型的 {@link E}。
     * @return 表示合并后的列表的 {@link List}{@code <}{@link E}{@code >}。
     */
    public static <E> List<E> merge(List<E> first, List<E> second) {
        ConflictResolverCollection conflictResolvers = ConflictResolverCollection.create();
        conflictResolvers.add(ObjectUtils.cast(new ListAppendConflictResolver<>()));
        return merge(first, second, conflictResolvers);
    }

    /**
     * 将两个列表按照指定的冲突解决策略合并成一个新的列表。
     *
     * @param first 表示待合并的第一个列表的 {@link List}{@code <}{@link E}{@code >}。
     * @param second 表示待合并的第二个列表的 {@link List}{@code <}{@link E}{@code >}。
     * @param defaultPolicy 表示指定的冲突解决策略的 {@link ConflictResolutionPolicy}。
     * @param <E> 表示列表中元素类型的 {@link E}。
     * @return 表示合并后的列表的 {@link List}{@code <}{@link E}{@code >}。
     * @throws modelengine.fitframework.merge.ConflictException 当合并过程中发生异常时。
     */
    public static <E> List<E> merge(List<E> first, List<E> second, ConflictResolutionPolicy defaultPolicy) {
        ConflictResolver<Object, List<E>, Conflict<Object>> defaultResolver = ConflictResolver.resolver(defaultPolicy);
        ConflictResolverCollection conflictResolvers = ConflictResolverCollection.create();
        conflictResolvers.add(ObjectUtils.cast(defaultResolver));
        return merge(first, second, conflictResolvers);
    }

    /**
     * 将两个列表按照指定的冲突处理器的集合合并成一个新的列表。
     *
     * @param first 表示待合并的第一个列表的 {@link List}{@code <}{@link E}{@code >}。
     * @param second 表示待合并的第二个列表的 {@link List}{@code <}{@link E}{@code >}。
     * @param conflictResolvers 表示指定的冲突处理器集合的 {@link ConflictResolverCollection}。
     * @param <E> 表示列表中元素类型的 {@link E}。
     * @return 表示合并后的列表的 {@link List}{@code <}{@link E}{@code >}。
     * @throws modelengine.fitframework.merge.ConflictException 当合并过程中发生异常时。
     */
    public static <E> List<E> merge(List<E> first, List<E> second, ConflictResolverCollection conflictResolvers) {
        ListMerger<E> listMerger = new DefaultListMerger<>(conflictResolvers);
        return listMerger.merge(first, second);
    }

    /**
     * 返回一个数组，包含集合中的所有元素。
     * <p>数组中元素的排序方式为 {@code collection} 中元素的排序方式。</p>
     *
     * @param collection 表示源集合的 {@link Collection}{@code <}{@link T}{@code >}。
     * @param clazz 表示集合中元素类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param <T> 表示集合中元素类型的 {@link T}。
     * @return 表示包含集合中所有元素的数组。
     * @throws IllegalArgumentException 当 {@code collection} 或 {@code clazz} 为 {@code null} 时。
     */
    public static <T> T[] toArray(Collection<T> collection, Class<T> clazz) {
        notNull(collection, "The collection to convert to array cannot be null.");
        notNull(clazz, "Class of list elements cannot be null.");
        T[] array = ObjectUtils.cast(Array.newInstance(clazz, 0));
        return collection.toArray(array);
    }

    /**
     * 将指定集合按照指定的键获取方法，转换成一个映射。
     *
     * @param collection 表示待转为映射的集合的 {@link Collection}{@code <}{@link V}{@code >}。
     * @param keyMapper 表示用以从元素中获取键的方法的 {@link Function}{@code <}{@link V}{@code , }{@link K}{@code >}。
     * @param <K> 表示键的类型的 {@link K}。
     * @param <V> 表示值的类型的 {@link V}。
     * @return 表示从集合转为的映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @throws IllegalArgumentException 当 {@code collection} 或 {@code keyMapper} 为 {@code null} 时。
     * @throws IllegalStateException 当发现了重复的键时。
     */
    public static <K, V> Map<K, V> toMap(Collection<V> collection, Function<V, K> keyMapper) {
        notNull(collection, "The collection to convert to map cannot be null.");
        notNull(keyMapper, "The mapper to generate map keys cannot be null.");
        return collection.stream().collect(Collectors.toMap(keyMapper, item -> item));
    }

    /**
     * 将指定集合按照指定的键获取方法，转换成一个映射。当发生键冲突时，抛出指定运行时异常。
     *
     * @param collection 表示待转为映射的集合的 {@link Collection}{@code <}{@link V}{@code >}。
     * @param keyMapper 表示用以从元素中获取键的方法的 {@link Function}{@code <}{@link V}{@code , }{@link K}{@code >}。
     * @param exceptionSupplier 表示键冲突时抛出异常的方法的 {@link BiFunction}{@code <}{@link V}{@code , }{@link
     * V}{@code , }{@link E}{@code >}。
     * @param <K> 表示键的类型的 {@link K}。
     * @param <V> 表示值的类型的 {@link V}。
     * @param <E> 表示运行时异常类型的 {@link E}。
     * @return 表示从集合转为的映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @throws IllegalArgumentException 当 {@code collection}，{@code keyMapper} 或 {@code exceptionSupplier} 为 {@code
     * null} 时。
     * @throws E 当发现了重复的键时。
     */
    public static <K, V, E extends RuntimeException> Map<K, V> toMap(Collection<V> collection, Function<V, K> keyMapper,
            BiFunction<V, V, E> exceptionSupplier) {
        notNull(collection, "The collection to convert to map cannot be null.");
        notNull(keyMapper, "The mapper to generate map keys cannot be null.");
        notNull(exceptionSupplier, "The supplier to handle duplicated keys cannot be null.");
        return collection.stream().collect(Collectors.toMap(keyMapper, item -> item, (v1, v2) -> {
            throw exceptionSupplier.apply(v1, v2);
        }));
    }

    /**
     * 将可迭代对象转换为字符串表现形式。
     * <p>以方括号包裹的，以半角逗号和一个空格分隔的每个元素通过 {@link ObjectUtils#toString(Object)}
     * 方法转换为字符串。</p>
     *
     * @param iterable 表示待转换为字符串表现形式的可迭代对象的 {@link Iterable}{@code <}{@link T}{@code >}。
     * @param <T> 表示可迭代对象中元素类型的 {@link T}。
     * @return 若可迭代对象为 {@code null}，则为空字符串；否则为可迭代对象的字符串表现形式的 {@link String}。
     */
    public static <T> String toString(Iterable<T> iterable) {
        if (iterable == null) {
            return StringUtils.EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        Iterator<T> iterator = iterable.iterator();
        if (iterator.hasNext()) {
            builder.append(ObjectUtils.toString(iterator.next()));
            while (iterator.hasNext()) {
                builder.append(", ").append(ObjectUtils.toString(iterator.next()));
            }
        }
        builder.append(']');
        return builder.toString();
    }

    /**
     * 求两个集合的并集。
     * <p>定义集合 {@code C} 为集合 {@code A} 与集合 {@code B} 的并集，集合 {@code C} 中的元素满足存在于集合 {@code
     * A}，或者存在于集合 {@code B}。</p>
     *
     * @param first 表示待求并集的源集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param second 表示待与源集合计算并集的集合的 {@link Collection}{@code <}{@link E}{@code >}。
     * @param <E> 表示集合中的元素类型的 {@link E}。
     * @return 表示两个集合的并集的 {@link Set}{@code <}{@link E}{@code >}。
     * @throws IllegalArgumentException 当 {@code first} 为 {@code null} 时。
     */
    public static <E> Set<E> union(Collection<E> first, Collection<E> second) {
        notNull(first, "The collection to union cannot be null.");
        Set<E> set = new HashSet<>(first);
        if (second != null) {
            set.addAll(second);
        }
        return set;
    }

    /**
     * 将迭代器转换为枚举。
     *
     * @param iterator 表示迭代器的 {@link Iterator}{@code <}{@link E}{@code >}。
     * @param <E> 表示迭代器中元素类型的 {@link E}。
     * @param <T> 表示迭代器中元素类型的上界类型的 {@link T}。
     * @return 表示枚举的 {@link Enumeration}{@code <}{@link E}{@code >}。
     */
    public static <E, T extends E> Enumeration<E> enumeration(Iterator<T> iterator) {
        return new IteratorEnumerationAdapter<>(iterator);
    }

    /**
     * 为指定的迭代程序应用一个过滤程序。
     *
     * @param origin 表示原始的迭代程序的 {@link Iterator}。
     * @param filter 表示应用的元素的过滤程序的 {@link Predicate}。
     * @param <E> 表示迭代器中元素的类型。
     * @return 表示应用了过滤程序后的迭代程序的 {@link Iterator}。
     */
    public static <E> Iterator<E> filtered(Iterator<E> origin, Predicate<E> filter) {
        return new FilteredIterator<>(origin, filter);
    }

    /**
     * 若集合中存在元素，则返回第一个元素，否则返回 {@code null}。
     *
     * @param values 表示待处理的集合的 {@link Iterable}。
     * @param <E> 表示集合中元素的类型。
     * @return 若集合中存在元素，则为表示第一个元素的 {@link Object}，否则为 {@code null}。
     */
    public static <E> E firstOrDefault(Iterable<E> values) {
        if (values == null) {
            return null;
        }
        Iterator<E> iterator = values.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    /**
     * 将指定的列表转换为其父类类型的列表。
     *
     * @param origin 表示待转换的子类型列表的 {@link List}{@code <}{@link T}{@code >}。
     * @param <E> 表示列表中元素的父类类型的 {@link E}。
     * @param <T> 表示列表中元素的子类类型的 {@link T}。
     * @return 表示转换后的父类类型列表的 {@link List}{@code <}{@link E}{@code >}。
     */
    public static <E, T extends E> List<E> asParent(List<T> origin) {
        if (origin == null) {
            return null;
        }
        return origin.stream().map(ObjectUtils::<T>cast).collect(Collectors.toList());
    }
}
