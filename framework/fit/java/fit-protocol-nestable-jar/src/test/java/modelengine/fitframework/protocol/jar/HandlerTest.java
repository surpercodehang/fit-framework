/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 为 {@link Handler} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-01-16
 */
@DisplayName("测试 Handler 类")
public class HandlerTest {
    @Nested
    @DisplayName("实例化 Handler 类")
    class InitializeHandler {
        private Handler handler;

        @BeforeEach
        void setup() {
            this.handler = new Handler();
        }

        @Test
        @DisplayName("打开一个有效 jar Url 值，返回为 NestableJarUrlConnection 类的实例")
        void givenValidJarUrlThenReturnNewNestableJarUrlConnectionInstance() throws IOException {
            String jarUrl = "jar:file:/D:/Learn/jdkTools/jre/lib/ext/fit.jar!/myClass.class";
            URL url = new URL(jarUrl);
            URLConnection urlConnection = this.handler.openConnection(url);
            assertThat(urlConnection).isExactlyInstanceOf(NestableJarUrlConnection.class);
            assertThat(urlConnection.getContentType()).isEqualTo("application/java-archive");
        }

        @Nested
        @DisplayName("解析 Url 值")
        class ParseURL {
            @Test
            @DisplayName("给定解析值起始字段为 'jar:'，更新输入 Url 值为解析值的值")
            void givenSpecValueStartWithJarColonThenUpdateUrlToSpecificationValue() throws IOException {
                String file = "jar:file:/c:/folder/my.jar!/com/myCompany/MyClass.class";
                URL actualUrl = new URL(new URL(file), file, handler);
                String specification = "jar:modify";
                handler.parseURL(actualUrl, specification, 4, specification.length());
                assertThat(actualUrl.toString()).isEqualTo("jar:modify");
            }

            @Test
            @DisplayName("给定传入的 Url 的 file 字段为空，更新输入 Url 中 file 字段值为解析值的内容")
            void givenFileInUrlEmptyThenUpdateToSpecificationValue() throws MalformedURLException {
                URL url = new URL("jar", null, -1, "");
                URL actualUrl = new URL(url, url.toString(), handler);
                String specification = "url.toString()";
                handler.parseURL(actualUrl, specification, 0, specification.length());
                assertThat(actualUrl.toString()).isEqualTo("jar:url.toString()");
            }

            @Test
            @DisplayName("给定解析值的起始字符为 '/'，file 中包含字段 '!/'，且不以该字段结尾，更新输入 Url 中 file 字段 '!/'"
                    + "之后内容为分隔符 '/' 之后内容")
            void givenSpecificationStartWithSlashAndFileInUrlContainsExclamationMarkSlashThenUpdateUrl()
                    throws MalformedURLException {
                URL url = new URL("jar", null, -1, "file:c!/MyClass/class/fit-test");
                URL actualUrl = new URL(url, url.toString(), handler);
                String specification = "/jarUrl";
                handler.parseURL(actualUrl, specification, 0, specification.length());
                assertThat(actualUrl.toString()).isEqualTo("jar:file:c!/jarUrl");
            }

            @Test
            @DisplayName(
                    "给定解析值的起始字符为 '/'，file 中不包含字符 '/'，更新输入 Url 中 file 字段内容为解析值 '/' 之后内容")
            void givenSpecificationStartWithSlashAndFileInUrlNotContainsSlashThenUpdateUrl()
                    throws MalformedURLException {
                URL url = new URL("jar", null, -1, "file:my.jar");
                URL actualUrl = new URL(url, url.toString(), handler);
                String specification = "/specification";
                handler.parseURL(actualUrl, specification, 0, specification.length());
                assertThat(actualUrl.toString()).isEqualTo("jar:specification");
            }

            @Test
            @DisplayName("给定解析值的起始字符不为 '/'，file 的最后字符为 '/'，更新输入 Url 中 file 字段添加下层目录")
            void givenSpecificationNotStartWithSlashAndFileInUrlEndWithSlashThenUpdateUrl()
                    throws MalformedURLException {
                URL url = new URL("jar", null, -1, "file:folder/my.jar/");
                URL actualUrl = new URL(url, url.toString(), handler);
                String specification = "specification";
                handler.parseURL(actualUrl, specification, 0, specification.length());
                assertThat(actualUrl.toString()).isEqualTo("jar:file:folder/my.jar/specification");
            }

