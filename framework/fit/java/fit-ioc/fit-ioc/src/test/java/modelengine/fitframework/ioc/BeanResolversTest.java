/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link BeanResolvers} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-28
 */
@DisplayName("测试 BeanResolvers 类")
public class BeanResolversTest {
    @Test
    @DisplayName("给定非空的类加载器，返回值不为空")
    void givenNotEmptyClassLoaderThenReturnNotEmpty() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        BeanResolver load = BeanResolvers.load(classLoader);
        assertThat(load).isInstanceOf(BeanResolver.class);
    }
}
