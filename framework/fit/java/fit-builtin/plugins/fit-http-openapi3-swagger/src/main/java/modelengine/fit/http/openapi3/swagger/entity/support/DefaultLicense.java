/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.openapi3.swagger.entity.License;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link License} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public class DefaultLicense implements License {
    private final String name;
    private final String url;

    private DefaultLicense(String name, String url) {
        this.name = notBlank(name, "The license name cannot be blank.");
        this.url = url;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String url() {
        return this.url;
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("name", this.name);
        if (StringUtils.isNotBlank(this.url)) {
            builder.put("url", this.url);
        }
        return builder.build();
    }

    /**
     * 表示 {@link License.Builder} 的默认实现。
     */
    public static class Builder implements License.Builder {
        private String name;
        private String url;

        @Override
        public License.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public License.Builder url(String url) {
            this.url = url;
            return this;
        }

        @Override
        public License build() {
            return new DefaultLicense(this.name, this.url);
        }
    }
}
