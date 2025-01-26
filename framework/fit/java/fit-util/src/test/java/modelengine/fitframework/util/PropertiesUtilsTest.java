/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * {@link PropertiesUtils} 的单元测试。
 *
 * @author 季聿阶
 * @since 2021-01-17
 */
public class PropertiesUtilsTest {
    @Nested
    @DisplayName("Test mapFrom")
    class TestMapFrom {
        @Nested
        @DisplayName("Test method: mapFrom(File file)")
        class TestMapFromFile {
            @Nested
            @DisplayName("Given .properties file is in resources folder")
            class GivenPropertiesFileInResources {
                @Test
                @DisplayName("Given fit-correct.properties then return correct map")
                void givenCorrectPropertiesFileThenReturnCorrectMap() {
                    // given
                    File file = new File("src/test/resources/property/fit-correct.properties");

                    // when
                    Map<String, Object> map = PropertiesUtils.mapFrom(file);

                    // then
                    ObjectAssert<Object> genericable1 = assertThat(map).isNotEmpty()
                            .containsKey("genericables")
                            .extractingByKey("genericables")
                            .asList()
                            .hasSize(1)
                            .element(0);
                    genericable1.extracting("id").isEqualTo("g1");
                    genericable1.extracting("name").isEqualTo("modelengine.fit.G1");
                    genericable1.extracting("tags").asList().containsSequence("t1", "t2");
                    genericable1.extracting("route").isEqualTo("a1");
                    genericable1.extracting("trust")
                            .hasFieldOrPropertyWithValue("validation", "validate")
                            .hasFieldOrPropertyWithValue("before", "before")
                            .hasFieldOrPropertyWithValue("after", "after")
                            .hasFieldOrPropertyWithValue("error", "error");
                    ObjectAssert<Object> fitable1 = genericable1.extracting("fitables").asList().hasSize(1).element(0);
                    fitable1.hasFieldOrPropertyWithValue("id", "modelengine.fit.F1.f1");
                    fitable1.extracting("tags").asList().containsSequence("f1", "f2");
                    fitable1.extracting("aliases").asList().containsSequence("a1", "a2");
                }
            }

            @Nested
            @DisplayName("Given .properties file is not in resources folder")
            class GivenPropertiesFileNotInResources {
                @Test
                @DisplayName("Given not exist file then return empty map")
                void givenFileNotExistThenReturnEmptyMap() {
                    Map<String, Object> actual = PropertiesUtils.mapFrom(new File(""));
                    assertThat(actual).isEmpty();
                }

                @Test
                @DisplayName("Given file with IOException then throw IllegalStateException")
                void givenFileWithIOExceptionThenThrowException() {
                    File spy = Mockito.spy(new File("\u0000"));
                    when(spy.exists()).thenReturn(true);
                    when(spy.getName()).thenReturn("testFile.txt");
                    IllegalStateException exception =
                            catchThrowableOfType(() -> PropertiesUtils.mapFrom(spy), IllegalStateException.class);
                    assertThat(exception).isNotNull()
                            .hasMessage("Failed to load configuration from properties. [file=testFile.txt]")
                            .getCause()
                            .isInstanceOf(IOException.class);
                }
            }
        }

        @Nested
        @DisplayName("Test method: mapFrom(InputStream in)")
        class TestMapFromInputStream {
            @Nested
            @DisplayName("Given .properties file is in resources folder")
            class GivenPropertiesFileInResources {
                @Test
                @DisplayName("Given fit-correct.properties then return correct map")
                void givenCorrectPropertiesFileThenReturnCorrectMap() {
                    // given
                    InputStream in = IoUtils.resource(PropertiesUtilsTest.class, "/property/fit-correct.properties");

                    // when
                    Map<String, Object> map = PropertiesUtils.mapFrom(in);

                    // then
                    ObjectAssert<Object> genericable1 = assertThat(map).isNotEmpty()
                            .containsKey("genericables")
                            .extractingByKey("genericables")
                            .asList()
                            .hasSize(1)
                            .element(0);
                    genericable1.extracting("id").isEqualTo("g1");
                    genericable1.extracting("name").isEqualTo("modelengine.fit.G1");
                    genericable1.extracting("tags").asList().containsSequence("t1", "t2");
                    genericable1.extracting("route").isEqualTo("a1");
                    genericable1.extracting("trust")
                            .hasFieldOrPropertyWithValue("validation", "validate")
                            .hasFieldOrPropertyWithValue("before", "before")
                            .hasFieldOrPropertyWithValue("after", "after")
                            .hasFieldOrPropertyWithValue("error", "error");
                    ObjectAssert<Object> fitable1 = genericable1.extracting("fitables").asList().hasSize(1).element(0);
                    fitable1.hasFieldOrPropertyWithValue("id", "modelengine.fit.F1.f1");
                    fitable1.extracting("tags").asList().containsSequence("f1", "f2");
                    fitable1.extracting("aliases").asList().containsSequence("a1", "a2");
                }
            }

            @Nested
            @DisplayName("Given .properties file is not in resources folder")
            class GivenPropertiesFileNotInResources {
                @Test
                @DisplayName("Given read input stream with IOException then throw IllegalStateException")
                void givenInputStreamWithExceptionThenThrowException() throws IOException {
                    InputStream in = Mockito.mock(InputStream.class);
                    when(in.read(any())).thenThrow(new IOException());
                    IllegalStateException exception =
                            catchThrowableOfType(() -> PropertiesUtils.mapFrom(in), IllegalStateException.class);
                    assertThat(exception).isNotNull()
                            .hasMessage("Failed to load configuration from properties input stream.")
                            .getCause().isInstanceOf(IOException.class);
                }
            }
        }

