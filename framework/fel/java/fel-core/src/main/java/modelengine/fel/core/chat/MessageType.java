/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.core.chat;

/**
 * 表示消息类型的枚举。
 *
 * @author 易文渊
 * @since 2024-04-16
 */
public enum MessageType {
    /**
     * 系统消息。
     */
    SYSTEM("system"),

    /**
     * 人类消息。
     */
    HUMAN("human"),

    /**
     * 人工智能消息。
     */
    AI("ai"),

    /**
     * 工具消息。
     */
    TOOL("tool");

    private final String role;

    MessageType(String role) {
        this.role = role;
    }

    /**
     * 获取消息类型代表的角色。
     *
     * @return 表示角色的 {@link String}。
     */
    public String getRole() {
        return role;
    }
}