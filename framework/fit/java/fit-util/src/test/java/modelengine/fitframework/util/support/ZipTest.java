/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

/**
 * {@link Zip} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-01-31
 */
public class ZipTest {
    @Nested
    @DisplayName("Given expected scenario")
    class GivenExpectedScenario {
        @Test
        @DisplayName("Given folder and files then zip correctly")
        void givenFolderAndFilesThenZipCorrectly() {
            File zipFile = new File("src/test/resources/zip-expected-tmp1.zip");
            zipFile.deleteOnExit();
            Zip zip = new Zip(zipFile, null).override(true)
                    .add(new File("src/test/resources/zip"))
                    .filter(file -> !file.getName().endsWith(".out"));
            assertThatNoException().isThrownBy(zip::start);
        }

        @Test
        @DisplayName("Given override and zip twice then zip correctly")
        void givenZipToExistFileWithOverrideThenZipCorrectly() throws IOException {
            File zipFile = new File("src/test/resources/zip-expected-tmp2.zip");
            zipFile.deleteOnExit();
            Zip zip = new Zip(zipFile, null).override(true).add(new File("src/test/resources/zip"));
            zip.start();
            assertThatNoException().isThrownBy(zip::start);
        }
    }

    @Nested
    @DisplayName("Given exception scenario")
    class GivenExceptionScenario {
        @Test
        @DisplayName("Given not allowed override and zip twice then throw IOException")
        void givenNotAllowedOverrideAndZipTwiceThenThrowException() throws IOException {
            File zipFile = new File("src/test/resources/zip-exception-tmp1.zip");
            zipFile.deleteOnExit();
            Zip zip = new Zip(zipFile, null).override(false).add(new File("src/test/resources/zip"));
            zip.start();
            IOException exception = catchThrowableOfType(zip::start, IOException.class);
            assertThat(exception).isNotNull()
                    .hasMessage("The output file of zip already exists. [name=zip-exception-tmp1.zip]");
        }
    }
}
