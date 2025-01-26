/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fitframework.annotation.Genericable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * {@link GenericableUtils} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-15
 */
@DisplayName("测试 GenericableUtils 类")
class GenericableUtilsTest {
    private static final String TEST_GENERIC_ID = "9588e5fc63cc4f1fbdcf2567bce0a454";

    @Test
    @DisplayName("提供 Genericable 类型接口方法存在时，获取方法类型")
    void givenExistMethodShouldReturnMethodType() {
        String methodName = "process";
        Class<?>[] parameterTypes = {Integer.class, String.class};
        String genericableId = GenericableUtils.getGenericableId(GenericableTest.class, methodName, parameterTypes);
        String expected = "java.util.List " + "modelengine.fitframework.util.GenericableUtilsTest$GenericableTest"
                + ".process(java.lang.Integer,java.lang.String)";
        assertThat(genericableId).isEqualTo(expected);
    }

    @Test
    @DisplayName("提供 Genericable 类型接口方法不存在时，抛出异常信息")
    void givenGenericableWhenMethodNotExistThenThrowException() {
        assertThatThrownBy(() -> GenericableUtils.getGenericableId(GenericableTest.class,
                "notExistMethod",
                new Class[0])).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("提供 Genericable 类型接口有一个宏方法时，获取宏方法名称")
    void givenGenericableWhenHaveMacroThenReturnMacroName() {
        Optional<Method> method = GenericableUtils.getMacroGenericableMethod(GenericableTest.class);
        assertThat(method).isPresent();
        String name = method.get().getName();
        assertThat(name).isEqualTo("process");
    }

    @Test
    @DisplayName("提供 Genericable 类型接口如果有多个宏方法时，抛出异常")
    void givenGenericableWhenHaveMoreMacroThenThrowException() {
        assertThatThrownBy(() -> GenericableUtils.getMacroGenericableMethod(NoMethodTest.class)).isInstanceOf(
                IllegalStateException.class);
    }

    /**
     * 表示测试的服务类。
     */
    @Genericable(TEST_GENERIC_ID)
    @FunctionalInterface
    public interface GenericableTest {
        /**
         * 表示测试的服务标识。
         */
        String GENERIC_ID = TEST_GENERIC_ID;

        /**
         * 表示测试的服务方法。
         *
         * @param num 表示测试的参数的 {@link Integer}。
         * @param str 表示测试的参数的 {@link String}。
         * @return 表示测试的返回值的 {@link List}{@code <}{@link Integer}{@code >}。
         */
        List<Integer> process(Integer num, String str);
    }

    /**
     * 表示测试的服务类。
     */
    @Genericable(TEST_GENERIC_ID)
    public interface NoMethodTest {
        /**
         * 表示测试的服务标识。
         */
        String GENERIC_ID = TEST_GENERIC_ID;

        /**
         * 表示测试的服务方法。
         *
         * @param num 表示测试的参数的 {@link Integer}。
         * @param str 表示测试的参数的 {@link String}。
         * @return 表示测试的返回值的 {@link List}{@code <}{@link Integer}{@code >}。
         */
        List<Integer> process(Integer num, String str);

        /**
         * 表示测试的服务方法。
         *
         * @param num 表示测试的参数的 {@link Integer}。
         * @return 表示测试的返回值的 {@link List}{@code <}{@link Integer}{@code >}。
         */
        List<Integer> process(Integer num);
    }

    /**
     * 表示测试的服务类。
     */
    public interface NoMacroTest {
        /**
         * 表示测试的服务标识。
         */
        String GENERIC_ID = TEST_GENERIC_ID;

        /**
         * 表示测试的服务方法。
         *
         * @param num 表示测试的参数的 {@link Integer}。
         * @param str 表示测试的参数的 {@link String}。
         * @return 表示测试的返回值的 {@link List}{@code <}{@link Integer}{@code >}。
         */
        List<Integer> processTest(Integer num, String str);
    }
}