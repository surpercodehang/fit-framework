/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Map;

import static modelengine.fitframework.protocol.jar.location.Locations.path;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.protocol.jar.FilesCache;

/**
 * 为 {@link WeakHashMapFilesCache} 提供单元测试。
 *
 * @author 杭潇
 * @since 2025-02-11
 */
@DisplayName("测试 Handler 类")
class WeakHashMapFilesCacheTest {
    private FilesCache filesCache;

    @BeforeEach
    void setUp() {
        this.filesCache = FilesCache.instance();
    }

    @Test
    @DisplayName("给定合法的文件，缓存文件成功。")
    void givenValidFileWhenGetCanonicalFileThenReturnCanonicalFileAndCacheIt()
            throws IOException, NoSuchFieldException, IllegalAccessException {
        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();

        File canonicalFile = tempFile.getCanonicalFile();
        File result = this.filesCache.getCanonicalFile(tempFile);
        assertThat(canonicalFile).isEqualTo(result);

        Field weakCacheField = WeakHashMapFilesCache.class.getDeclaredField("weakCache");
        weakCacheField.setAccessible(true);
        Map<String, WeakReference<File>> weakCache =
                (Map<String, WeakReference<File>>) weakCacheField.get(this.filesCache);

        WeakReference<File> cachedFileRef = weakCache.get(tempFile.getAbsolutePath());
        assertThat(cachedFileRef).isNotNull();
        assertThat(canonicalFile).isEqualTo(cachedFileRef.get());
    }

    @Test
    @DisplayName("给定不存在的文件，获取标准化文件时抛出异常。")
    void givenNonExistentFileWhenGetCanonicalFileThenThrowException() throws IOException {
        File mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn("../non_existent_file.txt");
        when(mockFile.getCanonicalFile()).thenThrow(new IOException("File does not exist"));

        IllegalArgumentException illegalArgumentException =
                catchThrowableOfType(() -> this.filesCache.getCanonicalFile(mockFile), IllegalArgumentException.class);

        String expectedMessage = String.format("The file of JAR location is not canonical. [path=%s]", path(mockFile));
        assertThat(illegalArgumentException.getMessage()).contains(expectedMessage);
    }

    @Test
    @DisplayName("给定一个缓存文件，获取标准化文件时返回缓存的文件。")
    void givenCachedFileWhenGetCanonicalFileThenReturnCachedFile()
            throws IOException, NoSuchFieldException, IllegalAccessException {
        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();

        File canonicalFile = tempFile.getCanonicalFile();

        File firstResult = this.filesCache.getCanonicalFile(tempFile);
        assertThat(canonicalFile).isEqualTo(firstResult);
        File secondResult = this.filesCache.getCanonicalFile(tempFile);
        assertThat(canonicalFile).isEqualTo(secondResult);

        Field weakCacheField = WeakHashMapFilesCache.class.getDeclaredField("weakCache");
        weakCacheField.setAccessible(true);
        Map<String, WeakReference<File>> weakCache =
                (Map<String, WeakReference<File>>) weakCacheField.get(this.filesCache);

        WeakReference<File> cachedFileRef = weakCache.get(tempFile.getAbsolutePath());
        assertThat(cachedFileRef).isNotNull();
        assertThat(canonicalFile).isEqualTo(cachedFileRef.get());
    }
}