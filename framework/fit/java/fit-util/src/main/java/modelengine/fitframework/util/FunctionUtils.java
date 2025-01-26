/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import modelengine.fitframework.inspection.Validation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 为 {@code java.util.function} 包下的类提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-08-29
 */
public final class FunctionUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private FunctionUtils() {}

    /**
     * 返回一个谓词，用以表示不论待判定的内容是什么，都返回 {@code true} 的实现。
     *
     * @param <T> 表示待判定的内容的类型。
     * @return 表示总是返回 {@code true} 的谓词的 {@link Predicate}。
     */
    public static <T> Predicate<T> alwaysTrue() {
        return obj -> true;
    }

    /**
     * 返回一个谓词，用以表示不论待判定的内容是什么，都返回 {@code false} 的实现。
     *
     * @param <T> 表示待判定的内容的类型。
     * @return 表示总是返回 {@code false} 的谓词的 {@link Predicate}。
     */
    public static <T> Predicate<T> alwaysFalse() {
        return obj -> false;
    }

    /**
     * 安全的消费值。
     *
     * @param consumer 表示待消费的函数的 {@link Consumer}{@code <}{@link T}{@code >}。
     * @param value 表示待消费的值的 {@link T}。
     * @param <T> 表示待消费值的类型的 {@link T}。
     */
    public static <T> void accept(Consumer<T> consumer, T value) {
        if (consumer != null) {
            consumer.accept(value);
        }
    }

    /**
     * 使用逻辑与关系连接两个二元校验器。
     * <p>若两个二元校验器中有一个为 {@code null}，则返回另一个；否则返回合并后的二元校验器。</p>
     *
     * @param first 表示待连接的第一个二元校验器的 {@link BiPredicate}{@code <}{@link T}{@code , }{@link R}{@code >}。
     * @param second 表示待连接的第二个二元校验器的 {@link BiPredicate}{@code <}{@link T}{@code , }{@link R}{@code >}。
     * @param <T> 表示二元校验器所判断第一个数据的类型的 {@link T}。
     * @param <R> 表示二元校验器所判断第二个数据的类型的 {@link R}。
     * @return 表示连接后的二元校验器的 {@link BiPredicate}{@code <}{@link T}{@code , }{@link R}{@code >}。
     */
    public static <T, R> BiPredicate<T, R> and(BiPredicate<T, R> first, BiPredicate<T, R> second) {
        return connect(first, second, BiPredicate::and);
    }

    /**
     * 使用逻辑与关系连接一组二元校验器。
     *
     * @param predicates 表示待连接的二元校验器数组的 {@link BiPredicate}{@code <}{@link T}{@code , }{@link R}{@code >[]}。
     * @param defaultValue 表示当连接后的二元校验器为 {@code null} 时的默认校验返回值的 {@code boolean}。
     * @param <T> 表示二元校验器所判断第一个数据的类型的 {@link T}。
     * @param <R> 表示二元校验器所判断第二个数据的类型的 {@link R}。
     * @return 表示连接后的二元校验器的 {@link BiPredicate}{@code <}{@link T}{@code , }{@link R}{@code >}。
     */
    public static <T, R> BiPredicate<T, R> and(BiPredicate<T, R>[] predicates, boolean defaultValue) {
        if (predicates == null) {
            return (item1, item2) -> defaultValue;
        }
        BiPredicate<T, R> composite = null;
        for (BiPredicate<T, R> predicate : predicates) {
            composite = and(composite, predicate);
        }
        return composite == null ? (item1, item2) -> defaultValue : composite;
    }

    /**
     * 使用逻辑与关系连接两个校验器。
     * <p>若两个校验器中有一个为 {@code null}，则返回另一个；否则返回合并后的校验器。</p>
     *
     * @param first 表示待连接的第一个校验器的 {@link Predicate}{@code <}{@link T}{@code >}。
     * @param second 表示待连接的第二个校验器的 {@link Predicate}{@code <}{@link T}{@code >}。
     * @param <T> 表示校验器所判断数据的类型的 {@link T}。
     * @return 表示连接后的校验器的 {@link Predicate}{@code <}{@link T}{@code >}。
     */
    public static <T> Predicate<T> and(Predicate<T> first, Predicate<T> second) {
        return connect(first, second, Predicate::and);
    }

    /**
     * 使用逻辑与关系连接一组校验器。
     *
     * @param predicates 表示待连接的校验器数组的 {@link Predicate}{@code <}{@link T}{@code >[]}。
     * @param defaultValue 表示当连接后的校验器为 {@code null} 时的默认校验返回值的 {@code boolean}。
     * @param <T> 表示校验器所判断数据的类型的 {@link T}。
     * @return 表示连接后的校验器的 {@link Predicate}{@code <}{@link T}{@code >}。
     */
    public static <T> Predicate<T> and(Predicate<T>[] predicates, boolean defaultValue) {
        if (predicates == null) {
            return item -> defaultValue;
        }
        Predicate<T> composite = null;
        for (Predicate<T> predicate : predicates) {
            composite = and(composite, predicate);
        }
        return composite == null ? item -> defaultValue : composite;
    }

    /**
     * 通过 {@link Consumer#andThen(Consumer)} 连接两个消费者。
     * <p>若两个消费者中有一个为 {@code null}，则返回另一个；否则返回合并后的消费者。</p>
     *
     * @param first 表示待连接的第一个消费者的 {@link Consumer}{@code <}{@link T}{@code >}。
     * @param second 表示待连接的第二个消费者的 {@link Consumer}{@code <}{@link T}{@code >}。
     * @param <T> 表示消费对象的类型的 {@link T}。
     * @return 表示连接后的消费者的 {@link Consumer}{@code <}{@link T}{@code >}。
     */
    public static <T> Consumer<T> connect(Consumer<T> first, Consumer<T> second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return first.andThen(second);
        }
    }

    /**
     * 通过 {@link Function#andThen(Function)} 连接指定函数数组。
     * <p>前一个函数的输出为后一个函数的输入。</p>
     *
     * @param functions 表示待拼接的函数列表的 {@link Function}{@code <}{@link T}{@code , }{@link T}{@code >[]}。
     * @param <T> 表示待拼接函数的入参和返回值的类型的 {@link T}。
     * @return 表示拼接后的函数的 {@link Function}{@code <}{@link T}{@code , }{@link T}{@code >}。
     */
    @SafeVarargs
    public static <T> Function<T, T> connect(Function<T, T>... functions) {
        if (functions == null) {
            return null;
        }
        List<Function<T, T>> actualFunctions =
                Arrays.stream(functions).filter(Objects::nonNull).collect(Collectors.toList());
        if (actualFunctions.isEmpty()) {
            return null;
        }
        Function<T, T> function = actualFunctions.get(0);
        for (int i = 1; i < actualFunctions.size(); i++) {
            function = function.andThen(actualFunctions.get(i));
        }
        return function;
    }

    /**
     * 通过 {@link Function#andThen(Function)} 连接两个函数。
     * <p>前一个函数的输出为后一个函数的输入。</p>
     *
     * @param first 表示待拼接的第一个函数的 {@link Function}{@code <}{@link T}{@code , }{@link U}{@code >}。
     * @param second 表示待拼接的第一个函数的 {@link Function}{@code <}{@link U}{@code , }{@link V}{@code >}。
     * @param <T> 表示待拼接的第一个函数的入参的类型的 {@link T}。
     * @param <U> 表示待拼接的第一个函数的返回值和第二个函数的入参的类型的 {@link U}。
     * @param <V> 表示待拼接的第二个函数的返回值的类型的 {@link V}。
     * @return 表示拼接后的函数的 {@link Function}{@code <}{@link T}{@code , }{@link V}{@code >}。
     */
    public static <T, U, V> Function<T, V> connect(Function<T, U> first, Function<U, V> second) {
        return Validation.notNull(first, "The first function to connect cannot be null.")
                .andThen(Validation.notNull(second, "The second function to connect cannot be null."));
    }

    /**
     * 使用逻辑或关系连接两个校验器。
     * <p>若两个校验器中有一个为 {@code null}，则返回另一个；否则返回合并后的校验器。</p>
     *
     * @param first 表示待连接的第一个校验器的 {@link Predicate}{@code <}{@link T}{@code >}。
     * @param second 表示待连接的第二个校验器的 {@link Predicate}{@code <}{@link T}{@code >}。
     * @param <T> 表示校验器所判断数据的类型的 {@link T}。
     * @return 表示连接后的校验器的 {@link Predicate}{@code <}{@link T}{@code >}。
     */
    public static <T> Predicate<T> or(Predicate<T> first, Predicate<T> second) {
        return connect(first, second, Predicate::or);
    }

    private static <T> Predicate<T> connect(Predicate<T> first, Predicate<T> second,
            BiFunction<Predicate<T>, Predicate<T>, Predicate<T>> connector) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return connector.apply(first, second);
        }
    }

    private static <T, R> BiPredicate<T, R> connect(BiPredicate<T, R> first, BiPredicate<T, R> second,
            BiFunction<BiPredicate<T, R>, BiPredicate<T, R>, BiPredicate<T, R>> connector) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return connector.apply(first, second);
        }
    }

    /**
     * 测试指定数据是否满足校验器判定。
     *
     * @param predicate 表示用以判定的校验器的 {@link Predicate}{@code <}{@link T}{@code >}。
     * @param value 表示待判定的值的 {@link T}。
     * @param defaultValue 若校验器不存在时使用的默认判定结果的 {@code boolean}。
     * @param <T> 表示待判定的值的类型的 {@link T}。
     * @return 若判定成功，则返回 {@code true}；否则返回 {@code false}。
     */
    public static <T> boolean test(Predicate<T> predicate, T value, boolean defaultValue) {
        if (predicate == null) {
            return defaultValue;
        } else {
            return predicate.test(value);
        }
    }
}
