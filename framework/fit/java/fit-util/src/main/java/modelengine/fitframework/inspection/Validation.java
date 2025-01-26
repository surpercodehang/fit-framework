/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.inspection;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 为入参校验提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class Validation {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Validation() {}

    /**
     * 检查指定可比较的对象是否介于最小值（包括）和最大值（包括）之间。
     *
     * @param actual 表示待检查的对象的 {@link T}。
     * @param min 表示作为边界的最小值的 {@link T}。
     * @param max 表示作为边界的最大值的 {@link T}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @param <T> 表示指定对象的类型的 {@link T}。
     * @return 表示符合校验规则的对象的 {@link T}，其引用等于 {@code actual}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static <T extends Comparable<T>> T between(T actual, T min, T max, String error, Object... args) {
        if (ObjectUtils.between(actual, min, max)) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定可比较的对象是否介于最小值（包括）和最大值（包括）之间。
     *
     * @param actual 表示待检查的对象的 {@link T}。
     * @param min 表示作为边界的最小值的 {@link T}。
     * @param max 表示作为边界的最大值的 {@link T}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <}{@link E}{@code >}。
     * @param <T> 表示指定对象的类型的 {@link T}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验规则的对象的 {@link T}，其引用等于 {@code actual}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <T extends Comparable<T>, E extends RuntimeException> T between(T actual, T min, T max,
            Supplier<E> exceptionSupplier) {
        if (ObjectUtils.between(actual, min, max)) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定对象是否和期望对象完全一致。
     *
     * @param actual 表示待检查的对象的 {@link T}。
     * @param expected 表示期望的对象的 {@link T}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @param <T> 表示待检查对象的类型的 {@link T}。
     * @return 表示符合校验逻辑的待检查对象的 {@link T}，其引用等于 {@code actual}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static <T> T equals(T actual, T expected, String error, Object... args) {
        if (Objects.equals(actual, expected)) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定对象是否和期望对象完全一致。
     *
     * @param actual 表示待检查的对象的 {@link T}。
     * @param expected 表示期望的对象的 {@link T}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <T> 表示待检查对象的类型的 {@link T}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的待检查对象的 {@link T}，其引用等于 {@code actual}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <T, E extends RuntimeException> T equals(T actual, T expected, Supplier<E> exceptionSupplier) {
        if (Objects.equals(actual, expected)) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定的整型应比指定值大。
     *
     * @param actual 表示待检查的整型的 {@code int}。
     * @param bound 表示边界的整型的 {@code int}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的整型的 {@code int}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static int greaterThan(int actual, int bound, String error, Object... args) {
        if (actual > bound) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定的整型应比指定值大。
     *
     * @param actual 表示待检查的整型的 {@code int}。
     * @param bound 表示边界的整型的 {@code int}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的整型的 {@code int}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <E extends RuntimeException> int greaterThan(int actual, int bound, Supplier<E> exceptionSupplier) {
        if (actual > bound) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定的长整型应比指定值大。
     *
     * @param actual 表示待检查的长整型的 {@code long}。
     * @param bound 表示边界的长整型的 {@code long}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的长整型的 {@code long}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static long greaterThan(long actual, long bound, String error, Object... args) {
        if (actual > bound) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定的长整型应比指定值大。
     *
     * @param actual 表示待检查的长整型的 {@code long}。
     * @param bound 表示边界的长整型的 {@code long}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的长整型的 {@code long}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <E extends RuntimeException> long greaterThan(long actual, long bound,
            Supplier<E> exceptionSupplier) {
        if (actual > bound) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定的整型应比指定的值大或等于该值。
     *
     * @param actual 表示待检查的整型的 {@code int}。
     * @param bound 表示边界的整型的 {@code int}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的整型的 {@code int}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static int greaterThanOrEquals(int actual, int bound, String error, Object... args) {
        if (actual >= bound) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定的整型应比指定的值大或等于该值。
     *
     * @param actual 表示待检查的整型的 {@code int}。
     * @param bound 表示边界的整型的 {@code int}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的整型的 {@code int}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <E extends RuntimeException> int greaterThanOrEquals(int actual, int bound,
            Supplier<E> exceptionSupplier) {
        if (actual >= bound) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定的长整型应比指定的值大或等于该值。
     *
     * @param actual 表示待检查的长整型的 {@code long}。
     * @param bound 表示边界的长整型的 {@code long}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的长整型的 {@code long}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static long greaterThanOrEquals(long actual, long bound, String error, Object... args) {
        if (actual >= bound) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定的长整型应比指定的值大或等于该值。
     *
     * @param actual 表示待检查的长整型的 {@code long}。
     * @param bound 表示边界的长整型的 {@code long}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的长整型的 {@code long}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <E extends RuntimeException> long greaterThanOrEquals(long actual, long bound,
            Supplier<E> exceptionSupplier) {
        if (actual >= bound) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查入参是否为 {@code false}。
     *
     * @param actual 表示待检查的入参的 {@code boolean}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的 {@code false}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static boolean isFalse(boolean actual, String error, Object... args) {
        if (!actual) {
            return false;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查入参是否为 {@code false}。
     *
     * @param actual 表示待检查的入参的 {@code boolean}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的 {@code false}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <E extends RuntimeException> boolean isFalse(boolean actual, Supplier<E> exceptionSupplier) {
        if (!actual) {
            return false;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 校验指定参数为指定的类型，并强制转换类型。
     *
     * @param actual 表示待校验的参数的值的 {@link U}。
     * @param clazz 表示待转换的类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @param <U> 表示入参的类型的 {@link U}。
     * @param <T> 表示待转换的类型的 {@link T}。
     * @return 表示符合校验逻辑的原始入参的强制转换类型后的引用。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static <U, T extends U> T isInstanceOf(U actual, Class<T> clazz, String error, Object... args) {
        if (clazz.isInstance(actual)) {
            return clazz.cast(actual);
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 校验指定参数为指定的类型，并强制转换类型。
     *
     * @param actual 表示待校验的参数的值的 {@link U}。
     * @param clazz 表示待转换的类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <}{@link E}{@code >}。
     * @param <U> 表示入参的类型的 {@link U}。
     * @param <T> 表示待转换的类型的 {@link T}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的原始入参的强制转换类型后的引用。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <U, T extends U, E extends RuntimeException> T isInstanceOf(U actual, Class<T> clazz,
            Supplier<E> exceptionSupplier) {
        if (clazz.isInstance(actual)) {
            return clazz.cast(actual);
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查入参是否为 {@code true}。
     *
     * @param actual 表示待检查的入参的 {@code boolean}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的 {@code true}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static boolean isTrue(boolean actual, String error, Object... args) {
        if (actual) {
            return true;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查入参是否为 {@code true}。
     *
     * @param actual 表示待检查的入参的 {@code boolean}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的 {@code true}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <E extends RuntimeException> boolean isTrue(boolean actual, Supplier<E> exceptionSupplier) {
        if (actual) {
            return true;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定的整型应比指定值小。
     *
     * @param actual 表示待检查的整型的 {@code int}。
     * @param bound 表示边界的整型的 {@code int}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的整型的 {@code int}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static int lessThan(int actual, int bound, String error, Object... args) {
        if (actual < bound) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定的整型应比指定值小。
     *
     * @param actual 表示待检查的整型的 {@code int}。
     * @param bound 表示边界的整型的 {@code int}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的整型的 {@code int}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <E extends RuntimeException> int lessThan(int actual, int bound, Supplier<E> exceptionSupplier) {
        if (actual < bound) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定的长整型应比指定值小。
     *
     * @param actual 表示待检查的长整型的 {@code long}。
     * @param bound 表示边界的长整型的 {@code long}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的长整型的 {@code long}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static long lessThan(long actual, long bound, String error, Object... args) {
        if (actual < bound) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定的长整型应比指定值小。
     *
     * @param actual 表示待检查的长整型的 {@code long}。
     * @param bound 表示边界的长整型的 {@code long}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的长整型的 {@code long}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <E extends RuntimeException> long lessThan(long actual, long bound, Supplier<E> exceptionSupplier) {
        if (actual < bound) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定的整型应比指定的值小或等于该值。
     *
     * @param actual 表示待检查的整型的 {@code int}。
     * @param bound 表示边界的整型的 {@code int}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的整型的 {@code int}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static int lessThanOrEquals(int actual, int bound, String error, Object... args) {
        if (actual <= bound) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定的整型应比指定的值小或等于该值。
     *
     * @param actual 表示待检查的整型的 {@code int}。
     * @param bound 表示边界的整型的 {@code int}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的整型的 {@code int}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <E extends RuntimeException> int lessThanOrEquals(int actual, int bound,
            Supplier<E> exceptionSupplier) {
        if (actual <= bound) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定的长整型应比指定的值小或等于该值。
     *
     * @param actual 表示待检查的长整型的 {@code long}。
     * @param bound 表示边界的长整型的 {@code long}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的长整型的 {@code long}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static long lessThanOrEquals(long actual, long bound, String error, Object... args) {
        if (actual <= bound) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定的长整型应比指定的值小或等于该值。
     *
     * @param actual 表示待检查的长整型的 {@code long}。
     * @param bound 表示边界的长整型的 {@code long}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的长整型的 {@code long}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <E extends RuntimeException> long lessThanOrEquals(long actual, long bound,
            Supplier<E> exceptionSupplier) {
        if (actual <= bound) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定字符串不能为 {@code null}、空字符串或者只有空白字符的字符串。
     *
     * @param actual 表示待检查的字符串的 {@link String}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的字符的 {@link String}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     * @see StringUtils#isNotBlank(String)
     */
    public static String notBlank(String actual, String error, Object... args) {
        if (StringUtils.isNotBlank(actual)) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定字符串不能为 {@code null}、空字符串或者只有空白字符的字符串。
     *
     * @param actual 表示待检查的字符串的 {@link String}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的字符的 {@link String}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     * @see StringUtils#isNotBlank(String)
     */
    public static <E extends RuntimeException> String notBlank(String actual, Supplier<E> exceptionSupplier) {
        if (StringUtils.isNotBlank(actual)) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定集合不能为空。
     *
     * @param actual 表示待检查的集合的 {@link Collection}{@code <?>}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的集合的 {@link Collection}{@code <?>}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     * @see StringUtils#isNotBlank(String)
     */
    public static Collection<?> notEmpty(Collection<?> actual, String error, Object... args) {
        if (CollectionUtils.isNotEmpty(actual)) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定集合不能为空。
     *
     * @param actual 表示待检查的集合的 {@link Collection}{@code <?>}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的集合的 {@link Collection}{@code <?>}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     * @see StringUtils#isNotBlank(String)
     */
    public static <E extends RuntimeException> Collection<?> notEmpty(Collection<?> actual,
            Supplier<E> exceptionSupplier) {
        if (CollectionUtils.isNotEmpty(actual)) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定的值不能为负数。
     *
     * @param value 表示待检查的值的 {@code int}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的值的 {@code int}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static int notNegative(int value, String error, Object... args) {
        if (value < 0) {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        } else {
            return value;
        }
    }

    /**
     * 检查指定的值不能为负数。
     *
     * @param value 表示待检查的值的 {@code long}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @return 表示符合校验逻辑的值的 {@code long}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static long notNegative(long value, String error, Object... args) {
        if (value < 0) {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        } else {
            return value;
        }
    }

    /**
     * 检查指定的值不能为负数。
     *
     * @param value 表示待检查的值的 {@code long}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @return 表示符合校验逻辑的值的 {@code long}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <E extends RuntimeException> long notNegative(long value, Supplier<E> exceptionSupplier) {
        if (value < 0) {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        } else {
            return value;
        }
    }

    /**
     * 检查指定参数不能为 {@code null}。
     *
     * @param actual 表示待检查的入参的值的 {@link T}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @param <T> 表示待校验的入参的类型的 {@link T}。
     * @return 表示符合校验逻辑的对象的 {@link T}，其引用等于 {@code actual}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static <T> T notNull(T actual, String error, Object... args) {
        if (actual != null) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定参数不能为 {@code null}。
     *
     * @param actual 表示待检查的入参的值的 {@link T}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <T> 表示待校验的入参的类型的 {@link T}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的对象的 {@link T}，其引用等于 {@code actual}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <T, E extends RuntimeException> T notNull(T actual, Supplier<E> exceptionSupplier) {
        if (actual != null) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }

    /**
     * 检查指定对象的引用是否等于期望对象的引用。
     * <p><b>注意：该方法为引用比较。</b></p>
     *
     * @param actual 表示待检查的对象引用的 {@link T}。
     * @param expected 表示待比较的期望对象的引用的 {@link T}。
     * @param error 表示当入参不符合校验时抛出的异常信息的格式化模板的 {@link String}，占位符为 {@code {\d}}，例如 {@code {0}}。
     * @param args 表示填入格式化模板的参数的 {@link Object}{@code []}。
     * @param <T> 表示待校验的对象的类型的 {@link T}。
     * @return 表示符合校验逻辑的对象的 {@link T}，其引用等于 {@code actual}。
     * @throws IllegalArgumentException 当不符合校验逻辑时。
     */
    public static <T> T same(T actual, T expected, String error, Object... args) {
        if (actual == expected) {
            return actual;
        } else {
            throw new IllegalArgumentException(StringUtils.format(error, args));
        }
    }

    /**
     * 检查指定对象的引用是否等于期望对象的引用。
     * <p><b>注意：该方法为引用比较。</b></p>
     *
     * @param actual 表示待检查的对象引用的 {@link T}。
     * @param expected 表示待比较的期望对象的引用的 {@link T}。
     * @param exceptionSupplier 表示当入参不符合校验时抛出异常的 {@link Supplier}{@code <E>}{@code <}{@link E}{@code >}。
     * @param <T> 表示待校验的对象的类型的 {@link T}。
     * @param <E> 表示抛出异常的类型的 {@link E}。
     * @return 表示符合校验逻辑的对象的 {@link T}，其引用等于 {@code actual}。
     * @throws E 当不符合校验逻辑时。
     * @throws IllegalArgumentException 当不符合校验逻辑且 {@code exceptionSupplier} 为 {@code null} 时。
     */
    public static <T, E extends RuntimeException> T same(T actual, T expected, Supplier<E> exceptionSupplier) {
        if (actual == expected) {
            return actual;
        } else {
            throw notNull(exceptionSupplier, "The exception supplier cannot be null.").get();
        }
    }
}
