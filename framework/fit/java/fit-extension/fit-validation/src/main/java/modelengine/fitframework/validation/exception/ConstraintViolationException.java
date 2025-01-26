/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.exception;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.validation.ConstraintViolation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表示校验失败的异常。
 *
 * @author 邬涨财
 * @since 2023-05-18
 */
public class ConstraintViolationException extends RuntimeException {
    private final List<ConstraintViolation> violations;

    /**
     * 表示创建一个 {@link ConstraintViolationException} 的新实例。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param violations 表示约束校验失败的数据类列表的 {@link List}{@code <}{@link ConstraintViolation}{@code >}。
     */
    public ConstraintViolationException(String message, List<ConstraintViolation> violations) {
        super(message);
        this.violations = nullIf(violations, Collections.emptyList());
    }

    /**
     * 表示创建一个 {@link ConstraintViolationException} 的新实例。
     *
     * @param violations 表示约束校验失败的数据类列表的 {@link List}{@code <}{@link ConstraintViolation}{@code >}。
     */
    public ConstraintViolationException(List<ConstraintViolation> violations) {
        this(buildMessage(violations), violations);
    }

    private static String buildMessage(List<ConstraintViolation> violations) {
        if (CollectionUtils.isEmpty(violations)) {
            return StringUtils.EMPTY;
        }
        return violations.stream()
                .filter(Objects::nonNull)
                .map(ConstraintViolation::message)
                .collect(Collectors.joining(", "));
    }

    /**
     * 获取约束校验失败的数据类列表信息。
     *
     * @return 表示约束校验失败的数据类列表信息的 {@link List}{@code <}{@link ConstraintViolation}{@code >}。
     */
    public List<ConstraintViolation> getViolations() {
        return this.violations;
    }
}