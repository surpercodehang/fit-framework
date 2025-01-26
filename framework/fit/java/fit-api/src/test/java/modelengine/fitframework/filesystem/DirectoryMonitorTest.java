/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.filesystem;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.util.LockUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 表示 {@link DirectoryMonitor} 的单元测试。
 *
 * @author 季聿阶
 * @since 2023-07-26
 */
@DisplayName("测试 DirectoryMonitor")
public class DirectoryMonitorTest {
    private File directory;
    private FileDetector detector;
    private DirectoryMonitor monitor;

    private final Object lock = LockUtils.newSynchronizedLock();

    @BeforeEach
    void setup() throws IOException {
        this.directory = Files.createTempDirectory("DirectoryMonitorTest-").toFile();
        this.directory.deleteOnExit();
        this.detector = new FileDetector(this.lock);
        this.monitor = DirectoryMonitor.create(this.directory,
                FileObservers.builder()
                        .created(this.detector)
                        .changed(this.detector)
                        .deleted(this.detector)
                        .visitedFailed(this.detector)
                        .build(),
                null,
                (thread, cause) -> {});
        this.monitor.start();
    }

    @AfterEach
    void teardown() {
        this.monitor.stop();
        this.detector = null;
    }

    @Nested
    @DisplayName("在指定监视目录下新增一个文件后")
    class AfterAddingNewFile {
        private File added;

        @BeforeEach
        void setup() throws IOException, InterruptedException {
            synchronized (DirectoryMonitorTest.this.lock) {
                this.added = new File(DirectoryMonitorTest.this.directory, "add");
                Files.createFile(this.added.toPath());
                this.added.deleteOnExit();
                DirectoryMonitorTest.this.lock.wait();
            }
        }

        @AfterEach
        void teardown() {
            this.added = null;
        }

        @Test
        @DisplayName("可以探查到新增文件数量为 1")
        void shouldReturn1CreatedFile() {
            int actual = DirectoryMonitorTest.this.detector.getAdded();
            assertThat(actual).isEqualTo(1);
        }

        @Test
        @DisplayName("修改该文件后，可以探查到修改文件数量为 1")
        void shouldReturn1ChangedFile() throws IOException, InterruptedException {
            synchronized (DirectoryMonitorTest.this.lock) {
                this.added.delete();
                File file = new File(DirectoryMonitorTest.this.directory, "add");
                Files.createFile(file.toPath());
                file.deleteOnExit();
                DirectoryMonitorTest.this.lock.wait();
            }
            int actual = DirectoryMonitorTest.this.detector.getChanged();
            assertThat(actual).isEqualTo(1);
        }

        @Test
        @DisplayName("删除该文件后，可以探查到删除文件数量为 1")
        void shouldReturn1DeletedFile() throws InterruptedException {
            synchronized (DirectoryMonitorTest.this.lock) {
                this.added.delete();
                DirectoryMonitorTest.this.lock.wait();
            }
            int actual = DirectoryMonitorTest.this.detector.getDeleted();
            assertThat(actual).isEqualTo(1);
        }
    }

    static class FileDetector
            implements FileCreatedObserver, FileChangedObserver, FileDeletedObserver, FileVisitedFailedObserver {
        private final AtomicInteger added = new AtomicInteger();
        private final AtomicInteger changed = new AtomicInteger();
        private final AtomicInteger deleted = new AtomicInteger();

        private final Object lock;

        public FileDetector(Object lock) {
            this.lock = lock;
        }

        @Override
        public void onFileCreated(File file) {
            this.added.getAndIncrement();
            this.notifyTest();
        }

        @Override
        public void onFileChanged(File file) {
            this.changed.getAndIncrement();
            this.notifyTest();
        }

        @Override
        public void onFileDeleted(File file) {
            this.deleted.getAndIncrement();
            this.notifyTest();
        }

        private void notifyTest() {
            synchronized (this.lock) {
                this.lock.notifyAll();
            }
        }

        @Override
        public void onFileVisitedFailed(File file, IOException exception) {}

        public int getAdded() {
            return this.added.get();
        }

        public int getChanged() {
            return this.changed.get();
        }

        public int getDeleted() {
            return this.deleted.get();
        }
    }
}
