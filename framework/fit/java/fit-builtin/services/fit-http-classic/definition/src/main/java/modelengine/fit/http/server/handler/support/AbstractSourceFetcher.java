/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.inspection.Validation.notEmpty;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.annotation.DefaultValue;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.handler.RequestMappingException;
import modelengine.fit.http.server.handler.SourceFetcher;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 处理注解 {@link RequestParam} 数据的是否必须和默认值逻辑。
 *
 * @author 曹嘉美
 * @since 2025-01-07
 */
public abstract class AbstractSourceFetcher implements SourceFetcher {
    private final boolean isRequired;
    private final String defaultValue;

    /**
     * 构造函数。
     *
     * @param isRequired 表示参数是否必须的 {@code boolean}，
     * @param defaultValue 表示参数的默认值的 {@link String}。
     */
    public AbstractSourceFetcher(boolean isRequired, String defaultValue) {
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
    }

    /**
     * 处理数据是否必须和获取默认值的逻辑。
     *
     * @param source 表示参数原始数据的 {@link Object}。
     * @return 表示处理后的数据的 {@link Object}。
     */
    protected Object resolveValue(Object source) {
        Object target = this.fetchDefaultValue(source);
        this.resolveRequired(target);
        return target;
    }

    private Object fetchDefaultValue(Object source) {
        if (this.defaultValue == null || Objects.equals(this.defaultValue, DefaultValue.VALUE)) {
            return source;
        }
        if (source == null) {
            return this.defaultValue;
        }
        if (source instanceof List) {
            List<Object> actualList = ObjectUtils.cast(source);
            if (actualList.isEmpty()) {
                return Collections.singletonList(this.defaultValue);
            }
        }
        return source;
    }

    private void resolveRequired(Object source) {
        if (this.isRequired) {
            notNull(source, () -> new RequestMappingException("The parameter is required."));
            if (source instanceof List) {
                notEmpty(ObjectUtils.cast(source), () -> new RequestMappingException("The parameter is required."));
            }
        }
    }
}
