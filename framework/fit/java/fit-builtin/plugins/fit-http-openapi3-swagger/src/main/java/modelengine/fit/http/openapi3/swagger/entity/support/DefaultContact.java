/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.openapi3.swagger.entity.Contact;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link Contact} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public class DefaultContact implements Contact {
    private final String name;
    private final String url;
    private final String email;

    private DefaultContact(String name, String url, String email) {
        this.name = notBlank(name, "The contact name cannot be blank.");
        this.url = url;
        this.email = email;
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
    public String email() {
        return this.email;
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("name", this.name);
        if (StringUtils.isNotBlank(this.url)) {
            builder.put("url", this.url);
        }
        if (StringUtils.isNotBlank(this.email)) {
            builder.put("email", this.email);
        }
        return builder.build();
    }

    /**
     * 表示 {@link Contact.Builder} 的默认实现。
     */
    public static class Builder implements Contact.Builder {
        private String name;
        private String url;
        private String email;

        @Override
        public Contact.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Contact.Builder url(String url) {
            this.url = url;
            return this;
        }

        @Override
        public Contact.Builder email(String email) {
            this.email = email;
            return this;
        }

        @Override
        public Contact build() {
            return new DefaultContact(this.name, this.url, this.email);
        }
    }
}
