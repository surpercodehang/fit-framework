/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.support;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.HttpHandler;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.HttpServerFilterChain;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.wildcard.Pattern;

import java.util.List;
import java.util.Optional;

/**
 * {@link HttpServerFilterChain} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-07-18
 */
public class DefaultHttpServerFilterChain implements HttpServerFilterChain {
    private static final char PATH_SEPARATOR = '/';

    private final List<HttpServerFilter> filters;
    private int index;
    private final HttpHandler handler;

    /**
     * 通过 Http 请求处理器来实例化 {@link DefaultHttpServerFilterChain}。
     *
     * @param handler 表示 Http 请求处理器的 {@link HttpHandler}。
     * @throws IllegalArgumentException 当 {@code handler} 为 {@code null} 时。
     */
    public DefaultHttpServerFilterChain(HttpHandler handler) {
        this.handler = Validation.notNull(handler, "The http handler cannot be null.");
        this.filters = handler.preFilters();
        this.index = -1;
    }

    @Override
    public void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response) {
        Optional<HttpServerFilter> opNextFilter = this.findNextFilter(request);
        if (opNextFilter.isPresent()) {
            opNextFilter.get().doFilter(request, response, this);
        } else {
            this.handler.handle(request, response);
        }
    }

    private Optional<HttpServerFilter> findNextFilter(HttpClassicServerRequest request) {
        for (int i = this.index + 1; i < this.filters.size(); i++) {
            HttpServerFilter nextFilter = this.filters.get(i);
            boolean isMismatch = nextFilter.mismatchPatterns()
                    .stream()
                    .map(pattern -> Pattern.forPath(pattern, PATH_SEPARATOR))
                    .anyMatch(pattern -> pattern.matches(request.path()));
            if (isMismatch) {
                continue;
            }
            for (String pattern : nextFilter.matchPatterns()) {
                boolean matches = Pattern.forPath(pattern, PATH_SEPARATOR).matches(request.path());
                if (matches) {
                    this.index = i;
                    return Optional.of(nextFilter);
                }
            }
        }
        return Optional.empty();
    }
}
