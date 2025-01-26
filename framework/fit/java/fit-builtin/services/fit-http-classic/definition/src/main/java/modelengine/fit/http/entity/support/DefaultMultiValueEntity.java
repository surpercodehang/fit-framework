/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity.support;

import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.MultiValueEntity;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.model.MultiValueMap;
import modelengine.fitframework.model.support.DefaultMultiValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示 {@link MultiValueEntity} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-08-05
 */
public class DefaultMultiValueEntity extends AbstractEntity implements MultiValueEntity {
    private final MultiValueMap<String, String> values;

    /**
     * 创建多值格式的消息体数据的实现对象、
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param values 表示指定的键值对映射的 {@link MultiValueMap}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    public DefaultMultiValueEntity(HttpMessage httpMessage, MultiValueMap<String, String> values) {
        super(httpMessage);
        this.values = getIfNull(values, DefaultMultiValueMap::new);
    }

    @Override
    public List<String> keys() {
        return Collections.unmodifiableList(new ArrayList<>(this.values.keySet()));
    }

    @Override
    public Optional<String> first(String key) {
        return Optional.ofNullable(this.values.getFirst(key));
    }

    @Override
    public List<String> all(String key) {
        return Optional.ofNullable(this.values.get(key)).orElseGet(Collections::emptyList);
    }

    @Override
    public int size() {
        return this.values.size();
    }

    @Override
    public String toString() {
        List<String> keyValues = new ArrayList<>();
        for (String key : this.keys()) {
            List<String> matchedValues = this.all(key);
            keyValues.add(matchedValues.stream().map(value -> key + '=' + value).collect(Collectors.joining("&")));
        }
        return String.join("&", keyValues);
    }

    @Nonnull
    @Override
    public MimeType resolvedMimeType() {
        return MimeType.APPLICATION_X_WWW_FORM_URLENCODED;
    }
}
