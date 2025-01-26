/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.util;

import modelengine.fitframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 表示创建 zip 文件的工具。
 *
 * @author 何天放
 * @since 2024-07-25
 */
public final class CreateZipUtils {
    private CreateZipUtils() {}

    /**
     * 根据指定的压缩文件路径、名称以及子文件数量创建 zip 类型的压缩文件。
     *
     * @param filePath 表示所创建压缩文件所在目录的 {@link String}。
     * @param fileName 表示所创建压缩文件名称的 {@link String}。
     * @param entryLengths 表示压缩文件中各个子文件大小的 {@link Integer}{@code []}。
     * @throws IOException 当文件输出流创建失败和写入内容失败时。
     */
    public static void create(String filePath, String fileName, Integer[] entryLengths) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath + "/" + fileName);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            for (int index = 0; index < entryLengths.length; index++) {
                String entryName = StringUtils.format("{0}.txt", index);
                ZipEntry zipEntry = new ZipEntry(entryName);
                zipOutputStream.putNextEntry(zipEntry);
                byte[] buffer = new byte[entryLengths[index]];
                Random random = new Random();
                random.nextBytes(buffer);
                zipOutputStream.write(buffer);
                zipOutputStream.closeEntry();
            }
        }
    }
}

