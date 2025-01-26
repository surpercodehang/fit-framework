/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.server.support;

import modelengine.fitframework.broker.server.DoGenericableServerFilterException;
import modelengine.fitframework.broker.server.GenericableServerFilter;
import modelengine.fitframework.broker.server.GenericableServerFilterChain;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.wildcard.Pattern;

import java.util.List;
import java.util.Optional;

/**
 * 表示 {@link GenericableServerFilterChain} 的默认实现。
 *
 * @author 李金绪
 * @since 2024-08-27
 */
public class DefaultGenericableServerFilterChain implements GenericableServerFilterChain {
    private static final char SEPARATOR = '.';

    private final List<GenericableServerFilter> filters;
    private final String genericableId;
    private int index;

    public DefaultGenericableServerFilterChain(String genericableId, List<GenericableServerFilter> filters) {
        this.genericableId = Validation.notBlank(genericableId, "The genericable id cannot be blank.");
        this.filters = Validation.notNull(filters, "The genericable server filters cannot be null.");
        this.index = -1;
    }

    @Override
    public void doFilter(Object[] args) throws DoGenericableServerFilterException {
        Optional<GenericableServerFilter> opNextFilter = this.findNextFilter(this.genericableId);
        if (opNextFilter.isPresent()) {
            opNextFilter.get().doFilter(genericableId, args, this);
        }
    }

    private Optional<GenericableServerFilter> findNextFilter(String genericableId) {
        for (int i = this.index + 1; i < this.filters.size(); i++) {
            GenericableServerFilter nextFilter = this.filters.get(i);
            boolean isMismatch = nextFilter.mismatchPatterns()
                    .stream()
                    .map(pattern -> Pattern.forPath(pattern, SEPARATOR))
                    .anyMatch(pattern -> pattern.matches(genericableId));
            if (isMismatch) {
                continue;
            }
            for (String pattern : nextFilter.matchPatterns()) {
                boolean isMatches = Pattern.forPath(pattern, SEPARATOR).matches(genericableId);
                if (isMatches) {
                    this.index = i;
                    return Optional.of(nextFilter);
                }
            }
        }
        return Optional.empty();
    }
}