/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security;

/**
 * 表示加密服务。
 *
 * @author 季聿阶
 * @since 2023-08-02
 */
public interface Encryptor {
    /**
     * 将指定明文进行加密。
     *
     * @param decrypted 表示待加密的明文的 {@link String}。
     * @return 表示加密后的密文的 {@link String}。
     */
    String encrypt(String decrypted);
}
