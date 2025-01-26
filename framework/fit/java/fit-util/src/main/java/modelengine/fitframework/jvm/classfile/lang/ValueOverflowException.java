/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.lang;

import modelengine.fitframework.util.StringUtils;

/**
 * 当发生值溢出时引发的异常。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public class ValueOverflowException extends RuntimeException {
    ValueOverflowException(String value, String minimum, String maximum) {
        super(StringUtils.format("The value is overflow. [value={0}, minimum={1}, maximum={2}]",
                value, minimum, maximum));
    }
}
