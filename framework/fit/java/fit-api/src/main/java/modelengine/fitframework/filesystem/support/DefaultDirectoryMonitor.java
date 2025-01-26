/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.filesystem.support;

import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.filesystem.DirectoryMonitor;
import modelengine.fitframework.filesystem.FileDeletedObserver;
import modelengine.fitframework.filesystem.FileObservers;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.ExecutePolicy;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.schedule.ThreadPoolScheduler;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 表示 {@link DirectoryMonitor} 的默认实现，该实现通过定时器轮询实现。
 *
 * @author 季聿阶
 * @since 2023-07-26
 */
public class DefaultDirectoryMonitor implements DirectoryMonitor {
    static final String ALL = "*";
    private static final Logger log = Logger.get(DefaultDirectoryMonitor.class);

    private final File directory;
    private final Set<String> fileExtensions;
    private final FileObservers observers;
    private final FileDeletedObserver deletedObserver;

    private final ExecutePolicy policy;
    private final Thread.UncaughtExceptionHandler handler;

    private volatile Map<File, FileTime> signatures = new HashMap<>();
    private final ThreadPoolScheduler scheduler;
    private final Object lock = LockUtils.newSynchronizedLock();
    private volatile boolean isStarted = false;

    /**
     * 创建文件变化监视器。
     *
     * @param directory 表示待监视的指定文件目录的 {@link File}。
     * @param fileExtensions 表示待监视的文件的扩展名集合的 {@link Set}{@code <}{@link String}{@code >}。
     * @param fileObservers 表示文件新增、变化、删除、访问错误等的观察者集合的 {@link FileObservers}。
     * @param policy 表示默认文件目录监视器的定时扫描策略的 {@link ExecutePolicy}。
     * @param handler 表示默认文件目录监视器定时扫描时的错误处理器的 {@link java.lang.Thread.UncaughtExceptionHandler}。
     */
    public DefaultDirectoryMonitor(File directory, Set<String> fileExtensions, FileObservers fileObservers,
            ExecutePolicy policy, Thread.UncaughtExceptionHandler handler) {
        this.directory = notNull(directory, "The directory to monitor cannot be null.");
        isTrue(this.directory.isDirectory(), "The directory to monitor must be a directory.");
        this.fileExtensions = ObjectUtils.getIfNull(fileExtensions, HashSet::new);
        if (this.fileExtensions.isEmpty()) {
            this.fileExtensions.add(ALL);
        }
        this.observers = ObjectUtils.getIfNull(fileObservers, () -> FileObservers.builder().build());
        this.deletedObserver = ObjectUtils.getIfNull(this.observers.deleted(), FileDeletedObserver::empty);

        this.policy = ObjectUtils.getIfNull(policy, () -> ExecutePolicy.fixedDelay(100));
        this.handler = notNull(handler, "The exception handler cannot be null.");
        this.scheduler = ThreadPoolScheduler.custom()
                .threadPoolName(StringUtils.format("directory-monitor-{0}", this.directory.getName()))
                .awaitTermination(500L, TimeUnit.MILLISECONDS)
                .isImmediateShutdown(true)
                .corePoolSize(1)
                .maximumPoolSize(1)
                .keepAliveTime(1, TimeUnit.SECONDS)
                .workQueueCapacity(0)
                .isDaemonThread(true)
                .rejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.AbortPolicy())
                .build();
    }

    @Override
    public void start() {
        if (this.isStarted) {
            return;
        }
        synchronized (this.lock) {
            if (this.isStarted) {
                return;
            }
            Task task = Task.builder()
                    .runnable(this::refresh)
                    .policy(this.policy)
                    .uncaughtExceptionHandler(this.handler)
                    .build();
            this.scheduler.schedule(task);
            this.isStarted = true;
        }
    }

    @Override
    public void stop() {
        if (!this.isStarted) {
            return;
        }
        synchronized (this.lock) {
            if (!this.isStarted) {
                return;
            }
            try {
                this.scheduler.shutdown();
            } catch (InterruptedException e) {
                Thread.interrupted();
                throw new IllegalStateException(StringUtils.format("Failed to stop directory monitor. [directory={0}]",
                        FileUtils.path(this.directory)), e);
            }
            this.isStarted = false;
        }
    }

    private void refresh() {
        RegularFileVisitor visitor = new RegularFileVisitor(this.fileExtensions, this.signatures, this.observers);
        Path path = this.directory.toPath();
        visitor.preVisitFileTree(path);
        try {
            Files.walkFileTree(path, visitor);
        } catch (IOException e) {
            log.warn("Failed to walk directory.", e);
        } finally {
            Map<File, FileTime> currentSignatures = visitor.getCurrentModifiedTimes();
            this.handleDeletedFiles(CollectionUtils.difference(this.signatures.keySet(), currentSignatures.keySet()));
            visitor.postVisitFileTree(path);
            this.signatures = currentSignatures;
        }
    }

    private void handleDeletedFiles(Set<File> deletedFiles) {
        for (File deletedFile : deletedFiles) {
            try {
                this.deletedObserver.onFileDeleted(deletedFile);
            } catch (IOException e) {
                log.warn(StringUtils.format("Failed to handle deleted files. [deletedFile={0}]", deletedFile.getName()),
                        e);
            }
        }
    }
}
