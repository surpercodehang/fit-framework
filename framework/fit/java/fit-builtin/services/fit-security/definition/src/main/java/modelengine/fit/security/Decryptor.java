/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security;

/**
 * 表示解密服务。
 *
 * @author 季聿阶
 * @since 2023-07-31
 */
public interface Decryptor {
    /** 表示通用待解密密文的前缀。 */
    String CIPHER_PREFIX = "enc(";

    /** 表示通用待解密密文的后缀。 */
    String CIPHER_SUFFIX = ")";

    /**
     * 将指定密文进行解密。
     *
     * @param encrypted 表示待解密的密文的 {@link String}。
     * @return 表示解密后的明文的 {@link String}。
     */
    String decrypt(String encrypted);
}
