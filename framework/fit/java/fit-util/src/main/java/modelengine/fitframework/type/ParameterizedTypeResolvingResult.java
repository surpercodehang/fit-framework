/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.type;

import modelengine.fitframework.type.support.ParameterizedTypeResolvingFailureResult;
import modelengine.fitframework.type.support.ParameterizedTypeResolvingSuccessResult;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 为参数化类型提供解析结果。
 *
 * @author 梁济时
 * @since 2022-07-05
 */
public interface ParameterizedTypeResolvingResult {
    /**
     * 获取一个值，该值指示是否解析成功。
     *
     * @return 若解析成功，则为 {@code true}；否则为 {@code false}。
     */
    boolean resolved();

    /**
     * 获取解析到的类型参数。
     *
     * @return 表示类型参数列表的 {@link List}{@code <}{@link Type}{@code >}。
     */
    List<Type> parameters();

    /**
     * 获取表示失败的解析结果。
     *
     * @return 表示解析失败的结果的 {@link ParameterizedTypeResolvingResult}。
     */
    static ParameterizedTypeResolvingResult failure() {
        return ParameterizedTypeResolvingFailureResult.INSTANCE;
    }

    /**
     * 创建表示成功的解析结果。
     *
     * @param parameters 表示解析到的类型参数的列表的 {@link List}{@code <}{@link Type}{@code >}。
     * @return 表示解析成功的结果的 {@link ParameterizedTypeResolvingResult}。
     */
    static ParameterizedTypeResolvingResult success(List<Type> parameters) {
        return new ParameterizedTypeResolvingSuccessResult(parameters);
    }
}
