/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.annotation;

/**
 * 表示注解中值的默认值。
 *
 * @author 季聿阶
 * @since 2023-01-30
 */
public interface DefaultValue {
    /**
     * 表示一个不可能人为输入的默认值。
     */
    String VALUE = "\0";
}
