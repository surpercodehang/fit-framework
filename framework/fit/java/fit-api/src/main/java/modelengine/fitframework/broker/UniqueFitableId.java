/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

import java.util.Objects;

/**
 * 表示全局唯一的服务实现的唯一标识。
 * <p>该接口的实现可以用于 {@link java.util.Map} 的键或者 {@link java.util.Set} 中。</p>
 *
 * @author 季聿阶
 * @since 2022-09-17
 */
public final class UniqueFitableId implements Comparable<UniqueFitableId> {
    private final UniqueGenericableId uniqueGenericableId;
    private final String fitableId;
    private final String fitableVersion;

    private UniqueFitableId(String genericableId, String genericableVersion, String fitableId, String fitableVersion) {
        notBlank(genericableId, "The genericable id cannot be blank.");
        notBlank(genericableVersion, "The genericable version cannot be blank.");
        this.uniqueGenericableId = UniqueGenericableId.create(genericableId, genericableVersion);
        this.fitableId = notBlank(fitableId, "The fitable id cannot be blank.");
        this.fitableVersion = notBlank(fitableVersion, "The fitable version cannot be blank.");
    }

    /**
     * 获取服务的唯一标识。
     *
     * @return 表示服务的唯一标识的 {@link String}。
     */
    public String genericableId() {
        return this.uniqueGenericableId.genericableId();
    }

    /**
     * 获取服务的版本号。
     *
     * @return 表示服务版本号的 {@link String}。
     */
    public String genericableVersion() {
        return this.uniqueGenericableId.genericableVersion();
    }

    /**
     * 获取服务实现的唯一标识。
     *
     * @return 表示服务实现的唯一标识的 {@link String}。
     */
    public String fitableId() {
        return this.fitableId;
    }

    /**
     * 获取服务实现的版本号。
     *
     * @return 表示服务实现版本号的 {@link String}。
     */
    public String fitableVersion() {
        return this.fitableVersion;
    }

    /**
     * 转换为全局唯一的服务的唯一标识。
     *
     * @return 表示转换后的全局唯一的服务的唯一标识的 {@link UniqueGenericableId}。
     */
    public UniqueGenericableId toUniqueGenericableId() {
        return this.uniqueGenericableId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        UniqueFitableId that = cast(obj);
        return Objects.equals(this.genericableId(), that.genericableId()) && Objects.equals(this.genericableVersion(),
                that.genericableVersion()) && Objects.equals(this.fitableId(), that.fitableId())
                && Objects.equals(this.fitableVersion(), that.fitableVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.genericableId(), this.genericableVersion(), this.fitableId(), this.fitableVersion());
    }

    @Override
    public int compareTo(@Nonnull UniqueFitableId another) {
        if (!Objects.equals(this.genericableId(), another.genericableId())) {
            return StringUtils.compare(this.genericableId(), another.genericableId());
        }
        if (!Objects.equals(this.genericableVersion(), another.genericableVersion())) {
            return StringUtils.compare(this.genericableVersion(), another.genericableVersion());
        }
        if (!Objects.equals(this.fitableId(), another.fitableId())) {
            return StringUtils.compare(this.fitableId(), another.fitableId());
        }
        return StringUtils.compare(this.fitableVersion(), another.fitableVersion());
    }

    @Override
    public String toString() {
        return "{\"genericableId\": \"" + this.genericableId() + '\"' + ", \"genericableVersion\": \""
                + this.genericableVersion() + '\"' + ", \"fitableId\": \"" + this.fitableId + '\"'
                + ", \"fitableVersion\": \"" + this.fitableVersion + '\"' + '}';
    }

    /**
     * 根据指定的服务唯一标识和服务实现唯一标识，创建一个全局服务实现唯一标识。
     *
     * @param genericableId 表示指定服务唯一标识的 {@link String}。
     * @param fitableId 表示指定服务实现唯一标识的 {@link String}。
     * @return 表示创建的全局服务实现唯一标识的 {@link UniqueFitableId}。
     */
    public static UniqueFitableId create(String genericableId, String fitableId) {
        return UniqueFitableId.create(genericableId,
                GenericableMetadata.DEFAULT_VERSION,
                fitableId,
                FitableMetadata.DEFAULT_VERSION);
    }

    /**
     * 根据指定的服务唯一标识、服务版本号、服务实现唯一标识和服务实现版本号，创建一个全局服务实现唯一标识。
     *
     * @param genericableId 表示指定服务唯一标识的 {@link String}。
     * @param genericableVersion 表示指定服务版本号的 {@link String}。
     * @param fitableId 表示指定服务实现唯一标识的 {@link String}。
     * @param fitableVersion 表示指定服务实现版本号的 {@link String}。
     * @return 表示创建的全局服务实现唯一标识的 {@link UniqueFitableId}。
     */
    public static UniqueFitableId create(String genericableId, String genericableVersion, String fitableId,
            String fitableVersion) {
        return new UniqueFitableId(genericableId, genericableVersion, fitableId, fitableVersion);
    }
}
