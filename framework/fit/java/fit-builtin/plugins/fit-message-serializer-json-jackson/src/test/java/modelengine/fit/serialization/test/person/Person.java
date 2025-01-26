/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.test.person;

import lombok.Data;
import modelengine.fitframework.serialization.annotation.Unwrapped;

/**
 * 为单元测试提供人的信息定义。
 *
 * @author 梁济时
 * @since 2020-11-23
 */
@Data
public class Person {
    @Unwrapped
    private PersonName name;
}
