/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.support.DefaultExceptionInfo;
import modelengine.fitframework.serialization.ResponseMetadata;
import modelengine.fitframework.serialization.tlv.TlvUtils;

import java.util.Map;

/**
 * 表示异常的信息。
 *
 * @author 何天放
 * @since 2024-05-11
 */
public interface ExceptionInfo {
    /**
     * 获取服务的唯一标识。
     *
     * @return 表示服务唯一标识的 {@link String}。
     */
    String genericableId();

    /**
     * 获取服务实现的唯一标识。
     *
     * @return 表示服务实现唯一标识的 {@link String}。
     */
    String fitableId();

    /**
     * 获取异常状态码。
     *
     * @return 表示状态码的 {@code int}。
     */
    int code();

    /**
     * 获取异常的消息。
     *
     * @return 表示异常消息的 {@link String}。
     */
    String message();

    /**
     * 获取异常的属性集。
     *
     * @return 表示异常属性集的 {@link String}。
     */
    Map<String, String> properties();

    /**
     * 通过各个属性构建异常信息。
     *
     * @param genericableId 表示服务唯一标识的 {@link String}。
     * @param fitableId 表示服务实现唯一标识的 {@link String}。
     * @param code 表示状态码的 {@code int}。
     * @param message 表示异常消息的 {@link String}。
     * @param properties 表示异常属性集的 {@link String}。
     * @return 表示异常信息的 {@link ExceptionInfo}。
     */
    static ExceptionInfo create(String genericableId, String fitableId, int code, String message,
            Map<String, String> properties) {
        return new DefaultExceptionInfo(genericableId, fitableId, code, message, properties);
    }

    /**
     * 通过泛服务实现对象和返回值元数据构建异常信息。
     *
     * @param fitable 表示泛服务实现对象的 {@link Fitable}。
     * @param responseMetadata 表示返回值元数据的 {@link ResponseMetadata}。
     * @return 表示异常信息的 {@link ExceptionInfo}。
     */
    static ExceptionInfo fromFitableAndResponseMetadata(Fitable fitable, ResponseMetadata responseMetadata) {
        notNull(fitable, "The fitable cannot be null.");
        notNull(responseMetadata, "The response metadata cannot be null.");
        Map<String, String> properties = TlvUtils.getExceptionProperties(responseMetadata.tagValues());
        return new DefaultExceptionInfo(fitable.genericable().id(),
                fitable.id(),
                responseMetadata.code(),
                responseMetadata.message(),
                properties);
    }
}