/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.log.console;

import modelengine.fitframework.util.StringUtils;

/**
 * 表示日志打印的颜色。
 *
 * @author 梁济时
 * @since 2022-11-14
 */
public enum ConsoleColor {
    BLACK(30),
    RED(31),
    GREEN(32),
    YELLOW(33),
    BLUE(34),
    PURPLE(35),
    AZURE(36),
    WHITE(37);

    private final int code;

    ConsoleColor(int code) {
        this.code = code;
    }

    /**
     * 将指定内容添加指定颜色进行输出显示。
     *
     * @param content 表示指定内容的 {@link String}。
     * @return 表示携带指定颜色的内容信息的 {@link String}。
     */
    public String format(String content) {
        return StringUtils.format("\033[{0}m{1}\033[0m", this.code, content);
    }
}
