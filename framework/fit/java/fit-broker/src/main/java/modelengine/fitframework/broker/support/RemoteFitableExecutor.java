/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notEmpty;

import modelengine.fit.client.Address;
import modelengine.fit.client.Client;
import modelengine.fit.client.Request;
import modelengine.fit.client.RequestContext;
import modelengine.fit.client.Response;
import modelengine.fit.service.RegisterAuthService;
import modelengine.fit.service.exception.AuthenticationException;
import modelengine.fitframework.broker.Endpoint;
import modelengine.fitframework.broker.ExceptionInfo;
import modelengine.fitframework.broker.FitExceptionCreator;
import modelengine.fitframework.broker.Fitable;
import modelengine.fitframework.broker.FitableExecutor;
import modelengine.fitframework.broker.Format;
import modelengine.fitframework.broker.InvocationContext;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.conf.runtime.MatataConfig;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.RequestMetadata;
import modelengine.fitframework.serialization.ResponseMetadata;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.Version;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link FitableExecutor} 的远程调用实现。
 *
 * @author 季聿阶
 * @since 2023-03-28
 */
public class RemoteFitableExecutor extends AbstractUnicastFitableExecutor {
    private static final Logger log = Logger.get(RemoteFitableExecutor.class);

    private final BeanContainer container;
    private final LazyLoader<FitExceptionCreator> exceptionCreatorLoader = new LazyLoader<>(this::getExceptionCreator);
    private final LazyLoader<RegisterAuthService> requireRegisterAuthService =
            new LazyLoader<>(this::requireRegisterAuthService);
    private final LazyLoader<Set<String>> requireMatataGenericables = new LazyLoader<>(this::requireMatataGenericables);
    private final LazyLoader<Boolean> isAccessEnable = new LazyLoader<>(this::isAccessEnable);

    RemoteFitableExecutor(BeanContainer container) {
        this.container = container;
    }

    @Override
    protected Object execute(Fitable fitable, Target target, InvocationContext context, Object[] args) {
        this.validateTarget(fitable, target);
        log.debug("Prepare to invoke remote fitable. [id={}, target={}]", fitable.toUniqueId(), target);
        Format format = this.chooseFormat(target);
        RequestMetadata requestMetadataBytes = this.getRequestMetadataBytes(format, fitable);
        Method method = fitable.genericable().method().method();
        Response response = this.requestResponse(target, context, requestMetadataBytes, args, method);
        if (this.isTokenValid(response.metadata())) {
            this.requireRegisterAuthService.get().refreshToken(Instant.now());
            requestMetadataBytes = requestMetadataBytes.copy()
                    .accessToken(this.requireRegisterAuthService.get().getToken().getAccessToken().getToken())
                    .build();
            response = this.requestResponse(target, context, requestMetadataBytes, args, method);
        }
        if (this.isSuccess(response.metadata())) {
            log.debug("Invoke remote fitable successfully. [id={}, target={}]", fitable.toUniqueId(), target);
            return response.data();
        }
        ExceptionInfo exceptionInfo = ExceptionInfo.fromFitableAndResponseMetadata(fitable, response.metadata());
        FitException responseException = this.exceptionCreatorLoader.get().buildException(exceptionInfo);
        log.error("Failed to invoke remote fitable. [id={}, target={}, message={}]",
                fitable.toUniqueId(),
                target,
                responseException.getMessage());
        throw responseException;
    }

    private FitExceptionCreator getExceptionCreator() {
        return this.container.lookup(FitExceptionCreator.class)
                .map(BeanFactory::<FitExceptionCreator>get)
                .orElseThrow(() -> new IllegalStateException("No exception creator."));
    }

    private void validateTarget(Fitable fitable, Target target) {
        notBlank(target.workerId(), "The target worker id cannot be blank. [id={0}]", fitable.toUniqueId());
        notBlank(target.host(), "The target host cannot be blank. [id={0}]", fitable.toUniqueId());
        notBlank(target.environment(), "The target environment cannot be blank. [id={0}]", fitable.toUniqueId());
        notEmpty(target.endpoints(), "The target endpoints cannot be empty. [id={0}]", fitable.toUniqueId());
        notEmpty(target.formats(), "The target formats cannot be empty. [id={0}]", fitable.toUniqueId());
    }