        @Nested
        @DisplayName("Test method: mapFrom(Properties properties)")
        class TestMapFromProperties {
            @Nested
            @DisplayName("Given .properties file is in resources folder")
            class GivenPropertiesFileInResources {
                @Test
                @DisplayName("Given fit-correct.properties then return correct map")
                void givenCorrectPropertiesFileThenReturnCorrectMap() {
                    // given
                    Properties properties =
                            IoUtils.properties(PropertiesUtilsTest.class, "/property/fit-correct.properties");

                    // when
                    Map<String, Object> map = PropertiesUtils.mapFrom(properties);

                    // then
                    ObjectAssert<Object> genericable1 = assertThat(map).isNotEmpty()
                            .containsKey("genericables")
                            .extractingByKey("genericables")
                            .asList()
                            .hasSize(1)
                            .element(0);
                    genericable1.extracting("id").isEqualTo("g1");
                    genericable1.extracting("name").isEqualTo("modelengine.fit.G1");
                    genericable1.extracting("tags").asList().containsSequence("t1", "t2");
                    genericable1.extracting("route").isEqualTo("a1");
                    genericable1.extracting("trust")
                            .hasFieldOrPropertyWithValue("validation", "validate")
                            .hasFieldOrPropertyWithValue("before", "before")
                            .hasFieldOrPropertyWithValue("after", "after")
                            .hasFieldOrPropertyWithValue("error", "error");
                    ObjectAssert<Object> fitable1 = genericable1.extracting("fitables").asList().hasSize(1).element(0);
                    fitable1.hasFieldOrPropertyWithValue("id", "modelengine.fit.F1.f1");
                    fitable1.extracting("tags").asList().containsSequence("f1", "f2");
                    fitable1.extracting("aliases").asList().containsSequence("a1", "a2");
                }

                @Test
                @DisplayName("Given fit-correct-random.properties then return correct map")
                void givenCorrectRandomPropertiesFileThenReturnCorrectMap() {
                    // given
                    Properties properties =
                            IoUtils.properties(PropertiesUtilsTest.class, "/property/fit-correct-random.properties");

                    // when
                    Map<String, Object> map = PropertiesUtils.mapFrom(properties);

                    // then
                    AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> genericables =
                            assertThat(map).isNotEmpty()
                                    .containsKey("genericables")
                                    .extractingByKey("genericables")
                                    .asList();
                    genericables.hasSize(2);
                    ObjectAssert<Object> genericable1 = genericables.element(0);
                    genericable1.extracting("id").isEqualTo("g1");
                    genericable1.extracting("name").isEqualTo("modelengine.fit.G1");
                    genericable1.extracting("tags").asList().containsSequence("t1", "t2");
                    genericable1.extracting("route").isEqualTo("a1");
                    ObjectAssert<Object> fitable1 = genericable1.extracting("fitables").asList().hasSize(1).element(0);
                    fitable1.hasFieldOrPropertyWithValue("id", "modelengine.fit.F1.f1");
                    fitable1.extracting("tags").asList().containsSequence("f1", "f2");
                    fitable1.extracting("aliases").asList().containsSequence("a1", "a2");
                    ObjectAssert<Object> genericable2 = genericables.element(1);
                    genericable2.extracting("id").isEqualTo("g2");
                    genericable2.extracting("name").isEqualTo("modelengine.fit.G2");
                    genericable2.extracting("tags").asList().containsSequence("t1", "t2");
                    genericable2.extracting("route").isEqualTo("a3");
                    ObjectAssert<Object> fitable2 = genericable2.extracting("fitables").asList().hasSize(1).element(0);
                    fitable2.hasFieldOrPropertyWithValue("id", "modelengine.fit.F1.f2");
                    fitable2.extracting("tags").asList().containsSequence("f1", "f2");
                    fitable2.extracting("aliases").asList().containsSequence("a3", "a4");
                }

                @Test
                @DisplayName("Given fit-big.properties then return correct map")
                void givenCorrectBigPropertiesFileThenReturnCorrectMap() {
                    // given
                    Properties properties =
                            IoUtils.properties(PropertiesUtilsTest.class, "/property/fit-big.properties");

                    // when
                    Map<String, Object> map = PropertiesUtils.mapFrom(properties);

                    // then
                    assertThat(map).isNotEmpty().hasSize(1).extractingByKey("genericables").asList().hasSize(11);
                }
            }

            @Nested
            @DisplayName("Given .properties file is not in resources folder")
            class GivenPropertiesNotInResources {
                @Test
                @DisplayName("Given {'': 'value'} then throw IllegalArgumentException")
                void givenUniqueKeyIsEmptyThenThrowException() {
                    Properties properties = new Properties();
                    properties.setProperty("", "value");
                    IllegalArgumentException exception = catchThrowableOfType(() -> PropertiesUtils.mapFrom(properties),
                            IllegalArgumentException.class);
                    assertThat(exception).isNotNull().hasMessage("Property key must have 1 sub-key at least. [key=]");
                }

                @Test
                @DisplayName("Given {'key[hello]': 'value'} then throw IllegalArgumentException")
                void givenKeyHasIllegalNumberAsArrayIndexThenThrowException() {
                    Properties properties = new Properties();
                    properties.setProperty("key[hello]", "value");
                    IllegalArgumentException exception = catchThrowableOfType(() -> PropertiesUtils.mapFrom(properties),
                            IllegalArgumentException.class);
                    assertThat(exception).isNotNull().hasMessage("Illegal property key pattern. [key=key[hello]]");
                }
            }
        }
    }
}
