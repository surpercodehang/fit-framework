/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.datasource.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.datasource.AccessMode;
import modelengine.fitframework.util.EnumUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.function.Predicate;

/**
 * 数据源配置类。
 *
 * @author 易文渊
 * @author 李金绪
 * @since 2024-07-27
 */
public class FitDataSourceConfig {
    /**
     * 表示数据源实例的前缀。
     */
    public static final String INSTANCE_PREFIX = "fit.datasource.instances.";

    /**
     * 表示数据源的模式。
     */
    public static final String PRIMARY_MODE = "mode";

    /**
     * 表示分隔符。
     */
    public static final String SEPARATOR = ".";
    private static final String PRIMARY_PREFIX = "fit.datasource.primary.";

    private String name;
    private AccessMode mode;

    /**
     * 创建数据源配置对象。
     *
     * @param config 表示配置的 {@link Config}。
     * @return 表示数据源配置的 {@link FitDataSourceConfig}。
     */
    public static FitDataSourceConfig create(Config config) {
        String primaryName = config.get(PRIMARY_PREFIX, String.class);
        notBlank(primaryName, "The primary data source is not configured.");
        String primaryMode =
                config.get(INSTANCE_PREFIX + primaryName + SEPARATOR + PRIMARY_MODE + SEPARATOR, String.class);
        notBlank(primaryMode, "The primary data source mode is not configured.");
        FitDataSourceConfig fitConfig = new FitDataSourceConfig();
        fitConfig.setName(primaryName);
        fitConfig.setMode(ObjectUtils.cast(toEnum(AccessMode.class, primaryMode)));
        return fitConfig;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccessMode getMode() {
        return this.mode;
    }

    public void setMode(AccessMode mode) {
        this.mode = mode;
    }

    private static Object toEnum(Class<?> enumClass, String value) {
        Class<? extends Enum<?>> actualClass = ObjectUtils.cast(enumClass);
        Predicate<Enum> predicate = enumConstant -> StringUtils.equalsIgnoreCase(enumConstant.toString(), value);
        return EnumUtils.firstOrDefault(ObjectUtils.cast(actualClass), predicate);
    }
}
