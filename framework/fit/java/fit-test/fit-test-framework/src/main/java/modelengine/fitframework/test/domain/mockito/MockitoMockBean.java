/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.mockito;

import modelengine.fitframework.test.domain.resolver.MockBean;

import org.mockito.Mockito;

import java.lang.reflect.Field;

/**
 * Mockito 的 BeanMock 工具类。
 *
 * @author 邬涨财
 * @since 2023-01-31
 */
public class MockitoMockBean implements MockBean {
    /**
     * 根据字段获得实例对象。
     *
     * @param field 表示字段的 {@link Field}。
     * @return 表示实例对象的 {@link Object}。
     */
    @Override
    public Object getBean(Field field) {
        return Mockito.mock(field.getType());
    }
}
