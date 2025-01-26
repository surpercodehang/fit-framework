/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fit.http.protocol.MessageHeaders;
import modelengine.fitframework.model.MultiValueMap;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link MessageHeaders} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-07-06
 */
public class DefaultMessageHeaders implements ConfigurableMessageHeaders {
    private final MultiValueMap<String, String> headers = MultiValueMap.create(LinkedHashMap::new);

    @Override
    public List<String> names() {
        return Collections.unmodifiableList(new ArrayList<>(this.headers.keySet()));
    }

    @Override
    public boolean contains(String name) {
        String actualName = this.normalizeName(name);
        return this.headers.containsKey(actualName);
    }

    @Override
    public Optional<String> first(String name) {
        String actualName = this.normalizeName(name);
        return Optional.ofNullable(this.headers.getFirst(actualName));
    }

    @Override
    public String require(String name) {
        return this.first(name)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format("No specified header. [header={0}]",
                        name)));
    }

    @Override
    public List<String> all(String name) {
        String actualName = this.normalizeName(name);
        return Optional.ofNullable(this.headers.get(actualName)).orElseGet(Collections::emptyList);
    }

    @Override
    public int size() {
        return this.headers.size();
    }

    @Override
    public ConfigurableMessageHeaders add(String name, String header) {
        String actualName = this.normalizeName(name);
        if (StringUtils.isBlank(header)) {
            return this;
        }
        this.headers.add(actualName, header);
        return this;
    }

    @Override
    public ConfigurableMessageHeaders set(String name, String header) {
        String actualName = this.normalizeName(name);
        return this.set(actualName, Collections.singletonList(header));
    }

    @Override
    public ConfigurableMessageHeaders set(String name, List<String> headers) {
        String actualName = this.normalizeName(name);
        List<String> actualHeaders = ObjectUtils.getIfNull(headers, Collections::<String>emptyList)
                .stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(actualHeaders)) {
            this.clear(actualName);
        } else {
            this.headers.put(actualName, actualHeaders);
        }
        return this;
    }

    @Override
    public ConfigurableMessageHeaders clear(String name) {
        String actualName = this.normalizeName(name);
        this.headers.remove(actualName);
        return this;
    }

    /**
     * 将指定消息头的名字标准化。
     *
     * @param name 表示指定消息头的名字的 {@link String}。
     * @return 表示标准化后的消息头名字的 {@link String}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 或空白字符串时。
     */
    private String normalizeName(String name) {
        return notBlank(name, "The name of header cannot be blank.").toLowerCase(Locale.ROOT);
    }
}
