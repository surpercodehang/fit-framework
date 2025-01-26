/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示泛服务的通信类型。
 *
 * @author 王成
 * @since 2023-11-17
 */
public enum CommunicationType {
    /**
     * 表示泛服务以同步方式通信。
     */
    SYNC("sync"),

    /**
     * 表示泛服务以异步方式通信。
     */
    ASYNC("async");

    /**
     * 表示默认的服务类型。
     */
    public static final CommunicationType DEFAULT = SYNC;

    private final String code;

    CommunicationType(String code) {
        this.code = code;
    }

    /**
     * 获取泛服务通信类型的编号。
     *
     * @return 表示类型编号的 {@link String}。
     */
    public String code() {
        return this.code;
    }

    /**
     * 获取指定编号的服务通信类型。
     *
     * @param code 表示通信类型编号的 {@link String}。
     * @return 表示指定编号的服务通信类型的 {@link CommunicationType}。
     */
    @Nonnull
    public static CommunicationType fromCode(String code) {
        for (CommunicationType value : CommunicationType.values()) {
            if (StringUtils.equalsIgnoreCase(value.code(), code)) {
                return value;
            }
        }
        return DEFAULT;
    }
}
