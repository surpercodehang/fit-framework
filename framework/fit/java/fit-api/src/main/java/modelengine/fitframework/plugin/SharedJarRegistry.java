/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin;

import java.net.URL;

/**
 * 为公共JAR提供注册入口。
 *
 * @author 梁济时
 * @since 2022-06-20
 */
public interface SharedJarRegistry {
    /**
     * 注册公共JAR。
     *
     * @param jar 表示公共JAR的URL的 {@link URL}。
     */
    void register(URL jar);
}
