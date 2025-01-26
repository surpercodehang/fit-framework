/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * {@link DigestUtils} 的测试类
 *
 * @author 李鑫
 * @since 2021-11-30
 */
public class DigestUtilsTest {
    private static final String ALGORITHM_TYPE = "MD5";
    private static final String INVALID_ALGORITHM_TYPE = "MD_5";

    @Test
    @DisplayName("given string list and valid algorithm type when get hash code then get correct hash code")
    void testGetHashCodeByMd5() {
        List<String> strList = Arrays.asList("gid1:1.0:fitableId1:1.1:0,1;", "gid2:2.0:fitableId2:2.1:0,1,2;");
        String hashValue = DigestUtils.hashCode(strList, ALGORITHM_TYPE);
        assertThat(hashValue).isEqualTo("f3dbc253a838290dd3de2476692b8edf");
    }

    @Test
    @DisplayName("given string list and invalid algorithm type when get hash code then throw illegal state exception")
    void testGetHashCodeWhenException() {
        List<String> strList = Arrays.asList("fitableId1:0,1", "fitableId2:0,1,2");
        IllegalStateException exception =
                catchThrowableOfType(() -> DigestUtils.hashCode(strList, INVALID_ALGORITHM_TYPE),
                        IllegalStateException.class);
        assertThat(exception).isNotNull().hasMessage("Failed to get hash code.");
    }
}
