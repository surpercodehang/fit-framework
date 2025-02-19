/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.starter.spring;

import modelengine.fitframework.protocol.jar.JarLocationParser;
import modelengine.fitframework.protocol.jar.location.AbstractJarLocationParser;

/**
 * 表示 {@link JarLocationParser} 的 SpringBoot 的实现。
 *
 * @author 高三海
 * @author 季聿阶
 * @since 2025-02-18
 */
public class SpringJarLocationParser extends AbstractJarLocationParser {
    static final JarLocationParser INSTANCE = new SpringJarLocationParser();

    private static final String SUPPORTED_PROTOCOL_NESTED = "nested";
    private static final String PROTOCOL_FILE = "file";
    private static final String SEPARATOR = "/!";

    private SpringJarLocationParser() {}

    @Override
    protected boolean isSupported(String url, int start, int stop) {
        return url.regionMatches(true,
                start,
                SUPPORTED_PROTOCOL_NESTED + ":",
                0,
                SUPPORTED_PROTOCOL_NESTED.length() + 1);
    }

    @Override
    protected String getSeparator() {
        return SEPARATOR;
    }

    @Override
    protected String process(String toProcess) {
        return PROTOCOL_FILE + toProcess.substring(SUPPORTED_PROTOCOL_NESTED.length());
    }
}
