/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import modelengine.fitframework.protocol.jar.support.WeakHashMapFilesCache;

import java.io.File;

/**
 * 表示文件缓存器的接口。
 *
 * @author 杭潇
 * @since 2025-02-11
 */
public interface FilesCache {
    /**
     * 根据给定文件获取其标准化值。
     *
     * @param file 表示给定文件的 {@link File}。
     * @return 获取标准化文件的 {@link File}。
     */
    File getCanonicalFile(File file);

    /**
     * 获取文件缓存器的实例。
     *
     * @return 表示文件缓存器实例的 {@link FilesCache}。
     */
    static FilesCache instance() {
        return WeakHashMapFilesCache.INSTANCE;
    }
}
