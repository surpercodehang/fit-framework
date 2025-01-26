/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.util;

import java.net.URL;
import java.util.Optional;

/**
 * 测试资源工具
 *
 * @author 夏斐
 * @since 1.0
 */
public class TestResource {
    /**
     * 测试文件前缀
     */
    public static final String OHSCRIPT = "ohscript/";

    /**
     * 获取文件路径
     *
     * @param fileName 文件名
     * @return 文件路径
     */
    public static String getFilePath(String fileName) {
        return Optional.ofNullable(TestResource.class.getClassLoader().getResource(TestResource.OHSCRIPT + fileName))
                .map(URL::getFile)
                .orElse(null);
    }
}
