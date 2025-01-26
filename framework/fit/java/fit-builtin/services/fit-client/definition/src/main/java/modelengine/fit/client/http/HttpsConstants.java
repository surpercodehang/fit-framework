/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http;

/**
 * 表示 https 所需要的配置项值。
 *
 * @author 杭潇
 * @since 2024-03-21
 */
public class HttpsConstants {
    /** 表示客户端是否选择忽略服务端证书 */
    public static final String CLIENT_SECURE_IGNORE_TRUST = "client.http.secure.ignore-trust";

    /** 表示客户端是否选择忽略 hostname */
    public static final String CLIENT_SECURE_IGNORE_HOSTNAME = "client.http.secure.ignore-hostname";

    /** 表示客户端根证书文件路径 */
    public static final String CLIENT_SECURE_TRUST_STORE_FILE = "client.http.secure.trust-store-file";

    /** 表示客户端根证书文件密码 */
    public static final String CLIENT_SECURE_TRUST_STORE_PASSWORD = "client.http.secure.trust-store-password";

    /** 表示客户端客户端证书文件路径 */
    public static final String CLIENT_SECURE_KEY_STORE_FILE = "client.http.secure.key-store-file";

    /** 表示客户端客户端文件密码 */
    public static final String CLIENT_SECURE_KEY_STORE_PASSWORD = "client.http.secure.key-store-password";

    /** 表示客户端是否启用强随机数生成器 */
    public static final String CLIENT_SECURE_STRONG_RANDOM = "client.http.secure.secure-random-enabled";

    /** 表示客户端通信协议 */
    public static final String CLIENT_SECURE_SECURITY_PROTOCOL = "client.http.secure.secure-protocol";
}