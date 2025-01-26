/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.merge;

/**
 * 为解决冲突提供策略。
 *
 * @author 梁济时
 * @since 2020-10-06
 */
public enum ConflictResolutionPolicy {
    /** 表示将终止执行，将抛出异常。 */
    ABORT,

    /** 表示将跳过冲突项，冲突将被忽略。 */
    SKIP,

    /** 表示使用新的内容覆盖冲突项。 */
    OVERRIDE,
}
