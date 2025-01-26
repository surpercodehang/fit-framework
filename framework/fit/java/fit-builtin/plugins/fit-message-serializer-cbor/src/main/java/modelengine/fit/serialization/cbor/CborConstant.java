/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.cbor;

/**
 * 表示 CBOR 编解码中的常量。
 *
 * @author 季聿阶
 * @since 2024-01-29
 */
public interface CborConstant {
    /** 表示 false 的值。 */
    byte FALSE = (byte) 0xF4;

    /** 表示 true 的值。 */
    byte TRUE = (byte) 0xF5;

    /** 表示 null 的值。 */
    byte NULL = (byte) 0xF6;

    /** 表示 {@link Float} 的类型。 */
    byte FLOAT = (byte) 0xFA;

    /** 表示 {@link Double} 的类型。 */
    byte DOUBLE = (byte) 0xFB;
}
