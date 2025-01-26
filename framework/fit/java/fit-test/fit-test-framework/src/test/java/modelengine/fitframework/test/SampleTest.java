/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.ScanPackages;
import modelengine.fitframework.test.annotation.EnableMockMvc;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.Spy;
import modelengine.fitframework.test.beans.Exclude1;
import modelengine.fitframework.test.beans.Include1;
import modelengine.fitframework.test.beans.Include2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 用于测试测试框架中的注解。
 *
 * @author 季聿阶
 * @since 2024-07-31
 */
@EnableMockMvc
@FitTestWithJunit(includeClasses = Include1.class, excludeClasses = Exclude1.class)
@ScanPackages("modelengine.fitframework.test.beans")
@DisplayName("测试测试框架自身")
public class SampleTest {
    @Fit
    private Include1 include1;

    @Spy
    private Include2 include2;

    @Mock
    private Exclude1 exclude1;

    @Test
    @DisplayName("@Fit, @Spied, @Mocked 注解注入正确")
    void shouldInjectSuccessfully() {
        assertThat(this.include1).isNotNull();
        assertThat(this.include2).isNotNull();
        assertThat(this.exclude1).isNotNull();
    }
}
