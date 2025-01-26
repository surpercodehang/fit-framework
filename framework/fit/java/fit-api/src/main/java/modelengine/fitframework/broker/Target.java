/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import modelengine.fitframework.broker.support.DefaultTarget;

import java.util.List;
import java.util.Map;

/**
 * 服务地址信息。
 *
 * @author 季聿阶
 * @since 2021-08-20
 */
public interface Target {
    /**
     * 获取进程唯一标识。
     *
     * @return 表示进程唯一标识的 {@link String}。
     */
    String workerId();

    /**
     * 获取进程所在的主机地址。
     *
     * @return 表示进程所在主机地址的 {@link String}。
     */
    String host();

    /**
     * 获取进程的环境标。
     *
     * @return 表示进程的环境标的 {@link String}。
     */
    String environment();

    /**
     * 获取提供的服务端点列表。
     *
     * @return 表示提供的服务端点列表的 {@link List}{@code <}{@link Endpoint}{@code >}。
     */
    List<Endpoint> endpoints();

    /**
     * 获取提供的序列化方式列表。
     *
     * @return 表示提供的序列化方式列表的 {@link List}{@code <}{@link Format}{@code >}。
     */
    List<Format> formats();

    /**
     * 获取进程的扩展信息集合。
     *
     * @return 表示进程的扩展信息集合的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    Map<String, String> extensions();

    /**
     * {@link Target} 的构建器。
     */
    interface Builder {
        /**
         * 向构建器中设置进程唯一标识。
         *
         * @param workerId 表示待设置的进程唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder workerId(String workerId);

        /**
         * 向构建器中设置进程所在的主机地址。
         *
         * @param host 表示待设置的进程所在的主机地址的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder host(String host);

        /**
         * 向构建器中设置进程的环境标。
         *
         * @param environment 表示待设置的进程的环境标的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder environment(String environment);

        /**
         * 向构建器中设置提供的服务端点列表。
         *
         * @param endpoints 表示待设置的提供的服务端点列表的 {@link List}{@code <}{@link Endpoint}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder endpoints(List<Endpoint> endpoints);

        /**
         * 向构建器中设置提供的序列化方式列表。
         *
         * @param formats 表示待设置的提供的序列化方式列表的 {@link List}{@code <}{@link Format}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder formats(List<Format> formats);

        /**
         * 向构建器中设置进程的扩展信息集合。
         *
         * @param extensions 表示待设置的进程的扩展信息集合的 {@link Map}{@code <}{@link String}{@code
         * , }{@link String}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder extensions(Map<String, String> extensions);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Target}。
         */
        Target build();
    }

    /**
     * 获取 {@link Target} 的构建器。
     *
     * @return 表示 {@link Target} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return custom(null);
    }

    /**
     * 获取 {@link Target} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link Target}。
     * @return 表示 {@link Target} 的构建器的 {@link Builder}。
     */
    static Builder custom(Target value) {
        return new DefaultTarget.Builder(value);
    }
}
