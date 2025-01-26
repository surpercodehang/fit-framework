/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.header;

import modelengine.fit.http.header.support.DefaultContentDisposition;
import modelengine.fit.http.header.support.DefaultContentType;
import modelengine.fit.http.header.support.DefaultHeaderValue;

/**
 * 表示消息头中值的信息。
 *
 * @author 季聿阶
 * @since 2022-09-04
 */
public interface HeaderValue {
    /**
     * 获取消息头的值。
     *
     * @return 表示消息头值的 {@link String}。
     */
    String value();

    /**
     * 获取所有参数集合。
     *
     * @return 表示所有参数集合的 {@link ParameterCollection}。
     */
    ParameterCollection parameters();

    /**
     * 获取消息头的值和所有参数的文本内容。
     *
     * @return 消息头的值和所有参数的文本内容的 {@link String}。
     */
    @Override
    String toString();

    /**
     * 根据消息头的值构建消息头的对象。
     *
     * @param value 表示消息头的值的 {@link String}。
     * @return 表示消息头对象的 {@link HeaderValue}。
     */
    static HeaderValue create(String value) {
        return create(value, null);
    }

    /**
     * 根据消息头的值和参数集合构建消息头的对象。
     *
     * @param value 表示消息头的值的 {@link String}。
     * @param parameterCollection 表示消息头的参数集合的 {@link ParameterCollection}。
     * @return 表示消息头对象的 {@link HeaderValue}。
     */
    static HeaderValue create(String value, ParameterCollection parameterCollection) {
        return new DefaultHeaderValue(value, parameterCollection);
    }

    /**
     * 将当前消息头对象转换成 {@code Content-Type} 对象。
     *
     * @return 表示转换后对象的 {@link ContentType}。
     */
    default ContentType toContentType() {
        return new DefaultContentType(this);
    }

    /**
     * 将当前消息头对象转换成 {@code Content-Disposition} 对象。
     *
     * @return 表示转换后对象的 {@link ContentDisposition}。
     */
    default ContentDisposition toContentDisposition() {
        return new DefaultContentDisposition(this);
    }
}
