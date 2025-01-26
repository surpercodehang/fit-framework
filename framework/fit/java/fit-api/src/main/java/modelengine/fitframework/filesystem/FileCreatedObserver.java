/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.filesystem;

import java.io.File;
import java.io.IOException;

/**
 * 表示文件新增的观察者。
 *
 * @author 季聿阶
 * @since 2023-07-26
 */
@FunctionalInterface
public interface FileCreatedObserver {
    /**
     * 获取空的观察者。
     *
     * @return 表示空的观察者的 {@link FileCreatedObserver}。
     */
    static FileCreatedObserver empty() {
        return file -> {};
    }

    /**
     * 表示当文件新增时，触发的事件。
     *
     * @param file 表示新增的文件的 {@link File}。
     * @throws IOException 当处理 {@code file} 新增事件过程中发生 IO 异常时。
     */
    void onFileCreated(File file) throws IOException;
}
