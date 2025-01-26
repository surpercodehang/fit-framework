/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.MultiValueEntity;
import modelengine.fit.http.server.handler.MockHttpClassicServerRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link FormUrlEncodedEntityFetcher} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-15
 */
@DisplayName("测试 FormUrlEncodedEntityFetcher 类")
class FormUrlEncodedEntityFetcherTest {
    private final FormUrlEncodedEntityFetcher encodedEntityFetcher =
            new FormUrlEncodedEntityFetcher(ParamValue.custom().name("key").build());

    @Test
    @DisplayName("判断来源数据的常用格式是否是数组")
    void shouldReturnIsArrayAble() {
        final boolean isArrayAble = this.encodedEntityFetcher.isArrayAble();
        assertThat(isArrayAble).isTrue();
    }

    @Test
    @DisplayName("获取当前消息体获取器可以获取的消息体的类型")
    void shouldReturnEntityType() {
        final Class<? extends Entity> entityType = this.encodedEntityFetcher.entityType();
        assertThat(entityType).isEqualTo(MultiValueEntity.class);
    }

    @Test
    @DisplayName("从 Http 请求中获取数据")
    void shouldReturnFromRequestData() {
        final MockHttpClassicServerRequest serverRequest = new MockHttpClassicServerRequest();
        final MultiValueEntity valueEntity = mock(MultiValueEntity.class);
        final List<String> list = Collections.singletonList("value");
        when(valueEntity.all(anyString())).thenReturn(list);
        final Object fromRequest = this.encodedEntityFetcher.getFromRequest(serverRequest.getRequest(), valueEntity);
        assertThat(fromRequest).isEqualTo(list);
    }
}
