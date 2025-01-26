/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.filesystem;

import java.io.File;

/**
 * 表示文件目录树遍历开始的观察者。
 *
 * @author 季聿阶
 * @since 2023-11-23
 */
@FunctionalInterface
public interface FileTreeVisitingObserver {
    /**
     * 获取空的观察者。
     *
     * @return 表示空的观察者的 {@link FileTreeVisitingObserver}。
     */
    static FileTreeVisitingObserver empty() {
        return file -> {};
    }

    /**
     * 表示当整个文件目录树访问开始时，触发的事件。
     *
     * @param root 表示整个文件目录树的根文件的 {@link File}。
     */
    void onFileTreeVisiting(File root);
}
