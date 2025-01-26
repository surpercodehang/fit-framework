/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.util;

import modelengine.fit.service.entity.Address;
import modelengine.fit.service.entity.ApplicationInstance;
import modelengine.fit.service.entity.FitableAddressInstance;
import modelengine.fit.service.entity.Worker;
import modelengine.fitframework.broker.Endpoint;
import modelengine.fitframework.broker.Format;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.conf.runtime.CommunicationProtocol;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 处理 {@link FitableAddressInstance} 的工具类。
 *
 * @author 季聿阶
 * @since 2022-09-17
 */
public class FitableInstanceUtils {
    /**
     * 将指定的服务实例转化成地址列表。
     *
     * @param fitableInstance 表示指定的服务实例的 {@link FitableAddressInstance}。
     * @return 表示转化后的地址列表的 {@link List}{@code <}{@link Target}{@code >}。
     */
    public static List<Target> toTargets(FitableAddressInstance fitableInstance) {
        return Optional.of(fitableInstance)
                .map(FitableAddressInstance::getApplicationInstances)
                .filter(CollectionUtils::isNotEmpty)
                .map(applicationInstances -> applicationInstances.stream()
                        .filter(Objects::nonNull)
                        .map(FitableInstanceUtils::toTargets)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private static List<Target> toTargets(ApplicationInstance applicationInstance) {
        return Optional.of(applicationInstance)
                .map(ApplicationInstance::getWorkers)
                .filter(CollectionUtils::isNotEmpty)
                .map(workers -> workers.stream()
                        .filter(Objects::nonNull)
                        .map(worker -> toTargets(worker, applicationInstance.getFormats()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private static List<Target> toTargets(Worker worker, List<Integer> formats) {
        List<Address> actualAddresses =
                worker.getAddresses().stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(actualAddresses)) {
            Address address = new Address();
            address.setEndpoints(new ArrayList<>());
            actualAddresses.add(address);
        }
        return actualAddresses.stream()
                .map(address -> toTarget(worker, formats, address))
                .distinct()
                .collect(Collectors.toList());
    }

    private static Target toTarget(Worker worker, List<Integer> formatCodes, Address address) {
        List<Format> formats = formatCodes.stream()
                .map(code -> Format.custom()
                        .name(SerializationFormat.from(code).name())
                        .code(code)
                        .build())
                .collect(Collectors.toList());
        return Target.custom()
                .formats(formats)
                .workerId(worker.getId())
                .host(address.getHost())
                .environment(worker.getEnvironment())
                .endpoints(toTargetEndPoints(address.getEndpoints()))
                .extensions(worker.getExtensions())
                .build();
    }

    private static List<Endpoint> toTargetEndPoints(List<modelengine.fit.service.entity.Endpoint> endpoints) {
        if (CollectionUtils.isEmpty(endpoints)) {
            return Collections.emptyList();
        }
        return endpoints.stream()
                .filter(Objects::nonNull)
                .filter(endpoint -> endpoint.getPort() != null)
                .filter(endpoint -> endpoint.getProtocol() != null)
                .map(endpoint -> Endpoint.custom()
                        .port(endpoint.getPort())
                        .protocol(CommunicationProtocol.from(endpoint.getProtocol()).name(), endpoint.getProtocol())
                        .build())
                .collect(Collectors.toList());
    }
}
