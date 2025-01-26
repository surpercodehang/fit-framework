/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 为 {@link DefaultNamedEntity} 提供单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-22
 */
@DisplayName("测试 DefaultNamedEntity 类")
class DefaultNamedEntityTest {
    private DefaultNamedEntity namedEntity;
    private Entity entity;
    private HttpMessage httpMessage;

    @BeforeEach
    void setup() {
        this.httpMessage = mock(HttpMessage.class);
        this.entity = mock(Entity.class);
        this.namedEntity = new DefaultNamedEntity(this.httpMessage, "generic", this.entity);
    }

    @Test
    @DisplayName("获取消息体数据的名字")
    void shouldReturnEntityName() {
        final String name = this.namedEntity.name();
        assertThat(name).isEqualTo("generic");
    }

    @Test
    @DisplayName("获取真正的消息体数据内容")
    void shouldReturnEntity() {
        final Entity entityData = this.namedEntity.entity();
        assertThat(entityData).isEqualTo(this.entity);
    }


    @Test
    @DisplayName("获取实体所属的 Http 消息")
    void shouldReturnHttpMessage() {
        final HttpMessage message = this.namedEntity.belongTo();
        assertThat(message).isEqualTo(this.httpMessage);
    }
}
