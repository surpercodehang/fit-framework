/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;

/**
 * 注册中心地址监听模式。
 *
 * @author 张浩亮
 * @since 2021-04-20
 */
public enum RegistryListenerMode {
    /** 仅限拉取的模式。 */
    PULL("pull"),

    /** 仅限推送的模式。 */
    PUSH("push"),

    /** 同时拉取和推送的模式。 */
    PUSH_AND_PULL("push-and-pull");

    private final String mode;

    RegistryListenerMode(String mode) {
        this.mode = notBlank(mode, "The listener mode cannot be blank.");
    }

    /**
     * 判断是否启用拉取模式。
     *
     * @return 启用拉取模式，则返回 {@code true}，否则，返回 {@code false}。
     */
    public boolean isPullEnabled() {
        return this == PUSH_AND_PULL || this == PULL;
    }

    /**
     * 判断是否启用推送模式。
     *
     * @return 启用推送模式，则返回 {@code true}，否则，返回 {@code false}。
     */
    public boolean isPushEnabled() {
        return this == PUSH_AND_PULL || this == PUSH;
    }

    /**
     * 将字符串监听模式转换为枚举常量。
     *
     * @param mode 表示注册中心的监听模式的 {@link String}。
     * @return 表示注册中心的监听模式枚举的 {@link RegistryListenerMode}。
     */
    public static RegistryListenerMode fromMode(String mode) {
        return Arrays.stream(RegistryListenerMode.values())
                .filter(registryListenerMode -> StringUtils.equals(registryListenerMode.mode, mode))
                .findFirst()
                .orElse(null);
    }
}
