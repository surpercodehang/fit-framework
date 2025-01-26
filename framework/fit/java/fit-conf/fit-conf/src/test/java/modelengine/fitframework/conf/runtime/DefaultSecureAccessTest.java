/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.runtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link DefaultSecureAccess} 的测试类。
 *
 * @author 李金绪
 * @since 2024-08-22
 */
@DisplayName("测试 DefaultSecureAccess")
public class DefaultSecureAccessTest {
    @Test
    @DisplayName("测试正确创建实例")
    void shouldCreateInstance() {
        DefaultSecureAccess secureAccess = new DefaultSecureAccess();
        secureAccess.setAccessKey("AK");
        secureAccess.setSecretKey("SK");
        secureAccess.setEnabled(true);
        secureAccess.setEncrypted(false);
        Assertions.assertEquals("AK", secureAccess.accessKey());
        Assertions.assertEquals("SK", secureAccess.secretKey());
        Assertions.assertTrue(secureAccess.enabled());
        Assertions.assertFalse(secureAccess.encrypted());
    }
}
