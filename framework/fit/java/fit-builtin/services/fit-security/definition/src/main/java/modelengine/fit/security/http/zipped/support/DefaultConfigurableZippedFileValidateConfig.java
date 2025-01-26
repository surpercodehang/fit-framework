/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.zipped.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.security.http.name.FileNameValidateConfig;
import modelengine.fit.security.http.name.support.DefaultConfigurableFileNameValidateConfig;
import modelengine.fit.security.http.zipped.ConfigurableZippedFileValidateConfig;

/**
 * 表示 {@link ConfigurableZippedFileValidateConfig} 的默认实现。
 * <p>配置项不能够设置为空，如果设置为空则自动设定为默认初始值。</p>
 *
 * @author 何天放
 * @since 2024-07-18
 */
public final class DefaultConfigurableZippedFileValidateConfig implements ConfigurableZippedFileValidateConfig {
    /**
     * 表示 {@link ConfigurableZippedFileValidateConfig} 默认实现的实例，通过该实例作为配置对于文件进行校验时将不会进行任何实质性校验。
     */
    public static final ConfigurableZippedFileValidateConfig INSTANCE =
            new DefaultConfigurableZippedFileValidateConfig();

    private FileNameValidateConfig fileNameValidateConfig = DefaultConfigurableFileNameValidateConfig.INSTANCE;
    private long zippedFileEntryCountLimit;
    private long zippedFileTotalSizeLimit;

    @Override
    public ConfigurableZippedFileValidateConfig fileNameValidateConfig(FileNameValidateConfig fileNameValidateConfig) {
        this.fileNameValidateConfig =
                nullIf(fileNameValidateConfig, DefaultConfigurableFileNameValidateConfig.INSTANCE);
        return this;
    }

    @Override
    public ConfigurableZippedFileValidateConfig zippedFileEntryCountLimit(long zippedFileEntryCountLimit) {
        this.zippedFileEntryCountLimit = zippedFileEntryCountLimit;
        return this;
    }

    @Override
    public ConfigurableZippedFileValidateConfig zippedFileTotalSizeLimit(long zippedFileTotalSizeLimit) {
        this.zippedFileTotalSizeLimit = zippedFileTotalSizeLimit;
        return this;
    }

    @Override
    public FileNameValidateConfig fileNameValidateConfig() {
        return this.fileNameValidateConfig;
    }

    @Override
    public long zippedFileEntryCountLimit() {
        return this.zippedFileEntryCountLimit;
    }

    @Override
    public long zippedFileTotalSizeLimit() {
        return this.zippedFileTotalSizeLimit;
    }
}
