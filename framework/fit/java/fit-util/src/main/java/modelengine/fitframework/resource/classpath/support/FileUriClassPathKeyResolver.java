/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.classpath.support;

import modelengine.fitframework.resource.classpath.ClassPathKey;
import modelengine.fitframework.resource.classpath.UriClassPathKeyResolver;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

/**
 * 为 {@link UriClassPathKeyResolver} 提供基于文件的实现。
 *
 * @author 梁济时
 * @since 2022-07-27
 */
public class FileUriClassPathKeyResolver implements UriClassPathKeyResolver {
    /**
     * 获取 {@link FileUriClassPathKeyResolver} 的唯一实例。
     */
    public static final FileUriClassPathKeyResolver INSTANCE = new FileUriClassPathKeyResolver();

    private FileUriClassPathKeyResolver() {}

    @Override
    public Optional<ClassPathKey> resolve(URI uri) throws IOException {
        if (StringUtils.equalsIgnoreCase(uri.getScheme(), "file")) {
            File file = new File(uri.getSchemeSpecificPart());
            return Optional.of(new FileClassPathKey(file));
        } else {
            return Optional.empty();
        }
    }
}
