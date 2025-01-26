/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.type.support;

import modelengine.fitframework.type.ParameterizedTypeResolvingResult;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link ParameterizedTypeResolvingResult} 的失败的解析结果。
 *
 * @author 梁济时
 * @since 2020-10-29
 */
public class ParameterizedTypeResolvingFailureResult implements ParameterizedTypeResolvingResult {
    /**
     * 提供一个静态的 {@link ParameterizedTypeResolvingResult} 的实例。
     */
    public static final ParameterizedTypeResolvingResult INSTANCE = new ParameterizedTypeResolvingFailureResult();

    private ParameterizedTypeResolvingFailureResult() {}

    @Override
    public boolean resolved() {
        return false;
    }

    @Override
    public List<Type> parameters() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "unresolvable";
    }
}
