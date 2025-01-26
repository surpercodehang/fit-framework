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
 * 表示全局唯一的服务的唯一标识。
 * <p>该接口的实现可以用于 {@link java.util.Map} 的键或者 {@link java.util.Set} 中。</p>
 *
 * @author 季聿阶
 * @since 2022-10-24
 */
public final class UniqueGenericableId implements Comparable<UniqueGenericableId> {
    private final String genericableId;
    private final String genericableVersion;

    private UniqueGenericableId(String genericableId, String genericableVersion) {
        this.genericableId = notBlank(genericableId, "The genericable id cannot be blank.");
        this.genericableVersion = notBlank(genericableVersion, "The genericable version cannot be blank.");
    }

    /**
     * 获取服务的唯一标识。
     *
     * @return 表示服务的唯一标识的 {@link String}。
     */
    public String genericableId() {
        return this.genericableId;
    }

    /**
     * 获取服务的版本号。
     *
     * @return 表示服务版本号的 {@link String}。
     */
    public String genericableVersion() {
        return this.genericableVersion;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        UniqueGenericableId that = cast(obj);
        return Objects.equals(this.genericableId(), that.genericableId()) && Objects.equals(this.genericableVersion(),
                that.genericableVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.genericableId(), this.genericableVersion());
    }

    @Override
    public int compareTo(@Nonnull UniqueGenericableId another) {
        if (!Objects.equals(this.genericableId(), another.genericableId())) {
            return StringUtils.compare(this.genericableId(), another.genericableId());
        }
        return StringUtils.compare(this.genericableVersion(), another.genericableVersion());
    }

    @Override
    public String toString() {
        return "{\"genericableId\": \"" + this.genericableId + '\"' + ", \"genericableVersion\": \""
                + this.genericableVersion + '\"' + '}';
    }

    /**
     * 根据指定的服务唯一标识、服务版本号，创建一个全局服务唯一标识。
     *
     * @param genericableId 表示指定服务唯一标识的 {@link String}。
     * @param genericableVersion 表示指定服务版本号的 {@link String}。
     * @return 表示创建的全局服务唯一标识的 {@link UniqueGenericableId}。
     */
    public static UniqueGenericableId create(String genericableId, String genericableVersion) {
        return new UniqueGenericableId(genericableId, genericableVersion);
    }
}
