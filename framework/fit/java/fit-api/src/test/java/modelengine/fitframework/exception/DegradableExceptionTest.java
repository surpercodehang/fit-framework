/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.exception;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link DegradableException} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-24
 */
@DisplayName("测试 DegradableException 类以及相关类")
class DegradableExceptionTest {
    @Test
    @DisplayName("提供 DegradableException 类不带异常原因时，返回正常信息")
    void givenNoThrowDegradableExceptionShouldReturnMessage() {
        String message = "fail";
        DegradableException exception = new DegradableException(message);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("提供 DegradableException 类带异常原因时，返回正常信息")
    void givenThrowDegradableExceptionShouldReturnMessage() {
        String message = "fail";
        DegradableException exception = new DegradableException(new Throwable(message));
        assertThat(exception.getCause().getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("提供 DegradableException 类带 genericable 时，返回正常信息")
    void givenGenericableDegradableExceptionShouldReturnMessage() {
        String genericableId = "9588e5fc63cc4f1fbdcf2567bce0a454";
        String fitableId = "9588e5fc63cc4f1fbdcf2567bce0a459";
        String message = "fail";
        DegradableException exception = new DegradableException(message);
        exception.associateFitable(genericableId, fitableId);
        assertThat(exception.associatedGenericableId()).isEqualTo(genericableId);
    }

    @Test
    @DisplayName("提供 DegradableException 类带 fitableId 关联 genericable 时，返回正常信息")
    void givenThrowGenericableDegradableExceptionShouldReturnMessage() {
        String genericableId = "9588e5fc63cc4f1fbdcf2567bce0a454";
        String fitableId = "9588e5fc63cc4f1fbdcf2567bce0a459";
        String message = "fail";
        DegradableException exception = new DegradableException(new Throwable(message));
        exception.associateFitable(genericableId, fitableId);
        assertThat(exception.associatedFitableId()).isEqualTo(fitableId);
    }

    @Test
    @DisplayName("提供 DegradableException 类降级服务时，返回正常信息")
    void givenDegradableExceptionWhenDegradationKeyThenReturnMessage() {
        String genericableId = "9588e5fc63cc4f1fbdcf2567bce0a454";
        String fitableId = "9588e5fc63cc4f1fbdcf2567bce0a459";
        String message = "fail";
        DegradableException exception = new DegradableException(new Throwable(message));
        exception.associateFitable(genericableId, fitableId);
        exception.setProperties(MapBuilder.<String, String>get().put("degradation", fitableId).build());
        assertThat(exception.degradationKey()).isEqualTo(fitableId);
    }
}