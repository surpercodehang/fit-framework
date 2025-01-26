/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.server.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.broker.LocalExecutorFactory;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.broker.server.Dispatcher;
import modelengine.fitframework.broker.server.GenericableServerFilterChain;
import modelengine.fitframework.broker.server.GenericableServerFilterManager;
import modelengine.fitframework.broker.server.Response;
import modelengine.fitframework.broker.server.ServerLocalExecutorNotFoundException;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.exception.MethodInvocationException;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.serialization.RequestMetadata;
import modelengine.fitframework.serialization.ResponseMetadata;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.tlv.TlvUtils;
import modelengine.fitframework.util.ExceptionUtils;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link Dispatcher} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-10-17
 */
public class DefaultDispatcher implements Dispatcher {
    private static final Logger log = Logger.get(DefaultDispatcher.class);

    private final WorkerConfig workerConfig;
    private final LazyLoader<LocalExecutorFactory> localExecutorFactoryLoader;
    private final GenericableServerFilterManager genericableServerFilterManager;

    /**
     * 通过 Bean 容器和当前进程配置来初始化 {@link DefaultDispatcher} 的新实例。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param workerConfig 表示当前进程配置的 {@link WorkerConfig}。
     * @param genericableServerFilterManager 表示过滤器管理的 {@link GenericableServerFilterManager}。
     */
    public DefaultDispatcher(BeanContainer container, WorkerConfig workerConfig,
            GenericableServerFilterManager genericableServerFilterManager) {
        this.workerConfig = notNull(workerConfig, "The worker config cannot be null.");
        this.localExecutorFactoryLoader = new LazyLoader<>(() -> container.factory(LocalExecutorFactory.class)
                .map(BeanFactory::<LocalExecutorFactory>get)
                .orElseThrow(() -> new IllegalStateException("No local executor factory.")));
        this.genericableServerFilterManager =
                notNull(genericableServerFilterManager, "The genericable server filter manager cannot be null.");
    }

    @Override
    public Response dispatch(RequestMetadata metadata, Object[] data) {
        try {
            LocalExecutor executor = this.getLocalExecutor(metadata);
            GenericableServerFilterChain chain = this.getGenericableServerFilterChain(metadata.genericableId(),
                    executor.metadata().container().plugin());
            chain.doFilter(data);
            Object response = executor.execute(data);
            log.debug("Execute fitable successfully. [genericableId={}, fitableId={}]",
                    metadata.genericableId(),
                    metadata.fitableId());
            return Response.create(this.successfulResponseMetadataBytes(metadata),
                    executor.method().getGenericReturnType(),
                    response);
        } catch (Throwable cause) {
            log.error("Failed to execute fitable. [genericableId={}, fitableId={}, cause={}]",
                    metadata.genericableId(),
                    metadata.fitableId(),
                    cause.getMessage(),
                    cause);
            return Response.create(this.getResponseMetadata(cause, metadata));
        } finally {
            log.debug("Prepare to clear global context.");
        }
    }

    private LocalExecutor getLocalExecutor(RequestMetadata reqMetadata) {
        return this.localExecutorFactoryLoader.get()
                .get(UniqueFitableId.create(reqMetadata.genericableId(), reqMetadata.fitableId()))
                .orElseThrow(() -> new ServerLocalExecutorNotFoundException("No local executor."));
    }

    private ResponseMetadata getResponseMetadata(Throwable cause, RequestMetadata metadata) {
        if (cause instanceof MethodInvocationException) {
            Throwable actualCause = ExceptionUtils.getActualCause((MethodInvocationException) cause);
            return this.handleException(actualCause, metadata);
        } else {
            return this.handleException(cause, metadata);
        }
    }

    private ResponseMetadata handleException(Throwable cause, RequestMetadata metadata) {
        if (cause instanceof FitException) {
            FitException actualCause = ObjectUtils.cast(cause);
            TagLengthValues tagLengthValues = this.buildTagLengthValues(actualCause.getProperties());
            return this.responseMetadataBytes(metadata,
                    actualCause.getCode(),
                    actualCause.getMessage(),
                    tagLengthValues);
        }
        return this.responseMetadataBytes(metadata, -1, cause.getMessage(), null);
    }

    private TagLengthValues buildTagLengthValues(Map<String, String> properties) {
        TagLengthValues tagLengthValues = TagLengthValues.create();
        TlvUtils.setExceptionProperties(tagLengthValues, properties);
        return tagLengthValues;
    }

    private ResponseMetadata successfulResponseMetadataBytes(RequestMetadata metadata) {
        return this.responseMetadataBytes(metadata, 0, StringUtils.EMPTY, null);
    }

    private ResponseMetadata responseMetadataBytes(RequestMetadata metadata, int code, String message,
            TagLengthValues tagLengthValues) {
        TagLengthValues actual = ObjectUtils.getIfNull(tagLengthValues, TagLengthValues::create);
        TlvUtils.setWorkerId(actual, this.workerConfig.id());
        TlvUtils.setWorkerInstanceId(actual, this.workerConfig.instanceId());
        return ResponseMetadata.custom()
                .dataFormat(valueFormat(metadata.dataFormat()))
                .code(code)
                .message(message)
                .tagValues(actual)
                .build();
    }

    private static byte valueFormat(int format) {
        return (byte) (format & 0xFF);
    }

    private GenericableServerFilterChain getGenericableServerFilterChain(String genericableId, Plugin plugin) {
        return new DefaultGenericableServerFilterChain(genericableId,
                this.genericableServerFilterManager.get()
                        .stream()
                        .filter(filter -> filter.scope() == Scope.GLOBAL || filter.plugin().equals(plugin))
                        .collect(Collectors.toList()));
    }
}
