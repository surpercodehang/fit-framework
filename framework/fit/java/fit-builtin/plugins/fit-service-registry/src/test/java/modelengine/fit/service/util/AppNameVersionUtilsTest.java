/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.service.entity.FitableInfo;
import modelengine.fit.service.entity.FitableMeta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * {@link AppNameVersionUtils} 的测试类
 *
 * @author 李鑫
 * @since 2021-11-30
 */
public class AppNameVersionUtilsTest {
    @Test
    @DisplayName("given fitable metas when calculate app name version then get correct value")
    void testCalculateAppNameVersion() {
        FitableMeta fitableMeta1 = new FitableMeta();
        FitableInfo fitable1 = new FitableInfo();
        fitable1.setGenericableId("gid1");
        fitable1.setGenericableVersion("1.0");
        fitable1.setFitableId("fitableId1");
        fitable1.setFitableVersion("1.1");
        fitableMeta1.setFitable(fitable1);
        fitableMeta1.setFormats(Arrays.asList(0, 1));

        FitableMeta fitableMeta2 = new FitableMeta();
        FitableInfo fitable2 = new FitableInfo();
        fitable2.setGenericableId("gid2");
        fitable2.setGenericableVersion("2.0");
        fitable2.setFitableId("fitableId2");
        fitable2.setFitableVersion("2.1");
        fitableMeta2.setFitable(fitable2);
        fitableMeta2.setFormats(Arrays.asList(1, 2, 0));

        List<FitableMeta> fitableMetas = Arrays.asList(fitableMeta1, fitableMeta2);
        String version = AppNameVersionUtils.calculateAppNameVersion(fitableMetas, "debug");
        assertThat(version).isEqualTo("c6eb350dd1e269abfe5cf21866ed5c53");
    }
}
