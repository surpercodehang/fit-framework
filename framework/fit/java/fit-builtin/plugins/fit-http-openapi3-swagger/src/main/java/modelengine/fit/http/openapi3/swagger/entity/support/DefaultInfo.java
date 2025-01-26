/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.openapi3.swagger.entity.Contact;
import modelengine.fit.http.openapi3.swagger.entity.Info;
import modelengine.fit.http.openapi3.swagger.entity.License;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link Info} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public class DefaultInfo implements Info {
    private final String title;
    private final String summary;
    private final String description;
    private final Contact contact;
    private final License license;
    private final String version;

    private DefaultInfo(String title, String summary, String description, Contact contact, License license,
            String version) {
        this.title = notBlank(title, "The title cannot be blank.");
        this.summary = summary;
        this.description = description;
        this.contact = contact;
        this.license = license;
        this.version = notBlank(version, "The version cannot be blank.");
    }

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public String summary() {
        return this.summary;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public Contact contact() {
        return this.contact;
    }

    @Override
    public License license() {
        return this.license;
    }

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder =
                MapBuilder.<String, Object>get().put("title", this.title).put("version", this.version);
        if (StringUtils.isNotBlank(this.summary)) {
            builder.put("summary", this.summary);
        }
        if (StringUtils.isNotBlank(this.description)) {
            builder.put("description", this.description);
        }
        if (this.contact != null) {
            builder.put("contact", this.contact.toJson());
        }
        if (this.license != null) {
            builder.put("license", this.license.toJson());
        }
        return builder.build();
    }

    /**
     * 表示 {@link Info.Builder} 的默认实现。
     */
    public static class Builder implements Info.Builder {
        private String title;
        private String summary;
        private String description;
        private Contact contact;
        private License license;
        private String version;

        @Override
        public Info.Builder title(String title) {
            this.title = title;
            return this;
        }

        @Override
        public Info.Builder summary(String summary) {
            this.summary = summary;
            return this;
        }

        @Override
        public Info.Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public Info.Builder contact(Contact contact) {
            this.contact = contact;
            return this;
        }

        @Override
        public Info.Builder license(License license) {
            this.license = license;
            return this;
        }

        @Override
        public Info.Builder version(String version) {
            this.version = version;
            return this;
        }

        @Override
        public Info build() {
            return new DefaultInfo(this.title,
                    this.summary,
                    this.description,
                    this.contact,
                    this.license,
                    this.version);
        }
    }
}
