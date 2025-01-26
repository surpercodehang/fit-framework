/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.plugin;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.plugin.PluginCategory;
import modelengine.fitframework.plugin.maven.support.AbstractManifest;
import modelengine.fitframework.util.CodeableEnum;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.XmlUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.OutputStream;

/**
 * 为插件提供 Manifest。
 *
 * @author 梁济时
 * @since 2022-09-06
 */
public class PluginManifest extends AbstractManifest {
    private static final int LEVEL_MINIMUM = 1;
    private static final int LEVEL_MAXIMUM = 7;
    private static final int DEFAULT_LEVEL = 4;

    private static final String ROOT_TAG = "plugin";
    private static final String GROUP_TAG = "group";
    private static final String NAME_TAG = "name";
    private static final String HIERARCHICAL_NAMES_TAG = "hierarchicalNames";
    private static final String VERSION_TAG = "version";
    private static final String CATEGORY_TAG = "category";
    private static final String LEVEL_TAG = "level";

    private final String group;
    private final String name;
    private final String hierarchicalNames;
    private final String version;
    private final PluginCategory category;
    private final int level;

    private PluginManifest(String group, String name, String hierarchicalNames, String version, PluginCategory category,
            int level) {
        this.group = group;
        this.name = name;
        this.hierarchicalNames = hierarchicalNames;
        this.version = version;
        this.category = category;
        this.level = level;
    }

    /**
     * 获取插件的分组。
     *
     * @return 表示插件分组的 {@link String}。
     */
    public final String group() {
        return this.group;
    }

    /**
     * 获取插件的名字。
     *
     * @return 表示插件名字的 {@link String}。
     */
    public final String name() {
        return this.name;
    }

    /**
     * 获取插件的层次化的名字。
     *
     * @return 表示插件的层次化名字的 {@link String}。
     */
    public final String hierarchicalNames() {
        return this.hierarchicalNames;
    }

    /**
     * 获取插件的版本号。
     *
     * @return 表示插件版本号的 {@link String}。
     */
    public final String version() {
        return this.version;
    }

    /**
     * 获取插件的分类。
     *
     * @return 表示插件分类的 {@link PluginCategory}。
     */
    public final PluginCategory category() {
        return this.category;
    }

    /**
     * 获取插件的级别。
     *
     * @return 表示插件级别的 {@code int}。
     */
    public final int level() {
        return this.level;
    }

    /**
     * 将插件元数据信息写入到指定的输出流中。
     *
     * @param out 表示待写入XML元数据信息的输出流的 {@link OutputStream}。
     * @throws IllegalArgumentException 当 {@code out} 为 {@code null} 时。
     * @throws MojoExecutionException 当写入过程中发生输入输出异常时。
     */
    public void write(OutputStream out) throws MojoExecutionException {
        notNull(out, "The output stream to write manifest cannot be null.");
        Document document = XmlUtils.createDocument();
        document.setXmlStandalone(true);
        Element root = XmlUtils.appendElement(document, ROOT_TAG);
        append(root, GROUP_TAG, this.group);
        append(root, NAME_TAG, this.name);
        append(root, HIERARCHICAL_NAMES_TAG, this.hierarchicalNames);
        append(root, VERSION_TAG, this.version);
        append(root, CATEGORY_TAG, this.category().getCode());
        append(root, LEVEL_TAG, Integer.toString(this.level()));
        outputDocument(out, document);
    }

    private static void append(Node parent, String tag, String content) {
        String actual = StringUtils.trim(content);
        if (StringUtils.isEmpty(actual)) {
            return;
        }
        Element node = XmlUtils.appendElement(parent, tag);
        node.setTextContent(actual);
    }

    /**
     * 为 {@link PluginManifest} 提供构建程序。
     *
     * @author 梁济时
     * @since 2022-09-06
     */
    public static class Builder {
        private String group;
        private String name;
        private String hierarchicalNames;
        private String version;
        private String category;
        private String level;

        private Builder() {}

