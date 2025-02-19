/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.location;

import modelengine.fitframework.protocol.jar.JarLocationParser;

/**
 * 表示 {@link JarLocationParser} 的标准解析器。
 *
 * @author 高三海
 * @author 季聿阶
 * @since 2025-02-18
 */
class StandardJarLocationParser extends AbstractJarLocationParser {
    static final JarLocationParser INSTANCE = new StandardJarLocationParser();

    private static final String SEPARATOR = "!/";
    private static final String SUPPORTED_PROTOCOL_PREFIX = "file:";

    private StandardJarLocationParser() {}

    @Override
    protected boolean isSupported(String url, int start, int stop) {
        return url.regionMatches(true, start, SUPPORTED_PROTOCOL_PREFIX, 0, SUPPORTED_PROTOCOL_PREFIX.length());
    }

    @Override
    protected String getSeparator() {
        return SEPARATOR;
    }

    @Override
    protected String process(String toProcess) {
        return toProcess;
    }
}
