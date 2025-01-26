/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.convert;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.convert.Converter;

/**
 * 表示将 {@link StringValue} 的值向 {@link IntValue} 转换的程序。
 *
 * @author 梁济时
 * @since 2023-01-28
 */
public class AnnotationValueConverter implements Converter {
    @Override
    public Object convert(Object value) {
        return Integer.parseInt(ObjectUtils.cast(value));
    }
}
