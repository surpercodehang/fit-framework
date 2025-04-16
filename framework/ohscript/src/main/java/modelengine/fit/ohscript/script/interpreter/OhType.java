/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.interpreter;

/**
 * oh的对象类型
 *
 * @since 1.0
 */
public enum OhType {
    /** Map类型，用于表示键值对映射集合 */
    MAP,
    /** List类型，用于表示可变长度的有序列表 */
    LIST,
    /** Tuple类型，用于表示固定长度的不可变序列 */
    TUPLE,
    /** Entity类型，用于表示业务实体对象 */
    ENTITY
}
