/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.test.person;

import lombok.Data;

/**
 * 为忽略字段提供测试支持。
 *
 * @author 易文渊
 * @since 2024-10-10
 */
@Data
public class PersonTransient {
    private String name;
    private transient int age;
}