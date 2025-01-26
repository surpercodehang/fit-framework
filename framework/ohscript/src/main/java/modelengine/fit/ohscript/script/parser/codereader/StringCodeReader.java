/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.codereader;

import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 读入string类型的源码
 *
 * @since 1.0
 */
public class StringCodeReader implements CodeReader {
    private final List<String> lines;

    private final int cursor = 0;

    /**
     * 构造函数
     *
     * @param code 源码字符串
     */
    public StringCodeReader(String code) {
        this.lines = Stream.of(code.split("\\r\\n|\\r|\\n"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    @Override
    public String readLine() {
        if (this.lines.size() == 0) {
            return "$";
        } else {
            String line = this.lines.remove(0);
            if (line.endsWith("{")) {
                line += this.readLine();
            }
            return line;
        }
    }

    @Override
    public void close() {
    }
}
