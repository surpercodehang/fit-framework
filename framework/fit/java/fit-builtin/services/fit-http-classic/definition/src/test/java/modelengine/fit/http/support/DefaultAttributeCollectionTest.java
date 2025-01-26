/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 * 为 {@link DefaultAttributeCollection} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-02-20
 */
@DisplayName("测试 DefaultAttributeCollection 类")
public class DefaultAttributeCollectionTest {
    private final DefaultAttributeCollection defaultAttributeCollection = new DefaultAttributeCollection();

    @BeforeEach
    void setup() {
        this.defaultAttributeCollection.set("testName-1", "testValue-1");
        this.defaultAttributeCollection.set("testName-2", "testValue-2");
    }

    @Test
    @DisplayName("测试 get() 方法，获取数据成功")
    void invokeGetMethodThenGetDataSuccessfully() {
        Optional<Object> objectOptional = this.defaultAttributeCollection.get("testName-1");
        assertThat(objectOptional).isPresent().get().isEqualTo("testValue-1");
    }

    @Test
    @DisplayName("测试移除方法，给定有效数据，移除数据成功")
    void givenValidDataWhenInvokeRemoveMethodThenMoveDataSuccessfully() {
        this.defaultAttributeCollection.remove("testName-2");
        Optional<Object> objectOptional = this.defaultAttributeCollection.get("testName-2");
        assertThat(objectOptional).isNotPresent();
        objectOptional = this.defaultAttributeCollection.get("testName-1");
        assertThat(objectOptional).isPresent();
    }

    @Test
    @DisplayName("测试 name() 方法，返回值与给定值相等")
    void invokeNameMethodThenReturnIsEqualsToTheGivenValue() {
        List<String> names = this.defaultAttributeCollection.names();
        assertThat(names).hasSize(2);
    }
}
