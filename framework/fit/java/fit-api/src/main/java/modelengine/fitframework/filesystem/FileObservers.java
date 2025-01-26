/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.filesystem;

import modelengine.fitframework.pattern.builder.BuilderFactory;

/**
 * 表示文件新增、变化、删除、访问失败等的观察者集合。
 *
 * @author 季聿阶
 * @since 2023-07-27
 */
public interface FileObservers {
    /**
     * 获取文件新增的观察者。
     *
     * @return 表示文件新增的观察者的 {@link FileCreatedObserver}。
     */
    FileCreatedObserver created();

    /**
     * 获取文件变化的观察者。
     *
     * @return 表示文件变化的观察者的 {@link FileChangedObserver}。
     */
    FileChangedObserver changed();

    /**
     * 获取文件删除的观察者。
     *
     * @return 表示文件删除的观察者的 {@link FileDeletedObserver}。
     */
    FileDeletedObserver deleted();

    /**
     * 获取文件访问失败的观察者。
     *
     * @return 表示文件访问失败的观察者的 {@link FileVisitedFailedObserver}。
     */
    FileVisitedFailedObserver visitedFailed();

    /**
     * 获取整个文件目录树访问开始的观察者。
     *
     * @return 表示整个文件目录树访问开始的观察者的 {@link FileTreeVisitingObserver}。
     */
    FileTreeVisitingObserver treeVisiting();

    /**
     * 获取整个文件目录树访问完成的观察者。
     *
     * @return 表示整个文件目录树访问完成的观察者的 {@link FileTreeVisitedObserver}。
     */
    FileTreeVisitedObserver treeVisited();

    /**
     * {@link FileObservers} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置文件新增的观察者。
         *
         * @param observer 表示待设置的观察者的 {@link FileCreatedObserver}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder created(FileCreatedObserver observer);

        /**
         * 向当前构建器中设置文件变化的观察者。
         *
         * @param observer 表示待设置的观察者的 {@link FileChangedObserver}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder changed(FileChangedObserver observer);

        /**
         * 向当前构建器中设置文件删除的观察者。
         *
         * @param observer 表示待设置的观察者的 {@link FileDeletedObserver}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder deleted(FileDeletedObserver observer);

        /**
         * 向当前构建器中设置访问文件失败的观察者。
         *
         * @param observer 表示待设置的观察者的 {@link FileVisitedFailedObserver}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder visitedFailed(FileVisitedFailedObserver observer);

        /**
         * 向当前构建器中设置整个文件目录树访问开始的观察者。
         *
         * @param observer 表示待设置的观察者的 {@link FileTreeVisitingObserver}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder treeVisiting(FileTreeVisitingObserver observer);

        /**
         * 向当前构建器中设置整个文件目录树访问完成的观察者。
         *
         * @param observer 表示待设置的观察者的 {@link FileTreeVisitedObserver}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder treeVisited(FileTreeVisitedObserver observer);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link FileObservers}。
         */
        FileObservers build();
    }

    /**
     * 获取 {@link FileObservers} 的构建器。
     *
     * @return 表示 {@link FileObservers} 的构建器的 {@link Builder}。
     */
    static Builder builder() {
        return builder(null);
    }

    /**
     * 获取 {@link FileObservers} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link FileObservers}。
     * @return 表示 {@link FileObservers} 的构建器的 {@link Builder}。
     */
    static Builder builder(FileObservers value) {
        return BuilderFactory.get(FileObservers.class, Builder.class).create(value);
    }
}
