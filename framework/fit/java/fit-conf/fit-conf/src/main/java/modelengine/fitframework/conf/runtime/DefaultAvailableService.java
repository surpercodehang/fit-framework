/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.runtime;

import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link MatataConfig.Registry.AvailableService} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-06-27
 */
public class DefaultAvailableService implements MatataConfig.Registry.AvailableService {
    private String genericableName;
    private String genericableId;
    private String genericableVersion;
    private String fitableId;
    private String fitableVersion;
    private List<Integer> formats;

    /**
     * 设置服务名字的配置。
     *
     * @param genericableName 表示待设置的服务名字配置的 {@link String}。
     */
    public void setGenericableName(String genericableName) {
        this.genericableName = genericableName;
    }

    /**
     * 设置服务的唯一标识的配置。
     *
     * @param genericableId 表示待设置的服务唯一标识配置的 {@link String}。
     */
    public void setGenericableId(String genericableId) {
        this.genericableId = genericableId;
    }

    /**
     * 设置服务版本号的配置。
     *
     * @param genericableVersion 表示待设置的服务版本号配置的 {@link String}。
     */
    public void setGenericableVersion(String genericableVersion) {
        this.genericableVersion = genericableVersion;
    }

    /**
     * 设置服务实现的唯一标识的配置。
     *
     * @param fitableId 表示待设置的服务实现唯一标识配置的 {@link String}。
     */
    public void setFitableId(String fitableId) {
        this.fitableId = fitableId;
    }

    /**
     * 设置服务实现版本号的配置。
     *
     * @param fitableVersion 表示待设置的服务实现版本号配置的 {@link String}。
     */
    public void setFitableVersion(String fitableVersion) {
        this.fitableVersion = fitableVersion;
    }

    /**
     * 设置服务支持的序列化方式列表的配置。
     *
     * @param formats 表示待设置的服务支持的序列化方式列表配置的 {@link List}{@code <}{@link Integer}{@code >}。
     */
    public void setFormats(List<Integer> formats) {
        this.formats = ObjectUtils.nullIf(formats, Collections.emptyList());
    }

    @Override
    public String genericableName() {
        return this.genericableName;
    }

    @Override
    public String genericableId() {
        return this.genericableId;
    }

    @Override
    public String genericableVersion() {
        return this.genericableVersion;
    }

    @Override
    public String fitableId() {
        return this.fitableId;
    }

    @Override
    public String fitableVersion() {
        return this.fitableVersion;
    }

    @Override
    public List<Integer> formatCodes() {
        return Collections.unmodifiableList(this.formats);
    }

    @Override
    public List<SerializationFormat> formats() {
        return Collections.unmodifiableList(this.formats)
                .stream()
                .map(SerializationFormat::from)
                .collect(Collectors.toList());
    }

    @Override
    public UniqueFitableId toUniqueId() {
        return UniqueFitableId.create(this.genericableId,
                this.genericableVersion(),
                this.fitableId(),
                this.fitableVersion());
    }

    @Override
    public String toString() {
        return StringUtils.format(
                "/{\"genericable-name\": \"{0}\", \"genericable-id\": \"{1}\", \"genericable-version\": \"{2}\", "
                        + "\"fitable-id\": \"{3}\", \"fitable-version\": \"{4}\", \"formats\": {5}/}",
                this.genericableName,
                this.genericableId,
                this.genericableVersion,
                this.fitableId,
                this.fitableVersion,
                this.formats);
    }
}
