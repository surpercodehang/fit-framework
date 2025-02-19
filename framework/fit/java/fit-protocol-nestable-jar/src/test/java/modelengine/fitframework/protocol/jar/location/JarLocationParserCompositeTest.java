/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.protocol.jar.JarLocation;
import modelengine.fitframework.protocol.jar.JarLocationParser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link JarLocationParserComposite} 的单元测试。
 *
 * @author 高三海
 * @author 季聿阶
 * @since 2025-02-18
 */
@DisplayName("测试 JarLocationParserComposite")
public class JarLocationParserCompositeTest {
    @Test
    @DisplayName("当解析 JAR 的位置信息，包含不支持的子协议类型时，抛出异常")
    void givenNoSupportedParserWhenParseJarThenThrowException() {
        UnsupportedOperationException cause =
                catchThrowableOfType(() -> JarLocationParser.instance().parseJar("jar:test:/a!/"),
                        UnsupportedOperationException.class);
        assertThat(cause).hasMessage("No supported parser to parse JAR. [url=jar:test:/a!/]");
    }

    @Test
    @DisplayName("当解析 JAR 中记录的位置信息，包含不支持的子协议类型时，抛出异常")
    void givenNoSupportedParserWhenParseEntryThenThrowException() {
        UnsupportedOperationException cause =
                catchThrowableOfType(() -> JarLocationParser.instance().parseEntry("jar:test:/a!/"),
                        UnsupportedOperationException.class);
        assertThat(cause).hasMessage("No supported parser to parse JAR entry. [url=jar:test:/a!/]");
    }

    @Test
    @DisplayName("当解析 JAR 的位置信息，包含支持的子协议类型时，解析成功")
    void givenSupportedParserWhenParseJarThenThrowException() {
        JarLocationParser parser = new TestJarLocationParser();
        assertThatNoException().isThrownBy(() -> JarLocationParser.register(parser));
        JarLocation jar = JarLocationParser.instance().parseJar("jar:test:/a!/");
        assertThat(jar).isNotNull();
        assertThatNoException().isThrownBy(() -> JarLocationParser.unregister(parser));
    }
}
