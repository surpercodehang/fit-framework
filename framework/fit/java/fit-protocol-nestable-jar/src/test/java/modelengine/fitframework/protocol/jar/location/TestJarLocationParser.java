/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.location;

/**
 * 表示测试的解析器。
 *
 * @author 高三海
 * @author 季聿阶
 * @since 2025-02-18
 */
public class TestJarLocationParser extends AbstractJarLocationParser {
    private static final String SEPARATOR = "!/";
    private static final String SUPPORTED_PROTOCOL_PREFIX = "test:";

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
        return "file:" + toProcess.substring(SUPPORTED_PROTOCOL_PREFIX.length());
    }
}
