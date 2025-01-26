/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime.discrete;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.plugin.RootPlugin;
import modelengine.fitframework.runtime.FitRuntimeStartupException;
import modelengine.fitframework.runtime.shared.SharedUrlClassLoader;
import modelengine.fitframework.runtime.support.AbstractFitRuntime;
import modelengine.fitframework.util.ArrayUtils;
import modelengine.fitframework.util.ClassUtils;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.net.URL;

/**
 * 为 FIT 运行时提供离散启动场景的实现。
 * <p>离散启动指的是 FIT 框架和必要的系统插件安装在文件系统中，用户插件在用户指定目录进行启动。</p>
 *
 * @author 季聿阶
 * @since 2023-07-29
 */
public class DiscreteFitRuntime extends AbstractFitRuntime {
    private static final String FIT_YAML = "fitframework.yaml";
    private static final String FIT_YML = "fitframework.yml";

    /**
     * 使用入口类和命令行参数来初始化 {@link DiscreteFitRuntime} 类的新实例。
     *
     * @param entry 表示入口类的 {@link Class}{@code <?>}。
     * @param args 表示命令行参数的 {@link String}{@code []}。
     */
    public DiscreteFitRuntime(Class<?> entry, String[] args) {
        super(entry, args);
    }

    @Override
    protected URL locateRuntime() {
        URL domain = ClassUtils.locateOfProtectionDomain(DiscreteFitRuntime.class);
        File codeFile = FileUtils.file(domain);
        notNull(codeFile,
                () -> new IllegalStateException(StringUtils.format("Failed to locate runtime JAR. [class={0}]",
                        DiscreteFitRuntime.class.getName())));
        notNull(codeFile.getParentFile(), () -> new IllegalStateException("Failed to locate lib directory."));
        return FileUtils.urlOf(codeFile.getParentFile().getParentFile());
    }

    @Override
    protected SharedUrlClassLoader obtainSharedClassLoader() {
        return new SharedUrlClassLoader(new URL[0], DiscreteFitRuntime.class.getClassLoader().getParent());
    }

    @Override
    protected String getExternalConfigFileName(Config config) {
        String externalConfigFileName = super.getExternalConfigFileName(config);
        if (StringUtils.isNotBlank(externalConfigFileName)) {
            return externalConfigFileName;
        }
        File root = FileUtils.file(this.location());
        File confDirectory = new File(root, "conf");
        if (!confDirectory.isDirectory()) {
            return null;
        }
        File[] files = confDirectory.listFiles((file, name) -> StringUtils.equalsIgnoreCase(name, FIT_YAML)
                || StringUtils.equalsIgnoreCase(name, FIT_YML));
        if (ArrayUtils.isEmpty(files)) {
            return null;
        }
        if (files.length > 1) {
            throw new FitRuntimeStartupException(StringUtils.format(
                    "Too many external config files, reserve '{1}' or '{2}'. [num={0}]",
                    files.length,
                    FIT_YAML,
                    FIT_YML));
        }
        return FileUtils.canonicalize(files[0]).getPath();
    }

    @Override
    protected RootPlugin createRootPlugin() {
        return new DiscreteRootPlugin(this);
    }
}
