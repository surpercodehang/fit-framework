/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.parameterization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 为 {@link ParameterizedStringResolver} 提供单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class ParameterizedStringResolverTest {
    /** 表示用以测试的参数前缀。 */
    private static final String PREFIX = "${";

    /** 表示用以测试的参数后缀。 */
    private static final String SUFFIX = "}";

    /** 表示用以测试的转义符。 */
    private static final char ESCAPE_CHARACTER = '/';

    /** 表示用以测试的空白转义符。 */
    private static final char EMPTY_ESCAPE_CHARACTER = '\0';

    /** 表示用以测试的源字符串。 */
    private static final String SOURCE_STRING = "Variable test. [variable=${/variable}]/${";

    /** 表示用以测试的解析后的字符串。 */
    private static final String FORMATTED_STRING = "Variable test. [variable=myVariable]${";

    /** 表示用以测试的无效的前缀。 */
    private static final String ILLEGAL_PREFIX = "/${";

    /** 表示用以测试的无效的后缀。 */
    private static final String ILLEGAL_SUFFIX = "/}";

    /** 表示用以测试的变量的名称。 */
    private static final String VARIABLE_NAME = "variable";

    /** 表示用以测试的变量的值。 */
    private static final String VARIABLE_VALUE = "myVariable";

    /** 表示变量的位置。 */
    private static final int VARIABLE_POSITION = 25;

    /** 表示用以测试的变量的映射。 */
    private static final Map<String, Object> VARIABLES = Collections.singletonMap(VARIABLE_NAME, VARIABLE_VALUE);

    private static ParameterizedString getParameterizedString(String format) {
        ParameterizedStringResolver resolver = ParameterizedStringResolver.create(PREFIX, SUFFIX, ESCAPE_CHARACTER);
        ParameterizedString parameterizedString = resolver.resolve(format);
        assertNotNull(parameterizedString);
        return parameterizedString;
    }

    /**
     * <p>解析字符串，结果正确。</p>
     */
    @Test
    public void should_return_result_when_resolved() {
        ParameterizedString result = getParameterizedString(SOURCE_STRING);
        assertNotNull(result);
        assertEquals(result.getOriginalString(), SOURCE_STRING);

        List<ResolvedParameter> variables = result.getParameters();
        assertNotNull(variables);
        assertEquals(variables.size(), 1);
        ResolvedParameter variable = variables.get(0);
        assertNotNull(variable);
        assertEquals(variable.getName(), VARIABLE_NAME);
        assertEquals(variable.getPosition(), VARIABLE_POSITION);

        String formattedString = result.format(VARIABLES);
        assertEquals(formattedString, FORMATTED_STRING);
    }

    /**
     * 目标方法：{@link ParameterizedStringResolver#resolve(String)}
     * <p>当以被转义的转义字符结尾时，解析后的字符串正确。</p>
     */
    @Test
    public void should_return_parameterized_string_when_end_with_escaped_escape_character() {
        final String originalString = "Parameterized string. //";
        final String escapedString = "Parameterized string. /";
        ParameterizedString parameterizedString = getParameterizedString(originalString);
        assertNotNull(parameterizedString);
        String resolvedString = parameterizedString.format(null);
        assertNotNull(resolvedString);
        assertEquals(resolvedString, escapedString);
    }

    /**
     * 目标方法：{@link ParameterizedString#format(Map)}
     * <p>当参数化字符串中仅包含参数时，格式化的结果正确。</p>
     */
    @Test
    public void should_return_formatted_string_when_only_parameters() {
        final String originalString = "${start}${end}";
        Map<String, Object> parameters =
                MapBuilder.<String, Object>get().put("start", "MyStartParameter").put("end", "MyEndParameter").build();
        final String resolvedString = "MyStartParameterMyEndParameter";
        ParameterizedString parameterizedString = getParameterizedString(originalString);
        String result = parameterizedString.format(parameters);
        assertEquals(result, resolvedString);
    }

    /**
     * 目标方法：{@link ParameterizedString#format(Map)}
     * <p>当参数名称中包含转义字符时，格式化结果正确。</p>
     */
    @Test
    public void should_return_formatted_string_when_parameter_contains_escape_character() {
        final String originalString = "${parameter/{in/}}";
        Map<String, Object> parameters = MapBuilder.<String, Object>get().put("parameter{in}", "parameter-in").build();
        final String resolvedString = "parameter-in";
        ParameterizedString parameterizedString = getParameterizedString(originalString);
        String result = parameterizedString.format(parameters);
        assertNotNull(result);
        assertEquals(result, resolvedString);
    }

    /**
     * 目标方法：{@link ParameterizedStringResolver#create(String, String, char)}
     * <p>当变量前缀为 {@code null} 时，抛出 {@link IllegalArgumentException} 异常。</p>
     */
    @Test
    public void should_throws_when_prefix_is_null() {
        Executable action = () -> ParameterizedStringResolver.create(null, SUFFIX, ESCAPE_CHARACTER);
        assertThrows(IllegalArgumentException.class, action);
    }

    /**
     * 目标方法：{@link ParameterizedStringResolver#create(String, String, char)}
     * <p>当变量后缀为空字符串时，抛出 {@link IllegalArgumentException} 异常。</p>
     */
    @Test
    public void should_throws_when_suffix_is_empty() {
        Executable action = () -> ParameterizedStringResolver.create(PREFIX, StringUtils.EMPTY, ESCAPE_CHARACTER);
        assertThrows(IllegalArgumentException.class, action);
    }

    /**
     * 目标方法：{@link ParameterizedStringResolver#create(String, String, char)}
     * <p>当变量前缀包含转义字符时，抛出 {@link IllegalArgumentException} 异常。</p>
     */
    @Test
    public void should_throws_when_prefix_contains_escape_character() {
        Executable action = () -> ParameterizedStringResolver.create(ILLEGAL_PREFIX, SUFFIX, ESCAPE_CHARACTER);
        assertThrows(IllegalArgumentException.class, action);
    }

    /**
     * 目标方法：{@link ParameterizedStringResolver#create(String, String, char)}
     * <p>当变量后缀包含转义字符时，抛出 {@link IllegalArgumentException} 异常。</p>
     */
    @Test
    public void should_throws_when_suffix_contains_escape_character() {
        Executable action = () -> ParameterizedStringResolver.create(PREFIX, ILLEGAL_SUFFIX, ESCAPE_CHARACTER);
        assertThrows(IllegalArgumentException.class, action);
    }

    /**
     * 目标方法：{@link ParameterizedStringResolver#resolve(String)}
     * <p>当参数未关闭时，引发 {@link StringFormatException} 异常。</p>
     */
    @Test
    public void should_throws_when_parameter_incomplete() {
        final String originalString = "Parameterized string. ${parameter";
        Executable action = () -> getParameterizedString(originalString);
        assertThrows(StringFormatException.class, action);
    }

    /**
     * 目标方法：{@link ParameterizedStringResolver#resolve(String)}
     * <p>当发现未转义的参数后缀时，引发 {@link StringFormatException} 异常。</p>
     */
    @Test
    public void should_throws_when_parameter_not_started() {
        final String originalString = "Parameterized string. parameter}";
        Executable action = () -> getParameterizedString(originalString);
        assertThrows(StringFormatException.class, action);
    }

    /**
     * 目标方法：{@link ParameterizedStringResolver#resolve(String)}
     * <p>当以单独的转义字符结尾时，引发 {@link StringFormatException} 异常。</p>
     */
    @Test
    public void should_throws_when_end_with_escape_character() {
        final String originalString = "Parameterized string. parameter/";
        Executable action = () -> getParameterizedString(originalString);
        assertThrows(StringFormatException.class, action);
    }

    /**
     * 目标方法：{@link ParameterizedString#format(Map)}
     * <p>当所需参数未提供时，引发 {@link StringFormatException} 异常。</p>
     */
    @Test
    public void should_throws_when_required_parameter_not_supplied() {
        final String originalString = "${required-parameter}";
        ParameterizedString parameterizedString = getParameterizedString(originalString);
        Executable action = () -> parameterizedString.format(null);
        assertThrows(StringFormatException.class, action);
    }

    /**
     * 目标方法：{@link ParameterizedStringResolver#resolve(String)}
     * <p>当递归包含参数时，引发 {@link StringFormatException} 异常。</p>
     */
    @Test
    public void should_throws_when_parameter_contained_recursively() {
        final String originalString = "${parameter${recursive-parameter}}";
        Executable action = () -> getParameterizedString(originalString);
        assertThrows(StringFormatException.class, action);
    }

    /**
     * 目标方法：{@link ParameterizedString#format(Map)}
     * <p>正确解析带有转义符的文本。</p>
     *
     * @throws IOException 读取资源文件发生IO异常。
     */
    @Test
    public void should_return_formatted_xml() throws IOException {
        String parameterizedString = IoUtils.content(ParameterizedStringResolverTest.class, "/ParameterizedString.xml");
        Map<String, String> params =
                MapBuilder.<String, String>get().put("namespace", "my-namespace").put("resultMap", "my-result").build();
        parameterizedString = parameterizedString.replace("/", "//");
        ParameterizedString format = getParameterizedString(parameterizedString);
        String result = format.format(params);
        assertNotNull(result);
        assertEquals(result, IoUtils.content(ParameterizedStringResolverTest.class, "/ParameterizedStringResult.xml"));
    }

    @Nested
    @DisplayName("验证方法：format(Map<?, ?> args)")
    class TestFormat {
        @Nested
        @DisplayName("异常场景")
        class GivenExceptionScenario {
            @Test
            @DisplayName("当参数中不包含格式化字符串所需要的变量时，抛出 StringFormatException")
            void givenArgsNotContainsRequiredVariableThenThrowStringFormatException() {
                ParameterizedString format = getParameterizedString("${required}");
                StringFormatException exception =
                        catchThrowableOfType(() -> format.format(MapBuilder.get().put("someOther", "value").build()),
                                StringFormatException.class);
                assertThat(exception).isNotNull().hasMessage("Parameter 'required' required but not supplied.");
            }
        }
    }

    @Nested
    @DisplayName("验证方法：toString()")
    class TestToString {
        @Test
        @DisplayName("当格式化字符串符合要求时，输出正确的字符串")
        void returnCorrectString() {
            ParameterizedString format = getParameterizedString("${required}");
            String actual = format.format(MapBuilder.get().put("required", "value").build());
            assertThat(actual).isEqualTo("value");
            assertThat(format.toString()).isEqualTo(
                    "[originalString=${required}, variables=[[position=0, variable=required]]]");
        }
    }

    @Test
    @DisplayName("当字符串包含${}{}时，输出正确的字符串")
    void shouldOkWhenSpaceHolderWithBrackets() {
        ParameterizedStringResolver resolver =
                ParameterizedStringResolver.create(PREFIX, SUFFIX, EMPTY_ESCAPE_CHARACTER, false);
        String input = "a:\n" + "  c: '{\"2\" : \"3\"}'\n" + "  d: '${hello} : ${bye} : {ok}'\n" + "  n: ${hello}\n"
                + "  x: '{\"name\":\"bob\"}'\n" + "  y: '{\"1\" : 2, \"2\" : ${hello}}'";
        Map<String, String> params =
                MapBuilder.<String, String>get().put("hi", "嗨").put("hello", "你好").put("bye", "再见").build();
        ParameterizedString parameterizedString = resolver.resolve(input);
        String actual = parameterizedString.format(params);
        String except = "a:\n" + "  c: '{\"2\" : \"3\"}'\n" + "  d: '你好 : 再见 : {ok}'\n" + "  n: 你好\n"
                + "  x: '{\"name\":\"bob\"}'\n" + "  y: '{\"1\" : 2, \"2\" : 你好}'";
        assertThat(actual).isEqualTo(except);
    }
}
