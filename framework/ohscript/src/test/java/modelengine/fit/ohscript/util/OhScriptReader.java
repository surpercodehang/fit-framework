/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.util;

import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 用于从文件中读取 OhScript 脚本内容。
 *
 * @author 季聿阶
 * @since 2023-10-20
 */
public class OhScriptReader {
    /**
     * 通过指定文件名读取 OhScript 脚本文件。
     *
     * @param fileName 表示 OhScript 脚本文件的名字的 {@link String}。
     * @return 表示 OhScript 脚本文件内容的 {@link String}。
     */
    public static String read(String fileName) {
        URL resource = OhScriptReader.class.getClassLoader().getResource(TestResource.OHSCRIPT + fileName);
        String actualFile = "test/resources/ohscript" + fileName;
        if (resource == null) {
            throw new IllegalStateException(StringUtils.format("No ohscript file. [file={0}]", actualFile));
        }
        try (InputStream in = resource.openStream()) {
            return IoUtils.content(in);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to read ohscript file. [file={0}]", actualFile),
                    e);
        }
    }
}
