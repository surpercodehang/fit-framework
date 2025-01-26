/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.support;

import modelengine.fitframework.plugin.maven.MavenCoordinate;
import modelengine.fitframework.plugin.maven.ResolvedDependency;
import modelengine.fitframework.plugin.maven.ResolvedDependencyPrinter;

import org.apache.maven.plugin.logging.Log;

import java.util.Collection;
import java.util.Locale;

/**
 * 解析依赖项 {@link ResolvedDependencyPrinter} 时打印日志类。
 *
 * @author 陈镕希
 * @since 2020-12-26
 */
public class LogResolvedDependencyPrinter implements ResolvedDependencyPrinter {
    private final String title;

    /** 此处使用的Log是maven自带的log，因此不能加入static */
    private final Log log;

    public LogResolvedDependencyPrinter(Log log, String title) {
        this.title = title;
        this.log = log;
    }

    private void info(String format, Object... args) {
        this.log.info(String.format(Locale.ROOT, format, args));
    }

    private void warn(String format, Object... args) {
        this.log.warn(String.format(Locale.ROOT, format, args));
    }

    @Override
    public void print(ResolvedDependency dependency) {
        this.info("");
        this.info("Dependency Resolution: " + this.title);
        this.printRedundantDependencies(dependency.getRedundantDependencies());
        this.printMissingDependencies(dependency);
    }

    private void printRedundantDependencies(Collection<MavenCoordinate> dependencies) {
        if (dependencies.isEmpty()) {
            this.info("No redundant dependency detected.");
            return;
        }
        this.warn("Total %d redundant dependencies detected:", dependencies.size());
        dependencies.stream()
                .map(MavenCoordinate::toString)
                .sorted()
                .forEach(dependency -> this.warn("  Redundant dependency: " + dependency));
    }

    private void printMissingDependencies(ResolvedDependency dependency) {
        Collection<MavenCoordinate> missingDependencies = dependency.getMissingDependencies();
        if (missingDependencies.isEmpty()) {
            this.info("No missing dependency detected.");
            return;
        }
        this.warn("Total %d missing dependencies detected:", missingDependencies.size());
        missingDependencies.forEach(missingDependency -> this.printMissingDependency(dependency, missingDependency));
    }

    private void printMissingDependency(ResolvedDependency dependency, MavenCoordinate missing) {
        this.warn("  Missing dependency: " + missing);
        Collection<String> dependedClassNames = dependency.getMissingDependedClassNames(missing);
        dependedClassNames.forEach(className -> {
            this.warn("    Depended on class: " + className);
            Collection<String> usageClassNames = dependency.getMissingDependencyUserClassNames(missing, className);
            usageClassNames.forEach(userClassName -> this.warn("      Used by: " + userClassName));
        });
    }
}
