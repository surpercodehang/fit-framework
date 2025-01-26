/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.notBlank;

import java.util.LinkedList;

/**
 * 环境的工具类。
 *
 * @author 季聿阶
 * @since 2023-05-18
 */
public class EnvironmentUtils {
    private static final char ENVIRONMENT_SEQUENCE_SEPARATOR = ',';

    /**
     * 根据配置的环境调用链和环境标来构建完整的环境调用链。
     *
     * @param environmentSequence 表示配置的环境调用链的 {@link String}。通过 {@code ','} 进行分隔。
     * @param environment 表示配置的环境标的 {@link String}。
     * @return 表示构建完整的环境调用链的 {@link LinkedList}{@code <}{@link String}{@code >}。
     */
    public static LinkedList<String> buildEnvironmentSequence(String environmentSequence, String environment) {
        notBlank(environment, "The environment cannot be blank.");
        notBlank(environmentSequence, "The environment sequence cannot be blank.");
        LinkedList<String> environmentPrioritySequence =
                new LinkedList<>(StringUtils.splitToList(environmentSequence, ENVIRONMENT_SEQUENCE_SEPARATOR));
        if (!environmentPrioritySequence.contains(environment)) {
            // 环境链中要是没有自身环境标，则将自身环境标添加到环境链首部
            environmentPrioritySequence.addFirst(environment);
        }
        return environmentPrioritySequence;
    }
}
