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
    private final String genericableId;

    private final String fitableId;

    public FitExecutionException(String genericableId, String message) {
        this(genericableId, StringUtils.EMPTY, message);
    }

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
