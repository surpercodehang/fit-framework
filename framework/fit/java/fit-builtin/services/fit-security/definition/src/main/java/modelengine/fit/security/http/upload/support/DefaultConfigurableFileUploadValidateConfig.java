/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.upload.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.security.http.name.FileNameValidateConfig;
import modelengine.fit.security.http.name.support.DefaultConfigurableFileNameValidateConfig;
import modelengine.fit.security.http.upload.ConfigurableFileUploadValidateConfig;

/**
 * 表示 {@link ConfigurableFileUploadValidateConfig} 的默认实现。
 * <p>配置项不能够设置为空，如果设置为空则自动设定为默认初始值。</p>
 *
 * @author 何天放
 * @since 2024-07-12
 */
public final class DefaultConfigurableFileUploadValidateConfig implements ConfigurableFileUploadValidateConfig {
    /**
     * 表示 {@link ConfigurableFileUploadValidateConfig} 默认实现的实例，通过该实例作为配置对于文件进行校验时将不会进行任何实质性校验。
     */
    public static final ConfigurableFileUploadValidateConfig INSTANCE =
            new DefaultConfigurableFileUploadValidateConfig();

    private static final long DEFAULT_FILE_SIZE_LIMIT = -1L;
    private static final String DEFAULT_FILE_SAVE_PATH = "";
    private static final long DEFAULT_FILE_SAVE_PATH_FILE_COUNT_LIMIT = -1L;
    private static final long DEFAULT_FILE_SAVE_PATH_REST_SPACE_LIMIT = -1L;

    private long fileSizeLimit = DEFAULT_FILE_SIZE_LIMIT;
    private String fileSavePath = DEFAULT_FILE_SAVE_PATH;
    private long fileSavePathFileCountLimit = DEFAULT_FILE_SAVE_PATH_FILE_COUNT_LIMIT;
    private long fileSavePathRestSpaceLimit = DEFAULT_FILE_SAVE_PATH_REST_SPACE_LIMIT;
    private FileNameValidateConfig fileNameValidateConfig = DefaultConfigurableFileNameValidateConfig.INSTANCE;

    @Override
    public ConfigurableFileUploadValidateConfig fileNameValidateConfig(FileNameValidateConfig fileNameValidateConfig) {
        this.fileNameValidateConfig =
                nullIf(fileNameValidateConfig, DefaultConfigurableFileNameValidateConfig.INSTANCE);
        return this;
    }

    @Override
    public ConfigurableFileUploadValidateConfig fileSizeLimit(long fileSizeLimit) {
        this.fileSizeLimit = fileSizeLimit;
        return this;
    }

    @Override
    public ConfigurableFileUploadValidateConfig fileSavePath(String fileSavePath) {
        this.fileSavePath = nullIf(fileSavePath, DEFAULT_FILE_SAVE_PATH);
        return this;
    }

    @Override
    public ConfigurableFileUploadValidateConfig fileSavePathFileCountLimit(long limit) {
        this.fileSavePathFileCountLimit = limit;
        return this;
    }

    @Override
    public ConfigurableFileUploadValidateConfig fileSavePathRestSpaceLimit(long limit) {
        this.fileSavePathRestSpaceLimit = limit;
        return this;
    }

    @Override
    public FileNameValidateConfig fileNameValidateConfig() {
        return this.fileNameValidateConfig;
    }

    @Override
    public long fileSizeLimit() {
        return this.fileSizeLimit;
    }

    @Override
    public String fileSavePath() {
        return this.fileSavePath;
    }

    @Override
    public long fileSavePathFileCountLimit() {
        return this.fileSavePathFileCountLimit;
    }

    @Override
    public long fileSavePathRestSpaceLimit() {
        return this.fileSavePathRestSpaceLimit;
    }
}