            @Test
            @DisplayName(
                    "给定解析值的起始字符不为 '/'，file 中包含且最后字符不为 '/'，更新输入 Url 中 file 字段最底层的文件值为解析值内容")
            void givenSpecificationNotStartWithSlashAndFileInUrlContainsButNotEndWithSlashThenUpdateUrl()
                    throws MalformedURLException {
                URL url = new URL("jar", null, -1, "file:c/MyClass/class/file-fit");
                URL actualUrl = new URL(url, url.toString(), handler);
                String specification = "specification";
                handler.parseURL(actualUrl, specification, 0, specification.length());
                assertThat(actualUrl.toString()).isEqualTo("jar:file:c/MyClass/class/specification");
            }

            @Test
            @DisplayName("给定解析值的起始字符不为 '/'，file 中不包含 '/'，更新输入 Url 中 file 字段内容为解析值内容")
            void givenSpecificationNotStartWithSlashAndFileInUrlNotContainsSlashThenUpdateUrl()
                    throws MalformedURLException {
                URL url = new URL("jar", null, -1, "file:MyClass.class");
                URL actualUrl = new URL(url, url.toString(), handler);
                String specification = "specification";
                handler.parseURL(actualUrl, specification, 0, specification.length());
                assertThat(actualUrl.toString()).isEqualTo("jar:specification");
            }
        }

        @Nested
        @DisplayName("判断两个 Url 是否相同")
        class CheckIsSameUrl {
            @Test
            @DisplayName("给定两个相同的 jar URL，返回为 true")
            void givenTwoSameJarUrlThenReturnTrue() throws MalformedURLException {
                String testUrl = "jar:file:/test.jar!/";
                URL url1 = new URL(testUrl);
                URL url2 = new URL(testUrl);
                boolean sameFile = handler.sameFile(url1, url2);
                assertThat(sameFile).isTrue();
            }

            @Test
            @DisplayName("给定两个不相同的 jar URL，返回为 false")
            void givenTwoDifferentJarUrlThenReturnFalse() throws MalformedURLException {
                URL url1 = new URL("jar:file:/test1.jar!/");
                URL url2 = new URL("jar:file:/test2.jar!/");
                boolean sameFile = handler.sameFile(url1, url2);
                assertThat(sameFile).isFalse();
            }

            @Test
            @DisplayName("给定两个相同的非 jar URL，返回为 false")
            void givenTwoSameNotJarUrlThenReturnFalse() throws MalformedURLException {
                URL url1 = new URL("https://huawei.com");
                URL url2 = new URL("https://huawei.com");
                boolean sameFile = handler.sameFile(url1, url2);
                assertThat(sameFile).isFalse();
            }

            @Test
            @DisplayName("给定两个不相同的非 jar URL，返回为 false")
            void givenTwoDifferentNotJarUrlThenReturnFalse() throws MalformedURLException {
                URL url1 = new URL("https://huawei.com");
                URL url2 = new URL("https://huaweiyun.com");
                boolean sameFile = handler.sameFile(url1, url2);
                assertThat(sameFile).isFalse();
            }
        }

        @Nested
        @DisplayName("获取 Url 的哈希值")
        class GetUrlHashCode {
            @Test
            @DisplayName("给定有效的 jar Url 值，返回为 hashcode 值不为 0")
            void givenValidJarUrlThenReturnHashCodeValueNotEqualZero() throws MalformedURLException {
                URL url = new URL("jar:file:/test.jar!/");
                int hashCode = handler.hashCode(url);
                assertThat(hashCode).isNotEqualTo(0);
            }

            @Test
            @DisplayName("给定有效的非 jar Url 值，返回为 hashcode 值不为 0")
            void givenValidNotJarUrlThenReturnHashCodeValueNotEqualZero() throws MalformedURLException {
                URL url = new URL("https://huawei.com");
                int hashCode = handler.hashCode(url);
                assertThat(hashCode).isNotEqualTo(0);
            }
        }
    }
}
