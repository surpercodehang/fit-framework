/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import modelengine.fitframework.io.InputStreamSupplier;
import modelengine.fitframework.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 为 {@link Resource} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-01-12
 */
public final class DefaultResource implements Resource {
    private final String name;
    private final URL url;
    private final InputStreamSupplier inputStreamSupplier;

    public DefaultResource(String name, URL url, InputStreamSupplier inputStreamSupplier) {
        this.name = name;
        this.url = url;
        this.inputStreamSupplier = inputStreamSupplier;
    }

    @Override
    public String filename() {
        return this.name;
    }

    @Override
    public URL url() {
        return this.url;
    }

    @Override
    public InputStream read() throws IOException {
        return this.inputStreamSupplier.get();
    }

    @Override
    public String toString() {
        return this.url.toExternalForm();
    }
}
