/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.Cookie;
import modelengine.fit.http.header.ConfigurableCookieCollection;
import modelengine.fit.http.header.CookieCollection;
import modelengine.fit.http.header.HeaderValue;
import modelengine.fit.http.header.support.DefaultHeaderValue;
import modelengine.fit.http.header.support.DefaultParameterCollection;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link CookieCollection} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-07-06
 */
public class DefaultCookieCollection extends DefaultHeaderValue implements ConfigurableCookieCollection {
    public DefaultCookieCollection() {
        super(StringUtils.EMPTY, new DefaultParameterCollection());
    }

    public DefaultCookieCollection(HeaderValue headerValue) {
        super(notNull(headerValue, "The header value cannot be null.").value(), headerValue.parameters());
    }

    @Override
    public Optional<Cookie> get(String name) {
        return this.parameters().get(name).map(value -> Cookie.builder().name(name).value(value).build());
    }

    @Override
    public List<Cookie> all() {
        return Collections.unmodifiableList(this.parameters()
                .keys()
                .stream()
                .map(this::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
    }

    @Override
    public int size() {
        return this.parameters().size();
    }

    @Override
    public void add(Cookie cookie) {
        if (cookie != null) {
            this.parameters().set(cookie.name(), cookie.value());
        }
    }
}