        /**
         * 设置插件的分组。
         *
         * @param group 表示插件分组的 {@link String}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        public Builder group(String group) {
            this.group = group;
            return this;
        }

        /**
         * 设置插件的名字。
         *
         * @param name 表示插件名字的 {@link String}。
         * @return 表示当前构建陈序的 {@link Builder}。
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置插件的层次化的名字。
         *
         * @param hierarchicalNames 表示插件层次化名字的 {@link String}。
         * @return 表示当前构建陈序的 {@link Builder}。
         */
        public Builder hierarchicalNames(String hierarchicalNames) {
            this.hierarchicalNames = hierarchicalNames;
            return this;
        }

        /**
         * 设置插件的版本号。
         *
         * @param version 表示插件版本号的 {@link String}。
         * @return 表示当前构建陈序的 {@link Builder}。
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * 设置插件的分类。
         *
         * @param category 表示插件分类的 {@link PluginCategory}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        public Builder category(String category) {
            this.category = category;
            return this;
        }

        /**
         * 设置插件的级别。
         *
         * @param level 表示插件级别的 {@code int}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        public Builder level(String level) {
            this.level = level;
            return this;
        }

        /**
         * 构建插件 Manifest 的新实例。
         *
         * @return 表示新构建的 Manifest 实例的 {@link PluginManifest}。
         * @throws MojoExecutionException 构建过程发生异常。
         */
        public PluginManifest build() throws MojoExecutionException {
            if (StringUtils.isBlank(this.group)) {
                throw new MojoExecutionException(StringUtils.format("Illegal group for plugin. [group={0}]",
                        this.group));
            }
            if (StringUtils.isBlank(this.name)) {
                throw new MojoExecutionException(StringUtils.format("Illegal name for plugin. [name={0}]", this.name));
            }
            if (StringUtils.isBlank(this.version)) {
                throw new MojoExecutionException(StringUtils.format("Illegal version for plugin. [version={0}]",
                        this.version));
            }
            PluginCategory pluginCategory = this.buildCategory();
            return new PluginManifest(this.group,
                    this.name,
                    this.buildHierarchicalNames(),
                    this.version,
                    pluginCategory,
                    this.buildLevel());
        }

        private String buildHierarchicalNames() {
            if (StringUtils.isNotBlank(this.hierarchicalNames)) {
                return this.hierarchicalNames;
            }
            return String.join(".", StringUtils.splitToList(this.name, "-"));
        }

        private PluginCategory buildCategory() throws MojoExecutionException {
            String actualCategoryValue = StringUtils.trim(this.category);
            PluginCategory actualCategory;
            if (StringUtils.isEmpty(actualCategoryValue)) {
                actualCategory = PluginCategory.USER;
            } else {
                actualCategory = CodeableEnum.fromCode(PluginCategory.class, actualCategoryValue);
                if (actualCategory == null) {
                    throw new MojoExecutionException(StringUtils.format("Illegal category of plugin. [category={0}]",
                            actualCategoryValue));
                }
            }
            return actualCategory;
        }

        private int buildLevel() throws MojoExecutionException {
            String actualLevelValue = StringUtils.trim(this.level);
            int actualLevel;
            if (StringUtils.isEmpty(actualLevelValue)) {
                actualLevel = DEFAULT_LEVEL;
            } else {
                try {
                    actualLevel = Integer.parseInt(actualLevelValue);
                } catch (NumberFormatException ex) {
                    throw new MojoExecutionException(StringUtils.format(
                            "The level of plugin must be an integer. [level={0}]",
                            this.level));
                }
                if (actualLevel < LEVEL_MINIMUM || actualLevel > LEVEL_MAXIMUM) {
                    throw new MojoExecutionException(StringUtils.format(
                            "The level of plugin is out of bounds. [level={0}, minimum={1}, maximum={2}]",
                            this.level,
                            LEVEL_MINIMUM,
                            LEVEL_MAXIMUM));
                }
            }
            return actualLevel;
        }
    }

    /**
     * 返回一个构建程序，用以构建 {@link PluginManifest} 类的新实例。
     *
     * @return 表示用以构建插件 Manifest 的构建程序的 {@link Builder}。
     */
    public static Builder custom() {
        return new Builder();
    }
}
