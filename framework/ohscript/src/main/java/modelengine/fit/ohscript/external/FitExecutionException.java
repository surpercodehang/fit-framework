/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.external;

import modelengine.fit.ohscript.script.errors.ScriptExecutionException;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示 FIT 调用的异常。
 *
 * @author 季聿阶
 * @since 2023-12-18
 */
public class FitExecutionException extends ScriptExecutionException {
    /**
     * 表示泛服务的唯一标识。
     */
    private final String genericableId;

    /**
     * 表示泛服务实现的唯一标识。
     */
    private final String fitableId;

    /**
     * 通过泛服务唯一标识和错误信息来初始化 {@link FitExecutionException} 的新实例。
     *
     * @param genericableId 表示泛服务的唯一标识的 {@link String}。
     * @param message 表示错误信息的 {@link String}。
     */
    public FitExecutionException(String genericableId, String message) {
        this(genericableId, StringUtils.EMPTY, message);
    }

    /**
     * 通过泛服务唯一标识、错误信息和异常原因来初始化 {@link FitExecutionException} 的新实例。
     *
     * @param genericableId 表示泛服务的唯一标识的 {@link String}。
     * @param message 表示错误信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public FitExecutionException(String genericableId, String message, Throwable cause) {
        this(genericableId, StringUtils.EMPTY, message, cause);
    }

    /**
     * 构造一个新的 {@link FitExecutionException}，它关联一个泛服务和一个泛服务实现。
     *
     * @param genericableId 泛服务的唯一标识。
     * @param fitableId 泛服务实现的唯一标识。
     * @param message 描述异常的消息。
     */
    public FitExecutionException(String genericableId, String fitableId, String message) {
        super(message);
        this.genericableId = genericableId;
        this.fitableId = fitableId;
    }

    /**
     * 构造一个新的 {@link FitExecutionException}，它关联一个泛服务和一个泛服务实现，并指定了导致此异常的异常。
     *
     * @param genericableId 泛服务的唯一标识。
     * @param fitableId 泛服务实现的唯一标识。
     * @param message 描述异常的消息。
     * @param cause 导致此异常的异常。
     */
    public FitExecutionException(String genericableId, String fitableId, String message, Throwable cause) {
        super(message, cause);
        this.genericableId = genericableId;
        this.fitableId = fitableId;
    }

    /**
     * 获取异常关联的泛服务的唯一标识的。
     *
     * @return 表示异常关联的泛服务的唯一标识的 {@link String}。
     */
    public String getGenericableId() {
        return this.genericableId;
    }

    /**
     * 获取异常关联的泛服务实现的唯一标识。
     *
     * @return 表示异常关联的泛服务实现的唯一标识的 {@link String}。
     */
    public String getFitableId() {
        return this.fitableId;
    }
}
