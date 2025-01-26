/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor;

import modelengine.fitframework.aop.interceptor.support.AccessibleMethodMatcher;
import modelengine.fitframework.aop.interceptor.support.AnnotationMethodMatcher;
import modelengine.fitframework.aop.interceptor.support.DefaultMatchResult;
import modelengine.fitframework.aop.interceptor.support.SpecifiedMethodMatcher;
import modelengine.fitframework.inspection.Nonnull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 表示方法匹配器。
 *
 * @author 季聿阶
 * @since 2022-05-05
 */
public interface MethodMatcher {
    /**
     * 提前判断指定类是否可以匹配。
     * <p>方法匹配器本身是用于匹配方法的，但是对于方法所属的类也可以成为匹配方法的一个条件，在某些情况下，如果类不满足，
     * 可以直接短路。</p>
     *
     * @param clazz 表示待匹配的指定类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 如果类型满足要求，返回 {@code true}，否则，返回 {@code false}。
     */
    default boolean couldMatch(Class<?> clazz) {
        return true;
    }

    /**
     * 匹配一个指定的方法。
     *
     * @param method 表示待匹配的方法的 {@link Method}。
     * @return 表示匹配结果的 {@link MatchResult}。
     */
    MatchResult match(@Nonnull Method method);

    /**
     * 匹配指定方法成功后的回调。
     * <p>{@link MethodMatcherCollection 方法匹配器集合} 中，存在很多方法匹配器，因此，在最终匹配成功一个方法后，
     * 可以回调通知对应的方法匹配器，来传递信息。</p>
     *
     * @param method 表示匹配过的方法的 {@link Method}。
     * @param result 表示匹配方法结果的 {@link MatchResult}。
     * @see MethodMatcherCollection
     */
    default void choose(Method method, MatchResult result) {}

    /**
     * 获取所有可访问方法的匹配器。
     *
     * @return 表示所有可访问方法的匹配器的 {@link MethodMatcher}。
     */
    static MethodMatcher accessible() {
        return AccessibleMethodMatcher.INSTANCE;
    }

    /**
     * 获取带指定注解的匹配器。
     *
     * @param annotationClass 表示指定注解类型的 {@link Class}{@code <? extends }{@link Annotation}{@code >}。
     * @return 表示带指定注解的匹配器的 {@link MethodMatcher}。
     * @throws IllegalArgumentException 当 {@code resolver} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code annotationClass} 为 {@code null} 时。
     */
    static MethodMatcher annotation(Class<? extends Annotation> annotationClass) {
        return new AnnotationMethodMatcher(annotationClass);
    }

    /**
     * 获取和指定方法相等的匹配器。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示和指定方法相等的匹配器的 {@link MethodMatcher}。
     * @throws IllegalArgumentException 当 {@code method} 为 {@code null} 时。
     */
    static MethodMatcher specified(Method method) {
        return new SpecifiedMethodMatcher(method);
    }

    /**
     * 表示匹配结果。
     */
    interface MatchResult {
        /**
         * 获取是否匹配成功。
         *
         * @return 如果匹配成功，返回 {@code true}，否则，返回 {@code false}。
         */
        boolean matches();

        /**
         * 获取方法匹配器的匹配结果。
         * <p>匹配结果的具体类型可以由子类指定。</p>
         *
         * @return 表示方法匹配器的匹配结果的 {@link Object}。
         */
        Object getResult();

        /**
         * 获取匹配成功的结果。
         *
         * @return 表示匹配成功的结果的 {@link MatchResult}。
         */
        static MatchResult match() {
            return match(true);
        }

        /**
         * 获取匹配结果。
         *
         * @param matches 表示匹配结果的 {@code boolean}。
         * @return 表示匹配结果的 {@link MatchResult}。
         */
        static MatchResult match(boolean matches) {
            return new DefaultMatchResult(matches);
        }

        /**
         * 获取匹配失败的结果。
         *
         * @return 表示匹配失败的结果的 {@link MatchResult}。
         */
        static MatchResult notMatch() {
            return match(false);
        }
    }
}
