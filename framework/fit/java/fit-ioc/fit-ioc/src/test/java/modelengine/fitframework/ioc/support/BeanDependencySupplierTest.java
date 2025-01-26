/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.ioc.BeanDependency;
import modelengine.fitframework.ioc.DependencyNotFoundException;
import modelengine.fitframework.ioc.DependencyResolvingResult;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link BeanDependencySupplier} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-03-02
 */
@DisplayName("测试 BeanDependencySupplier 类")
class BeanDependencySupplierTest {
    @Test
    @DisplayName("提供 BeanDependencySupplier 类 get 方法解析成功时，返回正常信息")
    void givenBeanDependencySupplierWhenSupplierSuccessThenReturnValue() {
        BeanDependency dependency = mock(BeanDependency.class);
        String expected = "success";
        DependencyResolvingResult success = DependencyResolvingResult.success(() -> expected);
        when(dependency.resolve()).thenReturn(success);
        BeanDependencySupplier supplier = new BeanDependencySupplier(dependency);
        Object actual = supplier.get();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("提供 BeanDependencySupplier 类 get 方法解析失败时，抛出异常")
    void givenBeanDependencySupplierWhenSupplierFailThenThrowException() {
        BeanDependency dependency = mock(BeanDependency.class);
        DependencyResolvingResult fail = DependencyResolvingResult.failure();
        when(dependency.resolve()).thenReturn(fail);
        when(dependency.required()).thenReturn(true);
        when(dependency.type()).thenReturn(String.class);
        when(dependency.name()).thenReturn("fail");
        BeanDependencySupplier supplier = new BeanDependencySupplier(dependency);
        assertThatThrownBy(supplier::get).isInstanceOf(DependencyNotFoundException.class);
    }
}