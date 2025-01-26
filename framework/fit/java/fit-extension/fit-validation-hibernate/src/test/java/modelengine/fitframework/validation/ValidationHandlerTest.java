/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.validation.data.Company;
import modelengine.fitframework.validation.data.Employee;
import modelengine.fitframework.validation.data.ValidateService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import javax.validation.ConstraintViolationException;

/**
 * {@link ValidationHandler} 的单元测试。
 *
 * @author 易文渊
 * @since 2024-09-27
 */
@DisplayName("测试注解校验功能")
@FitTestWithJunit(includeClasses = {ValidateService.class, ValidationHandler.class})
public class ValidationHandlerTest {
    @Fit
    private ValidateService validateService;

    @Test
    @DisplayName("测试校验原始类型成功")
    void givePrimitiveThenValidateOk() {
        assertThatThrownBy(() -> this.validateService.foo0(-1)).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("必须是正数");
    }

    @Test
    @DisplayName("测试校验结构体成功")
    void giveClassThenValidateOk() {
        assertThatThrownBy(() -> this.validateService.foo1(new Employee("sky", 17))).isInstanceOf(
                ConstraintViolationException.class).hasMessageContaining("年龄必须大于等于18");
    }

    @Test
    @DisplayName("测试嵌套结构体成功")
    void giveNestedClassThenValidateOk() {
        assertThatThrownBy(() -> {
            Employee employee = new Employee("sky", 17);
            this.validateService.foo2(new Company(Collections.singletonList(employee)));
        }).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("年龄必须大于等于18");
    }
}
