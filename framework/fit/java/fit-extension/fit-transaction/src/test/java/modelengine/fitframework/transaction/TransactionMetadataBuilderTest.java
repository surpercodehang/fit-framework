/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * 为 {@link TransactionMetadata} 提供单元测试。
 *
 * @author 梁济时
 * @since 2022-08-27
 */
@DisplayName("测试 TransactionMetadata 的构建程序")
class TransactionMetadataBuilderTest {
    @Test
    @DisplayName("构建的事务元数据，包含正确的信息")
    void should_build_metadata_with_correct_data() {
        final String name = "test-transaction";
        final TransactionPropagationPolicy propagation = TransactionPropagationPolicy.NESTED;
        final TransactionIsolationLevel isolation = TransactionIsolationLevel.SERIALIZABLE;
        final int timeout = 100;
        final boolean readonly = true;
        TransactionMetadata metadata = TransactionMetadata.custom()
                .name(name)
                .propagation(propagation)
                .isolation(isolation)
                .timeout(timeout)
                .readonly(readonly)
                .build();
        assertNotNull(metadata);
        assertEquals(name, metadata.name());
        assertEquals(propagation, metadata.propagation());
        assertEquals(isolation, metadata.isolation());
        assertEquals(timeout, metadata.timeout());
        assertEquals(readonly, metadata.readonly());
    }

    @Test
    @DisplayName("应返回包装了默认值的元数据")
    void should_return_metadata_with_default() {
        TransactionMetadata metadata = Mockito.mock(TransactionMetadata.class);
        when(metadata.name()).thenReturn(null);
        when(metadata.propagation()).thenReturn(null);
        when(metadata.isolation()).thenReturn(null);
        when(metadata.timeout()).thenReturn(0);
        when(metadata.readonly()).thenReturn(false);

        metadata = TransactionMetadata.withDefault(metadata);

        assertNotNull(metadata);
        assertEquals(TransactionMetadata.DEFAULT_NAME, metadata.name());
        assertEquals(TransactionPropagationPolicy.REQUIRED, metadata.propagation());
        assertEquals(TransactionIsolationLevel.READ_COMMITTED, metadata.isolation());
        assertEquals(0, metadata.timeout());
        assertFalse(metadata.readonly());
    }

    @Test
    @DisplayName("未提供元数据时返回默认的元数据")
    void should_return_default_metadata() {
        TransactionMetadata metadata = TransactionMetadata.withDefault(null);

        assertNotNull(metadata);
        assertEquals(TransactionMetadata.DEFAULT_NAME, metadata.name());
        assertEquals(TransactionPropagationPolicy.REQUIRED, metadata.propagation());
        assertEquals(TransactionIsolationLevel.READ_COMMITTED, metadata.isolation());
        assertEquals(0, metadata.timeout());
        assertFalse(metadata.readonly());
    }
}