    /**
     * 选择一个序列化协议。
     *
     * @param target 表示指定目标地址的 {@link Target}。
     * @return 表示选择的序列化协议的 {@link Format}。
     */
    protected Format chooseFormat(Target target) {
        return target.formats().iterator().next();
    }

    /**
     * 获取请求元数据。
     *
     * @param format 表示指定的序列化协议的 {@link Format}。
     * @param fitable 表示指定的泛化接口的 {@link Fitable}。
     * @return 表示请求元数据的 {@link RequestMetadata}。
     */
    protected RequestMetadata getRequestMetadataBytes(Format format, Fitable fitable) {
        String token =
                this.isAccessEnable.get() && this.requireMatataGenericables.get().contains(fitable.genericable().id())
                        ? this.requireRegisterAuthService.get().getToken().getAccessToken().getToken()
                        : null;
        return RequestMetadata.custom()
                .dataFormat(valueFormat(format.code()))
                .genericableId(fitable.genericable().id())
                .genericableVersion(Version.builder(fitable.genericable().version()).build())
                .fitableId(fitable.id())
                .fitableVersion(Version.builder(fitable.version()).build())
                .tagValues(this.getTlvFromSerializers())
                .accessToken(token)
                .build();
    }

    private static byte valueFormat(int format) {
        return (byte) (format & 0xFF);
    }

    private Client requireClient(String protocol) {
        return this.container.all(Client.class)
                .stream()
                .map(BeanFactory::<Client>get)
                .filter(client -> client.getSupportedProtocols().contains(protocol))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "No supported client at localhost. [protocol={0}]",
                        protocol)));
    }

    private RegisterAuthService requireRegisterAuthService() {
        return this.container.all(RegisterAuthService.class)
                .stream()
                .map(BeanFactory::<RegisterAuthService>get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "No supported register token service in container.")));
    }

    private Set<String> requireMatataGenericables() {
        return this.container.all(MatataConfig.class)
                .stream()
                .map(BeanFactory::<MatataConfig>get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "No supported matata config in container.")))
                .registry()
                .authRequiredServices()
                .stream()
                .map(MatataConfig.Registry.AvailableService::genericableId)
                .collect(Collectors.toSet());
    }

    private boolean isAccessEnable() {
        MatataConfig.Registry.SecureAccess secureAccess = this.container.all(MatataConfig.class)
                .stream()
                .map(BeanFactory::<MatataConfig>get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "No supported matata config in container.")))
                .registry()
                .secureAccess();
        return secureAccess != null && secureAccess.enabled();
    }

    private TagLengthValues getTlvFromSerializers() {
        Map<Integer, byte[]> tlvMaps = Collections.emptyMap();
        TagLengthValues result = TagLengthValues.create();
        result.putTags(tlvMaps);
        return result;
    }

    private boolean isSuccess(ResponseMetadata responseMetadata) {
        return responseMetadata.code() == ResponseMetadata.CODE_OK;
    }

    private boolean isTokenValid(ResponseMetadata responseMetadata) {
        return responseMetadata.code() == AuthenticationException.CODE;
    }

    private Response requestResponse(Target target, InvocationContext context, RequestMetadata metadata, Object[] args,
            Method method) {
        Endpoint endpoint = target.endpoints().iterator().next();
        Address address = Address.create(target.host(), endpoint.port());
        RequestContext requestContext = RequestContext.create(context.timeout(),
                context.timeoutUnit(),
                context.communicationType(),
                target.extensions());
        Type[] argumentTypes = this.getGenericParameterTypes(method, args);
        Request request = Request.custom()
                .protocol(endpoint.protocol())
                .address(address)
                .metadata(metadata)
                .dataTypes(argumentTypes)
                .data(args)
                .returnType(this.getGenericReturnType(method))
                .context(requestContext)
                .build();
        return this.requireClient(endpoint.protocol()).requestResponse(request);
    }

    /**
     * 获取泛化参数的类型数组。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @param args 表示调用参数的 {@link Object}{@code []}。
     * @return 表示泛化参数的类型数组的 {@link Type}{@code []}。
     */
    protected Type[] getGenericParameterTypes(Method method, Object[] args) {
        return Stream.of(method.getParameters()).map(Parameter::getParameterizedType).toArray(Type[]::new);
    }

    /**
     * 获取泛化返回值的类型。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示泛化返回值的类型的 {@link Type}。
     */
    protected Type getGenericReturnType(Method method) {
        return method.getGenericReturnType();
    }
}