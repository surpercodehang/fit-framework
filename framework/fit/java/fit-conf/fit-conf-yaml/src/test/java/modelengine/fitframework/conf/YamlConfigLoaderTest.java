/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

/**
 * 表示 {@link YamlConfigLoader} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @author 李金绪
 * @since 2022-05-25
 */
@DisplayName("测试 YamlConfigLoader 类")
public class YamlConfigLoaderTest {
    private static final String RESOURCE_KEY = "test.yaml";

    private Resource resource;
    private InputStream resourceInputStream;

    @BeforeEach
    void setup() throws IOException {
        URL resourceUrl = YamlConfigLoaderTest.class.getClassLoader().getResource(RESOURCE_KEY);
        if (resourceUrl == null) {
            throw new IllegalStateException(StringUtils.format("Failed to lookup resource. [key={0}]", RESOURCE_KEY));
        }
        this.resourceInputStream = resourceUrl.openStream();
        this.resource = mock(Resource.class);
        when(this.resource.filename()).thenReturn(RESOURCE_KEY);
        when(this.resource.url()).thenReturn(resourceUrl);
        when(this.resource.read()).thenReturn(this.resourceInputStream);
        System.setProperty("hi", "嗨");
        System.setProperty("hello", "你好");
        System.setProperty("bye", "再见");
    }

    @AfterEach
    void teardown() throws IOException {
        this.resourceInputStream.close();
        System.clearProperty("hi");
        System.clearProperty("hello");
        System.clearProperty("bye");
    }

    @Test
    @DisplayName("当不是预期的扩展名时，加载失败")
    void shouldFailWhenExtensionIsUnexpected() {
        YamlConfigLoader loader = new YamlConfigLoader();
        when(resource.filename()).thenReturn("test.xml");
        ConfigLoadingResult result = loader.load(this.resource, null);
        assertThat(result.loaded()).isFalse();
    }

    @Test
    @DisplayName("当是预期的扩展名时，加载成功")
    void shouldLoadConfigWhenExtensionIsExpected() {
        YamlConfigLoader loader = new YamlConfigLoader();
        ConfigLoadingResult result = loader.load(this.resource, null);
        assertThat(result.loaded()).isTrue();
    }

    @Test
    @DisplayName("当配置不是一个对象时（如列表），抛出异常")
    void shouldThrowWhenConfigIsNotObject() throws IOException {
        YamlConfigLoader loader = new YamlConfigLoader();
        try (InputStream in = IoUtils.resource(YamlConfigLoaderTest.class.getClassLoader(), "list-config.yaml")) {
            when(this.resource.read()).thenReturn(in);
            ConfigLoadException cause =
                    catchThrowableOfType(() -> loader.load(this.resource, null), ConfigLoadException.class);
            String error = StringUtils.format("The content of config must be an object. [url={0}]", this.resource);
            assertThat(cause).hasMessage(error);
        }
    }

    @Test
    @DisplayName("当从资源的输入流中读取数据抛出 IO 异常时，抛出异常")
    void shouldThrowWhenIoExceptionOccursWhenReadFromInputStream() throws IOException {
        InputStream in = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException();
            }
        };
        when(this.resource.read()).thenReturn(in);
        YamlConfigLoader loader = new YamlConfigLoader();
        ConfigLoadException cause = catchThrowableOfType(() -> loader.load(this.resource), ConfigLoadException.class);
        String error = StringUtils.format("Failed to parse YAML from config resource. [url={0}]", this.resource);
        assertThat(cause).hasMessage(error);
    }

    @Test
    @DisplayName("应包含 YAML 的常规扩展名")
    void shouldContainYamlExtensions() {
        YamlConfigLoader loader = new YamlConfigLoader();
        Set<String> extensions = loader.extensions();
        assertThat(2).isEqualTo(extensions.size());
        assertThat(extensions.contains(".yaml")).isTrue();
        assertThat(extensions.contains(".yml")).isTrue();
    }

    @Test
    @DisplayName("当配置中包含占位符时，正确替换")
    void shouldOKWhenLoadWithPlaceHolder() throws IOException {
        YamlConfigLoader loader = new YamlConfigLoader();
        try (InputStream in = IoUtils.resource(YamlConfigLoaderTest.class.getClassLoader(), "test.yaml")) {
            when(this.resource.read()).thenReturn(in);
            Config config = loader.load(this.resource, null).config();
            assertThat(config.get("a.b", String.class)).isNull();
            assertThat(config.get("a.c", String.class)).isEqualTo("{\"2\" : \"3\"}");
            assertThat(config.get("a.d", String.class)).isEqualTo("你好 : 再见 : {ok}");
            assertThat(config.get("a.n", String.class)).isEqualTo("你好");
            assertThat(config.get("a.x", String.class)).isEqualTo("{\"name\" : \"bob\"}");
            assertThat(config.get("a.y", String.class)).isEqualTo("{\"1\" : 2, \"2\" : 你好}");
        }
    }
}
