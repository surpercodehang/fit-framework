/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.external;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 用于测试的 FIT 泛服务接口。
 *
 * @author 季聿阶
 * @since 2023-11-01
 */
public interface SingleGenericable {
    /**
     * 表示测试的方法 4。
     *
     * @param param 表示第一个测试参数的
     * {@link List}{@code <}{@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}{@code >}。
     * @return 表示测试结果的
     * {@link List}{@code <}{@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}{@code >}。
     */
    @Genericable(id = "m")
    List<Map<String, Object>> handleTask(List<Map<String, Object>> param);
}
