/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fitframework.broker.Endpoint;
import modelengine.fitframework.broker.Format;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 表示 {@link Target} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-03-06
 */
public class DefaultTarget implements Target {
    private final String workerId;
    private final String host;
    private final String environment;
    private final List<Endpoint> endpoints;
    private final List<Format> formats;
    private final Map<String, String> extensions;

    private DefaultTarget(String workerId, String host, String environment, List<Endpoint> endpoints,
            List<Format> formats, Map<String, String> extensions) {
        this.workerId = workerId;
        this.host = host;
        this.environment = environment;
        this.endpoints = getIfNull(endpoints, Collections::emptyList);
        this.formats = getIfNull(formats, Collections::emptyList);
        this.extensions = getIfNull(extensions, Collections::emptyMap);
    }

    @Override
    public String workerId() {
        return this.workerId;
    }

    @Override
    public String host() {
        return this.host;
    }

    @Override
    public String environment() {
        return this.environment;
    }

    @Override
    public List<Endpoint> endpoints() {
        return this.endpoints;
    }

    @Override
    public List<Format> formats() {
        return this.formats;
    }

    @Override
    public Map<String, String> extensions() {
        return this.extensions;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || this.getClass() != another.getClass()) {
            return false;
        }
        DefaultTarget that = cast(another);
        return Objects.equals(this.workerId, that.workerId) && Objects.equals(this.host, that.host) && Objects.equals(
                this.environment,
                that.environment) && Objects.equals(this.endpoints, that.endpoints) && Objects.equals(this.formats,
                that.formats) && Objects.equals(this.extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.workerId, this.host, this.environment, this.endpoints, this.formats, this.extensions);
    }

    @Override
    public String toString() {
        return StringUtils.format(
                "/{\"workerId\": \"{0}\", \"host\": \"{1}\", \"environment\": \"{2}\", " + "\"endpoints\": "
                        + "{3}, \"formats\": {4}, \"extensions\": {5}/}",
                this.workerId,
                this.host,
                this.environment,
                this.endpoints,
                this.formats,
                this.extensions);
    }

    /**
     * 表示 {@link Target.Builder} 的默认实现。
     */
    public static class Builder implements Target.Builder {
        private String workerId;
        private String host;
        private String environment;
        private List<Endpoint> endpoints;
        private List<Format> formats;
        private Map<String, String> extensions;

        /**
         * 使用已知的服务地址信息初始化 {@link DefaultTarget.Builder} 类的新实例。
         *
         * @param value 表示已知的服务地址信息的 {@link Target}。
         */
        public Builder(Target value) {
            if (value != null) {
                this.workerId = value.workerId();
                this.host = value.host();
                this.environment = value.environment();
                this.endpoints = value.endpoints();
                this.formats = value.formats();
                this.extensions = value.extensions();
            }
        }

        @Override
        public Target.Builder workerId(String workerId) {
            this.workerId = workerId;
            return this;
        }

        @Override
        public Target.Builder host(String host) {
            this.host = host;
            return this;
        }

        @Override
        public Target.Builder environment(String environment) {
            this.environment = environment;
            return this;
        }

        @Override
        public Target.Builder endpoints(List<Endpoint> endpoints) {
            this.endpoints = endpoints;
            return this;
        }

        @Override
        public Target.Builder formats(List<Format> formats) {
            this.formats = formats;
            return this;
        }

        @Override
        public Target.Builder extensions(Map<String, String> extensions) {
            this.extensions = extensions;
            return this;
        }

        @Override
        public Target build() {
            return new DefaultTarget(this.workerId,
                    this.host,
                    this.environment,
                    this.endpoints,
                    this.formats,
                    this.extensions);
        }
    }
}
