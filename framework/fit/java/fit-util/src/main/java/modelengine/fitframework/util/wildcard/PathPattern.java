/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard;

/**
 * 表示路径的匹配模式。
 *
 * @author 季聿阶
 * @since 2022-12-21
 */
public interface PathPattern extends Pattern<String> {
    /**
     * 匹配一个指定的路径。
     *
     * @param path 表示待匹配的指定路径的 {@link String}。
     * @return 如果匹配成功，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean matches(String path);
}
