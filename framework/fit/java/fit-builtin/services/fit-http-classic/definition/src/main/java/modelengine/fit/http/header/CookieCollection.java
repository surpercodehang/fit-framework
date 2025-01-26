/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.header;

import modelengine.fit.http.Cookie;

import java.util.List;
import java.util.Optional;

/**
 * 表示 Http 中只读的 Cookie 集合。
 *
 * @author 季聿阶
 * @since 2022-07-06
 */
public interface CookieCollection extends HeaderValue {
    /**
     * 获取指定名字的 {@link Cookie}。
     *
     * @param name 表示 Cookie 名字的 {@link Optional}{@code <}{@link String}{@code >}。
     * @return 表示指定名字的 {@link Cookie}。
     */
    Optional<Cookie> get(String name);

    /**
     * 获取所有的 {@link Cookie}。
     *
     * @return 表示所有 {@link Cookie} 列表的 {@link List}{@code <}{@link Cookie}{@code >}。
     */
    List<Cookie> all();

    /**
     * 获取所有 {@link Cookie} 的数量。
     *
     * @return 表示所有 {@link Cookie} 的数量的 {@code int}。
     */
    int size();
}
