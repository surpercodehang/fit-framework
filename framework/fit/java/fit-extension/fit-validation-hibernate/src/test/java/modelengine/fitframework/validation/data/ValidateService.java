/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.data;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.validation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

/**
 * 表示测试用校验服务。
 *
 * @author 易文渊
 * @since 2024-09-27
 */
@Component
@Validated
public class ValidateService {
    private static final Logger LOG = Logger.get(ValidateService.class);

    /**
     * 测试原始类型。
     *
     * @param num 表示输入的 {@code int}。
     */
    public void foo0(@Positive(message = "必须是正数") int num) {
        LOG.debug("{}", num);
    }

    /**
     * 测试结构体类型。
     *
     * @param employee 表示输入的 {@code Employee}。
     */
    public void foo1(@Valid Employee employee) {
        LOG.debug("{}", employee);
    }

    /**
     * 测试嵌套类型。
     *
     * @param company 表示输入的 {@code Company}。
     */
    public void foo2(@Valid Company company) {
        LOG.debug("{}", company);
    }
}