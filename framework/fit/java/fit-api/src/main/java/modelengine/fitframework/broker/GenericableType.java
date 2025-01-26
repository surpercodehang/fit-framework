/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import modelengine.fitframework.util.StringUtils;

import java.util.EnumSet;
import java.util.Optional;

/**
 * 表示泛服务的类型。
 *
 * @author 梁济时
 * @since 2021-11-22
 */
public enum GenericableType {
    /**
     * 表示泛化服务是一个北向接口。
     */
    API("API"),

    /**
     * 表示泛化服务是一个南向接口。
     */
    SPI("SPI");

    /**
     * 表示默认的服务类型。
     */
    public static final GenericableType DEFAULT = API;

    private final String code;

    GenericableType(String code) {
        this.code = code;
    }

    /**
     * 获取泛化服务类型的编号。
     *
     * @return 表示类型编号的 {@link String}。
     */
    public String code() {
        return this.code;
    }

    /**
     * 获取指定编号的泛化服务类型。
     *
     * @param code 表示类型编号的 {@link String}。
     * @return 若存在该编号的泛化服务类型，则为表示该类型的 {@link Optional}{@code <}{@link GenericableType}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    public static Optional<GenericableType> fromCode(String code) {
        EnumSet<GenericableType> values = EnumSet.allOf(GenericableType.class);
        for (GenericableType value : values) {
            if (StringUtils.equalsIgnoreCase(value.code(), code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
