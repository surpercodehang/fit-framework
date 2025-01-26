/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 为对 {@code .zip} 格式文件的操作提供基类。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-10-05
 */
public abstract class AbstractZip<T extends AbstractZip<?>> {
    private final File zipFile;
    private final Charset charset;

    private boolean override;

    public AbstractZip(File zipFile, Charset charset) {
        this.zipFile = Validation.notNull(zipFile, "The zip file to process cannot be null.");
        this.charset = ObjectUtils.nullIf(charset, FileUtils.DEFAULT_CHARSET);
    }

    /**
     * 设置属性：是否覆盖。
     *
     * @param override 表示是否覆盖的属性的 {@code boolean}。
     * @return 表示设置属性后的自身对象的 {@link T}。
     */
    public T override(boolean override) {
        this.override = override;
        return cast(this);
    }

    /**
     * 获取待打包的文件。
     *
     * @return 表示待打包的文件的 {@link File}。
     */
    protected final File file() {
        return this.zipFile;
    }

    /**
     * 获取打包的字符集。
     *
     * @return 表示待打包的字符集的 {@link Charset}。
     */
    protected final Charset charset() {
        return this.charset;
    }

    /**
     * 获取是否覆盖的属性。
     *
     * @return 表示是否覆盖属性的 {@code boolean}。
     */
    protected final boolean override() {
        return this.override;
    }

    /**
     * 开始进行打包或者解包。
     *
     * @throws IOException 当打包或者解包过程中发生异常时。
     */
    public abstract void start() throws IOException;
}
