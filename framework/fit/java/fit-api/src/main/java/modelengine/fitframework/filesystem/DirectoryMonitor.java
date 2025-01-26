/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.filesystem;

import modelengine.fitframework.filesystem.support.DefaultDirectoryMonitor;
import modelengine.fitframework.schedule.ExecutePolicy;

import java.io.File;
import java.util.Set;

/**
 * 用于监视目录下的文件变化。
 *
 * @author 季聿阶
 * @since 2023-07-26
 */
public interface DirectoryMonitor {
    /**
     * 启动文件目录监视。
     */
    void start();

    /**
     * 关闭文件目录监视。
     */
    void stop();

    /**
     * 创建一个默认的文件目录监视器。
     *
     * @param directory 表示待监视的指定文件目录的 {@link File}。
     * @param fileObservers 表示文件新增、变化、删除、访问错误等的观察者集合的 {@link FileObservers}。
     * @param policy 表示默认文件目录监视器的定时扫描策略的 {@link ExecutePolicy}。
     * @param handler 表示默认文件目录监视器定时扫描时的错误处理器的 {@link java.lang.Thread.UncaughtExceptionHandler}。
     * @return 表示创建的文件目录监视器的 {@link DirectoryMonitor}。
     */
    static DirectoryMonitor create(File directory, FileObservers fileObservers, ExecutePolicy policy,
            Thread.UncaughtExceptionHandler handler) {
        return create(directory, null, fileObservers, policy, handler);
    }

    /**
     * 创建一个默认的文件目录监视器。
     *
     * @param directory 表示待监视的指定文件目录的 {@link File}。
     * @param fileExtensions 表示待监视的文件的扩展名集合的 {@link Set}{@code <}{@link String}{@code >}。
     * @param fileObservers 表示文件新增、变化、删除、访问错误等的观察者集合的 {@link FileObservers}。
     * @param policy 表示默认文件目录监视器的定时扫描策略的 {@link ExecutePolicy}。
     * @param handler 表示默认文件目录监视器定时扫描时的错误处理器的 {@link java.lang.Thread.UncaughtExceptionHandler}。
     * @return 表示创建的文件目录监视器的 {@link DirectoryMonitor}。
     */
    static DirectoryMonitor create(File directory, Set<String> fileExtensions, FileObservers fileObservers,
            ExecutePolicy policy, Thread.UncaughtExceptionHandler handler) {
        return new DefaultDirectoryMonitor(directory, fileExtensions, fileObservers, policy, handler);
    }
}
