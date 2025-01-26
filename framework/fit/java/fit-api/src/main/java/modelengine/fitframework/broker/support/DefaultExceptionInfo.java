/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.broker.ExceptionInfo;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 表示 {@link ExceptionInfo} 的默认实现。
 *
 * @author 何天放
 * @since 2024-05-11
 */
public class DefaultExceptionInfo implements ExceptionInfo {
    private final String genericableId;
    private final String fitableId;
    private final int code;
    private final String message;
    private final Map<String, String> properties;

    /**
     * 通过泛服务标识等信息构建默认实现的 {@link ExceptionInfo}
     *
     * @param genericableId 表示服务唯一标识的 {@link String}。
     * @param fitableId 表示服务实现唯一标识的 {@link String}。
     * @param code 表示状态码的 {@code int}。
     * @param message 表示异常消息的 {@link String}。
     * @param properties 表示异常属性集的 {@link String}。
     */
    public DefaultExceptionInfo(String genericableId, String fitableId, int code, String message,
            Map<String, String> properties) {
        this.genericableId = notBlank(genericableId, "The genericable id cannot be blank.");
        this.fitableId = notBlank(fitableId, "The fitable id cannot be blank.");
        this.code = code;
        this.message = StringUtils.blankIf(message, StringUtils.EMPTY);
        if (MapUtils.isEmpty(properties)) {
            this.properties = new HashMap<>();
        } else {
            this.properties = properties;
        }
    }

    @Override
    public String genericableId() {
        return this.genericableId;
    }

    @Override
    public String fitableId() {
        return this.fitableId;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public Map<String, String> properties() {
        return this.properties;
    }
}