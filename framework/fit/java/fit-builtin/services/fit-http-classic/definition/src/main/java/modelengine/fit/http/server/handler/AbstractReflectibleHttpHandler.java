/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.server.DoHttpHandlerException;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.HttpHandler;
import modelengine.fit.http.server.HttpServerResponseException;
import modelengine.fit.http.server.ReflectibleMappingHandler;
import modelengine.fitframework.exception.MethodInvocationException;
import modelengine.fitframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 对 Http 请求根据指定规则进行参数映射的 {@link HttpHandler}。
 *
 * @author 季聿阶
 * @since 2022-07-28
 */
public abstract class AbstractReflectibleHttpHandler extends AbstractHttpHandler implements ReflectibleMappingHandler {
    private final Object target;
    private final Method method;
    private final List<PropertyValueMapper> propertyValueMappers;
    private final StaticInfo staticInfo;
    private final List<PropertyValueMetadata> propertyValueMetadata;

    private String groupName;

    /**
     * 通过 Http 处理的相关静态信息和执行信息来实例化 {@link AbstractReflectibleHttpHandler}。
     *
     * @param staticInfo 表示 Http 处理器的相关静态信息的 {@link StaticInfo}。
     * @param executionInfo 表示 Http 处理器的相关执行信息的 {@link ExecutionInfo}。
     * @throws IllegalArgumentException 当 {@code staticInfo} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code staticInfo}{@link ExecutionInfo#httpServer() .httpServer()}
     * 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code staticInfo}{@link StaticInfo#pathPattern() .pathPattern()}
     * 为 {@code null} 或空白字符串时。
     * @throws IllegalArgumentException 当 {@code executionInfo} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code executionInfo}{@link ExecutionInfo#httpMappers() .httpMappers()}
     * 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code executionInfo}{@link ExecutionInfo#target() .target()}
     * 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code executionInfo}{@link ExecutionInfo#method() .method()}
     * 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code staticInfo}{@link StaticInfo#propertyValueMetadata()
     * .propertyValueMetadata()} 为 {@code null} 时。
     */
    protected AbstractReflectibleHttpHandler(StaticInfo staticInfo, ExecutionInfo executionInfo) {
        super(staticInfo, executionInfo);
        this.staticInfo = notNull(staticInfo, "The http handler static info cannot be null.");
        notNull(executionInfo, "The http handler execution info cannot be null.");
        this.propertyValueMappers = notNull(executionInfo.httpMappers(), "The http mappers cannot be null.");
        for (PropertyValueMapper propertyValueMapper : this.propertyValueMappers) {
            notNull(propertyValueMapper, "The http mapper cannot be null.");
        }
        this.target = notNull(executionInfo.target(), "The specified target of http handler cannot be null.");
        this.method = notNull(executionInfo.method(), "The specified method of http handler cannot be null.");
        this.propertyValueMetadata =
                notNull(staticInfo.propertyValueMetadata(), "The http value meta data cannot be null.");
    }

    @Override
    public Object target() {
        return this.target;
    }

    @Override
    public Method method() {
        return this.method;
    }

    @Override
    public List<PropertyValueMetadata> propertyValueMetadata() {
        return Collections.unmodifiableList(this.propertyValueMetadata);
    }

    @Override
    public int statusCode() {
        return this.staticInfo.statusCode();
    }

    @Override
    public boolean isDocumentIgnored() {
        return this.staticInfo.isDocumentIgnored();
    }

    @Override
    public String summary() {
        return this.staticInfo.summary();
    }

    @Override
    public String description() {
        return this.staticInfo.description();
    }

    @Override
    public String returnDescription() {
        return this.staticInfo.returnDescription();
    }

    @Override
    public void group(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String group() {
        return this.groupName;
    }

    @Override
    public void handle(HttpClassicServerRequest request, HttpClassicServerResponse response)
            throws DoHttpHandlerException {
        Object[] args;
        try {
            args = this.propertyValueMappers.stream()
                    .map(httpMapper -> httpMapper.map(request, response, null))
                    .toArray();
        } catch (HttpServerResponseException e) {
            this.handleException(request, response, new Object[0], e);
            return;
        }
        try {
            Object ret = ReflectionUtils.invoke(this.target, this.method, args);
            this.handleResult(request, response, args, ret);
        } catch (MethodInvocationException e) {
            this.handleException(request, response, args, e.getCause());
        }
    }

    /**
     * 处理参数映射后，调用本地指定方法后的结果。
     * <p>如果需要处理结果，子类需要覆盖该方法。</p>
     *
     * @param request 表示 Http 请求的 {@link HttpClassicServerRequest}。
     * @param response 表示 Http 响应的 {@link HttpClassicServerResponse}。
     * @param args 表示映射后的参数的 {@link List}{@code <}{@link Object}{@code >}。
     * @param result 表示调用映射方法的返回值的 {@link Object}。
     * @throws DoHttpHandlerException 当处理过程中发生异常时。
     */
    protected void handleResult(HttpClassicServerRequest request, HttpClassicServerResponse response, Object[] args,
            Object result) throws DoHttpHandlerException {}

    /**
     * 处理参数映射后，调用本地指定方法发生异常后的结果。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicServerRequest}。
     * @param response 表示 Http 响应的 {@link HttpClassicServerResponse}。
     * @param args 表示映射后的参数的 {@link List}{@code <}{@link Object}{@code >}。
     * @param cause 表示调用映射方法时抛出的异常的 {@link Throwable}。
     * @throws DoHttpHandlerException 当处理过程中发生异常时。
     */
    protected void handleException(HttpClassicServerRequest request, HttpClassicServerResponse response, Object[] args,
            Throwable cause) throws DoHttpHandlerException {}
}
