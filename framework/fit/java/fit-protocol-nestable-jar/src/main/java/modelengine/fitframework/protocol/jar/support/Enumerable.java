/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

/**
 * 表示内容可枚举的对象。
 *
 * @param <T> 表示可枚举的内容的类型。
 * @author 梁济时
 * @since 2022-09-20
 */
interface Enumerable<T> {
    /**
     * 获取一个枚举程序，用以遍历所有内容。
     *
     * @return 表示用以遍历所有内容的枚举程序的 {@link Enumerator}。
     */
    Enumerator<T> enumerator();
}
