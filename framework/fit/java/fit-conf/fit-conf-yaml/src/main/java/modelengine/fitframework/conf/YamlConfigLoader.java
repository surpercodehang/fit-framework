/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

import modelengine.fitframework.conf.support.AbstractConfigLoader;
import modelengine.fitframework.parameterization.ParameterizedString;
import modelengine.fitframework.parameterization.ParameterizedStringResolver;
import modelengine.fitframework.parameterization.ResolvedParameter;
import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.StringUtils;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 为 {@link ConfigLoader} 提供基于YAML格式的实现。
 *
 * @author 梁济时
 * @since 2022-05-25
 */
public class YamlConfigLoader extends AbstractConfigLoader {
    private static final Set<String> EXTENSIONS = new HashSet<>(Arrays.asList(".yaml", ".yml"));

    @Override
    public Set<String> extensions() {
        return Collections.unmodifiableSet(EXTENSIONS);
    }

    @Override
    public ConfigLoadingResult load(Resource resource, String name) {
        String extension = FileUtils.extension(resource.filename());
        if (!EXTENSIONS.contains(StringUtils.toLowerCase(extension))) {
            return ConfigLoadingResult.failure();
        }
        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
        Map<String, Object> configValues;
        try (InputStream in = resource.read()) {
            configValues = yaml.load(this.replacePlaceholders(IoUtils.content(in)));
        } catch (IOException | YAMLException e) {
            throw new ConfigLoadException(StringUtils.format("Failed to parse YAML from config resource. [url={0}]",
                    resource), e);
        } catch (ClassCastException e) {
            throw new ConfigLoadException(StringUtils.format("The content of config must be an object. [url={0}]",
                    resource), e);
        }
        String configName = this.nameOfConfig(resource, name);
        Config config = Config.fromHierarchical(configName, configValues);
        return ConfigLoadingResult.success(config);
    }

    private String replacePlaceholders(String value) {
        ParameterizedStringResolver resolver = ParameterizedStringResolver.create("${", "}", '\0', false);
        ParameterizedString template = resolver.resolve(value);
        List<ResolvedParameter> parameters = template.getParameters();
        Map<String, String> arguments = new HashMap<>(parameters.size());
        for (ResolvedParameter parameter : parameters) {
            String argumentValue = getValue(parameter).orElse(null);
            arguments.put(parameter.getName(), argumentValue);
        }
        return template.format(arguments);
    }

    private static Optional<String> getValue(ResolvedParameter parameter) {
        String propertyValue = System.getProperty(parameter.getName());
        if (StringUtils.isNotBlank(propertyValue)) {
            return Optional.of(propertyValue);
        }
        String envValue = System.getenv(parameter.getName());
        if (StringUtils.isNotBlank(envValue)) {
            return Optional.of(envValue);
        }
        return Optional.empty();
    }
}
