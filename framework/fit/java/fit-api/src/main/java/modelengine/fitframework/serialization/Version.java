/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization;

import modelengine.fitframework.serialization.support.DefaultVersion;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 为泛服务及其实现提供版本信息。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-11-13
 */
public interface Version {
    /**
     * 获取主版本号。
     *
     * @return 表示主版本号的字节。
     */
    byte major();

    /**
     * 获取次版本号。
     *
     * @return 表示次版本号的字节。
     */
    byte minor();

    /**
     * 获取修订版本号。
     *
     * @return 表示修订版本号的字节。
     */
    byte revision();

    /**
     * 获取版本的格式化文本内容。
     *
     * @return 表示版本的格式化文本内容的 {@link String}。
     */
    @Override
    String toString();

    /**
     * 为 {@link Version} 提供构建器。
     *
     * @author 梁济时
     * @since 2020-11-13
     */
    interface Builder {
        /**
         * 设置主版本号。
         *
         * @param major 表示主版本号的字节。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder major(byte major);

        /**
         * 设置次版本号。
         *
         * @param minor 表示次版本号的字节。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder minor(byte minor);

        /**
         * 设置修订版本号。
         *
         * @param revision 表示修订版本号的字节。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder revision(byte revision);

        /**
         * 构建一个版本的新实例。
         *
         * @return 表示包含构建器中包含的信息的版本实例的 {@link Version}。
         */
        Version build();
    }

    /**
     * 返回一个构建器，用以生成版本信息默认实现的新实例。
     *
     * @return 表示用以生成版本信息默认实现的构建器的 {@link Builder}。
     */
    static Builder builder() {
        return builder(ObjectUtils.<Version>cast(null));
    }

    /**
     * 返回一个构建器，用以生成携带指定版本信息的新实例。
     *
     * @param another 表示构建器初始状态包含信息的 {@link Version}。
     * @return 表示用以生成携带指定版本信息的新实例的构建器的 {@link Builder}。
     */
    static Builder builder(Version another) {
        return new DefaultVersion.Builder(another);
    }

    /**
     * 返回一个构建器，用以生成携带指定版本信息的新实例。
     *
     * @param versionString 表示指定版本信息的字符串形式的 {@link String}。
     * @return 表示用以生成携带指定版本信息的新实例的构建器的 {@link Builder}。
     */
    static Builder builder(String versionString) {
        return new DefaultVersion.Builder(versionString);
    }

    /**
     * 返回一个构建器，包含当前版本信息的数据。
     *
     * @return 表示包含当前版本信息的构建器的 {@link Builder}。
     */
    default Builder copy() {
        return builder(this);
    }

    /**
     * 返回一个序列化组件，用以对版本信息进行序列化与反序列化。
     *
     * @return 表示用以对版本信息进行序列化和反序列化的 {@link ByteSerializer}。
     */
    static ByteSerializer<Version> serializer() {
        return DefaultVersion.Serializer.INSTANCE;
    }
}
