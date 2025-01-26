/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.model.Version;
import modelengine.fitframework.plugin.PluginKey;
import modelengine.fitframework.util.CharacterUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 为 {@link PluginKey} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-09-05
 */
public class DefaultPluginKey implements PluginKey {
    private static final char GROUP_SEPARATOR = '.';

    private final String group;
    private final String name;
    private final Version version;

    public DefaultPluginKey(String group, String name, Version version) {
        this.group = group(group);
        this.name = name(name);
        this.version = notNull(version, "The version of a plugin cannot be null.");
    }

    private static String group(String value) {
        String actual = StringUtils.trim(value);
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("The group of a plugin cannot be an blank string.");
        }
        String[] parts = StringUtils.split(actual, GROUP_SEPARATOR);
        int offset = 0;
        for (String part : parts) {
            validateGroupPart(actual, offset, part);
            offset += part.length() + 1;
        }
        return actual;
    }

    private static void validateGroupPart(String group, int offset, String part) {
        if (part.isEmpty()) {
            throw new IllegalArgumentException(StringUtils.format(
                    "Any part of plugin group cannot be an empty string. [group={0}, position={1}]",
                    group,
                    offset));
        }
        for (int i = 0; i < part.length(); i++) {
            char ch = part.charAt(i);
            if (notLetterOrDigit(ch)) {
                throw new IllegalArgumentException(StringUtils.format(
                        "Any part of plugin group must consist of letters and numbers. [group={0}, position={1}]",
                        group,
                        offset + i));
            }
        }
    }

    private static String name(String name) {
        String actual = StringUtils.trim(name);
        for (int i = 0; i < actual.length(); i++) {
            char ch = actual.charAt(i);
            if (notLetterOrDigit(ch) && ch != '-') {
                throw new IllegalArgumentException(StringUtils.format(
                        "The name of a plugin can only contain letters, digits or '-'. [name={0}, position={1}]",
                        name,
                        i));
            }
        }
        return actual;
    }

    private static boolean notLetterOrDigit(char ch) {
        return !CharacterUtils.between(ch, '0', '9') && !CharacterUtils.between(ch, 'a', 'z')
                && !CharacterUtils.between(ch, 'A', 'Z');
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String version() {
        return this.version.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DefaultPluginKey) {
            DefaultPluginKey another = (DefaultPluginKey) obj;
            return Objects.equals(this.group(), another.group()) && Objects.equals(this.name(), another.name())
                    && Objects.equals(this.version(), another.version());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {DefaultPluginKey.class, this.group(), this.name(), this.version()});
    }

    @Override
    public String toString() {
        return StringUtils.format("[group={0}, name={1}, version={2}]", this.group(), this.name(), this.version());
    }
}
