/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.DefaultParameter;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#parameter-object">OpenAPI
 * 3.1.0</a> 文档中的参数信息。
 *
 * @author 季聿阶
 * @since 2023-08-21
 */
public interface Parameter extends Serializable {
    /**
     * 获取参数的名字。
     * <p><b>【必选】</b></p>
     *
     * @return 表示参数名字的 {@link String}。
     */
    String name();

    /**
     * 获取参数的位置。
     * <p><b>【必选】</b></p>
     *
     * @return 表示参数位置的 {@link String}。
     */
    String in();

    /**
     * 获取参数的描述信息。
     *
     * @return 表示参数的描述信息的 {@link String}。
     */
    String description();

    /**
     * 获取参数是否必选的标志。
     * <p><b>【必选】</b></p>
     *
     * @return 当参数必选时，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isRequired();

    /**
     * 获取参数是否废弃的标志。
     *
     * @return 当参数废弃时，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isDeprecated();

    /**
     * 获取参数的格式样例。
     *
     * @return 表示参数的格式样例的 {@link Schema}。
     */
    Schema schema();

    /**
     * 表示 {@link Parameter} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置参数的名字。
         * <p><b>【必选】</b></p>
         *
         * @param name 表示待设置的参数名字的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 向当前构建器中设置参数的位置。
         * <p><b>【必选】</b></p>
         *
         * @param in 表示待设置的参数位置的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder in(String in);

        /**
         * 向当前构建器中设置参数的描述信息。
         *
         * @param description 表示待设置的参数的描述信息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder description(String description);

        /**
         * 向当前构建器中设置参数是否必须的标志。
         * <p><b>【必选】</b></p>
         *
         * @param required 表示待设置的参数是否必选的标志的 {@code boolean}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder isRequired(boolean required);

        /**
         * 向当前构建器中设置参数是否废弃的标志。
         *
         * @param deprecated 表示待设置的参数是否废弃的标志的 {@code boolean}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder isDeprecated(boolean deprecated);

        /**
         * 向当前构建器中设置参数的格式样例。
         *
         * @param schema 表示待设置的参数的格式样例的 {@link Schema}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder schema(Schema schema);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Parameter}。
         */
        Parameter build();
    }

    /**
     * 获取 {@link Parameter} 的构建器。
     *
     * @return 表示 {@link Parameter} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultParameter.Builder();
    }
}
