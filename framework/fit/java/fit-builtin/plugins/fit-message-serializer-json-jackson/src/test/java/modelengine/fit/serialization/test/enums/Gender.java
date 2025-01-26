/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.test.enums;

import modelengine.fitframework.annotation.Property;

/**
 * 测试用性别枚举。
 *
 * @author 易文渊
 * @since 2024-09-27
 */
public enum Gender {
    @Property(name = "man")
    MAN,
    @Property(name = "woman")
    WOMAN
}