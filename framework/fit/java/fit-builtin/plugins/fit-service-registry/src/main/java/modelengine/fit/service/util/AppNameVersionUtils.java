/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.util;

import modelengine.fit.service.entity.FitableInfo;
import modelengine.fit.service.entity.FitableMeta;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 计算注册使用的应用版本号的工具类
 *
 * @author 李鑫
 * @since 2021-11-29
 */
public class AppNameVersionUtils {
    private static final String ALGORITHM_TYPE = "MD5";
    private static final String DELIMITER_BETWEEN_FORMATS = ",";
    private static final String FITABLE_STRING_FORMAT = "{0}:{1}:{2}:{3}:{4}:{5};";

    /**
     * 通过 {@link FitableMeta} 列表计算应用版本号。
     * <p>摘要格式如下：</p>
     * <p>{@code "${genericableId}:${genericableVersion}:${fitableId}:${fitableVersion}:${fitableFormat}:${
     * environment};"}</p>
     * <p>其中，{@code ${fitableFormat}} 中的值使用 {@code ','} 分隔，且按字母序排序。</p>
     * <p>例如：{@code "gid1:1.0:fitableId1:1.1:0,1,2:debug;gid2:2.0:fitableId2:2.1:0,2:debug;"}</p>
     *
     * @param fitableMetasWithFormats 表示服务实现元数据列表的 {@link List}{@code <}{@link FitableMeta}{@code >}。
     * @param environment 表示应用的环境标的 {@link String}。
     * @return 表示计算后的应用版本号的 {@link String}。
     */
    public static String calculateAppNameVersion(List<FitableMeta> fitableMetasWithFormats, String environment) {
        List<String> fitableStrings = fitableMetasWithFormats.stream()
                .filter(Objects::nonNull)
                .filter(fitableMeta -> fitableMeta.getFitable() != null)
                .map(fitableMeta -> buildFitableString(fitableMeta, environment))
                .sorted()
                .collect(Collectors.toList());
        return DigestUtils.hashCode(fitableStrings, ALGORITHM_TYPE);
    }

    private static String buildFitableString(FitableMeta fitableMeta, String environment) {
        FitableInfo fitable = fitableMeta.getFitable();
        return StringUtils.format(FITABLE_STRING_FORMAT,
                fitable.getGenericableId(),
                Optional.ofNullable(fitable.getGenericableVersion()).orElse(StringUtils.EMPTY),
                fitable.getFitableId(),
                Optional.ofNullable(fitable.getFitableVersion()).orElse(StringUtils.EMPTY),
                buildFormatsString(fitableMeta),
                Optional.ofNullable(environment).orElse(StringUtils.EMPTY));
    }

    private static String buildFormatsString(FitableMeta fitableMeta) {
        return fitableMeta.getFormats()
                .stream()
                .map(String::valueOf)
                .sorted()
                .collect(Collectors.joining(DELIMITER_BETWEEN_FORMATS));
    }
}
